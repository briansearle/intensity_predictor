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
package edu.washington.maccoss.intensity_predictor.properties;

import gnu.trove.map.hash.TIntDoubleHashMap;

import java.io.Serializable;

public abstract class AbstractProperty implements PropertyInterface, Serializable {
	private static final long serialVersionUID=1L;
	
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
