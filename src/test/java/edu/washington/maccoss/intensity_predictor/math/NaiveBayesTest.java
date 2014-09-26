package edu.washington.maccoss.intensity_predictor.math;

import junit.framework.TestCase;

public class NaiveBayesTest extends TestCase {

	/**
	 * http://en.wikipedia.org/wiki/Naive_Bayes_classifier#Examples
	 */
	public void testWikipediaExample() {
		double[][] male=new double[][] { {6, 180, 12}, {5.92, 190, 11}, {5.58, 170, 12}, {5.92, 165, 10}};
		double[][] female=new double[][] { {5, 100, 6}, {5.5, 150, 8}, {5.42, 130, 7}, {5.75, 150, 9}};
		NaiveBayes bayes=NaiveBayes.buildModel(male, female);

		assertEquals(-4.938427143365715, bayes.getLogLikelihood(new double[] {6, 130, 8}), 0.00001);
	}
}
