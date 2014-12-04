package edu.washington.maccoss.intensity_predictor.structures;



public class PeptideWithScores extends AbstractPeptide {
	public static char[] aas="ACDEFGHIKLMNPQRSTVWY".toCharArray();
	
	private final double[] scores;
	public PeptideWithScores(String sequence, float intensity, Protein protein, double[] properties) {
		super(sequence, intensity, protein);
		
		if (true) {
			this.scores=properties;
		} else {
			double[] aaComposition=Peptide.getAAComposition(getSequence(), aas);
			double[] scores=new double[properties.length+aaComposition.length];
			System.arraycopy(properties, 0, scores, 0, properties.length);
			System.arraycopy(aaComposition, 0, scores, properties.length, aaComposition.length);
			this.scores=scores;
		}
	}

	@Override
	public double[] getScoreArray() {
		return scores;
	}
}
