package edu.washington.maccoss.intensity_predictor.properties;

import java.io.Serializable;

public class HasProlineProperty extends AbstractProperty implements Serializable {
	private static final long serialVersionUID=1L;
	
	@Override
	public String toString() {
		return "Number of Prolines";
	}
	
	public HasProlineProperty() {
		super(false);
		addProperty('P', 1);
	}
	
	@Override
	public double getProperty(String sequence) {
		char[] aas=sequence.toCharArray();
		double value=0;
		for (int i=0; i<aas.length; i++) {
			value+=aaMap.get(aas[i]);
		}
		if (value>0) {
			return 1.0;
		} else {
			return 0.0;
		}
	}
}
