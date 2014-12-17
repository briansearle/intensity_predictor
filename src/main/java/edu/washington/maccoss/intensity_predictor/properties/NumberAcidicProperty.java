package edu.washington.maccoss.intensity_predictor.properties;

import java.io.Serializable;

public class NumberAcidicProperty extends AbstractProperty implements Serializable {
	private static final long serialVersionUID=1L;
	
	@Override
	public String toString() {
		return "Number of Acidic Residues";
	}
	
	public NumberAcidicProperty() {
		super(false);
		addProperty('D', 1);
		addProperty('E', 1);
	}
}
