package com.borasoft.radio.log.gui;

import java.awt.*; 
import java.awt.event.*; 
import javax.swing.*;

@SuppressWarnings("serial")
public final class SimpleLog extends JFrame implements ActionListener {
	private JPanel pane=new JPanel();
	private JLabel answer=new JLabel("");
	private JButton pressme=new JButton("Press Me");
	  
	public SimpleLog() {
		super("Simple Log"); 
		setBounds(100,100,300,100);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Container container=this.getContentPane(); // inherit main frame
		
		//container.setLayout(new FlowLayout());
		pane.setLayout(new GridLayout(1, 2, 5, 5));
		//pane.setLayout(new BoxLayout(pane,BoxLayout.X_AXIS));
		
		container.add(pane); // add the panel to frame
		
		// customize panel here
		// pane.add(someWidget);
		pressme.setMnemonic('P'); // associate hotkey
	    pressme.addActionListener(this); // register button listener
	    //pane.add(answer); pane.add(pressme); pressme.requestFocus();
	    
	    JLabel labelCall=new JLabel("Call");
	    JTextField textFieldCall = new JTextField();
	    pane.add(labelCall);
	    //Box.createHorizontalGlue();
	    pane.add(textFieldCall);
		
		setVisible(true); // display this frame
	}
	
	public void actionPerformed(ActionEvent event) {
		Object source=event.getSource();
		if (source==pressme) {
			answer.setText("Button pressed!");
			JOptionPane.showMessageDialog(null,"I hear you!","Message Dialog",JOptionPane.PLAIN_MESSAGE);
			setVisible(true);  // show something
		}
	}
	  
	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) { 
			// do nothing
		}		
		new SimpleLog();
	}

}
