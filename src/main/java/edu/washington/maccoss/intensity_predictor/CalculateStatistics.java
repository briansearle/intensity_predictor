package edu.washington.maccoss.intensity_predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.Correlation;
import edu.washington.maccoss.intensity_predictor.math.General;
import edu.washington.maccoss.intensity_predictor.math.Median;
import edu.washington.maccoss.intensity_predictor.math.NeuralNetworkData;
import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import edu.washington.maccoss.intensity_predictor.structures.PeptideScoreComparator;
import edu.washington.maccoss.intensity_predictor.structures.PeptideWithScores;
import edu.washington.maccoss.intensity_predictor.structures.Protein;
import gnu.trove.list.array.TDoubleArrayList;

public class CalculateStatistics {
	private static final int topN=25;
	private static final int minimumNumPeptides=4;

	public static void main(String[] args) {
		boolean random=false;
		boolean andrew=false;
		if (true) {
			if (andrew) args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/ppa_andrew_scores.txt"};
			else args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/ppa_scores.txt"};
		}
		if (true) {
			if (andrew) args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/esp_andrew_scores.txt"};
			else args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/esp_scores.txt"};
		}
		if (true) {
			if (andrew) args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/consequence_ann_andrew_scores.txt"};
			else args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/consequence_ann_scores.txt"};
		}
		if (true) {
			if (andrew) args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/consequence_svm_andrew_scores.txt"};
			else args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/consequence_svm_scores.txt"};
		}
		if (false) {
			if (andrew) args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/esp_andrew_scores.txt", "/Users/searleb/Documents/school/maccoss rotation/transitions/jarrett.nn"};
			else args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/esp_scores.txt", "/Users/searleb/Documents/school/maccoss rotation/transitions/jarrett.nn"};
		}
		System.out.println("Processing: "+args[0]);
		File peptidesWithIntensityFile=new File(args[0]);
		BackPropNeuralNetwork network=null;
		if (args.length>1) {
			File neuralNetworkFile=new File(args[1]);
			network=NeuralNetworkData.readNetwork(neuralNetworkFile);
			int count=0;
			for (AbstractProperty property : network.getPropertyList()) {
				count++;
				System.out.println(count+") "+property.toString());
			}
		}
		
		ArrayList<Protein> proteins=getProteins(peptidesWithIntensityFile, network, random);
		
		ArrayList<TDoubleArrayList> scoresByRank=new ArrayList<TDoubleArrayList>();
		for (int i=0; i<topN; i++) {
			scoresByRank.add(new TDoubleArrayList());
		}
		
		//System.out.println("Accession\tNumPeptides\tSpearmans");
		for (Protein protein : proteins) {
			ArrayList<AbstractPeptide> peptides=protein.getPeptides();
			//if (peptides.size()!=topN) continue;
			Collections.sort(peptides);
			Collections.reverse(peptides);
			int rank=0;
			TDoubleArrayList localRanks=new TDoubleArrayList();
			TDoubleArrayList localScores=new TDoubleArrayList();
			for (AbstractPeptide peptide : peptides) {
				if (rank<scoresByRank.size()) {
					TDoubleArrayList scores=scoresByRank.get(rank);
					scores.add(peptide.getScoreArray()[0]);
				} else {
					break;
				}
				rank++;
				localRanks.add(rank);
				localScores.add(peptide.getScoreArray()[0]);
				//System.out.println(protein.getAccessionNumber()+"\t"+peptide.getSequence()+"\t"+rank+"\t"+peptide.getScoreArray()[0]);
			}
			//System.out.println(protein.getAccessionNumber()+"\t"+protein.getPeptides().size()+"\t"+Correlation.getPearsons(localRanks.toArray(), localScores.toArray()));
			//System.out.println(General.toPropertyString(localRanks.toArray()));
			//System.out.println(General.toPropertyString(localScores.toArray()));
		}
		System.out.println();
		
		double[] medians=new double[scoresByRank.size()];
		int rank=0;
		System.out.println("Rank\tQ1\tM\tQ3");
		for (TDoubleArrayList list : scoresByRank) {
			rank++;
			
			double[] values=list.toArray();
			if (values.length<3) continue;
			
			Arrays.sort(values);
			double[] quartiles=Median.quartiles(values);
			medians[rank-1]=quartiles[1];
			System.out.println(rank+"\t"+quartiles[0]+"\t"+quartiles[1]+"\t"+quartiles[2]);
		}
		double[] movingAverage=new double[medians.length];
		movingAverage[0]=medians[0];
		movingAverage[movingAverage.length-1]=medians[medians.length-1];
		for (int i=1; i<movingAverage.length-1; i++) {
			movingAverage[i]=(medians[i]+medians[i-1]+medians[i+1])/3;
		}
		System.out.println("\nRank\tMoving Average");
		for (int i=0; i<movingAverage.length; i++) {
			System.out.println((i+1)+"\t"+movingAverage[i]);
		}

		float targetPercent=0.75f;
		int[] chooseNTop25=new int[5];
		//System.out.println("\nAccession\t#Peptides\tChoicesBeforeTarget");
		for (Protein protein : proteins) {
			ArrayList<AbstractPeptide> peptides=protein.getPeptides();
			double[] intensities=new double[peptides.size()];
			for (int i=0; i<intensities.length; i++) {
				intensities[i]=peptides.get(i).getIntensity();
			}
			Arrays.sort(intensities);
			//double[] quartiles=Median.quartiles(intensities);
			//double target=quartiles[2];
			double target=intensities[(int)Math.floor(intensities.length*targetPercent)];
			
			Collections.sort(peptides, new PeptideScoreComparator());
			Collections.reverse(peptides);
			
			for (int i=0; i<peptides.size(); i++) {
				if (peptides.get(i).getIntensity()>=target) {
					//System.out.println(protein.getAccessionNumber()+"\t"+peptides.size()+"\t"+(i+1));
					for (int j=i; j<chooseNTop25.length; j++) {
						chooseNTop25[j]++;
					}
					break;
				}
			}
		}
		
		System.out.println("\nChoose N\tTop "+Math.round((1.0f-targetPercent)*100f)+"%");
		for (int i=0; i<chooseNTop25.length; i++) {
			System.out.println((i+1)+"\t"+chooseNTop25[i]/(float)proteins.size());
		}
		

		System.out.println("\nSTAT5A rank\tscore");
		for (Protein protein : proteins) {
			if (protein.getAccessionNumber().endsWith("STAT5A")) {
				ArrayList<AbstractPeptide> peptides=protein.getPeptides();	
				Collections.sort(peptides);
				Collections.reverse(peptides);
				
				for (int i=0; i<peptides.size(); i++) {

					//System.out.print(protein.getAccessionNumber()+"\t");
					System.out.println((i+1)+"\t"+peptides.get(i).getScoreArray()[0]);
					//System.out.println(peptides.get(i).getIntensity());
				}
			}
		}
	}

	/**
	 * 
	 * @param peptidesWithIntensityFile TSV: accession, sequence, intensity, score
	 * @param network can be null
	 * @return
	 */
	public static ArrayList<Protein> getProteins(File peptidesWithIntensityFile, BackPropNeuralNetwork network, boolean random) {
		HashMap<String, Protein> proteinMap=new HashMap<>();
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(peptidesWithIntensityFile));
			String line=null;
			while ((line=reader.readLine())!=null) {
				if (line.startsWith("#")) continue; // comment
				StringTokenizer st=new StringTokenizer(line);
				String accession=st.nextToken();
				Protein protein=proteinMap.get(accession);
				if (protein==null) {
					protein=new Protein(accession);
					proteinMap.put(accession, protein);
				}
				
				String sequence=st.nextToken();
				float intensity=0.0f;
				if (st.hasMoreTokens()) {
					intensity=Float.parseFloat(st.nextToken());
				}
				
				final double score;
				if (random) {
					int ragu=0;
					if (sequence.length()>25||sequence.length()<8) {
						ragu=-1;
					} else {
						for (int i=0; i<sequence.length(); i++) {
							char c=sequence.charAt(i);
							if (i==0&&('Q'==c||'E'==c||'C'==c)) {
								ragu=ragu-10;
							}
							if ('M'==c) {
								ragu=ragu-10;
							} else if ('N'==c||'Q'==c) {
								ragu=ragu-1;
							} else if ('P'==c) {
								ragu=ragu+5;
							}
						}
					}
					score=ragu;
				} else if (network!=null) {
					score=network.getScore(sequence);
				} else if (st.hasMoreTokens()) {
					score=Double.parseDouble(st.nextToken());
				} else {
					score=0.0;
				}
				protein.addPeptide(new PeptideWithScores(sequence, intensity, protein, new double[] {score}));
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (reader!=null) reader.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		
		ArrayList<Protein> proteinList=new ArrayList<Protein>();
		for (Protein protein : proteinMap.values()) {
			if (protein.getPeptides().size()>=minimumNumPeptides) {
				proteinList.add(protein);
			}
		}
		return proteinList;
	}
}
