package edu.washington.maccoss.intensity_predictor.parsers;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import junit.framework.TestCase;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.General;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import edu.washington.maccoss.intensity_predictor.structures.Protein;
import gnu.trove.list.array.TIntArrayList;

public class PeptideFeatureSetParserTest extends TestCase {
	private static final double ONE_MINUS_BIT=Double.longBitsToDouble(Double.doubleToLongBits(1.0)-1);
	private static final double ZERO_PLUS_BIT=1.0-ONE_MINUS_BIT;
	private static final int TOTAL_FEATURES_CONSIDERED=10;

	public void testParsing() throws Exception {
		ArrayList<AbstractPeptide> sprgPeptides=getSPRGPeptides();
		
		//tests for data consistency
		assertEquals(912, sprgPeptides.size());
		for (AbstractPeptide peptide : sprgPeptides) {
			assertEquals(PeptideFeatureSetParser.scoreNames.length, peptide.getScoreArray().length);	
		}
		
		ArrayList<AbstractPeptide> jarrettPeptides=getJarrettPeptides();
		assertEquals(1255, jarrettPeptides.size());
		for (AbstractPeptide peptide : jarrettPeptides) {
			assertEquals(PeptideFeatureSetParser.scoreNames.length, peptide.getScoreArray().length);	
		}
	}
	public void testAnalysis() throws Exception {
		ArrayList<AbstractPeptide> testingPeptides=getJimPeptides();
		ArrayList<AbstractPeptide> trainingPeptides=getJarrettPeptides();
		
		//training data
		double[] trainingIntensities=new double[trainingPeptides.size()];
		for (int i=0; i<trainingIntensities.length; i++) {
			AbstractPeptide peptide=trainingPeptides.get(i);
			trainingIntensities[i]=peptide.getIntensity();
		}
		trainingIntensities=rank(trainingIntensities);
		
		// testing data
		double[] testingIntensities=new double[testingPeptides.size()];
		for (int i=0; i<testingIntensities.length; i++) {
			AbstractPeptide peptide=testingPeptides.get(i);
			testingIntensities[i]=peptide.getIntensity();
		}
		testingIntensities=rank(testingIntensities);
		
		double[][] testingValues=new double[PeptideFeatureSetParser.scoreNames.length][];
		for (int i=0; i<testingValues.length; i++) {
			testingValues[i]=new double[testingPeptides.size()];
		}
		for (int i=0; i<testingPeptides.size(); i++) {
			AbstractPeptide peptide=testingPeptides.get(i);
			double[] features=peptide.getScoreArray();
			for (int j=0; j<features.length; j++) {
				testingValues[j][i]=features[j];
			}
		}
		for (int i=0; i<testingValues.length; i++) {
			testingValues[i]=normalize(testingValues[i]);
		}
		
		double[][] trainingValues=new double[PeptideFeatureSetParser.scoreNames.length][];
		TIntArrayList bestFeatureIndicies=getBestFeatureIndicies(trainingPeptides, trainingIntensities, trainingValues);
		BackPropNeuralNetwork backprop=getNeuralNetwork(trainingIntensities, trainingValues, bestFeatureIndicies);

		ArrayList<double[]> trainingFeatures=new ArrayList<double[]>();
		for (int index : bestFeatureIndicies.toArray()) {
			trainingFeatures.add(trainingValues[index]);
		}
		
		for (int i=0; i<trainingIntensities.length; i++) {
			double[] featureArray=new double[TOTAL_FEATURES_CONSIDERED];
			for (int j=0; j<trainingFeatures.size(); j++) {
				featureArray[j]=trainingFeatures.get(j)[i];
			}
			double prob=backprop.getScore(featureArray);
			if (prob==1.0) prob=ONE_MINUS_BIT;
			if (prob==0.0) prob=ZERO_PLUS_BIT;
			double score=Math.log10(prob)-Math.log10(1.0-prob);
			
			AbstractPeptide peptide=trainingPeptides.get(i);
			System.out.println(peptide.getSequence()+"\t"+trainingIntensities[i]+"\t"+score+"\t"+prob);
		}
		
		System.out.println("\n\n");

		ArrayList<double[]> testingFeatures=new ArrayList<double[]>();
		for (int index : bestFeatureIndicies.toArray()) {
			testingFeatures.add(testingValues[index]);
		}
		
		for (int i=0; i<testingIntensities.length; i++) {
			double[] featureArray=new double[TOTAL_FEATURES_CONSIDERED];
			for (int j=0; j<testingFeatures.size(); j++) {
				featureArray[j]=testingFeatures.get(j)[i];
			}
			double prob=backprop.getScore(featureArray);
			if (prob==1.0) prob=ONE_MINUS_BIT;
			if (prob==0.0) prob=ZERO_PLUS_BIT;
			double score=Math.log10(prob)-Math.log10(1.0-prob);

			AbstractPeptide peptide=testingPeptides.get(i);
			System.out.println(peptide.getSequence()+"\t"+testingIntensities[i]+"\t"+score+"\t"+prob);
		}
	}
	private BackPropNeuralNetwork getNeuralNetwork(double[] originalIntensities, double[][] values, TIntArrayList bestFeatureIndicies) {
		ArrayList<double[]> bestFeatures=new ArrayList<double[]>();
		for (int index : bestFeatureIndicies.toArray()) {
			bestFeatures.add(values[index]);
		}

		double[] intensityArray=originalIntensities.clone();
		ArrayList<double[]> goodFeatures=new ArrayList<double[]>();
		ArrayList<double[]> badFeatures=new ArrayList<double[]>();
		double q1=General.getPercentile(intensityArray, 0.25);
		double q3=General.getPercentile(intensityArray, 0.75);
		for (int i=0; i<originalIntensities.length; i++) {
			double[] featureArray=new double[TOTAL_FEATURES_CONSIDERED];
			for (int j=0; j<bestFeatures.size(); j++) {
				featureArray[j]=bestFeatures.get(j)[i];
			}
			if (q1>originalIntensities[i]) {
				badFeatures.add(featureArray);
			} else if (q3<originalIntensities[i]) {
				goodFeatures.add(featureArray);
			}
		}
		
		BackPropNeuralNetwork backprop=BackPropNeuralNetwork.buildModel(goodFeatures.toArray(new double[goodFeatures.size()][]), badFeatures.toArray(new double[badFeatures.size()][]));
		return backprop;
	}

	private TIntArrayList getBestFeatureIndicies(ArrayList<AbstractPeptide> trainingPeptides, double[] originalIntensities, double[][] values) {
		for (int i=0; i<values.length; i++) {
			values[i]=new double[trainingPeptides.size()];
		}
		
		for (int i=0; i<trainingPeptides.size(); i++) {
			AbstractPeptide peptide=trainingPeptides.get(i);
			double[] features=peptide.getScoreArray();
			for (int j=0; j<features.length; j++) {
				values[j][i]=features[j];
			}
		}
		
		// normalize (doesn't affect correlation)
		double[] intensities=normalize(originalIntensities);
		for (int i=0; i<values.length; i++) {
			values[i]=normalize(values[i]);
		}

		ArrayList<ScoredArray> features=new ArrayList<ScoredArray>();
		for (int i=0; i<values.length; i++) {
			features.add(new ScoredArray(0.0, values[i], i));
		}
		
		// A type of Minimum-redundancy-maximum-relevance (mRMR) feature selection algorithm:
		// http://en.wikipedia.org/wiki/Feature_selection#Minimum-redundancy-maximum-relevance_.28mRMR.29_feature_selection
		TIntArrayList bestFeatureIndicies=new TIntArrayList();
		for (int iter=0; iter<TOTAL_FEATURES_CONSIDERED; iter++) {
			ArrayList<ScoredArray> arrays=new ArrayList<ScoredArray>();
			for (int i=0; i<features.size(); i++) {
				double correlation=getCorrelation(intensities, features.get(i).array);
				ScoredArray scoredArray=new ScoredArray(correlation, features.get(i).array, features.get(i).index);
				if (scoredArray.getName().equals("P Composition")) {
					System.out.println("\tP: "+scoredArray.toString());
				}
				arrays.add(scoredArray);
			}
			Collections.sort(arrays);
			ScoredArray best=arrays.remove(arrays.size()-1);
			for (int i=0; i<intensities.length; i++) {
				intensities[i]-=best.correlation*best.array[i];
			}
			
			features=arrays;
			bestFeatureIndicies.add(best.index);
			System.out.println(best.index+"\t"+best.score+"\t"+best.toString());
		}
		return bestFeatureIndicies;
	}
	
	private double[] rank(double[] values) {
		double[] sorted=values.clone();
		Arrays.sort(sorted);
		double[] ranks=new double[values.length];
		for (int i=0; i<values.length; i++) {
			ranks[i]=Arrays.binarySearch(sorted, values[i]);
		}
		return ranks;
	}

	private ArrayList<AbstractPeptide> getJimPeptides() throws URISyntaxException {
		URI uri=ClassLoader.getSystemResource("jim_PeptideFeatureSet.csv").toURI();
		File f=new File(uri);
		ArrayList<Protein> proteins=PeptideFeatureSetParser.parseTSV(f);
		ArrayList<AbstractPeptide> peptides=proteins.get(0).getPeptides();
		return peptides;
	}

	private ArrayList<AbstractPeptide> getJarrettPeptides() throws URISyntaxException {
		URI uri=ClassLoader.getSystemResource("jarrett_PeptideFeatureSet.csv").toURI();
		File f=new File(uri);
		ArrayList<Protein> proteins=PeptideFeatureSetParser.parseTSV(f);
		ArrayList<AbstractPeptide> peptides=proteins.get(0).getPeptides();
		return peptides;
	}

	private ArrayList<AbstractPeptide> getSPRGPeptides() throws URISyntaxException {
		URI uri=ClassLoader.getSystemResource("sprg_PeptideFeatureSet.csv").toURI();
		File f=new File(uri);
		ArrayList<Protein> proteins=PeptideFeatureSetParser.parseTSV(f);
		ArrayList<AbstractPeptide> peptides=proteins.get(0).getPeptides();
		return peptides;
	}

	public class ScoredArray implements Comparable<ScoredArray> {
		private final double score;
		private final double correlation;
		private final double[] array;
		private final int index;
		@Override
		public String toString() {
			return correlation+" --> "+getName();
		}

		public ScoredArray(double correlation, double[] array, int index) {
			this.correlation=correlation;
			this.score=Math.abs(correlation);
			this.array=array;
			this.index=index;
		}
		public String getName() {
			return PeptideFeatureSetParser.scoreNames[index];
		}
		@Override
		public int compareTo(ScoredArray o) {
			int c=Double.compare(score, o.score);
			if (c!=0) return c;
			return index-o.index;
		}
	}
	
	public double[] normalize(double[] x) {
		double[] y=new double[x.length];
		
		Max maxCalc=new Max();
		Min minCalc=new Min();
		double xMax=maxCalc.evaluate(x);
		double xMin=minCalc.evaluate(x);
		double xRange=xMax-xMin;
		
		if (xRange==0) {
			return y;
		}
		
		// scale from -1 to 1
		for (int i=0; i<y.length; i++) {
			y[i]=(2.0*(x[i]-xMin)/xRange)-1.0;
		}
		return y;
	}
	
	public double getCorrelation(double[] x, double[] y) {
		Mean meanCalc=new Mean();
		double xBar=meanCalc.evaluate(x);
		double yBar=meanCalc.evaluate(y);
		
		double numerator=0.0;
		double xSS=0.0;
		double ySS=0.0;
		for (int i=0; i<y.length; i++) {
			double xDiff=x[i]-xBar;
			double yDiff=y[i]-yBar;
			numerator+=xDiff*yDiff;
			xSS+=xDiff*xDiff;
			ySS+=yDiff*yDiff;
		}
		if (xSS==0||ySS==0) {
			return 0.0;
		}
		return numerator/Math.sqrt(xSS*ySS);
	}
}
