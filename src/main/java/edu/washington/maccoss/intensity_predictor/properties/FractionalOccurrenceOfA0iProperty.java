package edu.washington.maccoss.intensity_predictor.properties;

public class FractionalOccurrenceOfA0iProperty extends AbstractProperty {
	/**
	 * H RACS820101
	 * D Average relative fractional occurrence in A0(i) (Rackovsky-Scheraga, 1982)
	 * R LIT:0903736
	 * A Rackovsky, S. and Scheraga, H.A.
	 * T Differential geometry and polymer conformation. 4. Conformational and 
	 *   nucleation properties of individual amino acids
	 * J Macromolecules 15, 1340-1346 (1982)
	 * C 
	 * I    A/L     R/K     N/M     D/F     C/P     Q/S     E/T     G/W     H/Y     I/V
	 *     0.85    2.02    0.88    1.50    0.90    1.71    1.79    1.54    1.59    0.67
	 *     1.03    0.88    1.17    0.85    1.47    1.50    1.96    0.83    1.34    0.89
	 */

	public FractionalOccurrenceOfA0iProperty() {
		super(true);
		
		setBase(0.0);
		addProperty('A', 0.85);
		addProperty('L', 1.03);
		addProperty('R', 2.02);
		addProperty('K', 0.88);
		addProperty('N', 0.88);
		addProperty('M', 1.17);
		addProperty('D', 1.50);
		addProperty('F', 0.85);
		addProperty('C', 0.90);
		addProperty('P', 1.47);
		addProperty('Q', 1.71);
		addProperty('S', 1.50);
		addProperty('E', 1.79);
		addProperty('T', 1.96);
		addProperty('G', 1.54);
		addProperty('W', 0.83);
		addProperty('H', 1.59);
		addProperty('Y', 1.34);
		addProperty('I', 0.67);
		addProperty('V', 0.89);	
	}
}
