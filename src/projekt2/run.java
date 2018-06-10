package projekt2;

import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.SwingUtilities;


public class run
{
	private static double HThreshold;
	private static double LThreshold;
	private static float Sigma;
	private static int KernelSeedSize;
	
	
	
	public static void setHThreshold(double hThreshold)
	{
		HThreshold = hThreshold;
	}

	
	
	public static void setLThreshold(double lThreshold)
	{
		LThreshold = lThreshold;
	}

	public static void setKernelSeedSize(int kernelSeedSize)
	{
		KernelSeedSize = kernelSeedSize;
	}

	public static void setSigma(float sigma)
	{
		Sigma = sigma;
	}



	public static void process()
	{
		// TODO Auto-generated method stub
		ImageProcessing Converter = new ImageProcessing();
		
		try
		{
			BufferedImage image=ImageIO.read(new File("0006.dcm"));
			   
			ImageDemo app1 = new ImageDemo(image, "Obraz przed algorytmem");
			app1.setVisible(true);
					
			Converter.setSigma(Sigma);
			Converter.setK(KernelSeedSize);
			Converter.setThresholdHigh(HThreshold);
			Converter.setThresholdLow(LThreshold);
					
			final BufferedImage image2=Converter.filter(image, image);
			ImageDemo app2 = new ImageDemo(image2, "Obraz po algorytmie");
			app2.setVisible(true);
					
		}
		catch (IOException ioe)
		{
			System.out.println("Error while reading image");
			System.exit(-1);
		}
	}
	
	public run()
	{
	}
}
