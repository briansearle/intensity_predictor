package edu.washington.maccoss.intensity_predictor.properties;

public class C1Property extends AbstractProperty {
	/**
	 * H RICJ880114
	 * D Relative preference value at C1 (Richardson-Richardson, 1988)
	 * R LIT:1408116 PMID:3381086
	 * A Richardson, J.S. and Richardson, D.C.
	 * T Amino acid preferences for specific locations at the ends of alpha helices
	 * J Science 240, 1648-1652 (1988)
	 * C 
	 * I    A/L     R/K     N/M     D/F     C/P     Q/S     E/T     G/W     H/Y     I/V
	 *      1.1      1.     1.2     0.4     1.6     2.1     0.8     0.2     3.4     0.7
	 *      0.7      2.      1.     0.7      0.     1.7      1.      0.     1.2     0.7
	 */
	
	public C1Property() {
		super(true);
		
		setBase(0.0);
		addProperty('A', 1.1);
		addProperty('L', 0.7);
		addProperty('R', 1);
		addProperty('K', 2);
		addProperty('N', 1.2);
		addProperty('M', 1);
		addProperty('D', 0.4);
		addProperty('F', 0.7);
		addProperty('C', 1.6);
		addProperty('P', 0);
		addProperty('Q', 2.1);
		addProperty('S', 1.7);
		addProperty('E', 0.8);
		addProperty('T', 1);
		addProperty('G', 0.2);
		addProperty('W', 0);
		addProperty('H', 3.4);
		addProperty('Y', 1.2);
		addProperty('I', 0.7);
		addProperty('V', 0.7);	
	}
}
