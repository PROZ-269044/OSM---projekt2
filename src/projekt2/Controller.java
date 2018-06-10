package projekt2;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


import javax.swing.BorderFactory;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.border.Border;

public class Controller implements ActionListener
{
	private ParametersView controlView;
	private FileChooserView FileWalker;
	
	
	public Controller(ParametersView Control,  FileChooserView FileShow)
	{
		this.controlView = Control;
		this.FileWalker = FileShow;
		
		controlView.getSaveButton().addActionListener(this);
		controlView.getopenButton().addActionListener(this);
		controlView.getProcessButton().addActionListener(this);
	};
	
	
	public void enableInputMode()
	{
		controlView.getKernelDimensionField().setEditable(true);
		controlView.getSigmaField().setEditable(true);
		controlView.getHighThresholdField().setEditable(true);
		controlView.getLowThresholdField().setEditable(true);
		controlView.getProcessButton().setEnabled(true);
		controlView.getSaveButton().setEnabled(true);
		controlView.getopenButton().setEnabled(true);
		
		/*if(selected !=  null)
		{
			examinationView.getAiatField().setEditable(true);
			examinationView.getCalendar().setEnabled(true);
			examinationView.getAspatField().setEditable(true);
			examinationView.getGgtField().setEditable(true);
			examinationView.getSaveButton().setEnabled(true);
			examinationView.getCancelButton().setEnabled(true);
		}*/
	}

	public void disableInputMode()
	{
		controlView.getKernelDimensionField().setEditable(false);
		controlView.getSigmaField().setEditable(false);
		controlView.getHighThresholdField().setEditable(false);
		controlView.getLowThresholdField().setEditable(false);
		controlView.getProcessButton().setEnabled(false);
		controlView.getSaveButton().setEnabled(false);
		controlView.getopenButton().setEnabled(false);
	}

	public void clearParametersView()
	{
		controlView.getKernelDimensionField().setText("");
		controlView.getSigmaField().setText("");
		controlView.getHighThresholdField().setText("");
		controlView.getLowThresholdField().setText("");
	}

	public boolean checkData()
	{		
		boolean DataChecked = true;
		
		float sigma = (float) Double.parseDouble(ParametersView.getSigmaField().getText().toString());
		Integer Hthreshold = Integer.parseInt(ParametersView.getHighThresholdField().getText().toString());
		Integer Lthreshold = Integer.parseInt(ParametersView.getLowThresholdField().getText().toString());
		Integer kernelSize = Integer.parseInt(ParametersView.getKernelDimensionField().getText().toString());

		if (Hthreshold < 0)
		{
			JOptionPane.showMessageDialog(null,
				    "Wartoœæ progu wysokiego mniejsza od 0",
				    "Uwaga",
				    JOptionPane.ERROR_MESSAGE);
					DataChecked = false;
		}
			
		else if(Hthreshold > 255)
		{
			JOptionPane.showMessageDialog(null,
				    "Wartoœæ progu wysokiego wiêksza od 255",
				    "Uwaga",
				    JOptionPane.ERROR_MESSAGE);
					DataChecked = false;
		}
			
		
		if (Lthreshold < 0)
		{
			JOptionPane.showMessageDialog(null,
				    "Wartoœæ progu niskiego mniejsza od 0",
				    "Uwaga",
				    JOptionPane.ERROR_MESSAGE);
					DataChecked = false;
		}
			
		else if(Lthreshold > 255)
		{
			JOptionPane.showMessageDialog(null,
				    "Wartoœæ progu wysokiego wiêksza od 255",
				    "Uwaga",
				    JOptionPane.ERROR_MESSAGE);
					DataChecked = false;
			
		}
			
		if (kernelSize < 0)
		{
			JOptionPane.showMessageDialog(null,
				    "Wartoœæ zarodka wielkoœci maski filtru mniejsza od 0",
				    "Uwaga",
				    JOptionPane.ERROR_MESSAGE);
					DataChecked = false;
		}
		
		if (sigma < 0)
		{
			JOptionPane.showMessageDialog(null,
				    "Sigma mniejsza od 0",
				    "Uwaga",
				    JOptionPane.ERROR_MESSAGE);
					DataChecked = false;
		}
			
		
		if (Lthreshold >= Hthreshold)
		{
			JOptionPane.showMessageDialog(null,
				    "Wartoœæ progu niskiego wiêksza od wartoœci progu wysokiego",
				    "Uwaga",
				    JOptionPane.WARNING_MESSAGE);
		}
			
		if(DataChecked == true)
		{
			run.setSigma(sigma);
			run.setHThreshold(Hthreshold);
			run.setLThreshold(Lthreshold);
			run.setKernelSeedSize(kernelSize);
		}
		
		return DataChecked;
	
	};
	/*
	 * Obsluga przyciskw 
	 */
	@Override
	public void actionPerformed(ActionEvent e)
	{
		// przycisk do zapisu obrazu po wykonaniu akcji
		if(e.getSource() == controlView.getSaveButton())
		{
			FileWalker.ShowGUI();
		} 
		// przycisk do uruchomienia operacji przetwarzania obrazu
		else if(e.getSource() == controlView.getProcessButton())
		{
			if(checkData() == true)
			{
				JOptionPane.showMessageDialog(null,
					    "Dane poprawne",
					    "Kontynuuje",
					    JOptionPane.WARNING_MESSAGE);
				run.process();
			}
			//tu przeprowadzenie procesu transformacji obrazu;
		}
		else if(e.getSource() == controlView.getopenButton())
		{
			FileWalker.ShowGUI();
		}
	}

}
