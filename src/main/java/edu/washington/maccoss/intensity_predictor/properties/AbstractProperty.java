package edu.washington.maccoss.intensity_predictor.properties;

import gnu.trove.map.hash.TIntDoubleHashMap;

public abstract class AbstractProperty implements PropertyInterface {
	TIntDoubleHashMap aaMap=new TIntDoubleHashMap();
	private double base=0.0;
	private final boolean isAverage;
	
	public AbstractProperty(boolean isAverage) {
		this.isAverage=isAverage;
	}
	
	protected void setBase(double value) {
		base=value;
	}
	
	protected void addProperty(char aa, double value) {
		aaMap.put(aa, value);
	}

	@Override
	public double getProperty(String sequence) {
		char[] aas=sequence.toCharArray();
		double value=base;
		for (int i=0; i<aas.length; i++) {
			value+=aaMap.get(aas[i]);
		}
		if (isAverage) {
			return value/sequence.length();
		} else {
			return value;
		}
	}
}
