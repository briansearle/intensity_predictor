package edu.washington.maccoss.intensity_predictor.math;

import java.util.Arrays;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;

public class BackPropNeuralNetwork {
	NeuralNetwork<BackPropagation> neuralNetwork;
	double[] min;
	double[] max;

	public BackPropNeuralNetwork(NeuralNetwork<BackPropagation> neuralNetwork, double[] min, double[] max) {
		this.neuralNetwork=neuralNetwork;
		this.min=min;
		this.max=max;
	}
	public double getScore(double[] data) {
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
	public NeuralNetwork<BackPropagation> getNeuralNetwork() {
		return neuralNetwork;
	}

	public static BackPropNeuralNetwork buildModel(double[][] positiveData, double[][] negativeData) {
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
		for (double[] ds : positiveData) {
			trainingSet.addRow(new DataSetRow(ds, new double[] {1}));
		}
		for (double[] ds : negativeData) {
			trainingSet.addRow(new DataSetRow(ds, new double[] {0}));
		}

		neuralNetwork.learn(trainingSet);
		return new BackPropNeuralNetwork(neuralNetwork, min, max);
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
		return (x-min)/(max-min);
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
