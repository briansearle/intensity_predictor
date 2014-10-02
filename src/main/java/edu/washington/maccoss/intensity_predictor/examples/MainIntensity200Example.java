package edu.washington.maccoss.intensity_predictor.examples;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;

import edu.washington.maccoss.intensity_predictor.math.NaiveBayes;
import edu.washington.maccoss.intensity_predictor.parsers.IntensityTsvParser;
import edu.washington.maccoss.intensity_predictor.structures.Peptide;
import edu.washington.maccoss.intensity_predictor.structures.Protein;

public class MainIntensity200Example {

	public static void main(String[] args) {
		DecimalFormat formatter=new DecimalFormat("0.00");
		try {
			URI uri=IntensityTsvParser.class.getResource("/intensity_200.xls").toURI();
			File f=new File(uri);
			ArrayList<Protein> proteins=IntensityTsvParser.parseTSV(f);

			ArrayList<double[]> highScores=new ArrayList<double[]>();
			ArrayList<double[]> lowScores=new ArrayList<double[]>();
			for (Protein protein : proteins) {
				float totalIntensity=protein.getSummedIntensity();
				ArrayList<Peptide> peptides=protein.getPeptides();
				int peptideCount=peptides.size();

				for (Peptide peptide : peptides) {
					float predictorScore=peptide.getPredictorScore(totalIntensity, peptideCount);
					if (predictorScore>=1.0f) {
						highScores.add(peptide.getScoreArray());
					} else if (predictorScore<=-2.0f) {
						lowScores.add(peptide.getScoreArray());
					}
				}
			}

			NaiveBayes bayes=NaiveBayes.buildModel(highScores.toArray(new double[highScores.size()][]), lowScores.toArray(new double[lowScores.size()][]));

			for (Protein protein : proteins) {
				// System.out.println(protein.getAccessionNumber());
				float totalIntensity=protein.getSummedIntensity();
				ArrayList<Peptide> peptides=protein.getPeptides();
				int peptideCount=peptides.size();
				Collections.sort(peptides);
				Collections.reverse(peptides);

				for (Peptide peptide : peptides) {
					float predictorScore=peptide.getPredictorScore(totalIntensity, peptideCount);
					double logLikelihood=bayes.getLogLikelihood(peptide.getScoreArray());
					System.out.println(protein.getAccessionNumber()+"\t"+peptide.getSequence()+"\t"+formatter.format(100.0f*peptide.getIntensity()/totalIntensity)+"%\t"+predictorScore+"\t"
							+logLikelihood);
				}
			}
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
		}
	}
}
