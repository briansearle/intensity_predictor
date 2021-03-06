/**
 * Copyright 2015 Brian C. Searle
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package edu.washington.maccoss.intensity_predictor.math;

import java.util.ArrayList;
import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;
import gnu.trove.list.array.TDoubleArrayList;

public class BackPropNeuralNetwork {
	public static final double ONE_MINUS_BIT=Double.longBitsToDouble(Double.doubleToLongBits(1.0)-1);
	public static final double ZERO_PLUS_BIT=1.0-ONE_MINUS_BIT;
	
	NeuralNetwork<BackPropagation> neuralNetwork;
	double[] min;
	double[] max;
	ArrayList<AbstractProperty> propertyList;

	public BackPropNeuralNetwork(NeuralNetwork<BackPropagation> neuralNetwork, double[] min, double[] max, ArrayList<AbstractProperty> finalPropertyList) {
		this.neuralNetwork=neuralNetwork;
		this.min=min;
		this.max=max;
		this.propertyList=finalPropertyList;
	}
	public double getScore(String sequence) {
		TDoubleArrayList features=new TDoubleArrayList();
		for (AbstractProperty property : propertyList) {
			features.add(property.getProperty(sequence));
		}
		double prob=getProbability(features.toArray());
		if (prob==1.0) prob=ONE_MINUS_BIT;
		if (prob==0.0) prob=ZERO_PLUS_BIT;
		double score=Math.log10(prob)-Math.log10(1.0-prob);
		return score;
	}
	
	public double getProbability(double[] data) {
		neuralNetwork.setInput(normalize(data, min, max));
		neuralNetwork.calculate();
		double[] networkOutput=neuralNetwork.getOutput();
		return networkOutput[0];
	}
	public double[] getMax() {
		return max;
	}
	public double[] getMin() {
		return min;
	}
	public ArrayList<AbstractProperty> getPropertyList() {
		return propertyList;
	}
	public NeuralNetwork<BackPropagation> getNeuralNetwork() {
		return neuralNetwork;
	}

	public static BackPropNeuralNetwork buildModel(double[][] data, double[] intensities, ArrayList<AbstractProperty> finalPropertyList) {
		double[][] bounds=getMinMax(data);
		double[] min=bounds[0];
		double[] max=bounds[1];
		double[][][] normMF=normalizeMinMax(min, max, data);
		data=normMF[0];

		int numHiddenNodes=Math.round((min.length+1.0f)*2.0f/3.0f);
		numHiddenNodes=min.length;
		NeuralNetwork<BackPropagation> neuralNetwork=new MultiLayerPerceptron(min.length, numHiddenNodes, 1);

		BackPropagation learningRule=new BackPropagation();
		learningRule.setMaxIterations(20000);
		neuralNetwork.setLearningRule(learningRule);
		
		DataSet trainingSet=new DataSet(min.length, 1);
		for (int i=0; i<data.length; i++) {
			trainingSet.addRow(new DataSetRow(data[i], new double[] {intensities[i]}));
		}

		neuralNetwork.learn(trainingSet);
		return new BackPropNeuralNetwork(neuralNetwork, min, max, finalPropertyList);
	}

	public static BackPropNeuralNetwork buildModel(double[][] positiveData, double[] positiveIntensities, double[][] negativeData, double[] negativeIntensities, ArrayList<AbstractProperty> finalPropertyList) {
		double[][] bounds=getMinMax(positiveData, negativeData);
		double[] min=bounds[0];
		double[] max=bounds[1];
		double[][][] normMF=normalizeMinMax(min, max, positiveData, negativeData);
		positiveData=normMF[0];
		negativeData=normMF[1];

		int numHiddenNodes=Math.round((min.length+1.0f)*2.0f/3.0f);
		numHiddenNodes=min.length;
		NeuralNetwork<BackPropagation> neuralNetwork=new MultiLayerPerceptron(min.length, numHiddenNodes, 1);

		BackPropagation learningRule=new BackPropagation();
		learningRule.setMaxIterations(20000);
		neuralNetwork.setLearningRule(learningRule);
		
		DataSet trainingSet=new DataSet(min.length, 1);
		for (int i=0; i<positiveData.length; i++) {
			double[] ds=positiveData[i];
			trainingSet.addRow(new DataSetRow(ds, new double[] {positiveIntensities[i]}));
		}
		for (int i=0; i<negativeData.length; i++) {
			double[] ds=negativeData[i];
			trainingSet.addRow(new DataSetRow(ds, new double[] {negativeIntensities[i]}));
		}

		neuralNetwork.learn(trainingSet);
		return new BackPropNeuralNetwork(neuralNetwork, min, max, finalPropertyList);
	}

	static double[][][] normalizeMinMax(double[] min, double[] max, double[][]... dataset) {
		double[][][] r=new double[dataset.length][][];
		for (int i=0; i<dataset.length; i++) {
			r[i]=new double[dataset[i].length][];
			for (int j=0; j<dataset[i].length; j++) {
				r[i][j]=normalize(dataset[i][j], min, max);
			}
		}
		return r;
	}

	public double[] normalize(double[] x) {
		return normalize(x, min, max);
	}
	
	static double[] normalize(double[] x, double[] min, double[] max) {
		double[] y=new double[x.length];
		for (int i=0; i<y.length; i++) {
			y[i]=normalize(x[i], min[i], max[i]);
		}
		return y;
	}

	static double normalize(double x, double min, double max) {
		double n=(x-min)/(max-min);
		if (n>1) n=1;
		if (n<0) n=0;
		return n;
	}

	static double[][] getMinMax(double[][]... dataset) {
		double[] max=new double[dataset[0][0].length];
		double[] min=new double[dataset[0][0].length];
		Arrays.fill(max, -Double.MAX_VALUE);
		Arrays.fill(min, Double.MAX_VALUE);

		for (int i=0; i<dataset.length; i++) {
			for (int j=0; j<dataset[i].length; j++) {
				for (int k=0; k<dataset[i][j].length; k++) {
					if (dataset[i][j][k]>max[k]) {
						max[k]=dataset[i][j][k];
					}
					if (dataset[i][j][k]<min[k]) {
						min[k]=dataset[i][j][k];
					}
				}
			}
		}

		double[][] bounds=new double[][] {min, max};
		return bounds;
	}
}
