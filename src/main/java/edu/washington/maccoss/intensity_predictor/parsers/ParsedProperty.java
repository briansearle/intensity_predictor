package edu.washington.maccoss.intensity_predictor.parsers;

import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;

public class ParsedProperty extends AbstractProperty {
	String name;

	public ParsedProperty(boolean isAverage) {
		super(isAverage);
	}

	public void setName(String name) {
		this.name=name;
	}
	@Override
	public String toString() {
		return name;
	}
	@Override
	protected void addProperty(char aa, double value) {
		// TODO Auto-generated method stub
		super.addProperty(aa, value);
	}
}
