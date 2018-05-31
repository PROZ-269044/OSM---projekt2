package projekt2;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

//do utworzenia okna, w ktorym jest obraz
public class ImageDemo extends JFrame 
{
		/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

		public ImageDemo(BufferedImage image, String Title) 
		{
			ImageView imageView=new ImageView();
			this.setContentPane(new JScrollPane(imageView));
			imageView.setImage(image);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			this.setTitle(Title);
			this.pack();
		}
}
