package edu.washington.maccoss.intensity_predictor.properties;

public class PositiveChargeProperty extends AbstractProperty {
	/**
	 * H FAUJ880111
	 * D Positive charge (Fauchere et al., 1988)
	 * R LIT:1414114 PMID:3209351
	 * A Fauchere, J.L., Charton, M., Kier, L.B., Verloop, A. and Pliska, V.
	 * T Amino acid side chain parameters for correlation studies in biology and 
	 *   pharmacology
	 * J Int. J. Peptide Protein Res. 32, 269-278 (1988)
	 * C ZIMJ680104    0.813
	 * I    A/L     R/K     N/M     D/F     C/P     Q/S     E/T     G/W     H/Y     I/V
	 *       0.      1.      0.      0.      0.      0.      0.      0.      1.      0.
	 *       0.      1.      0.      0.      0.      0.      0.      0.      0.      0.
	 */
	public PositiveChargeProperty() {
		super(true);
		addProperty('R', 1);
		addProperty('K', 1);
		addProperty('H', 1);
	}
}
