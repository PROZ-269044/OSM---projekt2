
package projekt2;

import java.awt.*;
import java.awt.image.*;
import javax.swing.*;

public class ImageView extends JPanel
{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private BufferedImage myImage = null;
	
	public ImageView()
	{
		this.setPreferredSize(new Dimension(500,500));
	}
		
	// ustawienie obrazu zgodnie z jego wymiarami i wymuszenie odrysowania
	public void setImage(BufferedImage image)
	{
		if (image != null)
		{
			this.myImage=image;
			this.setPreferredSize(new Dimension(image.getWidth(),image.getHeight()));
			this.repaint();
		}
		else
		{
			System.out.println("nie ma obrazu do wyœwietlenia");
		}
	}
	
	@Override
	public void paintComponent(Graphics g)
	{
		Graphics2D g2d=(Graphics2D)g;
		super.paintComponent(g2d);
		if (this.myImage != null)
		{
			g2d.drawImage(this.myImage,null,0,0);
		}
			
	}
	
}
