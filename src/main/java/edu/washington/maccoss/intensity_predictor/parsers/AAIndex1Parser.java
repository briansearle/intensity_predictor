package edu.washington.maccoss.intensity_predictor.parsers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import edu.washington.maccoss.intensity_predictor.properties.PropertyInterface;

public class AAIndex1Parser {

	//see ftp://ftp.genome.jp/pub/db/community/aaindex/aaindex.doc
	public static ArrayList<PropertyInterface> parseAAIndex1(File f, boolean alsoUseTotalProperties) {
		ArrayList<PropertyInterface> properties=new ArrayList<>();
		BufferedReader reader=null;
		try {
			reader=new BufferedReader(new FileReader(f));
			String line=null;
			boolean primed=false;
			boolean isFirstLine=true;
			ParsedProperty averageProperty=new ParsedProperty(true);
			ParsedProperty totalProperty=new ParsedProperty(false);
			
			while ((line=reader.readLine())!=null) {
				if (line.startsWith("D ")) {
					String index=line.substring(1).trim();
					averageProperty.setName("Average "+index);
					totalProperty.setName("Total "+index);
				} else if (line.startsWith("I ")) {
					primed=true;
					isFirstLine=true;
				} else if (line.startsWith("//")) {
					properties.add(averageProperty);
					if (alsoUseTotalProperties) {
						properties.add(totalProperty);
					}
					averageProperty=new ParsedProperty(true);
					totalProperty=new ParsedProperty(false);
					
					primed=false;
				} else {
					String trim=line.trim();
					char firstChar=trim.charAt(0);
					String firstWord=trim.substring(0, Math.min(trim.length(), 3));
					if (primed&&(Character.isDigit(firstChar)||firstChar=='-'||"NA ".equals(firstWord))) {
						StringTokenizer st=new StringTokenizer(trim);
						for (int i=0; i<10; i++) {
							String valueString=st.nextToken();
							double value="NA".equals(valueString)?0.0:Double.parseDouble(valueString);
							char aa=getAA(isFirstLine, i);
							averageProperty.addProperty(aa, value);
							totalProperty.addProperty(aa, value);
						}
						isFirstLine=!isFirstLine;
					}
				}
			}

		} catch (IOException ioe) {
			ioe.printStackTrace();
		} finally {
			try {
				if (reader!=null) reader.close();
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
		return properties;
	}

	//0:A/L, 1:R/K, 2:N/M, 3:D/F, 4:C/P, 5:Q/S, 6:E/T, 7:G/W, 8:H/Y, 9:I/V
	private static char getAA(boolean isFirstLine, int i) {
		char aa='?';
		switch (i) {
			case 0: if (isFirstLine) aa='A'; else aa='L'; break;
			case 1: if (isFirstLine) aa='R'; else aa='K'; break;
			case 2: if (isFirstLine) aa='N'; else aa='M'; break;
			case 3: if (isFirstLine) aa='D'; else aa='F'; break;
			case 4: if (isFirstLine) aa='C'; else aa='P'; break;
			case 5: if (isFirstLine) aa='Q'; else aa='S'; break;
			case 6: if (isFirstLine) aa='E'; else aa='T'; break;
			case 7: if (isFirstLine) aa='G'; else aa='W'; break;
			case 8: if (isFirstLine) aa='H'; else aa='Y'; break;
			case 9: if (isFirstLine) aa='I'; else aa='V'; break;
		}
		return aa;
	}
}
