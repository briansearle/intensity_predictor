package edu.washington.maccoss.intensity_predictor.parsers;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;

import junit.framework.TestCase;
import edu.washington.maccoss.intensity_predictor.properties.PropertyInterface;

public class AAIndex1ParserTest extends TestCase {

	public void testParseAAIndex1() throws Exception {
		URI uri=ClassLoader.getSystemResource("aaindex1").toURI();
		File f=new File(uri);
		ArrayList<PropertyInterface> properties=AAIndex1Parser.parseAAIndex1(f, true);
		assertEquals(1088, properties.size());
		ParsedProperty p=(ParsedProperty)properties.get(2);
		assertEquals("Average Hydrophobicity index (Argos et al., 1982)", p.toString());
		assertEquals(1.09, p.getProperty("PEPTIDEK"), 0.01);

		p=(ParsedProperty)properties.get(3);
		assertEquals("Total Hydrophobicity index (Argos et al., 1982)", p.toString());
		assertEquals(8.72, p.getProperty("PEPTIDEK"), 0.01);

		p=(ParsedProperty)properties.get(1086);
		assertEquals("Average Hydrophobicity index (Fasman, 1989)", p.toString());
		assertEquals(0.91375, p.getProperty("PEPTIDEK"), 0.01);

		p=(ParsedProperty)properties.get(1087);
		assertEquals("Total Hydrophobicity index (Fasman, 1989)", p.toString());
		assertEquals(7.31, p.getProperty("PEPTIDEK"), 0.01);
	}

}
