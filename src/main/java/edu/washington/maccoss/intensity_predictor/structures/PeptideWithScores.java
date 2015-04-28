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
package edu.washington.maccoss.intensity_predictor.structures;

import java.util.Map;

import org.biojava3.aaproperties.PeptideProperties;



public class PeptideWithScores extends AbstractPeptide {
	public static char[] aas="ACDEFGHIKLMNPQRSTVWY".toCharArray();
	
	private final double[] scores;
	public PeptideWithScores(String sequence, float intensity, Protein protein, double[] properties) {
		super(sequence, intensity, protein);
		
		this.scores=properties;
	}

	@Override
	public double[] getScoreArray() {
		return scores;
	}

	public static double[] getAAComposition(String sequence, char[] aas) {
		Map<Character, Double> map=PeptideProperties.getAACompositionChar(sequence);
		double[] aaArray=new double[aas.length];
		for (int i=0; i<aaArray.length; i++) {
			Double value=map.get(aas[i]);
			if (value==null) {
				aaArray[i]=0.0;
			} else {
				aaArray[i]=value;
			}
		}
		return aaArray;
	}
}
