package edu.washington.maccoss.intensity_predictor.swing;

import java.util.ArrayList;

import javax.swing.table.AbstractTableModel;

import edu.washington.maccoss.intensity_predictor.structures.PeptideData;

public class PeptideDataTableModel extends AbstractTableModel {

	private final String[] columnNames= {"Accession", "Rank", "Sequence", "Score"};
	private final int[] preferredWidths= new int[] {100, 30, 100, 40};
	private final ArrayList<PeptideData> data=new ArrayList<>();
	
	public void updatePeptideData(ArrayList<PeptideData> newData) {
		data.clear();
		data.addAll(newData);
		fireTableDataChanged();
	}
	
	public int getWidth(int column) {
		return preferredWidths[column];
	}

	@Override
	public int getRowCount() {
		return data.size();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}
	
	@Override
	public String getColumnName(int column) {
		return columnNames[column];
	}
	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		switch (columnIndex) {
			case 0: return String.class;
			case 1: return Integer.class;
			case 2: return String.class;
			case 3: return Double.class;
				
			default: return null;
		}
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		PeptideData row=data.get(rowIndex);
		switch (columnIndex) {
			case 0: return row.getAccession();
			case 1: return row.getRank();
			case 2: return row.getSequence();
			case 3: return row.getScore();
				
			default: return null;
		}
	}

}
