package edu.washington.maccoss.intensity_predictor.examples;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.General;
import edu.washington.maccoss.intensity_predictor.math.NaiveBayes;
import edu.washington.maccoss.intensity_predictor.parsers.IntensityTsvParser;
import edu.washington.maccoss.intensity_predictor.properties.AbstractProperty;
import edu.washington.maccoss.intensity_predictor.structures.AbstractPeptide;
import edu.washington.maccoss.intensity_predictor.structures.Protein;
import gnu.trove.list.array.TDoubleArrayList;

public class MainIntensity200Example {

	public static void main(String[] args) {
		DecimalFormat formatter=new DecimalFormat("0.00");
		try {
			URI uri=IntensityTsvParser.class.getResource("/jim_intensities.xls").toURI();
			//URI uri=IntensityTsvParser.class.getResource("/sprg_peptides.txt").toURI();
			//URI uri=MainIntensity200Example.class.getResource("/jarrett1600.txt").toURI();
			
			File f=new File(uri);
			ArrayList<Protein> proteins=IntensityTsvParser.parseTSV(f);

			TDoubleArrayList intensityList=new TDoubleArrayList();
			for (Protein protein : proteins) {
				for (AbstractPeptide peptide : protein.getPeptides()) {
					intensityList.add(peptide.getIntensity());
				}
			}
			double[] intensityArray=intensityList.toArray();
			double q1=General.getPercentile(intensityArray, 0.25);
			double q3=General.getPercentile(intensityArray, 0.75);
			
			//MutableRegressionProblemImpl<Float>  
			
			ArrayList<double[]> highScores=new ArrayList<double[]>();
			ArrayList<double[]> lowScores=new ArrayList<double[]>();
			for (Protein protein : proteins) {
				float totalIntensity=protein.getSummedIntensity();
				ArrayList<AbstractPeptide> peptides=protein.getPeptides();
				int peptideCount=peptides.size();

				for (AbstractPeptide peptide : peptides) {
					if (peptide.getIntensity()>=q3) {
						highScores.add(peptide.getScoreArray());
					} else if (peptide.getIntensity()<=q1) {
						lowScores.add(peptide.getScoreArray());
					}
				}
			}
			
			System.out.println(highScores.size()+" / "+lowScores.size());

			//NaiveBayes bayes=NaiveBayes.buildModel(highScores.toArray(new double[highScores.size()][]), lowScores.toArray(new double[lowScores.size()][]));
			//LinearDiscriminantAnalysis lda=LinearDiscriminantAnalysis.buildModel(highScores.toArray(new double[highScores.size()][]), lowScores.toArray(new double[lowScores.size()][]));
			BackPropNeuralNetwork lda=BackPropNeuralNetwork.buildModel(highScores.toArray(new double[highScores.size()][]), lowScores.toArray(new double[lowScores.size()][]), new ArrayList<AbstractProperty>());
			
			for (Protein protein : proteins) {
				// System.out.println(protein.getAccessionNumber());
				float totalIntensity=protein.getSummedIntensity();
				ArrayList<AbstractPeptide> peptides=protein.getPeptides();
				int peptideCount=peptides.size();
				Collections.sort(peptides);
				Collections.reverse(peptides);

				for (AbstractPeptide peptide : peptides) {
					//double logLikelihood=bayes.getLogLikelihood(peptide.getScoreArray());
					double logLikelihood=lda.getScore(peptide.getScoreArray());
					System.out.println(protein.getAccessionNumber()+"\t"+peptide.getSequence()+"\t"+formatter.format(100.0f*peptide.getIntensity()/totalIntensity)+"%\t"+peptide.getIntensity()+"\t"
							+logLikelihood);
				}
			}
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
		}
	}
}
