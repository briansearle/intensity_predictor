package edu.washington.maccoss.intensity_predictor.structures;

import java.util.HashMap;
import java.util.Map;

import org.biojava3.aaproperties.PeptideProperties;


public class Peptide implements Comparable<Peptide> {
	private static final double LOG2=Math.log(2.0);
	
	private final String sequence;
	private final Protein protein;
	private final HashMap<String, Float> modifiedForms;

	Peptide(String sequence, float intensity, Protein protein) {
		this.sequence=stripMods(sequence);
		this.protein=protein;
		this.modifiedForms=new HashMap<String, Float>();
		maybeAddForm(sequence, intensity);
	}
	
	@Override
	public int compareTo(Peptide o) {
		if (o==null) return 1;
		return Float.compare(this.getIntensity(), o.getIntensity());
	}
	
	public boolean maybeAddForm(String sequence, float intensity) {
		Float previous=modifiedForms.get(sequence);
		if (previous==null) {
			modifiedForms.put(sequence, intensity);
			return true;
		} else {
			if (previous<intensity) {
				modifiedForms.put(sequence, intensity);
				return true;
			}
		}
		return false;
	}

	public float getIntensity() {
		float intensity=0.0f;
		for (Float i : modifiedForms.values()) {
			intensity+=i;
		}
		return intensity;
	}

	public String getSequence() {
		return sequence;
	}

	public Protein getProtein() {
		return protein;
	}

	private static char[] aas="ACDEFGHIKLMNPQRSTVWY".toCharArray();
	public double[] getScoreArray() {
		double ab=PeptideProperties.getAbsorbance(sequence, true);
		double ai=PeptideProperties.getApliphaticIndex(sequence);
		double ah=PeptideProperties.getAvgHydropathy(sequence);
		double ec=PeptideProperties.getExtinctionCoefficient(sequence, true);
		double ii=PeptideProperties.getInstabilityIndex(sequence);
		double ip=PeptideProperties.getIsoelectricPoint(sequence);
		double mw=PeptideProperties.getMolecularWeight(sequence);
		double nc=PeptideProperties.getNetCharge(sequence);
		double[] properties=new double[] {ab, ai, ah, ec, ii, ip, mw, nc};
		
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
		
		double[] scores=new double[properties.length+aaArray.length];
		System.arraycopy(properties, 0, scores, 0, properties.length);
		System.arraycopy(aaArray, 0, scores, properties.length, aaArray.length);
		
		return scores;
	}
	
	public static String stripMods(String sequence) {
		StringBuilder sb=new StringBuilder();
		for (char c : sequence.toCharArray()) {
			if (Character.isLetter(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public float getPredictorScore(float totalIntensity, int peptideCount) {
		float predictorScore=peptideCount*getIntensity()/totalIntensity;
		predictorScore=(float)(Math.log(predictorScore)/LOG2);
		return predictorScore;
	}
}
