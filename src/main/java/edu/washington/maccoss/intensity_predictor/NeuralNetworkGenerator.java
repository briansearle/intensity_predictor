package edu.washington.maccoss.intensity_predictor;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.General;
import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math.stat.descriptive.moment.Mean;
import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;

public class NeuralNetworkGenerator {
	public static final int TOTAL_FEATURES_CONSIDERED=10;

	public static TIntArrayList getBestFeatureIndicies(ArrayList<AbstractPeptide> trainingPeptides, double[] originalIntensities, double[][] values, String[] scoreNames) {
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
		double[][] normalized=new double[values.length][];
		for (int i=0; i<values.length; i++) {
			normalized[i]=normalize(values[i]);
		}
		values=normalized;

		ArrayList<ScoredArray> features=new ArrayList<ScoredArray>();
		for (int i=0; i<values.length; i++) {
			features.add(new ScoredArray(0.0, normalized[i], i, scoreNames[i]));
		}
		
		// A type of Minimum-redundancy-maximum-relevance (mRMR) feature selection algorithm:
		// http://en.wikipedia.org/wiki/Feature_selection#Minimum-redundancy-maximum-relevance_.28mRMR.29_feature_selection
		TIntArrayList bestFeatureIndicies=new TIntArrayList();
		for (int iter=0; iter<TOTAL_FEATURES_CONSIDERED; iter++) {
			ArrayList<ScoredArray> arrays=new ArrayList<ScoredArray>();
			for (int i=0; i<features.size(); i++) {
				double correlation=getCorrelation(intensities, features.get(i).normalizedArray);
				ScoredArray scoredArray=new ScoredArray(correlation, features.get(i).normalizedArray, features.get(i).index, scoreNames[features.get(i).index]);
				if (scoredArray.getName().equals("P Composition")) {
					System.out.println("\tP: "+scoredArray.toString());
				}
				arrays.add(scoredArray);
			}
			Collections.sort(arrays);
			ScoredArray best=arrays.remove(arrays.size()-1);
			for (int i=0; i<intensities.length; i++) {
				intensities[i]-=best.correlation*best.normalizedArray[i];
			}
			
			features=arrays;
			bestFeatureIndicies.add(best.index);
			System.out.println(best.index+"\t"+best.score+"\t"+best.toString());
		}
		return bestFeatureIndicies;
	}

	public static BackPropNeuralNetwork getNeuralNetwork(double[] originalIntensities, double[][] values, TIntArrayList bestFeatureIndicies, ArrayList<AbstractProperty> finalPropertyList) {
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
			double[] featureArray=new double[NeuralNetworkGenerator.TOTAL_FEATURES_CONSIDERED];
			for (int j=0; j<bestFeatures.size(); j++) {
				featureArray[j]=bestFeatures.get(j)[i];
			}
			if (q1>originalIntensities[i]) {
				badFeatures.add(featureArray);
			} else if (q3<originalIntensities[i]) {
				goodFeatures.add(featureArray);
			}
		}
		
		BackPropNeuralNetwork backprop=BackPropNeuralNetwork.buildModel(goodFeatures.toArray(new double[goodFeatures.size()][]), badFeatures.toArray(new double[badFeatures.size()][]), finalPropertyList);
		return backprop;
	}
	
	public static double[] normalize(double[] x) {
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
	
	public static double getCorrelation(double[] x, double[] y) {
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

	public static class ScoredArray implements Comparable<ScoredArray> {
		private final double score;
		private final double correlation;
		private final double[] normalizedArray;
		private final int index;
		private final String scoreName;
		@Override
		public String toString() {
			return correlation+" --> "+getName();
		}

		public ScoredArray(double correlation, double[] normalizedArray, int index, String scoreName) {
			this.correlation=correlation;
			this.score=Math.abs(correlation);
			this.normalizedArray=normalizedArray;
			this.index=index;
			this.scoreName=scoreName;
		}
		public String getName() {
			return scoreName;
		}
		@Override
		public int compareTo(ScoredArray o) {
			int c=Double.compare(score, o.score);
			if (c!=0) return c;
			return index-o.index;
		}
	}
}
