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
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.NeuralNetworkData;
import edu.washington.maccoss.intensity_predictor.structures.PeptideData;

public class ClassifyPeptides {
	public static List<PeptideData> getGoodPeptides(List<String> peptides) {
		return getGoodPeptides(peptides, 5);
	}
	public static List<PeptideData> getGoodPeptides(List<String> peptides, int numberOfPeptides) {
		BackPropNeuralNetwork network=Prego.getNetwork();

		TreeMap<String, ArrayList<String>> proteinMap=new TreeMap<>();

		for (String string : peptides) {
			string=string.trim();
			if (string.length()==0) continue;

			StringTokenizer st=new StringTokenizer(string, ",");
			String accession;
			String sequence;
			if (st.countTokens()==0) {
				continue; // drop empty lines
			} else if (st.countTokens()==1) {
				accession="Unknown";
				sequence=st.nextToken();
			} else {
				accession=st.nextToken();
				sequence=st.nextToken();
			}
			ArrayList<String> list=proteinMap.get(accession);
			if (list==null) {
				list=new ArrayList<>();
				proteinMap.put(accession, list);
			}
			list.add(sequence);
		}

		List<PeptideData> result=new ArrayList<PeptideData>();

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

			for (int i=0; i<scores.size(); i++) {
				if (i>=numberOfPeptides) break;
				
				ScoredPeptide scoredPeptide=scores.get(i);
				int index=i+1;

				result.add(new PeptideData(index, scoredPeptide.sequence, accession, scoredPeptide.score));
			}
		}
		return result;
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

	private static boolean getPeptideSequencesOnly(ArrayList<String> arguments) {
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
}
