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
package edu.washington.maccoss.intensity_predictor.structures;

import edu.washington.maccoss.intensity_predictor.properties.LengthProperty;
import edu.washington.maccoss.intensity_predictor.properties.MassProperty;
import edu.washington.maccoss.intensity_predictor.properties.NumberAcidicProperty;
import edu.washington.maccoss.intensity_predictor.properties.NumberBasicProperty;
import edu.washington.maccoss.intensity_predictor.properties.PropertyInterface;


public class Peptide extends AbstractPeptide {
	PropertyInterface[] properties=new PropertyInterface[] {
			new MassProperty(),
			new NumberBasicProperty(),
			new NumberAcidicProperty(),
			new LengthProperty()
	};
	double[] scores=null;
	
	public Peptide(String sequence, float intensity, Protein protein) {
		super(sequence, intensity, protein);
	}

	@Override
	public double[] getScoreArray() {
		if (scores!=null) return scores;
		
		scores=new double[properties.length];
		for (int i=0; i<properties.length; i++) {
			scores[i]=properties[i].getProperty(sequence);
		}
		return scores;
	}
}
