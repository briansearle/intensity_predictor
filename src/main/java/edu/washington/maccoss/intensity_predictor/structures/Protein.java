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

import java.util.ArrayList;
import java.util.HashMap;

public class Protein {
	private final String accessionNumber;
	private final HashMap<String, AbstractPeptide> peptides;

	public Protein(String accessionNumber) {
		this.accessionNumber=accessionNumber;
		this.peptides=new HashMap<String, AbstractPeptide>();
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public ArrayList<AbstractPeptide> getPeptides() {
		return new ArrayList<AbstractPeptide>(peptides.values());
	}
	public void addPeptide(AbstractPeptide peptide) {
		peptides.put(peptide.getSequence(), peptide);
	}
	public AbstractPeptide addPeptide(String sequence, float intensity, byte charge) {
		return addPeptide(sequence, intensity, charge, null);
	}
	public AbstractPeptide addPeptide(String sequence, float intensity, byte charge, double[] scoreArray) {
		String key=AbstractPeptide.stripMods(sequence);
		AbstractPeptide peptide=peptides.get(key);
		if (peptide==null) {
			if (scoreArray!=null) {
				peptide=new PeptideWithScores(sequence+"_+"+charge, intensity, this, scoreArray);
			} else {
				peptide=new Peptide(sequence+"_+"+charge, intensity, this);
			}
		} else {
			peptide.maybeAddForm(sequence+"_+"+charge, intensity);
		}
		peptides.put(key, peptide);
		return peptide;
	}

	public float getSummedIntensity() {
		float intensity=0.0f;
		for (AbstractPeptide peptide : peptides.values()) {
			intensity+=peptide.getIntensity();
		}
		return intensity;
	}
}
