package edu.washington.maccoss.intensity_predictor.properties;

public class SimilarityToCytoplasmicProteinsProperty extends AbstractProperty {
	/**
	 * H NAKH920101
	 * D AA composition of CYT of single-spanning proteins (Nakashima-Nishikawa, 1992)
	 * R LIT:1811095b PMID:1607012
	 * A Nakashima, H. and Nishikawa, K.
	 * T The amino acid composition is different between the cytoplasmic and 
	 *   extracellular sides in membrane proteins
	 * J FEBS Lett. 303, 141-146 (1992)
	 * C CEDJ970105    0.942  NAKH920106    0.929  NAKH920102    0.929
	 *   CEDJ970104    0.920  NAKH900101    0.907  JOND920101    0.900
	 *   CEDJ970102    0.898  DAYM780101    0.882  FUKS010112    0.856
	 *   NAKH900102    0.854  CEDJ970101    0.850  JUKT750101    0.849
	 *   FUKS010110    0.833  JUNJ780101    0.826  NAKH920104    0.822
	 *   NAKH920103    0.811
	 * I    A/L     R/K     N/M     D/F     C/P     Q/S     E/T     G/W     H/Y     I/V
	 *     8.63    6.75    4.18    6.24    1.03    4.76    7.82    6.80    2.70    3.48
	 *     8.44    6.25    2.14    2.73    6.28    8.53    4.43    0.80    2.54    5.44
	 */

	public SimilarityToCytoplasmicProteinsProperty() {
		super(true);
		
		setBase(0.0);
		addProperty('A', 8.63);
		addProperty('L', 8.44);
		addProperty('R', 6.75);
		addProperty('K', 6.25);
		addProperty('N', 4.18);
		addProperty('M', 2.14);
		addProperty('D', 6.24);
		addProperty('F', 2.73);
		addProperty('C', 1.03);
		addProperty('P', 6.28);
		addProperty('Q', 4.76);
		addProperty('S', 8.53);
		addProperty('E', 7.82);
		addProperty('T', 4.43);
		addProperty('G', 6.80);
		addProperty('W', 0.80);
		addProperty('H', 2.70);
		addProperty('Y', 2.54);
		addProperty('I', 3.48);
		addProperty('V', 5.44);	
	}
}
