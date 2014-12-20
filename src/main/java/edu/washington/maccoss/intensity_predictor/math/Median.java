package edu.washington.maccoss.intensity_predictor.math;

import gnu.trove.list.array.TDoubleArrayList;

public class Median {

	/**
	 * @param values
	 *            assumes a sorted array
	 * @return
	 */
	public static double median(double[] values) {
		if (values.length<1) throw new IllegalArgumentException("Too few values to compute median.");

		if (values.length%2==1) {
			return values[(values.length+1)/2-1];
		} else {
			double lower=values[values.length/2-1];
			double upper=values[values.length/2];

			return (lower+upper)/2.0;
		}
	}

	/**
	 * @param values
	 *            assumes a sorted array
	 * @return
	 */
	public static double[] quartiles(double[] values) {
		if (values.length<3) throw new IllegalArgumentException("Too few values to compute quartiles.");

		double m=median(values);

		double[] lower=getValuesLessThan(values, m);
		double[] upper=getValuesGreaterThan(values, m);

		return new double[] {median(lower), m, median(upper)};
	}

	private static double[] getValuesGreaterThan(double[] values, double limit) {
		TDoubleArrayList list=new TDoubleArrayList();

		for (int i=0; i<values.length; i++) {
			if (values[i]>limit||(values[i]==limit)) {
				list.add(values[i]);
			}
		}
		return list.toArray();
	}

	private static double[] getValuesLessThan(double[] values, double limit) {
		TDoubleArrayList list=new TDoubleArrayList();

		for (int i=0; i<values.length; i++) {
			if (values[i]<limit||(values[i]==limit)) {
				list.add(values[i]);
			}
		}
		return list.toArray();
	}
}
