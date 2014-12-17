package edu.washington.maccoss.intensity_predictor.parsers;

import java.io.Serializable;

import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;

public class ParsedProperty extends AbstractProperty implements Serializable {
	private static final long serialVersionUID=1L;
	
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
