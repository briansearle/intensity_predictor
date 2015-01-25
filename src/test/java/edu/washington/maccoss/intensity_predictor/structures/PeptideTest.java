package edu.washington.maccoss.intensity_predictor.structures;

import junit.framework.TestCase;

import org.biojava3.aaproperties.PeptideProperties;

public class PeptideTest extends TestCase {
	public void testScores() {
		String sequence="ELVISLIVESK";
		assertEquals(0.0, PeptideProperties.getAbsorbance(sequence, true));
		assertEquals(194.54545454545453, PeptideProperties.getApliphaticIndex(sequence));
		assertEquals(1.1363636363636362, PeptideProperties.getAvgHydropathy(sequence));
		assertEquals(0.0, PeptideProperties.getExtinctionCoefficient(sequence, true));
		assertEquals(18.881818181818186, PeptideProperties.getInstabilityIndex(sequence));
		assertEquals(4.531639099121094, PeptideProperties.getIsoelectricPoint(sequence));
		assertEquals(1229.4795000000001, PeptideProperties.getMolecularWeight(sequence));
		assertEquals(-1.1613609203277973, PeptideProperties.getNetCharge(sequence));
	}
	
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
