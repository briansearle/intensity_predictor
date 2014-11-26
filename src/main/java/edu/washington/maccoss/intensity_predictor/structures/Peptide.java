package edu.washington.maccoss.intensity_predictor.structures;

import java.util.Map;

import org.biojava3.aaproperties.PeptideProperties;


public class Peptide extends AbstractPeptide {
	protected static char[] aas="HCKPWAILN".toCharArray();
	
	public Peptide(String sequence, float intensity, Protein protein) {
		super(sequence, intensity, protein);
	}

	@Override
	public double[] getScoreArray() {
		double ab=PeptideProperties.getAbsorbance(sequence, true);
		//double ai=PeptideProperties.getApliphaticIndex(sequence); // really spelt aliphatic
		//double ah=PeptideProperties.getAvgHydropathy(sequence);
		double ec=PeptideProperties.getExtinctionCoefficient(sequence, true);
		//double ii=PeptideProperties.getInstabilityIndex(sequence);
		//double ip=PeptideProperties.getIsoelectricPoint(sequence);
		double mw=PeptideProperties.getMolecularWeight(sequence);
		double nc=PeptideProperties.getNetCharge(sequence);
		double[] properties=new double[] {ab, ec,  mw, nc};
		
		Map<Character, Double> map=PeptideProperties.getAACompositionChar(sequence);
		double[] aaArray=new double[aas.length];
		for (int i=0; i<aaArray.length; i++) {
			Double value=map.get(aas[i]);
			if (value==null) {
				aaArray[i]=0.0;
			} else {
				aaArray[i]=value;
			}
		}
		
		double[] scores=new double[properties.length+aaArray.length];
		System.arraycopy(properties, 0, scores, 0, properties.length);
		System.arraycopy(aaArray, 0, scores, properties.length, aaArray.length);
		
		return scores;
	}
}
