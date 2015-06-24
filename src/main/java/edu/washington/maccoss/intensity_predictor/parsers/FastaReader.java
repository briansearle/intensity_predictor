package edu.washington.maccoss.intensity_predictor.parsers;

import gnu.trove.map.hash.TIntIntHashMap;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class FastaReader {
	public static void main(String[] args) {
		File f=new File("/Users/searleb/Documents/school/maccoss rotation/uniprot_sprot.fasta");
		TIntIntHashMap map=new TIntIntHashMap();
		for (FastaEntry entry : readFasta(f)) {
			entry.addStatistics(map);
		}

		for (char c : "ACDEFGHIKLMNPQRSTVWY".toCharArray()) {
			System.out.println(c+"\t"+map.get(c));
		}
	}
	public static ArrayList<FastaEntry> readFasta(File f) {
		BufferedReader in=null;
		ArrayList<FastaEntry> entryList=new ArrayList<FastaEntry>();
		try {
			in=new BufferedReader(new FileReader(f));
			return readFasta(in, f.getName());

		} catch (IOException ioe) {
			System.out.println("I/O Error found reading FASTA ["+f.getAbsolutePath()+"]");
			ioe.printStackTrace();
			return entryList;
		} finally {
			if (in!=null) {
				try {
					in.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
	
	public static ArrayList<FastaEntry> readFasta(String s, String fileName) {
		return readFasta(new BufferedReader(new InputStreamReader(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)))), fileName);
	}
	
	public static ArrayList<FastaEntry> readFasta(BufferedReader in, String fileName) {
		ArrayList<FastaEntry> entryList=new ArrayList<FastaEntry>();
		try {
			String eachline;
			String annotation=null;
			StringBuilder sequence=new StringBuilder();
			while ((eachline=in.readLine())!=null) {
				if (eachline.trim().length()==0) {
					continue;
				}
				if (eachline.startsWith(">")) {
					if (annotation!=null) {
						entryList.add(new FastaEntry(fileName, annotation, sequence.toString()));
					}
					annotation=eachline;
					sequence.setLength(0);
				} else {
					sequence.append(eachline);
				}
			}
			if (annotation!=null) {
				entryList.add(new FastaEntry(fileName, annotation, sequence.toString()));
			}
			return entryList;

		} catch (IOException ioe) {
			System.out.println("I/O Error found reading FASTA ["+fileName+"]");
			ioe.printStackTrace();
			return entryList;
		} finally {
			if (in!=null) {
				try {
					in.close();
				} catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
		}
	}
}
