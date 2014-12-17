package edu.washington.maccoss.intensity_predictor.structures;

import edu.washington.maccoss.intensity_predictor.properties.C1Property;
import edu.washington.maccoss.intensity_predictor.properties.FractionalOccurrenceOfA0iProperty;
import edu.washington.maccoss.intensity_predictor.properties.FrequencyOfAlphaHelixProperty;
import edu.washington.maccoss.intensity_predictor.properties.GibbsEnergyOfUnfoldingProperty;
import edu.washington.maccoss.intensity_predictor.properties.LengthProperty;
import edu.washington.maccoss.intensity_predictor.properties.LinkerPropensityProperty;
import edu.washington.maccoss.intensity_predictor.properties.MassProperty;
import edu.washington.maccoss.intensity_predictor.properties.NumberBasicProperty;
import edu.washington.maccoss.intensity_predictor.properties.PositiveChargeProperty;
import edu.washington.maccoss.intensity_predictor.properties.PropertyInterface;
import edu.washington.maccoss.intensity_predictor.properties.SimilarityToCytoplasmicProteinsProperty;


public class Peptide extends AbstractPeptide {
	PropertyInterface[] properties=new PropertyInterface[] {
			new MassProperty(),
			new C1Property(),
			new NumberBasicProperty(),
			new LinkerPropensityProperty(),
			new GibbsEnergyOfUnfoldingProperty(),
			new SimilarityToCytoplasmicProteinsProperty(),
			new FrequencyOfAlphaHelixProperty(),
			new FractionalOccurrenceOfA0iProperty(),
			new LengthProperty(),
			new PositiveChargeProperty()
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
