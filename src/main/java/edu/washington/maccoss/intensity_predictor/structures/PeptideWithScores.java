package edu.washington.maccoss.intensity_predictor.structures;



public class PeptideWithScores extends AbstractPeptide {
	private final double[] scores;
	public PeptideWithScores(String sequence, float intensity, Protein protein, double[] scores) {
		super(sequence, intensity, protein);
		this.scores=scores;
	}

	@Override
	public double[] getScoreArray() {
		return scores;
	}
}
