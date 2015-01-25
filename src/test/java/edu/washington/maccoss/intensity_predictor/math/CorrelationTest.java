package edu.washington.maccoss.intensity_predictor.math;

import junit.framework.TestCase;

public class CorrelationTest extends TestCase {
	/**
	 * from http://en.wikipedia.org/wiki/Spearman%27s_rank_correlation_coefficient
	 */
	public void testSpearmans() {
		double[] iq=new double[] {106.0, 86.0, 100.0, 101.0, 99.0, 103.0, 97.0, 113.0, 112.0, 110.0};
		double[] hoursTV=new double[] {7.0, 0.0, 27.0, 50.0, 28.0, 29.0, 20.0, 12.0, 6.0, 17.0};
		
		assertEquals(-0.17575757575757575, Correlation.getSpearmans(iq, hoursTV), 0.0000001);
	}

}
