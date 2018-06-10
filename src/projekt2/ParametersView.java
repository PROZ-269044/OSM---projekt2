package projekt2;

import javax.swing.*;

import java.awt.*;


public class ParametersView extends JPanel 
{	
		private static final long serialVersionUID = 3088520475104112398L;
		
		private JPanel formPane;
		private JLabel LowThreshold;
		private JLabel HighThreshold;
		private JLabel Sigma;
		private JLabel KernelDimension;
		private static JTextField LowThresholdField;
		private static JTextField HighThresholdField;
		private static JTextField SigmaField;
		private static JTextField KernelDimensionField;
		private JPanel buttonPane;
		private JButton saveButton;
		private JButton openButton; //
		private JButton processButton; // do uruchomienia programu przetwarzaj¹cego obraz
		
		
		public JButton getopenButton()
		{
			return this.openButton;
		}
		
		public JButton getProcessButton() //przycisk do wywo³ania procesu przetwarzania obrazu
		{
			return this.processButton;
		}

		public ParametersView()
		{
			this.setBorder(BorderFactory.createTitledBorder("Parametry dla algorytmu wykrywania krawêdzi"));
			this.setPreferredSize(new Dimension(400,180));
			
			setComponents();
		}
				
		public static long getSerialversionuid()
		{
			return serialVersionUID;
		}

		public static JTextField getLowThresholdField()
		{
			return LowThresholdField;
		}

		public static JTextField getHighThresholdField()
		{
			return HighThresholdField;
		}

		public static JTextField getSigmaField()
		{
			return SigmaField;
		}

		public static JTextField getKernelDimensionField()
		{
			return KernelDimensionField;
		}

		public JPanel getFormPane()
		{
			return this.formPane;
		}
		
		public JPanel getButtonPane()
		{
			return this.buttonPane;
		}
		
		public JButton getSaveButton()
		{
			return this.saveButton;
		}
		
		
		private void setComponents()
		{
			formPane = new JPanel();
			formPane.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			
			KernelDimension = new JLabel("Kernel:");
			c.ipadx = 50;
			c.gridx = 0;
			c.gridy = 1;
			formPane.add(KernelDimension, c);
			
			Sigma = new JLabel("Sigma:");
			c.gridx = 0;
			c.gridy = 2;
			formPane.add(Sigma, c);
			
			LowThreshold = new JLabel("Próg dolny:");
			c.gridx = 0;
			c.gridy = 3;
			formPane.add(LowThreshold, c);
			
			HighThreshold = new JLabel("Próg górny");
			c.anchor = GridBagConstraints.LINE_END;
			c.gridx = 0;
			c.gridy = 4;
			formPane.add(HighThreshold, c);
			
			KernelDimensionField = new JTextField();
			c.gridx = 1;
			c.gridy = 1;
			formPane.add(KernelDimensionField, c);
			
			SigmaField = new JTextField();
			c.gridx = 1;
			c.gridy = 2;
			formPane.add(SigmaField, c);
			
			LowThresholdField = new JTextField();
			c.gridx = 1;
			c.gridy = 3;
			formPane.add(LowThresholdField, c);
			
			HighThresholdField = new JTextField();
			c.gridx = 1;
			c.gridy = 4;
			formPane.add(HighThresholdField, c);
						
			buttonPane = new JPanel();
			buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.LINE_AXIS));
			
			saveButton = new JButton("Zapisz obraz wynikowy");
			openButton = new JButton("Otworz plik");
			processButton = new JButton("Zastosuj");
			buttonPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
			
			buttonPane.add(saveButton);
			buttonPane.add(Box.createRigidArea(new Dimension(10,0)));
			buttonPane.add(openButton);
			buttonPane.add(Box.createRigidArea(new Dimension(10,0)));
			buttonPane.add(processButton);
			
			this.setLayout(new BorderLayout());
			this.add(formPane, BorderLayout.CENTER);	
			this.add(buttonPane, BorderLayout.PAGE_END);

		}
}

