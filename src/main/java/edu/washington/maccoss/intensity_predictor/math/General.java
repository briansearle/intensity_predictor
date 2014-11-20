package edu.washington.maccoss.intensity_predictor.math;

import java.util.Arrays;

public class General {
	public static double getPercentile(double[] array, double percentile) {
		Arrays.sort(array);
		int index=(int)Math.round(percentile*array.length);
		if (index<0) index=0;
		if (index>=array.length) index=array.length-1;
		return array[index];
	}

}
