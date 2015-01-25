package edu.washington.maccoss.intensity_predictor;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.Correlation;
import edu.washington.maccoss.intensity_predictor.math.General;
import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;

public class NeuralNetworkGenerator {

	public static TIntArrayList getMRMRFeatureIndicies(ArrayList<AbstractPeptide> trainingPeptides, double[] originalIntensities, double[][] values, String[] scoreNames, int numberOfFeatures, boolean useSpearmans, double minCorrelationForGrouping) {
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

		boolean[] featureAvailable=new boolean[normalized.length];
		ScoredArray[] features=new ScoredArray[normalized.length];
		for (int i=0; i<normalized.length; i++) {
			featureAvailable[i]=true;
			features[i]=new ScoredArray(0.0, normalized[i], i, scoreNames[i]);
		}
		
		double[][] correlationMatrix=new double[scoreNames.length][];
		for (int i=0; i<correlationMatrix.length; i++) {
			correlationMatrix[i]=new double[scoreNames.length];
		}
		for (int i=0; i<correlationMatrix.length; i++) {
			for (int j=i+1; j<correlationMatrix.length; j++) {
				double correlation;
				if (useSpearmans) {
					correlation=Correlation.getSpearmans(normalized[i], normalized[j]);
				} else {
					correlation=Correlation.getPearsons(normalized[i], normalized[j]);
				}
				correlation=Math.abs(correlation);
				correlationMatrix[i][j]=correlation;
				correlationMatrix[j][i]=correlation;
			}
		}
		
		// A type of Minimum-redundancy-maximum-relevance (mRMR) feature selection algorithm:
		// http://en.wikipedia.org/wiki/Feature_selection#Minimum-redundancy-maximum-relevance_.28mRMR.29_feature_selection
		
		TIntArrayList bestFeatureIndicies=new TIntArrayList();
		for (int iter=0; iter<numberOfFeatures; iter++) {
			ArrayList<ScoredArray> arrays=new ArrayList<ScoredArray>();
			for (int i=0; i<features.length; i++) {
				if (featureAvailable[i]) {
					double correlation;
					if (useSpearmans) {
						correlation=Correlation.getSpearmans(intensities, features[i].normalizedArray);
					} else {
						correlation=Correlation.getPearsons(intensities, features[i].normalizedArray);
					}
					ScoredArray scoredArray=new ScoredArray(correlation, features[i].normalizedArray, features[i].index, scoreNames[features[i].index]);
					arrays.add(scoredArray);
				}
			}
			if (arrays.size()==0) {
				continue;
			}
			Collections.sort(arrays);
			ScoredArray best=arrays.get(arrays.size()-1);
			if (best.correlation==0) break;
			
			featureAvailable[best.index]=false;
			
			// remove similar features
			for (int i=0; i<correlationMatrix.length; i++) {
				if (minCorrelationForGrouping<=correlationMatrix[best.index][i]) {
					featureAvailable[i]=false;
				}
			}
			
			bestFeatureIndicies.add(best.index);
			System.out.println(bestFeatureIndicies.size()+"\t"+best.index+"\t"+best.score+"\t"+best.toString());
		}
		return bestFeatureIndicies;
	}

	public static TIntArrayList getBestFeatureIndicies(ArrayList<AbstractPeptide> trainingPeptides, double[] originalIntensities, double[][] values, String[] scoreNames, int numberOfFeatures, boolean randomlyChooseSecond, boolean useSpearmans) {
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

		ArrayList<ScoredArray> features=new ArrayList<ScoredArray>();
		for (int i=0; i<normalized.length; i++) {
			features.add(new ScoredArray(0.0, normalized[i], i, scoreNames[i]));
		}
		
		TIntArrayList bestFeatureIndicies=new TIntArrayList();
		for (int iter=0; iter<numberOfFeatures; iter++) {
			ArrayList<ScoredArray> arrays=new ArrayList<ScoredArray>();
			for (int i=0; i<features.size(); i++) {
				double correlation;
				if (useSpearmans) {
					correlation=Correlation.getSpearmans(intensities, features.get(i).normalizedArray);
				} else {
					correlation=Correlation.getPearsons(intensities, features.get(i).normalizedArray);
				}
				ScoredArray scoredArray=new ScoredArray(correlation, features.get(i).normalizedArray, features.get(i).index, scoreNames[features.get(i).index]);
				arrays.add(scoredArray);
			}
			Collections.sort(arrays);
			ScoredArray best=arrays.remove(arrays.size()-1);
			
			// choose second instead
			if (randomlyChooseSecond&&Math.random()>(best.correlation/(best.correlation+arrays.get(arrays.size()-1).correlation))) {
				ScoredArray second=arrays.remove(arrays.size()-1);
				arrays.add(best);
				best=second;
			}
			
			for (int i=0; i<intensities.length; i++) {
				intensities[i]-=best.correlation*best.normalizedArray[i];
			}
			
			features=arrays;
			bestFeatureIndicies.add(best.index);
			//System.out.println(best.index+"\t"+best.score+"\t"+best.toString());
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
			double[] featureArray=new double[bestFeatures.size()];
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
		
		// scale from 0 to 1
		for (int i=0; i<y.length; i++) {
			y[i]=((x[i]-xMin)/xRange);
		}
		return y;
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
