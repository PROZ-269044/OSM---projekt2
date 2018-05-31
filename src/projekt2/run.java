package projekt2;

import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.SwingUtilities;


public class run {

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		ImageProcessing Converter = new ImageProcessing();
	   
		
		try
		{
			BufferedImage image=ImageIO.read(new File("content_LENA_G_100.jpg"));
			   
		     
		     SwingUtilities.invokeLater(new Runnable()
		     {
				public void run() 
				{
					ImageDemo app1 = new ImageDemo(image, "Obraz przed algorytmem");
					app1.setVisible(true);
					
					Converter.setSigma((float) 1.1);
					Converter.setK(2);
					
					final BufferedImage image2=Converter.filter(image, image);
					ImageDemo app2 = new ImageDemo(image2, "Obraz po algorytmie");
					app2.setVisible(true);
					
					
				}
		    });
		}
		catch (IOException ioe)
		{
			System.out.println("Error while reading image");
			System.exit(-1);
		}
	}
}
