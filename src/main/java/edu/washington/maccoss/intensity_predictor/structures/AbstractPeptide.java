package edu.washington.maccoss.intensity_predictor.structures;

import java.util.HashMap;

public abstract class AbstractPeptide implements Comparable<AbstractPeptide> {

	private static final double LOG2=Math.log(2.0);
	protected final String sequence;
	protected final Protein protein;
	protected final HashMap<String, Float> modifiedForms;
	protected static char[] aas="HCKPWAILN".toCharArray();

	public AbstractPeptide(String sequence, float intensity, Protein protein) {
		this.sequence=stripMods(sequence);
		this.protein=protein;
		this.modifiedForms=new HashMap<String, Float>();
		maybeAddForm(sequence, intensity);
	}
	
	abstract public double[] getScoreArray();
	
	@Override
	public int compareTo(AbstractPeptide o) {
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

	public static String stripMods(String sequence) {
		StringBuilder sb=new StringBuilder();
		for (char c : sequence.toCharArray()) {
			if (Character.isLetter(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}