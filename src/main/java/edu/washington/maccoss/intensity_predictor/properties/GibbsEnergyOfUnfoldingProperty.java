package edu.washington.maccoss.intensity_predictor.properties;

public class GibbsEnergyOfUnfoldingProperty extends AbstractProperty {
	/**
	 * H YUTK870103
	 * D Activation Gibbs energy of unfolding, pH7.0 (Yutani et al., 1987)
	 * R LIT:2004127b PMID:3299367
	 * A Yutani, K., Ogasahara, K., Tsujita, T. and Sugino, Y.
	 * T Dependence of conformational stability on hydrophobicity of the amino acid 
	 *   residue in a series of variant proteins substituted at a unique position of 
	 *   tryptophan synthase alpha subunit
	 * J Proc. Natl. Acad. Sci. USA 84, 4441-4444 (1987) (Arg missing)
	 * C YUTK870104    0.997  EISD860102   -0.839
	 * I    A/L     R/K     N/M     D/F     C/P     Q/S     E/T     G/W     H/Y     I/V
	 *    18.08      0.   17.47   17.36   18.17   17.93   18.16   18.24   18.49   18.62
	 *    18.60   17.96   18.11   17.30   18.16   17.57   17.54   17.19   17.99   18.30
	 */

	public GibbsEnergyOfUnfoldingProperty() {
		super(true);
		
		setBase(0.0);
		addProperty('A', 18.08);
		addProperty('L', 18.60);
		addProperty('R', 0);
		addProperty('K', 17.96);
		addProperty('N', 17.47);
		addProperty('M', 18.11);
		addProperty('D', 17.36);
		addProperty('F', 17.30);
		addProperty('C', 18.17);
		addProperty('P', 18.16);
		addProperty('Q', 17.93);
		addProperty('S', 17.57);
		addProperty('E', 18.16);
		addProperty('T', 17.54);
		addProperty('G', 18.24);
		addProperty('W', 17.19);
		addProperty('H', 18.49);
		addProperty('Y', 17.99);
		addProperty('I', 18.62);
		addProperty('V', 18.30);	
	}
}
