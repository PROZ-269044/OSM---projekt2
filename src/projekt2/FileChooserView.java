package projekt2;

import java.io.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.*;

public class FileChooserView extends JPanel
{
	private static JFrame window;
	JButton openFile;
	JButton saveFile;
	JFileChooser FC;
	
	
	public FileChooserView()
	{
		 	JFrame window = new JFrame("FileChooserView");
	        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        //Add content to the window.
	        
	        //Display the window.
	        window.pack();
	        window.setVisible(false);
	}
	
	 static void ShowGUI()
	 {
		    window.add(new FileChooserView());
	        //Create and set up the window.
	        window.setVisible(true);
	 }
}
