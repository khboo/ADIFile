package com.borasoft.radio.log.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.borasoft.radio.log.adif.utils.ADIFQSODateFilter;

@SuppressWarnings("serial")
public class ADIFQSODateFilterClient extends JFrame implements  ActionListener {
	
	private JPanel mainJPanel = new JPanel(new BorderLayout());
	private JPanel submainJPanel = new JPanel(new GridLayout(5,1,3,3));
	
	private File adifInputFile;
	private JTextField adifInputFilePathJTextField = new JTextField(40);
	private JButton inputFileBrowseJButton;
	
	private JTextField startingDateJTextField;
	private JTextField endingDateJTextField;
	
	private File adifOutputFile;
	private JTextField adifOutputFilePathJTextField = new JTextField(40);
	private JButton outputFileBrowseJButton;
	
	private JButton processJButton;

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// do nothing
		}
		ADIFQSODateFilterClient view = new ADIFQSODateFilterClient("ADIF QSO Filter");
	}
	
	public ADIFQSODateFilterClient(String title)// constructor method for GUI
	{
		super(title);
		setBounds(100,100,550,200);
		setResizable(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Input file
		JPanel jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		//JPanel jPanel = new JPanel(new GridLayout(1,3,2,2));
	    
		JLabel adifInputJLabel = new JLabel("ADIF Input File: ");
		adifInputJLabel.setForeground(Color.black);
		jPanel.add(adifInputJLabel);
		
		jPanel.add(adifInputFilePathJTextField);
		
		inputFileBrowseJButton = new JButton("Browse...");
		inputFileBrowseJButton.addActionListener(this);
		jPanel.add(inputFileBrowseJButton);
		submainJPanel.add(jPanel);

		// Starting date
		jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel startingDateJLabel = new JLabel("QSO Starting Date(YYYYMMDD): ");
		startingDateJLabel.setForeground(Color.black);
		jPanel.add(startingDateJLabel);	
		
		startingDateJTextField = new JTextField(8);
		jPanel.add(startingDateJTextField);
		submainJPanel.add(jPanel);
		
		// Ending date
		jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel endingDateJLabel = new JLabel("QSO Ending Date(YYYYMMDD): ");
		endingDateJLabel.setForeground(Color.black);
		jPanel.add(endingDateJLabel);	
		
		endingDateJTextField = new JTextField(8);
		jPanel.add(endingDateJTextField);
		submainJPanel.add(jPanel);
		
		// Output file
		jPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		
		JLabel adifOutputJLabel = new JLabel("ADIF Output File: ");
		adifOutputJLabel.setForeground(Color.black);
		jPanel.add(adifOutputJLabel);
		
		jPanel.add(adifOutputFilePathJTextField);		
		submainJPanel.add(jPanel);
		
		outputFileBrowseJButton = new JButton("Browse...");
		outputFileBrowseJButton.addActionListener(this);
		jPanel.add(outputFileBrowseJButton);
		submainJPanel.add(jPanel);
		
		processJButton = new JButton("Process");
		processJButton.addActionListener(this);
		submainJPanel.add(processJButton);
		
		//JButton jButton = new JButton("Browse...");
		//rightJPanel.add(jButton);
		
	    //complete the panel assembly
		mainJPanel.add("Center",submainJPanel); 
		//mainJPanel.add("East",rightJPanel);
	    this.getContentPane().add("Center",mainJPanel);
	    setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==processJButton) {
			// validate input fields
			String inputFilename = adifInputFilePathJTextField.getText();
			String outputFilename = adifOutputFilePathJTextField.getText();
			String startingDate = startingDateJTextField.getText();
			String endingDate = endingDateJTextField.getText();
			if (!inputFilename.isEmpty() 
					&& !outputFilename.isEmpty() 
					&& !startingDate.isEmpty() 
					&& !endingDate.isEmpty()) {
				ADIFQSODateFilter filter = new ADIFQSODateFilter();
				try {
					filter.processADIF(inputFilename, outputFilename, startingDate, endingDate);
				    JOptionPane.showMessageDialog
				    	(null,
				    	 "Total of " + filter.getTotalRecords() + 
				    	 " records have been processsed, and " + filter.getFilteredRecords() + 
				    	 " records were written to the new ADIF file.",
				    	 "ADIF File Filter",
				    	 JOptionPane.PLAIN_MESSAGE);
				} catch (Exception ex) {
					// TBD: pop up processing error message
					JOptionPane.showMessageDialog(null,"FAILURE: Unknown error occured during processing.","ADIF File Filter",JOptionPane.PLAIN_MESSAGE);
					ex.printStackTrace();
				}
			} else {
				// TBD: pop up input validation error dialog
				JOptionPane.showMessageDialog(null,"FAILURE: Invalid input fields.","ADIF File Filter",JOptionPane.PLAIN_MESSAGE);
			}
		} else {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setMultiSelectionEnabled(false);
			FileFilter filter=new FileNameExtensionFilter("ADIF file","adi");
			chooser.addChoosableFileFilter(filter);
			int option = chooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION)
			{
				if(e.getSource()==inputFileBrowseJButton) {
					adifInputFile = chooser.getSelectedFile();
					String filepath = adifInputFile.getAbsolutePath();
					adifInputFilePathJTextField.setText(filepath);
					adifOutputFilePathJTextField.setText(filepath);
				} else if (e.getSource()==outputFileBrowseJButton) {
					adifOutputFile = chooser.getSelectedFile();
					String filepath = adifOutputFile.getAbsolutePath();
					adifOutputFilePathJTextField.setText(filepath);
				}
			}
		}
	}

}
