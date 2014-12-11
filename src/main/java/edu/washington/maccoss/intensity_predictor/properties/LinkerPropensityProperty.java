package edu.washington.maccoss.intensity_predictor.properties;

public class LinkerPropensityProperty extends AbstractProperty {
	/**
	 * H GEOR030101
	 * D Linker propensity from all dataset (George-Heringa, 2003)
	 * R PMID:12538906
	 * A George, R.A. and Heringa, J.
	 * T An analysis of protein domain linkers: their classification and role in 
	 *   protein folding
	 * J Protein Eng. 15, 871-879 (2003)
	 * C GEOR030106    0.938  GEOR030102    0.859  GEOR030103    0.839
	 *   GEOR030104    0.834
	 * I    A/L     R/K     N/M     D/F     C/P     Q/S     E/T     G/W     H/Y     I/V
	 *    0.964   1.143   0.944   0.916   0.778   1.047   1.051   0.835   1.014   0.922
	 *    1.085   0.944   1.032   1.119   1.299   0.947   1.017   0.895       1   0.955
	 */

	public LinkerPropensityProperty() {
		super(true);
		
		setBase(0.0);
		addProperty('A', 0.964);
		addProperty('L', 1.085);
		addProperty('R', 1.143);
		addProperty('K', 0.944);
		addProperty('N', 0.944);
		addProperty('M', 1.032);
		addProperty('D', 0.916);
		addProperty('F', 1.119);
		addProperty('C', 0.778);
		addProperty('P', 1.299);
		addProperty('Q', 1.047);
		addProperty('S', 0.947);
		addProperty('E', 1.051);
		addProperty('T', 1.017);
		addProperty('G', 0.835);
		addProperty('W', 0.895);
		addProperty('H', 1.014);
		addProperty('Y', 1);
		addProperty('I', 0.922);
		addProperty('V', 0.955);	
	}
}
