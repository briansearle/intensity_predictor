package edu.washington.maccoss.intensity_predictor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.StringTokenizer;

import junit.framework.Assert;
import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.Correlation;
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
import edu.washington.maccoss.intensity_predictor.structures.PeptideScoreComparator;
import edu.washington.maccoss.intensity_predictor.structures.PeptideWithScores;
import edu.washington.maccoss.intensity_predictor.structures.Protein;
import gnu.trove.list.array.TDoubleArrayList;
import gnu.trove.list.array.TIntArrayList;

public class Main {
	public static final int TOTAL_FEATURES_CONSIDERED=10;
	public static void main(String[] args) {
		args=new String[] {
				"/Users/searleb/Documents/school/maccoss rotation/transitions/jarrett_intensities.txt", 
				"/Users/searleb/Documents/school/maccoss rotation/transitions/jarrett.nn"
		};
		
		File peptidesWithIntensityFile=new File(args[0]);
		File neuralNetworkFile=new File(args[1]);
		
		if (true) {
			BackPropNeuralNetwork backprop=buildNN(peptidesWithIntensityFile, 100, false, 0.3, true);
			double testTotal=testTotal(backprop, true);
			//NeuralNetworkData.saveNetwork(backprop, neuralNetworkFile);
			System.exit(0);
		}
		
		double[] minCorrelationForGrouping=new double[] {0.0, 0.3, 0.35, 0.4, 0.45, 0.5, 0.55, 0.6, 0.65, 0.7};
		
		double previousTestScore=0.0;
		for (int i=10; i<=15; i++) { // number of features
			for (int j=0; j<5; j++) { // iteration
					for (int l=0; l<minCorrelationForGrouping.length; l++) {
						BackPropNeuralNetwork backprop=buildNN(peptidesWithIntensityFile, i, false, minCorrelationForGrouping[l], minCorrelationForGrouping[l]!=0.0);

						double testTotal=testTotal(backprop, false);
						
						//double testAPOB=testAPOB(backprop, false);
						System.out.println("Test:\t"+i+"\t"+j+"\t"+minCorrelationForGrouping[l]+"\t"+testTotal);

						if (testTotal>previousTestScore) {
							previousTestScore=testTotal;
						
							//NeuralNetworkData.saveNetwork(backprop, neuralNetworkFile);
							//BackPropNeuralNetwork readbp=NeuralNetworkData.readNetwork(neuralNetworkFile);
							Assert.assertEquals(i, backprop.getPropertyList().size());
						}
				}
			}
		}
	}
	
	private static double testTotal(BackPropNeuralNetwork network, boolean print) {
		ArrayList<Protein> proteins=CalculateStatistics.getProteins(new File("/Users/searleb/Documents/school/maccoss rotation/transitions/esp_scores.txt"), network, false);
		float targetPercent=0.75f;
		int[] chooseNTop25=new int[5];
		for (Protein protein : proteins) {
			ArrayList<AbstractPeptide> peptides=protein.getPeptides();
			double[] intensities=new double[peptides.size()];
			for (int i=0; i<intensities.length; i++) {
				intensities[i]=peptides.get(i).getIntensity();
			}
			Arrays.sort(intensities);
			double target=intensities[(int)Math.floor(intensities.length*targetPercent)];
			
			Collections.sort(peptides, new PeptideScoreComparator());
			Collections.reverse(peptides);
			
			for (int i=0; i<peptides.size(); i++) {
				if (peptides.get(i).getIntensity()>=target) {
					for (int j=i; j<chooseNTop25.length; j++) {
						chooseNTop25[j]++;
					}
					break;
				}
			}
		}
		
		double total=0.0;
		for (int i=0; i<chooseNTop25.length; i++) {
			float percent=chooseNTop25[i]/(float)proteins.size();
			total+=(5.0f-i)*percent;
		}
		if (print) {
			System.out.println("\nChoose N\tTop "+Math.round((1.0f-targetPercent)*100f)+"%");
			for (int i=0; i<chooseNTop25.length; i++) {
				float percent=chooseNTop25[i]/(float)proteins.size();
				System.out.println((i+1)+"\t"+percent);
			}
		}
		return total;
	}
	
	private static double testAPOB(BackPropNeuralNetwork network, boolean usePrecursor) {
		BufferedReader reader=null;
		try {
			File apobFile=new File(ClassLoader.getSystemResource("apob_human.txt").toURI());

			reader=new BufferedReader(new FileReader(apobFile));
			String line=null;
			
			ArrayList<String> peptides=new ArrayList<String>();
			TDoubleArrayList intensities=new TDoubleArrayList();
			TDoubleArrayList scores=new TDoubleArrayList();
			while ((line=reader.readLine())!=null) {
				if (line.startsWith("#")) continue; // comment
				StringTokenizer st=new StringTokenizer(line);
				String sequence=st.nextToken();
				double preIntensity=Double.parseDouble(st.nextToken());
				double fragIntensity=Double.parseDouble(st.nextToken());
				
				peptides.add(sequence);
				if (usePrecursor) {
					intensities.add(preIntensity);
				} else {
					intensities.add(fragIntensity);
				}
				scores.add(network.getScore(sequence));
			}
			return Correlation.getSpearmans(intensities.toArray(), scores.toArray());

		} catch (URISyntaxException urise) {
			Logger.writeError("Error parsing APOB test");
			Logger.writeError(urise);
			return 0.0;

		} catch (IOException ioe) {
			Logger.writeError("Error parsing APOB test");
			Logger.writeError(ioe);
			return 0.0;
			
		} finally {
			try {
				if (reader!=null) reader.close();
			} catch (IOException ioe) {
				Logger.writeError("Error parsing APOB test");
				Logger.writeError(ioe);
			}
		}
	}

	private static BackPropNeuralNetwork buildNN(File peptidesWithIntensityFile, int numFeatures, boolean useSpearmans, double minCorrelationForGrouping, boolean useMRMR) {
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
		TIntArrayList bestFeatureIndicies;
		if (useMRMR) {
			bestFeatureIndicies=NeuralNetworkGenerator.getMRMRFeatureIndicies(peptides, trainingIntensities, trainingValues, propertyNames, numFeatures, useSpearmans, minCorrelationForGrouping);
		} else {
			bestFeatureIndicies=NeuralNetworkGenerator.getBestFeatureIndicies(peptides, trainingIntensities, trainingValues, propertyNames, numFeatures, false, useSpearmans);
		}
		ArrayList<AbstractProperty> usedProperties=new ArrayList<AbstractProperty>();
		for (int index : bestFeatureIndicies.toArray()) {
			usedProperties.add((AbstractProperty)properties.get(index));
		}

		BackPropNeuralNetwork backprop=NeuralNetworkGenerator.getNeuralNetwork(trainingIntensities, trainingValues, bestFeatureIndicies, usedProperties);
		
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
