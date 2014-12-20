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
