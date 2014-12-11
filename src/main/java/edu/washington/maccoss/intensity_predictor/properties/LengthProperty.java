package edu.washington.maccoss.intensity_predictor.properties;

public class LengthProperty implements PropertyInterface {
	@Override
	public double getProperty(String sequence) {
		return sequence.length();
	}
}
