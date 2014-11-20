package edu.washington.maccoss.intensity_predictor.math;

import junit.framework.TestCase;

import org.apache.commons.math.stat.descriptive.rank.Max;
import org.apache.commons.math.stat.descriptive.rank.Min;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.data.DataSet;
import org.neuroph.core.data.DataSetRow;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.Perceptron;

public class NeuralNetworkTest extends TestCase {
	public void ttestNeuralNetwork() {
		// create new perceptron network
		NeuralNetwork neuralNetwork=new Perceptron(3, 1);
		// create training set
		DataSet trainingSet=new DataSet(3, 1);
		// add training data to training set (logical OR function)
		trainingSet.addRow(new DataSetRow(new double[] {0, 0, 0}, new double[] {0}));
		trainingSet.addRow(new DataSetRow(new double[] {0, 1, 0}, new double[] {1}));
		trainingSet.addRow(new DataSetRow(new double[] {1, 0, 1}, new double[] {1}));
		trainingSet.addRow(new DataSetRow(new double[] {1, 1, 1}, new double[] {1}));
		// learn the training set
		neuralNetwork.learn(trainingSet);
		
		// set network input 
		neuralNetwork.setInput(1, 1, 1); 
		// calculate network 
		neuralNetwork.calculate(); 
		// get network output 
		double[] networkOutput = neuralNetwork.getOutput();
		for (int i=0; i<networkOutput.length; i++) {
			System.out.println(networkOutput[i]);
		}
	}
	
	public void testMaleFemale() {
		double[][] male=new double[][] { {6, 180, 12}, {5.92, 190, 11}, {5.58, 170, 12}, {5.92, 165, 10}};
		double[][] female=new double[][] { {5, 100, 6}, {5.5, 150, 8}, {5.42, 130, 7}, {5.75, 150, 9}};
		//FIXME need to normalize data! both together!

		double[][] bounds=getMinMax(male, female);
		double[][][] normMF=normalizeMinMax(bounds, male, female);
		male=normMF[0];
		female=normMF[1];
		
		// create new perceptron network
		NeuralNetwork neuralNetwork=new MultiLayerPerceptron(3, 2, 1);
		// create training set
		DataSet trainingSet=new DataSet(3, 1);
		// add training data to training set (logical OR function)
		for (double[] ds : female) {
			trainingSet.addRow(new DataSetRow(ds, new double[] {1}));
		}
		for (double[] ds : male) {
			trainingSet.addRow(new DataSetRow(ds, new double[] {0}));
		}
		
		// learn the training set
		neuralNetwork.learn(trainingSet);
		
		// set network input 
		neuralNetwork.setInput(
				normalize(5, bounds[0][0], bounds[1][0]), 
				normalize(130, bounds[0][1], bounds[1][1]), 
				normalize(8, bounds[0][2], bounds[1][2])); 
		// calculate network 
		neuralNetwork.calculate(); 
		// get network output 
		double[] networkOutput = neuralNetwork.getOutput();
		for (int i=0; i<networkOutput.length; i++) {
			System.out.println(networkOutput[i]);
		}
	}

	private double[][][] normalizeMinMax(double[][] bounds, double[][]... dataset) {
		double[][][] r=new double[dataset.length][][];
		for (int i=0; i<dataset.length; i++) {
			r[i]=new double[dataset[i].length][];
			for (int j=0; j<dataset[i].length; j++) {
				r[i][j]=new double[dataset[i][j].length];
				for (int k=0; k<dataset[i][j].length; k++) {
					r[i][j][k]=normalize(dataset[i][j][k], bounds[0][k], bounds[1][k]);
				}
			}
		}
		return r;
	}
	
	private double normalize(double x, double min, double max) {
		return (x-min)/(max-min);
	}

	private double[][] getMinMax(double[][]... dataset) {
		double[] max=new double[dataset[0][0].length];
		double[] min=new double[dataset[0][0].length];
		
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
