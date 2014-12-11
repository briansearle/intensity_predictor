package edu.washington.maccoss.intensity_predictor.properties;

public class FrequencyOfAlphaHelixProperty extends AbstractProperty {
	/**
	 * H PALJ810107
	 * D Normalized frequency of alpha-helix in all-alpha class (Palau et al., 1981)
	 * R LIT:0805095 PMID:7118409
	 * A Palau, J., Argos, P. and Puigdomenech, P.
	 * T Protein secondary structure
	 * J Int. J. Peptide Protein Res. 19, 394-401 (1981) LG :a set of protein samples 
	 *   formed by 44 proteins. CF :a set of protein samples formed by 33 proteins.
	 * C GEIM800102    0.919  GEIM800109   -0.909
	 * I    A/L     R/K     N/M     D/F     C/P     Q/S     E/T     G/W     H/Y     I/V
	 *     1.08    0.93    1.05    0.86    1.22    0.95    1.09    0.85    1.02    0.98
	 *     1.04    1.01    1.11    0.96    0.91    0.95    1.15    1.17    0.80    1.03
	 */
	public FrequencyOfAlphaHelixProperty() {
		super(true);
		
		setBase(0.0);
		addProperty('A', 1.08);
		addProperty('L', 1.04);
		addProperty('R', 0.93);
		addProperty('K', 1.01);
		addProperty('N', 1.05);
		addProperty('M', 1.11);
		addProperty('D', 0.86);
		addProperty('F', 0.96);
		addProperty('C', 1.22);
		addProperty('P', 0.91);
		addProperty('Q', 0.95);
		addProperty('S', 0.95);
		addProperty('E', 1.09);
		addProperty('T', 1.15);
		addProperty('G', 0.85);
		addProperty('W', 1.17);
		addProperty('H', 1.02);
		addProperty('Y', 0.80);
		addProperty('I', 0.98);
		addProperty('V', 1.03);	
	}
}
