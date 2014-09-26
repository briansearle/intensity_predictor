package edu.washington.maccoss.intensity_predictor.structures;

import java.util.ArrayList;

public class Protein {
	private final String accessionNumber;
	private final ArrayList<Peptide> peptides;

	public Protein(String accessionNumber) {
		this.accessionNumber=accessionNumber;
		this.peptides=new ArrayList<Peptide>();
	}

	public String getAccessionNumber() {
		return accessionNumber;
	}

	public ArrayList<Peptide> getPeptides() {
		return peptides;
	}

	public Peptide addPeptide(String sequence, float intensity) {
		Peptide peptide=new Peptide(sequence, intensity, this);
		peptides.add(peptide);
		return peptide;
	}

	public float getSummedIntensity() {
		float intensity=0.0f;
		for (Peptide peptide : peptides) {
			intensity+=peptide.getIntensity();
		}
		return intensity;
	}
}
