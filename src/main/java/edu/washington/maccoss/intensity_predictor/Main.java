package edu.washington.maccoss.intensity_predictor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;

import edu.washington.maccoss.intensity_predictor.swing.PregoPanel;

public class Main {
	public static void main(String[] args) {
		final JFrame f=new JFrame("PREGO Peptide Response Predictor");
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

		f.getContentPane().add(new PregoPanel(), BorderLayout.CENTER);

		f.pack();
		f.setSize(new Dimension(850, 800));
		f.setVisible(true);
	}

}
