package edu.washington.maccoss.intensity_predictor.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import edu.washington.maccoss.intensity_predictor.Prego;
import edu.washington.maccoss.intensity_predictor.structures.PeptideData;

public class PregoPanel extends JPanel {
	static private final String albumin=">ALBU_HUMAN Serum albumin OS=Homo sapiens GN=ALB PE=1 SV=2\n"+"MKWVTFISLLFLFSSAYSRGVFRRDAHKSEVAHRFKDLGEENFKALVLIAFAQYLQQCPF\n"
			+"EDHVKLVNEVTEFAKTCVADESAENCDKSLHTLFGDKLCTVATLRETYGEMADCCAKQEP\n"+"ERNECFLQHKDDNPNLPRLVRPEVDVMCTAFHDNEETFLKKYLYEIARRHPYFYAPELLF\n"
			+"FAKRYKAAFTECCQAADKAACLLPKLDELRDEGKASSAKQRLKCASLQKFGERAFKAWAV\n"+"ARLSQRFPKAEFAEVSKLVTDLTKVHTECCHGDLLECADDRADLAKYICENQDSISSKLK\n"
			+"ECCEKPLLEKSHCIAEVENDEMPADLPSLAADFVESKDVCKNYAEAKDVFLGMFLYEYAR\n"+"RHPDYSVVLLLRLAKTYETTLEKCCAAADPHECYAKVFDEFKPLVEEPQNLIKQNCELFE\n"
			+"QLGEYKFQNALLVRYTKKVPQVSTPTLVEVSRNLGKVGSKCCKHPEAKRMPCAEDYLSVV\n"+"LNQLCVLHEKTPVSDRVTKCCTESLVNRRPCFSALEVDETYVPKEFNAETFTFHADICTL\n"
			+"SEKERQIKKQTALVELVKHKPKATKEQLKAVMDDFAAFVEKCCKADDKETCFAEEGKKLV\n"+"AASQAALGL";
	static private final String copy="<html><b>Using data independent acquisition to model high-responding peptides for targeted proteomics experiments.</b><br><i>Brian C. Searle, Jarrett D. Egertson, James G. Bollinger, Andrew B. Stergachis and Michael J. MacCoss<br>Published by Molecular and Cellular Proteomics on June 22, 2015, doi: 10.1074/mcp.M115.051300</i><br><br>PREGO is a software tool that predicts high responding peptides for SRM experiments. PREGO predicts peptide responses with an artificial neural network trained using 11 minimally redundant, maximally relevant properties. Crucial to its success, PREGO is trained using fragment ion intensities of equimolar synthetic peptides extracted from data independent acquisition (DIA) experiments.";

	private final PeptideDataTableModel model=new PeptideDataTableModel();
	private final SpinnerModel spinnerModel = new SpinnerNumberModel(5, 1, 999, 1);
	
	public PregoPanel() {
		super(new BorderLayout());
		setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		setBackground(Color.white);

		JPanel top=new JPanel(new BorderLayout());
		ImageIcon image=new ImageIcon(this.getClass().getClassLoader().getResource("prego_graphic.png"));
		top.add(new JLabel(image), BorderLayout.WEST);
		JEditorPane editor=new JEditorPane("text/html", copy);
		editor.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		top.add(editor, BorderLayout.CENTER);
		top.setBackground(Color.white);
		
		this.add(top, BorderLayout.NORTH);

		JSplitPane split=new JSplitPane();

		final JTextArea leftTextArea=new JTextArea(albumin, 20, 60);
		JTable table=new JTable(model);
		for (int i=0; i<table.getColumnCount(); i++) {
			table.getColumnModel().getColumn(i).setPreferredWidth(model.getWidth(i));
		}
		table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		

		JScrollPane leftScrollPane=new JScrollPane(leftTextArea);
		leftScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		leftTextArea.setEditable(true);
		leftTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
		leftTextArea.getDocument().addDocumentListener(new DocumentListener() {
			public void changedUpdate(DocumentEvent e) {
				process();
			}

			public void removeUpdate(DocumentEvent e) {
				process();
			}

			public void insertUpdate(DocumentEvent e) {
				process();
			}

			public void process() {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						processText(leftTextArea.getText());
					}
				});
			}
		});
		DefaultContextMenu contextMenu = new DefaultContextMenu();
		contextMenu.add(leftTextArea);

		JPanel leftPane=new JPanel(new BorderLayout());
		JLabel label=new JLabel("<html><center>Paste in your FASTA here:<br><small>This interface is designed for less than 100 proteins.<br>Please use the command line tools if you need to do more.", new EmptyIcon(1, 50), SwingConstants.LEFT);
		JPanel leftLabelPanel=new JPanel(new FlowLayout());
		leftLabelPanel.add(label);
		leftPane.add(leftLabelPanel, BorderLayout.NORTH);
		leftPane.add(leftScrollPane, BorderLayout.CENTER);
		split.setLeftComponent(leftPane);

		JScrollPane rightScrollPane=new JScrollPane(table);
		rightScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		JPanel rightPane=new JPanel(new BorderLayout());
		JPanel spinnerLabel=new JPanel(new FlowLayout());
		spinnerLabel.add(new JLabel("Top "));

	    JSpinner spinner = new JSpinner(spinnerModel);
	    spinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				processText(leftTextArea.getText());
			}
		});
		spinnerLabel.add(spinner);
		spinnerLabel.add(new JLabel(" peptides per protein:", new EmptyIcon(1, 50), SwingConstants.LEFT));
		
		rightPane.add(spinnerLabel, BorderLayout.NORTH);
		rightPane.add(rightScrollPane, BorderLayout.CENTER);
		split.setRightComponent(rightPane);
		split.setDividerLocation(470);

		this.add(split, BorderLayout.CENTER);
		processText(albumin);
	}

	public void processText(String text) {
		try {
			this.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
			ArrayList<PeptideData> data=Prego.processText(text, (Integer)spinnerModel.getValue());
			model.updatePeptideData(data);
		} finally {
			this.setCursor(Cursor.getDefaultCursor());
		}
	}
}
