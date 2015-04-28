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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.NeuralNetworkData;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import edu.washington.maccoss.intensity_predictor.structures.PeptideScoreComparator;
import edu.washington.maccoss.intensity_predictor.structures.Protein;

public class TestClassifiers {
	private static final float percentBetterThan=0.80f;

	public static void main(String[] args) throws IOException {
		int bestScore=-10;
		int index=0;
		for (int i=0; i<1000; i++) {
			String neuralNetworkLocation="/Users/searleb/Documents/school/maccoss rotation/transitions/test_"+index+".nn";
			int score=iteration(neuralNetworkLocation);
			if (score>bestScore) {
				bestScore=score;
				index++;
				System.out.println("Incrementing index to: "+index);
			}
		}
		System.out.println("bestScore: "+bestScore+", "+(index-1));
	}

	private static int iteration(String neuralNetworkLocation) {
		BuildClassifier.main(new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/jarrett_intensities.txt", neuralNetworkLocation});
		String[] args=new String[] {"/Users/searleb/Documents/school/maccoss rotation/transitions/newjim_intensities.txt", neuralNetworkLocation};

		File peptidesWithIntensityFile=new File(args[0]);
		BackPropNeuralNetwork network=null;
		if (args.length>1) {
			File neuralNetworkFile=new File(args[1]);
			network=NeuralNetworkData.readNetwork(neuralNetworkFile);
		}

		ArrayList<Protein> espProteins=CalculateStatistics.getProteins(peptidesWithIntensityFile, null, false);
		HashMap<String, Protein> espMap=new HashMap<>();
		for (Protein protein : espProteins) {
			espMap.put(protein.getAccessionNumber(), protein);
		}

		ArrayList<Protein> pregoProteins=CalculateStatistics.getProteins(peptidesWithIntensityFile, network, false);

		int sumPrego=0;
		int sumESP=0;
		int[] pregoNTop25=new int[5];
		int[] espNTop25=new int[5];
		for (Protein protein : pregoProteins) {
			int pregoRank=getBestRank(protein);
			int espRank=getBestRank(espMap.get(protein.getAccessionNumber()));
			sumPrego+=pregoRank;
			sumESP+=espRank;
			if (espRank<pregoRank) {
				//System.out.println(pregoRank+"\t"+espRank+"\t"+getBestPeptide(protein).getSequence()+"\t"+getBestPeptide(espMap.get(protein.getAccessionNumber())).getSequence());
			}
			
			for (int j=pregoRank; j<pregoNTop25.length; j++) {
				pregoNTop25[j]++;
			}
			for (int j=espRank; j<espNTop25.length; j++) {
				espNTop25[j]++;
			}
		}

		System.out.println(sumPrego+"\t"+sumESP);

		System.out.println("\nChoose N\tTop "+Math.round((1.0f-percentBetterThan)*100f)+"%");
		for (int i=0; i<pregoNTop25.length; i++) {
			System.out.println((i+1)+"\t"+(pregoNTop25[i]/(float)pregoProteins.size())+"\t"+(espNTop25[i]/(float)espProteins.size()));
		}
		return sumESP-sumPrego;
	}

	private static int getBestRank(Protein protein) {
		ArrayList<AbstractPeptide> peptides=protein.getPeptides();
		double[] intensities=new double[peptides.size()];
		for (int i=0; i<intensities.length; i++) {
			intensities[i]=peptides.get(i).getIntensity();
		}
		Arrays.sort(intensities);
		double target=intensities[(int)Math.floor(intensities.length*percentBetterThan)];

		Collections.sort(peptides, new PeptideScoreComparator());
		Collections.reverse(peptides);

		int bestRank=0;
		for (; bestRank<peptides.size(); bestRank++) {
			if (peptides.get(bestRank).getIntensity()>=target) {
				return bestRank;
			}
		}
		return -1;
	}

	private static AbstractPeptide getBestPeptide(Protein protein) {
		ArrayList<AbstractPeptide> peptides=protein.getPeptides();
		double[] intensities=new double[peptides.size()];
		for (int i=0; i<intensities.length; i++) {
			intensities[i]=peptides.get(i).getIntensity();
		}
		Arrays.sort(intensities);
		double target=intensities[(int)Math.floor(intensities.length*percentBetterThan)];

		Collections.sort(peptides, new PeptideScoreComparator());
		Collections.reverse(peptides);

		int bestRank=0;
		for (; bestRank<peptides.size(); bestRank++) {
			if (peptides.get(bestRank).getIntensity()>=target) {
				return peptides.get(bestRank);
			}
		}
		return null;
	}

}
