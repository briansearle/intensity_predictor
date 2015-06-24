package edu.washington.maccoss.intensity_predictor.structures;

public class PeptideData {
	private final int rank;
	private final String sequence;
	private final String accession;
	private final double score;

	public PeptideData(int rank, String sequence, String accession, double score) {
		this.rank=rank;
		this.sequence=sequence;
		this.accession=accession;
		this.score=score;
	}

	@Override
	public String toString() {
		return accession+"\t"+rank+"\t"+sequence+"\t"+score;
	}

	public int getRank() {
		return rank;
	}

	public String getSequence() {
		return sequence;
	}

	public String getAccession() {
		return accession;
	}

	public double getScore() {
		return score;
	}
}
