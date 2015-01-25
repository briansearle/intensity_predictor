package edu.washington.maccoss.intensity_predictor.parsers;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import junit.framework.TestCase;
import edu.washington.maccoss.intensity_predictor.NeuralNetworkGenerator;
import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.General;
import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import edu.washington.maccoss.intensity_predictor.structures.Peptide;
import edu.washington.maccoss.intensity_predictor.structures.Protein;
import gnu.trove.list.array.TIntArrayList;

public class PeptideFeatureSetParserTest extends TestCase {
	public static final int TOTAL_FEATURES_CONSIDERED=10;

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
		trainingIntensities=General.rank(trainingIntensities);
		
		// testing data
		double[] testingIntensities=new double[testingPeptides.size()];
		for (int i=0; i<testingIntensities.length; i++) {
			AbstractPeptide peptide=testingPeptides.get(i);
			testingIntensities[i]=peptide.getIntensity();
		}
		testingIntensities=General.rank(testingIntensities);
		
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
			testingValues[i]=NeuralNetworkGenerator.normalize(testingValues[i]);
		}
		
		double[][] trainingValues=new double[PeptideFeatureSetParser.scoreNames.length][];
		TIntArrayList bestFeatureIndicies=NeuralNetworkGenerator.getBestFeatureIndicies(trainingPeptides, trainingIntensities, trainingValues, PeptideFeatureSetParser.scoreNames, TOTAL_FEATURES_CONSIDERED, false, false);
		BackPropNeuralNetwork backprop=NeuralNetworkGenerator.getNeuralNetwork(trainingIntensities, trainingValues, bestFeatureIndicies, new ArrayList<AbstractProperty>());
		//NeuralNetworkData.saveNetwork(backprop, new File("/Users/searleb/tmp/nn"));
		
		ArrayList<double[]> trainingFeatures=new ArrayList<double[]>();
		for (int index : bestFeatureIndicies.toArray()) {
			trainingFeatures.add(trainingValues[index]);
		}
		
		for (int i=0; i<trainingIntensities.length; i++) {
			double[] featureArray=new double[TOTAL_FEATURES_CONSIDERED];
			for (int j=0; j<trainingFeatures.size(); j++) {
				featureArray[j]=trainingFeatures.get(j)[i];
			}
			double prob=backprop.getProbability(featureArray);
			if (prob==1.0) prob=BackPropNeuralNetwork.ONE_MINUS_BIT;
			if (prob==0.0) prob=BackPropNeuralNetwork.ZERO_PLUS_BIT;
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
			double prob=backprop.getProbability(featureArray);
			if (prob==1.0) prob=BackPropNeuralNetwork.ONE_MINUS_BIT;
			if (prob==0.0) prob=BackPropNeuralNetwork.ZERO_PLUS_BIT;
			double score=Math.log10(prob)-Math.log10(1.0-prob);

			AbstractPeptide peptide=testingPeptides.get(i);
			System.out.println(peptide.getSequence()+"\t"+testingIntensities[i]+"\t"+score+"\t"+prob);

			
			Peptide p=new Peptide(peptide.getSequence(), peptide.getIntensity(), peptide.getProtein());
			double[] scores=p.getScoreArray();
			for (int j=0; j<scores.length; j++) {
				/*if (scores[j]<backprop.getMin()[j]) {
					System.out.println("TOO LOW:  "+backprop.getMin()[j]+"\t"+scores[j]+"\t"+backprop.getMax()[j]);
				} else if (scores[j]>backprop.getMax()[j]) {
					System.out.println("TOO HIGH: "+backprop.getMin()[j]+"\t"+scores[j]+"\t"+backprop.getMax()[j]);
				}*/
				//System.out.println("\t"+scores[j]+" == "+featureArray[j]);
			}
		}
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
}
