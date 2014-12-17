package edu.washington.maccoss.intensity_predictor.structures;

import edu.washington.maccoss.intensity_predictor.properties.LengthProperty;
import edu.washington.maccoss.intensity_predictor.properties.MassProperty;
import edu.washington.maccoss.intensity_predictor.properties.NumberAcidicProperty;
import edu.washington.maccoss.intensity_predictor.properties.NumberBasicProperty;
import edu.washington.maccoss.intensity_predictor.properties.PropertyInterface;


public class Peptide extends AbstractPeptide {
	PropertyInterface[] properties=new PropertyInterface[] {
			new MassProperty(),
			new NumberBasicProperty(),
			new NumberAcidicProperty(),
			new LengthProperty()
	};
	double[] scores=null;
	
	public Peptide(String sequence, float intensity, Protein protein) {
		super(sequence, intensity, protein);
	}

	@Override
	public double[] getScoreArray() {
		if (scores!=null) return scores;
		
		scores=new double[properties.length];
		for (int i=0; i<properties.length; i++) {
			scores[i]=properties[i].getProperty(sequence);
		}
		return scores;
	}
}
