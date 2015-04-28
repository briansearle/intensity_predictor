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

import java.util.Arrays;

import org.apache.commons.math.stat.descriptive.moment.Mean;

public class Correlation {
	public static double getSpearmans(double[] x, double[] y) {
		return getPearsons(rank(x), rank(y));
	}
	
	public static double getPearsons(double[] x, double[] y) {
		Mean meanCalc=new Mean();
		double xBar=meanCalc.evaluate(x);
		double yBar=meanCalc.evaluate(y);
		
		double numerator=0.0;
		double xSS=0.0;
		double ySS=0.0;
		for (int i=0; i<y.length; i++) {
			double xDiff=x[i]-xBar;
			double yDiff=y[i]-yBar;
			numerator+=xDiff*yDiff;
			xSS+=xDiff*xDiff;
			ySS+=yDiff*yDiff;
		}
		if (xSS==0||ySS==0) {
			return 0.0;
		}
		return numerator/Math.sqrt(xSS*ySS);
	}
	
	public static double[] rank(double[] values) {
		double[] sorted=values.clone();
		Arrays.sort(sorted);
		double[] sortedRanks=new double[sorted.length];

		// basic rank
		for (int i=0; i<sorted.length; i++) {
			sortedRanks[i]=i+1;
		}

		for (int i=0; i<sorted.length; i++) {
			int start=i;
			int stop=i;

			// find ties
			boolean ties=false;
			while (++stop<sorted.length&&sorted[start]==sorted[stop]) {
				ties=true;
			}

			// substitute rank average for ties
			if (stop-start>1&&ties) {
				double avg=0;
				for (int j=start; j<stop; j++) {
					avg+=sortedRanks[j];
				}
				avg=avg/(stop-start);

				for (int x=start; x<stop; x++) {
					sortedRanks[x]=avg;
				}
			}
			
			// advance i to end of stop
			i=stop-1;
		}
		double[] ranksInOrder=new double[sortedRanks.length];
		for (int i=0; i<sortedRanks.length; i++) {
			int index=Arrays.binarySearch(sorted, values[i]);
			ranksInOrder[i]=sortedRanks[index];
		}

		return ranksInOrder;
	}
}
