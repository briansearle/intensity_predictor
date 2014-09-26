package edu.washington.maccoss.intensity_predictor.parsers;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import junit.framework.TestCase;
import edu.washington.maccoss.intensity_predictor.structures.Protein;

public class IntensityTsvParserTest extends TestCase {
	public void testParsing() throws Exception {
		URI uri=ClassLoader.getSystemResource("jim_intensities.xls").toURI();
		File f=new File(uri);
		ArrayList<Protein> proteins=IntensityTsvParser.parseTSV(f);
		assertEquals(19, proteins.size());
	}
}
