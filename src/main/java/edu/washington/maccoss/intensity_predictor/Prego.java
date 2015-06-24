package edu.washington.maccoss.intensity_predictor;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map.Entry;
import java.util.StringTokenizer;
import java.util.TreeMap;

import edu.washington.maccoss.intensity_predictor.Classify.ScoredPeptide;
import edu.washington.maccoss.intensity_predictor.math.BackPropNeuralNetwork;
import edu.washington.maccoss.intensity_predictor.math.NeuralNetworkData;
import edu.washington.maccoss.intensity_predictor.parsers.FastaEntry;
import edu.washington.maccoss.intensity_predictor.parsers.FastaReader;
import edu.washington.maccoss.intensity_predictor.structures.PeptideData;

public class Prego {
	private static BackPropNeuralNetwork network=null;
	
	public static ArrayList<PeptideData> processText(String text, int numberOfPeptides) {
		TreeMap<String, ArrayList<String>> proteinMap=new TreeMap<String, ArrayList<String>>();
		
		text=text.trim();
		if (text.startsWith(">")) {
			// fasta
			ArrayList<FastaEntry> proteins=FastaReader.readFasta(text, "GUI input");
			for (FastaEntry entry : proteins) {
				ArrayList<String> peptides=Classify.digestProtein(entry.getSequence(), 8, 25);
				String accession=entry.getAnnotation().substring(1, entry.getAnnotation().indexOf(' '));
				proteinMap.put(accession, peptides);
			}
		} else {
			StringTokenizer st=new StringTokenizer(text);
			ArrayList<String> peptides=new ArrayList<>();
			while (st.hasMoreTokens()) {
				peptides.add(st.nextToken());
			}
			proteinMap.put("Unknown Protein", peptides);
		}

		ArrayList<PeptideData> result=new ArrayList<PeptideData>();

		for (Entry<String, ArrayList<String>> entry : proteinMap.entrySet()) {
			String accession=entry.getKey();
			ArrayList<String> sequences=entry.getValue();

			ArrayList<ScoredPeptide> scores=new ArrayList<ScoredPeptide>();
			for (String sequence : sequences) {
				double score=getNetwork().getScore(sequence);
				ScoredPeptide peptide=new ScoredPeptide(score, sequence);
				scores.add(peptide);
			}

			Collections.sort(scores);
			Collections.reverse(scores);

			for (int i=0; i<scores.size(); i++) {
				if (i>=numberOfPeptides) break;
				
				ScoredPeptide scoredPeptide=scores.get(i);
				int index=i+1;

				result.add(new PeptideData(index, scoredPeptide.sequence, accession, scoredPeptide.score));
			}
		}
		return result;
	}

	public static BackPropNeuralNetwork getNetwork() {
		if (network!=null) return network;

		try {
			network=readNetwork("new_jarrett_intensities.nn");
		} catch (Exception e) {
			Logger.writeError("Error default reading neural network file!");
			Logger.writeError(e);
			System.exit(1);
		}
		return network;
	}

	public static BackPropNeuralNetwork readNetwork(String path) {
		ClassLoader classLoader=ClassifyPeptides.class.getClassLoader();
		InputStream inClassifier=classLoader.getResourceAsStream(path+"/neural_network_classifier.nn");
		InputStream inMetadata=classLoader.getResourceAsStream(path+"/neural_network_metadata.nn");
		InputStream inProperties=classLoader.getResourceAsStream(path+"/neural_network_properties.nn");
		return NeuralNetworkData.readNetwork(inClassifier, inMetadata, inProperties);
	}
}
