/**
 * Copyright 2015 Brian C. Searle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.washington.maccoss.intensity_predictor;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.NeuralNetworkData;
import edu.washington.maccoss.intensity_predictor.parsers.FastaEntry;
import edu.washington.maccoss.intensity_predictor.parsers.FastaReader;

public class Classify {
	public static void main(String[] args) {
		if (args.length==0) {
			Logger.writeError("Incorrect number of arguments! Classify takes at least one argument. Options are:");
			Logger.writeError("\t-f\t(Optional) Files are in FASTA format. Proteins will be tryptically digested and peptides >25 or <8 will be discarded.");
			Logger.writeError("\t-p\t(Optional) Files only have peptide sequences, not protein accessions. This argument must be used first if specified.");
			Logger.writeError("\t-nn [file.nn]\t(Optional) Use alternate neural network file generated by BuildClassifier. This argument must be used before the files are specified.");
			Logger.writeError("\t-r [report.txt]\t(Optional) Use alternate report file. This argument must be used before the files are specified.");
			Logger.writeError("\t[file.txt]...\tSpecifies one or more input text files of protein accessions and peptide sequences where each protein/sequence pair is a tab deliminated row.");
			System.exit(1);
		}
		
		ArrayList<String> arguments=new ArrayList(Arrays.asList(args));
		boolean sequencesOnly=usePeptideSequencesOnly(arguments);
		boolean useFasta=useFasta(arguments);
		
		BackPropNeuralNetwork network=getNetwork(arguments);
		File reportFile=getReportFile(arguments);

		System.out.println("Reading sequences...");
		TreeMap<String, ArrayList<String>> proteinMap;
		if (useFasta) {
			proteinMap=getFastaSequences(arguments, true, 8, 25);
		} else {
			proteinMap=getSequences(sequencesOnly, arguments);
		}


		BufferedWriter out=null;
		if (reportFile!=null) {
			System.out.println("Writing file...");
			try {
				out=new BufferedWriter(new FileWriter(reportFile));
				out.write("accession\trank\tsequence\tscore");
				out.newLine();
			} catch (IOException ioe) {
				Logger.writeError("Error writing data to: "+reportFile.getName());
				ioe.printStackTrace();
				System.exit(1);
			}
		}
		
		for (Entry<String, ArrayList<String>> entry : proteinMap.entrySet()) {
			String accession=entry.getKey();
			ArrayList<String> sequences=entry.getValue();
			
			ArrayList<ScoredPeptide> scores=new ArrayList<ScoredPeptide>();
			for (String sequence : sequences) {
				double score=network.getScore(sequence);
				ScoredPeptide peptide=new ScoredPeptide(score, sequence);
				scores.add(peptide);
			}
			
			Collections.sort(scores);
			Collections.reverse(scores);
			
			if (out!=null) {
				try {
					for (int i=0; i<scores.size(); i++) {
						ScoredPeptide scoredPeptide=scores.get(i);
						int index=i+1;

						if (index%1000==0) System.out.print(".");
						if (index%10000==0) System.out.print(" ");
						if (index%50000==0) System.out.println();
						
						out.write(accession+"\t"+index+"\t"+scoredPeptide.sequence+"\t"+scoredPeptide.score);
						out.newLine();
					}
				} catch (IOException ioe) {
					Logger.writeError("Error writing data to: "+reportFile.getName());
					ioe.printStackTrace();
					System.exit(1);
				}
			} else {
				for (int i=0; i<scores.size(); i++) {
					ScoredPeptide scoredPeptide=scores.get(i);
					System.out.println(accession+"\t"+(i+1)+"\t"+scoredPeptide.sequence+"\t"+scoredPeptide.score);
				}
			}
		}
		if (out!=null) {
			try {
				out.close();
			} catch (IOException ioe) {
				Logger.writeError("Error closing file: "+reportFile.getName());
				System.exit(1);
			}
		}
	}
	
	private static class ScoredPeptide implements Comparable<ScoredPeptide> {
		private final double score;
		private final String sequence;
		public ScoredPeptide(double score, String sequence) {
			this.score=score;
			this.sequence=sequence;
		}
		@Override
		public int compareTo(ScoredPeptide o) {
			if (o==null) return 1;
			int compare=Double.compare(score, o.score);
			if (compare!=0) return compare;
			return sequence.compareTo(o.sequence);
		}
	}

	private static TreeMap<String, ArrayList<String>> getFastaSequences(List<String> arguments, boolean trypticDigest, int minLength, int maxLength) {
		if (arguments.size()==0) {
			Logger.writeError("You need to specify at least one peptide file!");
			System.exit(1);
		}
		
		TreeMap<String, ArrayList<String>> proteinMap=new TreeMap<String, ArrayList<String>>();
		for (String fileName : arguments) {
			File f=new File(fileName);
			ArrayList<FastaEntry> proteins=FastaReader.readFasta(f);
			for (FastaEntry entry : proteins) {
				ArrayList<String> peptides;
				if (!trypticDigest) {
					peptides=new ArrayList<>();
					peptides.add(entry.getSequence());
				} else {
					peptides=digestProtein(entry.getSequence(), minLength, maxLength);
				}
				String accession=entry.getAnnotation().substring(0, entry.getAnnotation().indexOf(' '));
				proteinMap.put(accession, peptides);
			}
		}
		return proteinMap;
	}

	private static ArrayList<String> digestProtein(String sequence, int minLength, int maxLength) {
		ArrayList<String> peptides=new ArrayList<String>();
		String peptide;
		int start=0;
		int stop;

		while (start<sequence.length()) {
			stop=start;
			while ((stop<sequence.length()-1)&&(((sequence.charAt(stop)=='K'||sequence.charAt(stop)=='R')&&sequence.charAt(stop+1)=='P')||(sequence.charAt(stop)!='K'&&sequence.charAt(stop)!='R'))) {
				stop++;
			}
			peptide=sequence.substring(start, stop+1);
			if ((peptide.length()>=minLength)&&(peptide.length()<=maxLength)) {
				peptides.add(peptide);
			}

			start=stop+1;
		}
		return peptides;
	}

	private static TreeMap<String, ArrayList<String>> getSequences(boolean onlySequences, List<String> arguments) {
		if (arguments.size()==0) {
			Logger.writeError("You need to specify at least one peptide file!");
			System.exit(1);
		}
		
		TreeMap<String, ArrayList<String>> proteinMap=new TreeMap<String, ArrayList<String>>();
		BufferedReader reader=null;
		for (String fileName : arguments) {
			File f=new File(fileName);
			try {
				reader=new BufferedReader(new FileReader(f));

				String line=null;
				while ((line=reader.readLine())!=null) {
					if (line.startsWith("#")) continue; // comment

					StringTokenizer st=new StringTokenizer(line);
					String accession;
					String sequence;
					if (onlySequences) {
						accession="Unknown";
						sequence=st.nextToken();
					} else {
						if (st.countTokens()<2) {
							Logger.writeError("Sequence file has the incorrect number of columns. Expected 2 columns but found "+st.countTokens());
							System.exit(1);
						}
						accession=st.nextToken();
						sequence=st.nextToken();
					}
					
					ArrayList<String> sequences=proteinMap.get(accession);
					if (sequences==null) {
						sequences=new ArrayList<String>();
						proteinMap.put(accession, sequences);
					}
					sequences.add(sequence);					
				}

			} catch (IOException ioe) {
				Logger.writeError("Error parsing peptide file!");
				Logger.writeError(ioe);
				System.exit(1);
			} finally {
				try {
					if (reader!=null) reader.close();
				} catch (IOException ioe) {
					Logger.writeError("Error closing peptide file!");
					Logger.writeError(ioe);
				}
			}
		}
		return proteinMap;
	}
	
	private static boolean useFasta(ArrayList<String> arguments) {
		if (arguments.size()>1&&arguments.get(0).equals("-f")) {
			arguments.remove(0);
			return true;
		} else {
			return false;
		}
		
	}
	
	private static boolean usePeptideSequencesOnly(ArrayList<String> arguments) {
		if (arguments.size()>1&&arguments.get(0).equals("-p")) {
			arguments.remove(0);
			return true;
		} else {
			return false;
		}
		
	}

	private static File getReportFile(ArrayList<String> arguments) {
		if (arguments.size()>1&&arguments.get(0).equals("-r")) {
			File reportFile=new File(arguments.get(1));
			arguments.remove(0);
			arguments.remove(0);
			return reportFile;
		}
		return null;
	}

	private static BackPropNeuralNetwork getNetwork(ArrayList<String> arguments) {
		BackPropNeuralNetwork network=null;
		if (arguments.size()>1&&arguments.get(0).equals("-nn")) {
			File neuralNetworkFile=new File(arguments.get(1));
			if (!neuralNetworkFile.exists()||!neuralNetworkFile.canRead()) {
				Logger.writeError("Can't read the neural network file! Please make sure it available and readable. The file you specified was:");
				Logger.writeError("\t["+neuralNetworkFile.getAbsolutePath()+"]");
				System.exit(1);
			}

			try {
				network=NeuralNetworkData.readNetwork(neuralNetworkFile);
			} catch (Exception e) {
				Logger.writeError("Error reading neural network file!");
				Logger.writeError(e);
				System.exit(1);
			}
			arguments.remove(0);
			arguments.remove(0);
			
		} else {
			try {
				
				URI uri=NeuralNetworkData.class.getClassLoader().getResource("new_jarrett_intensities.nn").toURI();
				File neuralNetworkFile=new File(uri);

				try {
					network=NeuralNetworkData.readNetwork(neuralNetworkFile);
				} catch (Exception e) {
					Logger.writeError("Error default reading neural network file!");
					Logger.writeError(e);
					System.exit(1);
				}

			} catch (URISyntaxException urise) {
				Logger.writeError("Error finding default neural network file.");
				Logger.writeError(urise);
				System.exit(1);
			}
		}
		return network;
	}
}
