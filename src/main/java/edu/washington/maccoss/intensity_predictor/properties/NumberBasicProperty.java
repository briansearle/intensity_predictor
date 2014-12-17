package edu.washington.maccoss.intensity_predictor.properties;

import java.io.Serializable;

public class NumberBasicProperty extends AbstractProperty implements Serializable {
	private static final long serialVersionUID=1L;
	
	@Override
	public String toString() {
		return "Number of Basic Residues";
	}
	
	public NumberBasicProperty() {
		super(false);
		addProperty('R', 1);
		addProperty('K', 1);
		addProperty('H', 1);
	}
}
