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


public class MassProperty extends AbstractProperty implements Serializable {
	private static final long serialVersionUID=1L;
	
	@Override
	public String toString() {
		return "Peptide Mass";
	}

	public MassProperty() {
		super(false);
		
		setBase(18.01057);
		addProperty('A', 71.0371);
		addProperty('R', 156.1011);
		addProperty('N', 114.0429);
		addProperty('D', 115.027);
		addProperty('C', 103.0092);
		addProperty('E', 129.0426);
		addProperty('Q', 128.0586);
		addProperty('G', 57.0215);
		addProperty('H', 137.0589);
		addProperty('L', 113.0841);
		addProperty('I', 113.0841);
		addProperty('K', 128.095);
		addProperty('M', 131.0405);
		addProperty('F', 147.0684);
		addProperty('P', 97.0528);
		addProperty('S', 87.032);
		addProperty('T', 101.0477);
		addProperty('W', 186.0793);
		addProperty('Y', 163.0633);
		addProperty('V', 99.0684);
		addProperty('J', 113.0841); // indecision between leucine/isoleucine
		addProperty('U', 150.95361); // selenocysteine
	}
}
