package edu.washington.maccoss.intensity_predictor.properties;

import java.io.Serializable;

public class LengthProperty extends AbstractProperty implements Serializable {
	private static final long serialVersionUID=1L;
	
	public LengthProperty() {
		super(false);
	}
	
	@Override
	public String toString() {
		return "Peptide Length";
	}
	@Override
	public double getProperty(String sequence) {
		return sequence.length();
	}
}
