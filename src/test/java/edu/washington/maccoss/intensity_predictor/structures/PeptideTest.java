package edu.washington.maccoss.intensity_predictor.structures;

import junit.framework.TestCase;

public class PeptideTest extends TestCase {
	
	public void testScoresPeptide() {
		String sequence="ELVISLIVESK";
		Peptide peptide=new Peptide(sequence, 0.0f, null);
		double[] scores=peptide.getScoreArray();
		assertEquals(4, scores.length);
		assertEquals(1228.7279700000001, scores[0], 0.000001);
		assertEquals(1.0, scores[1]);
		assertEquals(2.0, scores[2]);
		assertEquals(11.0, scores[3]);
	}
}
