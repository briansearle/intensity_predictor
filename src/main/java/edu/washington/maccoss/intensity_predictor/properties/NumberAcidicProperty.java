package edu.washington.maccoss.intensity_predictor.properties;

public class NumberAcidicProperty extends AbstractProperty {
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
