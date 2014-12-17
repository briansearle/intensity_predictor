package edu.washington.maccoss.intensity_predictor.structures;

import java.util.Map;

import org.biojava3.aaproperties.PeptideProperties;



public class PeptideWithScores extends AbstractPeptide {
	public static char[] aas="ACDEFGHIKLMNPQRSTVWY".toCharArray();
	
	private final double[] scores;
	public PeptideWithScores(String sequence, float intensity, Protein protein, double[] properties) {
		super(sequence, intensity, protein);
		
		if (true) {
			this.scores=properties;
		} else {
			double[] aaComposition=getAAComposition(getSequence(), aas);
			double[] scores=new double[properties.length+aaComposition.length];
			System.arraycopy(properties, 0, scores, 0, properties.length);
			System.arraycopy(aaComposition, 0, scores, properties.length, aaComposition.length);
			this.scores=scores;
		}
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
