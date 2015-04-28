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
