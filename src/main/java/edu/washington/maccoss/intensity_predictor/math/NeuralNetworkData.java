package edu.washington.maccoss.intensity_predictor.math;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.nnet.learning.BackPropagation;


public class NeuralNetworkData {
	private static final String minArrayName="min array";
	private static final String maxArrayName="max array";
	private static final String nnName="neural_network_classifier.nn";
	private static final String nnMetaName="neural_network_metadata.nn";

	public static void saveNetwork(BackPropNeuralNetwork nn, File dir) {
		File classifier=new File(dir, nnName);
		File metadata=new File(dir, nnMetaName);

		if (dir.exists()) {
			if (!dir.isDirectory()) {
				dir.delete();
			} else {
				if (classifier.exists()) classifier.delete();
				if (metadata.exists()) metadata.delete();
			}
		} else {
			dir.mkdirs();
		}

		nn.getNeuralNetwork().save(classifier.getAbsolutePath());

		Properties prop=new Properties();
		prop.setProperty(minArrayName, General.toPropertyString(nn.getMin()));
		prop.setProperty(maxArrayName, General.toPropertyString(nn.getMax()));
		try {
			prop.store(new FileOutputStream(metadata), "Neural Network Intensity Predictor Metadata");
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException writing properties file.");
			fnfe.printStackTrace();
		} catch (IOException ioe) {
			System.err.println("IOException writing properties file.");
			ioe.printStackTrace();
		}
	}

	public static BackPropNeuralNetwork readNetwork(File dir) {
		File classifier=new File(dir, nnName);
		File metadata=new File(dir, nnMetaName);

		NeuralNetwork<BackPropagation> nn=NeuralNetwork.createFromFile(classifier);

		Properties props=new Properties();

		try {
			FileInputStream in=new FileInputStream(metadata);
			props.load(in);
			double[] min=General.fromPropertyString(props.getProperty(minArrayName));
			double[] max=General.fromPropertyString(props.getProperty(maxArrayName));
			return new BackPropNeuralNetwork(nn, min, max);
			
		} catch (FileNotFoundException fnfe) {
			System.err.println("FileNotFoundException writing properties file. Cannot continue!");
			fnfe.printStackTrace();
			return null;
		} catch (IOException ioe) {
			System.err.println("IOException writing properties file. Cannot continue!");
			ioe.printStackTrace();
			return null;
		}
	}
}
