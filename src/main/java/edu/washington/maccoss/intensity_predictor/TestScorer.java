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
import edu.washington.maccoss.intensity_predictor.math.Median;
import edu.washington.maccoss.intensity_predictor.math.NeuralNetworkData;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import edu.washington.maccoss.intensity_predictor.structures.PeptideWithScores;
import edu.washington.maccoss.intensity_predictor.structures.Protein;
import gnu.trove.list.array.TDoubleArrayList;

public class TestScorer {
	public static void main(String[] args) {
		if (true) {
			args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/ppa_scores.txt"};
		}
		if (false) {
			args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/esp_scores.txt"};
		}
		if (true) {
			args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/esp_scores.txt", 
					"/Users/searleb/Documents/school/maccoss rotation/transitions/jarrett.nn"};
		}
		File peptidesWithIntensityFile=new File(args[0]);
		BackPropNeuralNetwork network=null;
		if (args.length>1) {
			File neuralNetworkFile=new File(args[1]);
			network=NeuralNetworkData.readNetwork(neuralNetworkFile);
		}
		
		ArrayList<Protein> proteins=getProteins(peptidesWithIntensityFile, network);
		
		ArrayList<TDoubleArrayList> scoresByRank=new ArrayList<TDoubleArrayList>();
		for (int i=0; i<15; i++) {
			scoresByRank.add(new TDoubleArrayList());
		}
		
		for (Protein protein : proteins) {
			ArrayList<AbstractPeptide> peptides=protein.getPeptides();
			Collections.sort(peptides);
			Collections.reverse(peptides);
			int rank=0;
			for (AbstractPeptide peptide : peptides) {
				if (rank<scoresByRank.size()) {
					TDoubleArrayList scores=scoresByRank.get(rank);
					scores.add(peptide.getScoreArray()[0]);
				} else {
					break;
				}
				rank++;
			}
		}
		
		double[] medians=new double[scoresByRank.size()];
		int rank=0;
		System.out.println("Rank\tQ1\tM\tQ3");
		for (TDoubleArrayList list : scoresByRank) {
			rank++;
			
			double[] values=list.toArray();
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
	}

	/**
	 * 
	 * @param peptidesWithIntensityFile TSV: accession, sequence, intensity, score
	 * @param network can be null
	 * @return
	 */
	private static ArrayList<Protein> getProteins(File peptidesWithIntensityFile, BackPropNeuralNetwork network) {
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
				if (network!=null) {
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
		return new ArrayList<Protein>(proteinMap.values());
	}
}
