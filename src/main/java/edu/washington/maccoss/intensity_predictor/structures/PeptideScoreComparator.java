package edu.washington.maccoss.intensity_predictor.structures;

import java.util.Comparator;

public class PeptideScoreComparator implements Comparator<AbstractPeptide> {
	@Override
	public int compare(AbstractPeptide o1, AbstractPeptide o2) {
		if (o1==null&&o2==null) return 0;
		if (o1==null) return -1;
		if (o2==null) return 1;
		
		return Double.compare(o1.getScoreArray()[0], o2.getScoreArray()[0]);
	}
}
