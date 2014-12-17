package edu.washington.maccoss.intensity_predictor.properties;

public class NumberBasicProperty extends AbstractProperty {
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
