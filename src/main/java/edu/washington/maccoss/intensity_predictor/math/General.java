package edu.washington.maccoss.intensity_predictor.math;

import gnu.trove.list.array.TDoubleArrayList;

import java.util.Arrays;
import java.util.StringTokenizer;

public class General {
	public static double[] rank(double[] values) {
		double[] sorted=values.clone();
		Arrays.sort(sorted);
		double[] ranks=new double[values.length];
		for (int i=0; i<values.length; i++) {
			ranks[i]=Arrays.binarySearch(sorted, values[i]);
		}
		return ranks;
	}
	
	public static double getPercentile(double[] array, double percentile) {
		Arrays.sort(array);
		int index=(int)Math.round(percentile*array.length);
		if (index<0) index=0;
		if (index>=array.length) index=array.length-1;
		return array[index];
	}

	public static String toPropertyString(double[] d) {
		StringBuilder sb=new StringBuilder();
		for (int i=0; i<d.length; i++) {
			if (i>0) sb.append(",");
			sb.append(Double.toString(d[i]));
		}
		return sb.toString();
	}
	
	public static double[] fromPropertyString(String s) {
		StringTokenizer st=new StringTokenizer(s, ",");
		TDoubleArrayList d=new TDoubleArrayList();
		while (st.hasMoreTokens()) {
			d.add(Double.parseDouble(st.nextToken()));
		}
		return d.toArray();
	}
}
