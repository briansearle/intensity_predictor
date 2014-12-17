package edu.washington.maccoss.intensity_predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.General;
import edu.washington.maccoss.intensity_predictor.math.NeuralNetworkData;
import edu.washington.maccoss.intensity_predictor.parsers.AAIndex1Parser;
import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;
import edu.washington.maccoss.intensity_predictor.properties.LengthProperty;
import edu.washington.maccoss.intensity_predictor.properties.MassProperty;
import edu.washington.maccoss.intensity_predictor.properties.NumberAcidicProperty;
import edu.washington.maccoss.intensity_predictor.properties.NumberBasicProperty;
import edu.washington.maccoss.intensity_predictor.properties.PropertyInterface;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import edu.washington.maccoss.intensity_predictor.structures.PeptideWithScores;
import edu.washington.maccoss.intensity_predictor.structures.Protein;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

public class Main {
	public static void main(String[] args) {
		File peptidesWithIntensityFile=new File(args[0]);
		File neuralNetworkFile=new File(args[1]);
		
		BackPropNeuralNetwork backprop=buildAndSaveNN(peptidesWithIntensityFile, neuralNetworkFile);
	}

	private static BackPropNeuralNetwork buildAndSaveNN(File peptidesWithIntensityFile, File neuralNetworkFile) {
		ArrayList<PropertyInterface> properties=getProperties();
		String[] propertyNames=new String[properties.size()];
		for (int i=0; i<propertyNames.length; i++) {
			propertyNames[i]=properties.get(i).toString();
		}
		
		ArrayList<AbstractPeptide> peptides=getPeptides(peptidesWithIntensityFile, properties);

		double[] trainingIntensities=new double[peptides.size()];
		for (int i=0; i<trainingIntensities.length; i++) {
			AbstractPeptide peptide=peptides.get(i);
			trainingIntensities[i]=peptide.getIntensity();
		}
		trainingIntensities=General.rank(trainingIntensities);
		
		double[][] trainingValues=new double[propertyNames.length][];
		TIntArrayList bestFeatureIndicies=NeuralNetworkGenerator.getBestFeatureIndicies(peptides, trainingIntensities, trainingValues, propertyNames);
		
		ArrayList<AbstractProperty> usedProperties=new ArrayList<AbstractProperty>();
		for (int index : bestFeatureIndicies.toArray()) {
			usedProperties.add((AbstractProperty)properties.get(index));
		}
		
		BackPropNeuralNetwork backprop=NeuralNetworkGenerator.getNeuralNetwork(trainingIntensities, trainingValues, bestFeatureIndicies, usedProperties);
		
		NeuralNetworkData.saveNetwork(backprop, neuralNetworkFile);
		return backprop;
	}

	private static ArrayList<AbstractPeptide> getPeptides(File peptidesWithIntensityFile, ArrayList<PropertyInterface> properties) {
		ArrayList<AbstractPeptide> peptides=new ArrayList<>();
		Protein protein=new Protein(peptidesWithIntensityFile.getName());
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(peptidesWithIntensityFile));
			String line=null;
			while ((line=reader.readLine())!=null) {
				if (line.startsWith("#")) continue; // comment
				StringTokenizer st=new StringTokenizer(line);
				String sequence=st.nextToken();
				float intensity=0.0f;
				if (st.hasMoreTokens()) {
					intensity=Float.parseFloat(st.nextToken());
				}
				TDoubleArrayList peptideProperties=new TDoubleArrayList();
				for (PropertyInterface property : properties) {
					peptideProperties.add(property.getProperty(sequence));
				}
				PeptideWithScores peptide=new PeptideWithScores(sequence, intensity, protein, peptideProperties.toArray());
				peptides.add(peptide);
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (reader!=null) reader.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return peptides;
	}

	private static ArrayList<PropertyInterface> getProperties() {
		ArrayList<PropertyInterface> properties=new ArrayList<>();
		properties.add(new LengthProperty());
		properties.add(new NumberBasicProperty());
		properties.add(new NumberAcidicProperty());
		properties.add(new MassProperty());
		
		try {
			URI uri=ClassLoader.getSystemResource("aaindex1").toURI();
			File f=new File(uri);
			properties.addAll(AAIndex1Parser.parseAAIndex1(f, false));
		} catch (URISyntaxException urise) {
			Logger.writeError("Error parsing amino acid properties file.");
			Logger.writeError(urise);
			fail();
		}
		return properties;
	}
	
	private static void fail() {
		System.exit(1);
	}
}
