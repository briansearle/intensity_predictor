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
