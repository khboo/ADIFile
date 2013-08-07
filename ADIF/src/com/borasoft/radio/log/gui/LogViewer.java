/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

package com.borasoft.radio.log.gui;

/*
 * LogViewer.java requires SpringUtilities.java
 */

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableRowSorter;

import com.borasoft.radio.log.adif.ADIFObject;
import com.borasoft.radio.log.adif.ADIFReader;
import com.borasoft.radio.log.adif.ADIFStream;

import java.awt.Dimension;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Iterator;

import layout.SpringUtilities;

@SuppressWarnings("serial")
public class LogViewer extends JPanel implements ActionListener {
    private boolean DEBUG = false;
    private JTable table;
    private JTextField filterText;
    private JTextField statusText;
    private TableRowSorter<MyTableModel> sorter;
    
    private JMenuItem openFileMenuItem;
    private JMenuItem exitMenuItem;
    
    MyTableModel model = new MyTableModel();

    public LogViewer() {
        super();
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        //Create a table with a sorter.
        sorter = new TableRowSorter<MyTableModel>(model);
        table = new JTable(model);
        table.setRowSorter(sorter);
        table.setPreferredScrollableViewportSize(new Dimension(500, 500));
        table.setFillsViewportHeight(true);

        //For the purposes of this example, better to have a single
        //selection.
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //When selection changes, provide user with row numbers for
        //both view and model.
        table.getSelectionModel().addListSelectionListener(
                new ListSelectionListener() {
                    public void valueChanged(ListSelectionEvent event) {
                        int viewRow = table.getSelectedRow();
                        if (viewRow < 0) {
                            //Selection got filtered away.
                            statusText.setText("");
                        } else {
                            int modelRow = 
                                table.convertRowIndexToModel(viewRow);
                            statusText.setText(
                                String.format("Selected Row in view: %d. " +
                                    "Selected Row in model: %d.", 
                                    viewRow, modelRow));
                        }
                    }
                }
        );


        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);

        //Create a separate form for filterText and statusText
        JPanel form = new JPanel(new SpringLayout());
        JLabel l1 = new JLabel("Filter Text:", SwingConstants.TRAILING);
        form.add(l1);
        filterText = new JTextField();
        //Whenever filterText changes, invoke newFilter.
        filterText.getDocument().addDocumentListener(
                new DocumentListener() {
                    public void changedUpdate(DocumentEvent e) {
                        newFilter();
                    }
                    public void insertUpdate(DocumentEvent e) {
                        newFilter();
                    }
                    public void removeUpdate(DocumentEvent e) {
                        newFilter();
                    }
                });
        l1.setLabelFor(filterText);
        form.add(filterText);
        
        JLabel l2 = new JLabel("Status:", SwingConstants.TRAILING);
        form.add(l2);
        statusText = new JTextField();
        l2.setLabelFor(statusText);
        form.add(statusText);
        
        SpringUtilities.makeCompactGrid(form, 2, 2, 6, 6, 6, 6);
        add(form);
    }

    /** 
     * Update the row filter regular expression from the expression in
     * the text box.
     */
    private void newFilter() {
        RowFilter<MyTableModel, Object> rf = null;
        //If current expression doesn't parse, don't update.
        try {
            rf = RowFilter.regexFilter(filterText.getText(), 0);
        } catch (java.util.regex.PatternSyntaxException e) {
            return;
        }
        sorter.setRowFilter(rf);
    }

	class MyTableModel extends AbstractTableModel {
        private String[] columnNames = {"QSO Date",
                                        "Time On",
                                        "Call",
                                        "Name",
                                        "Band",
                                        "Freq",
                                        "Mode"};
        private Object[][] data = {};
        /*
        private Object[][] data = {
	    {"20090307", "0519",
	     "SN7Q", "3.751", new Boolean(false)},
	    {"20090308", "0518",
	     "SN7QO", "3.751", new Boolean(true)},
	    {"20090309", "0529",
	     "SN7PQ", "3.751", new Boolean(false)},
	    {"20090310", "0619",
	     "SM7Q", "3.751", new Boolean(true)},
	    {"20090311", "0419",
	     "TN7Q", "3.751", new Boolean(false)}
        };
        */
        
        public MyTableModel() {
        	super();
        }

        public void loadADIF(String inputFilename) {
        	// initialize Object[][] from an ADIF file.
        	ADIFStream adif=null;
        	try {
        		FileInputStream stream = new FileInputStream(inputFilename);
        		InputStreamReader reader = new InputStreamReader(stream);
        		ADIFReader adifReader = new ADIFReader(reader);
        		adif = adifReader.readADIFStream();
        		reader.close();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        	int entries = adif.getRecords().size();
        	data = new Object[entries][7];
        	Iterator<ADIFObject> iter = adif.getRecords().iterator();
        	ADIFObject obj;
        	int index=0;
        	String s;
        	while(iter.hasNext()) {
        		//result = someCondition ? value1 : value2;
        		obj = iter.next();
        		data[index][0] = obj.getQSODate();
        		data[index][1] = obj.getTimeOn();
        		data[index][2] = obj.getCall();
        		data[index][3] = (s=obj.getName())==null?"":s;
        		data[index][4] = (s=obj.getBand())==null?"":s;
        		data[index][5] = (s=obj.getFreq())==null?"":s;
        		data[index++][6] = obj.getMode();
        		/*
        		qslReceived = obj.getQSLReceived();
        		if(qslReceived!=null && qslReceived.equalsIgnoreCase("Y")) {
        			data[index++][4] = new Boolean(true);
        		} else {
        			data[index++][4] = new Boolean(false);
        		}
        		*/
        	}       	
        }
        
        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        /*
         * JTable uses this method to determine the default renderer/
         * editor for each cell.  If we didn't implement this method,
         * then the last column would contain text ("true"/"false"),
         * rather than a check box.
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        /*
         * Don't need to implement this method unless your table's
         * editable.
         */
        public boolean isCellEditable(int row, int col) {
            //Note that the data/cell address is constant,
            //no matter where the cell appears onscreen.
            if (col < 2) {
                return false;
            } else {
                return true;
            }
        }

        /*
         * Don't need to implement this method unless your table's
         * data can change.
         */
        public void setValueAt(Object value, int row, int col) {
            if (DEBUG) {
                System.out.println("Setting value at " + row + "," + col
                                   + " to " + value
                                   + " (an instance of "
                                   + value.getClass() + ")");
            }

            data[row][col] = value;
            fireTableCellUpdated(row, col);

            if (DEBUG) {
                System.out.println("New value of data:");
                printDebugData();
            }
        }

        private void printDebugData() {
            int numRows = getRowCount();
            int numCols = getColumnCount();

            for (int i=0; i < numRows; i++) {
                System.out.print("    row " + i + ":");
                for (int j=0; j < numCols; j++) {
                    System.out.print("  " + data[i][j]);
                }
                System.out.println();
            }
            System.out.println("--------------------------");
        }
    }
    
    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
        menuBar.add(menu);

        //a group of JMenuItems
        openFileMenuItem = new JMenuItem("Open File...",KeyEvent.VK_O);
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        openFileMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        openFileMenuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
        openFileMenuItem.addActionListener(this);
        menu.add(openFileMenuItem);
        menu.addSeparator();
        
        exitMenuItem = new JMenuItem("Exit",KeyEvent.VK_X);
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.ALT_MASK));
        exitMenuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
        exitMenuItem.addActionListener(this);
        menu.add(exitMenuItem);
        menuBar.add(menu);

        return menuBar;
    }
    
    public void actionPerformed(ActionEvent e) {
    	if(e.getSource()==openFileMenuItem) {
			JFileChooser chooser = new JFileChooser();
			chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
			chooser.setMultiSelectionEnabled(false);
			FileFilter filter=new FileNameExtensionFilter("ADIF file","adi");
			chooser.addChoosableFileFilter(filter);
			int option = chooser.showOpenDialog(this);
			if (option == JFileChooser.APPROVE_OPTION)
			{
				File adifInputFile = chooser.getSelectedFile();
				String filepath = adifInputFile.getAbsolutePath();
				model.loadADIF(filepath);
				table.revalidate();
			}
    	} else if(e.getSource()==exitMenuItem) {
    		System.exit(0);
    	}
    }

    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			// do nothing
		}
        //Create and set up the window.
        JFrame frame = new JFrame("QSO Log Viewer");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        LogViewer newContentPane = new LogViewer();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setJMenuBar(newContentPane.createMenuBar());
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
