package projekt2;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;



public class AppGUI extends JFrame implements ActionListener
{
 static final long serialVersionUID = -5778856184731491899L;

	public AppGUI()
	{
		//ImageProcessing ImageProcessor = new ImageProcessing();
		ParametersView parametersView = new ParametersView();
		FileChooserView FileSearcher = new FileChooserView();
		Controller AppController = new Controller(parametersView, FileSearcher);
	//	FileChooserView FileViewer = new FileChooserView();
		
		
		//@SuppressWarnings("unused");
		//Controller AppController = new Controller(controlView);
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		c.weightx = 0.5;
		c.weighty = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		this.getContentPane().add(parametersView, c);
		
		c.gridx = 0;
		c.gridy = 1;
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridheight = 2;
		//this.getContentPane().add(patientListView, c);
		
		this.setMenu();
		
		this.setTitle("Aplikacja do wykrywania krawêdzi algorytmem Canny");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
	}

	public static void main(String[] args) 
	{
		Runnable thread = new Runnable()
		{
			@Override
			public void run()
			{
				AppGUI app = new AppGUI();
				app.setVisible(true);
		
			}
		};
		SwingUtilities.invokeLater(thread);
	}
	
	private void setMenu()
	{
		JMenuBar menuBar = new JMenuBar();
		this.setJMenuBar(menuBar);
		menuBar.setVisible(true);
		
		JMenu menu = new JMenu("Aplikacja do wykrywania krawêdzi obrazu algorytmem Canny");
		menuBar.add(menu);
		
		JMenuItem menuItem = new JMenuItem("Zamknij");
		menuItem.addActionListener(this);
		menu.add(menuItem);
	}

	@Override
	public void actionPerformed(ActionEvent e) 
	{
		if (e.getActionCommand().equals("Zamknij"))
		{
			this.dispose();
		}
	}
}

