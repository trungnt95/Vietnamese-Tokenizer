package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import tok.Tokenizer;

public class MyFrame extends JFrame implements ActionListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private Tokenizer tokenizer = null;

	private JPanel startPanel, firstPanel, secondPanel, thirdPanel, fourthPanel, fifthPanel;
	private JButton vocaBtn, patternBtn, inputBtn, outputBtn, tokenBtn;
	private JTextField vocaTf, patternTf, inputTf, outputTf;
	private JFileChooser fileChooser = new JFileChooser();
	private final int VOCABULARY_OPTION = 1, PATTERN_OPTION = 2, INPUT_OPTION = 3;

	public MyFrame() {
		addContent();
		setDisplay();
	}

	private void setDisplay() {
		setTitle("Vietnamese Word Tokenizer");
		setSize(600, 400);
		setLocationRelativeTo(null);
		pack();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setVisible(true);

	}

	private void addContent() {
		// thiet lap 5 hang va 1 cot cho grid layout
		setLayout(new GridLayout(6, 1));

		firstPanel = createJPanel(vocaTf = createJTextField(), vocaBtn = createJButton("Choose"),
				"Vocabulary File Directory:");
		secondPanel = createJPanel(patternTf = createJTextField(), patternBtn = createJButton("Choose"),
				"Regular Expressions File Directory:");
		thirdPanel = createJPanel(inputTf = createJTextField(), inputBtn = createJButton("Choose"),
				"Input File Directory:");
		fourthPanel = createJPanel(outputTf = createJTextField(), outputBtn = createJButton("Choose"),
				"Output File Directory:");
		fifthPanel = createJPanel(null, tokenBtn = createJButton("Tokenize"), null);

		startPanel = new JPanel(new GridLayout(1, 1));
		JLabel label = new JLabel("Vietnamese Word Tokenizer");
		startPanel.add(label);
		label.setHorizontalAlignment(JLabel.CENTER);
		label.setFont(new Font("Courier New", Font.BOLD, 30));
		startPanel.setBackground(Color.green.darker());

		add(startPanel);
		add(firstPanel);
		add(secondPanel);
		add(thirdPanel);
		add(fourthPanel);
		add(fifthPanel);
	}

	private JPanel createJPanel(JTextField textField, JButton btn, String labelContent) {
		JPanel panel = new JPanel(new BorderLayout());
		JPanel innerPanel = new JPanel();
		if (textField != null) {
			panel.add(new Label(labelContent), BorderLayout.NORTH);
			innerPanel.add(textField);
		}
		innerPanel.add(btn);
		panel.add(innerPanel, BorderLayout.CENTER);
		return panel;
	}

	private JTextField createJTextField() {
		JTextField textField = new JTextField(30);
		textField.setEditable(false);
		textField.setPreferredSize(new Dimension(0, 29));
		return textField;
	}

	private JButton createJButton(String title) {
		JButton btn = new JButton(title);
		btn.setPreferredSize(new Dimension(100, 30));
		btn.addActionListener(this);
		return btn;
	}

	private void openFile(int option) {
		int select = fileChooser.showOpenDialog(this);
		if (select == JFileChooser.APPROVE_OPTION) {
			if (option == VOCABULARY_OPTION) {
				vocaTf.setText(fileChooser.getCurrentDirectory().getAbsolutePath() + "\\"
						+ fileChooser.getSelectedFile().getName());
			} else if (option == PATTERN_OPTION) {
				patternTf.setText(fileChooser.getCurrentDirectory().getAbsolutePath() + "\\"
						+ fileChooser.getSelectedFile().getName());
			} else {
				inputTf.setText(fileChooser.getCurrentDirectory().getAbsolutePath() + "\\"
						+ fileChooser.getSelectedFile().getName());
			}
		}
	}

	private void saveFile() {
		int select = fileChooser.showSaveDialog(this);
		if (select == JFileChooser.APPROVE_OPTION) {
			outputTf.setText(fileChooser.getCurrentDirectory().getAbsolutePath() + "\\"
					+ fileChooser.getSelectedFile().getName());
		}
	}

	// Thực hiện chức năng tách từ
	private void tokenize() {
		if (tokenizer == null) {
			tokenizer = new Tokenizer();
		}
		SubFrame sf = new SubFrame();
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				sf.setVisible(true);
			}
		});

		sf.updateLog("START TOKENIZING");
		try {
			tokenizer.loadVocabulary(vocaTf.getText());
			tokenizer.loadPatternFile(patternTf.getText());
			tokenizer.loadInputFile(inputTf.getText());
			
			sf.updateLog("TOKENIZING...");
			tokenizer.performsTokenizingEachSentence();
			tokenizer.exportResultsToFile(outputTf.getText());

			sf.updateLog("DONE TOKENIZING!");
			sf.updateLog("OUTPUT LOCATION: " + outputTf.getText());
			
			// Open file result.
			Desktop.getDesktop().open(new File(outputTf.getText()));
		} catch (Exception e) {
			// e.printStackTrace();
			sf.updateLog("ERROR: " + e.toString());
		}

	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				new MyFrame();
			}
		});
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == vocaBtn) {
			openFile(VOCABULARY_OPTION);
			return;
		} else if (e.getSource() == patternBtn) {
			openFile(PATTERN_OPTION);
			return;
		} else if (e.getSource() == inputBtn) {
			openFile(INPUT_OPTION);
			return;
		} else if (e.getSource() == outputBtn) {
			saveFile();
			return;
		} else if (e.getSource() == tokenBtn) {
			tokenize();
			return;
		}
	}
}