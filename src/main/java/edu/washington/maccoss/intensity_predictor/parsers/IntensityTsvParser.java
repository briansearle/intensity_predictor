package edu.washington.maccoss.intensity_predictor.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import edu.washington.maccoss.intensity_predictor.structures.Protein;

public class IntensityTsvParser {
	public static ArrayList<Protein> parseTSV(File tsv) {
		HashMap<String, Protein> map=new HashMap<String, Protein>();
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(tsv));
			String line=null;
			while ((line=reader.readLine())!=null) {
				if (line.startsWith("#")) continue; // comment
				StringTokenizer st=new StringTokenizer(line);
				String accession=st.nextToken();
				String sequence=st.nextToken();
				String intensityString=st.nextToken();
				String chargeString=st.nextToken();
				float intensity=Float.parseFloat(intensityString);
				byte charge=Byte.parseByte(chargeString);

				Protein protein=map.get(accession);
				if (protein==null) {
					protein=new Protein(accession);
					map.put(accession, protein);
				}
				protein.addPeptide(sequence, intensity, charge);
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
