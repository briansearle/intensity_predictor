/**
 * Copyright 2015 Brian C. Searle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
