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

import java.util.HashMap;

public abstract class AbstractPeptide implements Comparable<AbstractPeptide> {
	protected final String sequence;
	protected final Protein protein;
	protected final HashMap<String, Float> modifiedForms;

	public AbstractPeptide(String sequence, float intensity, Protein protein) {
		this.sequence=stripMods(sequence);
		this.protein=protein;
		this.modifiedForms=new HashMap<String, Float>();
		maybeAddForm(sequence, intensity);
	}
	
	abstract public double[] getScoreArray();
	
	@Override
	public int compareTo(AbstractPeptide o) {
		if (o==null) return 1;
		return Float.compare(this.getIntensity(), o.getIntensity());
	}

	public boolean maybeAddForm(String sequence, float intensity) {
		Float previous=modifiedForms.get(sequence);
		if (previous==null) {
			modifiedForms.put(sequence, intensity);
			return true;
		} else {
			if (previous<intensity) {
				modifiedForms.put(sequence, intensity);
				return true;
			}
		}
		return false;
	}

	public float getIntensity() {
		float intensity=0.0f;
		for (Float i : modifiedForms.values()) {
			intensity+=i;
		}
		return intensity;
	}

	public String getSequence() {
		return sequence;
	}

	public Protein getProtein() {
		return protein;
	}

	public static String stripMods(String sequence) {
		StringBuilder sb=new StringBuilder();
		for (char c : sequence.toCharArray()) {
			if (Character.isLetter(c)) {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}