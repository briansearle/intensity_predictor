package edu.washington.maccoss.intensity_predictor.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.washington.maccoss.intensity_predictor.structures.Peptide;
import edu.washington.maccoss.intensity_predictor.structures.Protein;

public class IntensityTsvParser {
	public static void main(String[] args) {
		DecimalFormat formatter = new DecimalFormat("0.00");
		try {
			URI uri=IntensityTsvParser.class.getResource("/jim_intensities.xls").toURI();
			File f=new File(uri);
			ArrayList<Protein> proteins=IntensityTsvParser.parseTSV(f);
			
			for (Protein protein : proteins) {
				System.out.println(protein.getAccessionNumber());
				float totalIntensity=protein.getSummedIntensity();
				for (Peptide peptide : protein.getPeptides()) {
					System.out.println("\t"+peptide.getSequence()+" ("+formatter.format(100.0f*peptide.getIntensity()/totalIntensity)+"%)");
				}
			}
		} catch (URISyntaxException urise) {
			urise.printStackTrace();
		}
	}

	public static ArrayList<Protein> parseTSV(File tsv) {
		HashMap<String, Protein> map=new HashMap<String, Protein>();
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(tsv));
			String line=null;
			while ((line=reader.readLine())!=null) {
				StringTokenizer st=new StringTokenizer(line);
				String accession=st.nextToken();
				String sequence=st.nextToken();
				String intensityString=st.nextToken();
				float intensity=Float.parseFloat(intensityString);

				Protein protein=map.get(accession);
				if (protein==null) {
					protein=new Protein(accession);
					map.put(accession, protein);
				}
				protein.addPeptide(sequence, intensity);
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

		return new ArrayList<Protein>(map.values());
	}
}
