package edu.washington.maccoss.intensity_predictor.parsers;

import java.io.File;
import java.net.URI;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.General;
import edu.washington.maccoss.intensity_predictor.math.LinearDiscriminantAnalysis;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import edu.washington.maccoss.intensity_predictor.structures.Protein;
import junit.framework.TestCase;

public class PeptideFeatureSetParserTest extends TestCase {
	private static final int TOTAL_FEATURES_CONSIDERED=10;

	public void testParsing() throws Exception {
		URI uri=ClassLoader.getSystemResource("sprg_PeptideFeatureSet.csv").toURI();
		File f=new File(uri);
		ArrayList<Protein> proteins=PeptideFeatureSetParser.parseTSV(f);
		ArrayList<AbstractPeptide> peptides=proteins.get(0).getPeptides();
		//tests for data consistency
		assertEquals(912, peptides.size());
		for (AbstractPeptide peptide : peptides) {
			assertEquals(PeptideFeatureSetParser.scoreNames.length, peptide.getScoreArray().length);	
		}
		
		//transposition
		double[] originalIntensities=new double[912];
		double[][] values=new double[550][];
		for (int i=0; i<values.length; i++) {
			values[i]=new double[912];
		}
		for (int i=0; i<peptides.size(); i++) {
			AbstractPeptide peptide=peptides.get(i);
			originalIntensities[i]=peptide.getIntensity();
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
		ArrayList<ScoredArray> bestFeatures=new ArrayList<ScoredArray>();
		for (int iter=0; iter<TOTAL_FEATURES_CONSIDERED; iter++) {
			ArrayList<ScoredArray> arrays=new ArrayList<ScoredArray>();
			for (int i=0; i<features.size(); i++) {
				double correlation=getCorrelation(intensities, features.get(i).array);
				ScoredArray scoredArray=new ScoredArray(correlation, features.get(i).array, features.get(i).index);
				arrays.add(scoredArray);
			}
			Collections.sort(arrays);
			ScoredArray best=arrays.remove(arrays.size()-1);
			for (int i=0; i<intensities.length; i++) {
				intensities[i]-=best.correlation*best.array[i];
			}
			
			features=arrays;
			bestFeatures.add(best);
			System.out.println(best);
		}

		double[] intensityArray=originalIntensities.clone();
		ArrayList<double[]> goodFeatures=new ArrayList<double[]>();
		ArrayList<double[]> badFeatures=new ArrayList<double[]>();
		double q1=General.getPercentile(intensityArray, 0.25);
		double q3=General.getPercentile(intensityArray, 0.75);
		for (int i=0; i<originalIntensities.length; i++) {
			double[] featureArray=new double[TOTAL_FEATURES_CONSIDERED];
			for (int j=0; j<bestFeatures.size(); j++) {
				featureArray[j]=bestFeatures.get(j).array[i];
			}
			if (q1>originalIntensities[i]) {
				badFeatures.add(featureArray);
			} else if (q3<originalIntensities[i]) {
				goodFeatures.add(featureArray);
			}
		}
		
		LinearDiscriminantAnalysis lda=LinearDiscriminantAnalysis.buildModel(goodFeatures.toArray(new double[goodFeatures.size()][]), badFeatures.toArray(new double[badFeatures.size()][]));
		BackPropNeuralNetwork backprop=BackPropNeuralNetwork.buildModel(goodFeatures.toArray(new double[goodFeatures.size()][]), badFeatures.toArray(new double[badFeatures.size()][]));
		for (int i=0; i<originalIntensities.length; i++) {
			double[] featureArray=new double[TOTAL_FEATURES_CONSIDERED];
			for (int j=0; j<bestFeatures.size(); j++) {
				featureArray[j]=bestFeatures.get(j).array[i];
			}
			double logLikelihood=lda.getScore(featureArray);
			double prob=backprop.getScore(featureArray);
			System.out.println(originalIntensities[i]+"\t"+logLikelihood+"\t"+prob);
		}
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
		return numerator/Math.sqrt(xSS*ySS);
	}
}
