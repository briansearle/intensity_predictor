package edu.washington.maccoss.intensity_predictor.math;

import junit.framework.TestCase;

public class LinearDiscriminantAnalysisTest extends TestCase {

	public void testWikipediaExample() {
		double[][] male=new double[][] { {6, 180, 12}, {5.92, 190, 11}, {5.58, 170, 12}, {5.92, 165, 10}};
		double[][] female=new double[][] { {5, 100, 6}, {5.5, 150, 8}, {5.42, 130, 7}, {5.75, 150, 9}};
		LinearDiscriminantAnalysis lda=LinearDiscriminantAnalysis.buildModel(male, female);

		double[] coefficients= {-0.152, -0.00113, 0.868};
		for (int i=0; i<coefficients.length; i++) {
			assertEquals(coefficients[i], lda.getCoefficients()[i], 0.001);
		}
		assertEquals(-7.100718067847837, lda.getConstant(), 0.000001);
	}
}
