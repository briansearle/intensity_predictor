package edu.washington.maccoss.intensity_predictor.structures;

import java.util.ArrayList;
import java.util.HashMap;

public class Protein {
	private final String accessionNumber;
	private final HashMap<String, Peptide> peptides;

	public Protein(String accessionNumber) {
		this.accessionNumber=accessionNumber;
		this.peptides=new HashMap<String, Peptide>();
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public ArrayList<Peptide> getPeptides() {
		return new ArrayList<Peptide>(peptides.values());
	}

	public Peptide addPeptide(String sequence, float intensity, byte charge) {
		String key=Peptide.stripMods(sequence);
		Peptide peptide=peptides.get(key);
		if (peptide==null) {
			peptide=new Peptide(sequence+"_+"+charge, intensity, this);
		} else {
			peptide.maybeAddForm(sequence+"_+"+charge, intensity);
		}
		peptides.put(key, peptide);
		return peptide;
	}

	public float getSummedIntensity() {
		float intensity=0.0f;
		for (Peptide peptide : peptides.values()) {
			intensity+=peptide.getIntensity();
		}
		return intensity;
	}
}
