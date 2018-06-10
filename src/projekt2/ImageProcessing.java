package projekt2;

import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

// do przetwarzania obrazu, bêdzie implementowaæ interfejs buffered op
public class ImageProcessing implements BufferedImageOp
{
	//dla filtracji Sobela
	int filterKernelSize = 3;
	float[] sobelXData3 = {1, 0, -1, 2, 0, -2, 1, 0, -1};
	float[] sobelYData3 = {1, 2, 1, 0, 0, 0, -1, -2, -1};
	//float[] sobelYData5 = {1, 2, 4, 2, 1, 2, 4, 8, 4, 2, 0, 0, 0, 0, 0, -2, -4, -8, -4, -2, -1, -2, -4, -2, -1};
	//float[] sobelXData5 = {1, 2, 0, -2, -1, 2, 4, 0, -4, -2, 4, 8, 0, -8, -4, 2, 4, 0, -4, -2, 1, 2, 0, -2, -1};

	
	private Kernel sobelX3 = new Kernel(filterKernelSize, filterKernelSize, sobelXData3);
	private Kernel sobelY3 = new Kernel(filterKernelSize, filterKernelSize, sobelYData3);
	
	
	
	//dla filtru gaussowsiego
	private float sigma;//parametry filtru gaussowskiego
	private int k;
	private Kernel kernelFilter;//filtr gaussowski, tworzony w metodzie gaussianFilter
	private Kernel kernelFilter2;
	
	//dla obliczania amplitud i kierunku gradientu
	private double[][] magnitudes;
	private double[][] directions;
	private int[][] isEdge;
	
	
	//do przechowywania informacji o wielkoœci obrazu
	private int height;
	private int width;
	
	// dla ustalenia progow pikseli
	final int[] tmp255 = {255};
	final int[] tmp127 = {127};
	final int[] tmp0 = {0};
	int[] tmpPixel = {0};
	
	//dla ustalenia poziomów wykrywania pikseli
	double thresholdLow;
	double thresholdHigh;
	
	
	//gettery i settery dla progów
	public double getThresholdLow() {
		return thresholdLow;
	}

	public void setThresholdLow(double thresholdLow) {
		this.thresholdLow= thresholdLow;
	}

	
	public double getThresholdHigh() {
		return thresholdHigh;
	}

	public void setThresholdHigh(double thresholdHigh) {
		this.thresholdHigh = thresholdHigh;
	}

	// gettery i settery dla parametrów filtra gaussowskiego
	public float getSigma() {
		return sigma;
	}

	public int getK() {
		return k;
	}

	public void setK(int k) {
		this.k = k;
	}

	public void setSigma(float sigma) {
		this.sigma = sigma;
	}

	//konstruktor
	ImageProcessing()
	{}

	@Override
	public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel dCM) {
		if (dCM == null)
			dCM = src.getColorModel();
			return(new BufferedImage(dCM,dCM.createCompatibleWritableRaster(src.getWidth(),src.getHeight()),
			dCM.isAlphaPremultiplied(),null));
	}

	@Override
	public BufferedImage filter(BufferedImage src, BufferedImage dest) 
	{
		BufferedImage SobelXImage;
		BufferedImage SobelYImage;
		BufferedImage transferImage;
		// TODO Auto-generated method stub
		if (dest == null)
			dest = createCompatibleDestImage(src, src.getColorModel());
		
			//przypisanie wlaœciwego kernela do filtru
			//kernelFilter = gaussianFilter(sigma, k);
			kernelFilter2 = createKernel(sigma, k);
			System.out.println("przeszlo tworzenie filtru gaussowskiego");
			
			//tworzenie splotów obrazu z filtrami
			ConvolveOp op3= new ConvolveOp(kernelFilter2);
			ConvolveOp op1= new ConvolveOp(sobelX3);
			ConvolveOp op2= new ConvolveOp(sobelY3);
						
			transferImage = op3.filter(src, null);
			//transferImage = op.filter(src, null);
			
			
			transferImage = grayscaling(transferImage);
			SobelXImage = op1.filter(transferImage, null);
			SobelYImage = op2.filter(transferImage, null);
			magnitudes = getGradientsMagnitude(SobelXImage, SobelYImage);
			directions = getGradientsDirection(SobelXImage, SobelYImage);
			
			edgeThinning(transferImage, magnitudes, directions);
			//HysteresisAndRemoving(transferImage);
			thresholdingWithHysteresis(transferImage);
		
		return transferImage;
	}
	
	//funkcja do przekszta³cenia obrazu na obraz w skali szaroœci
	public static void makeGray(BufferedImage img)
	{
	    for (int x = 0; x < img.getWidth(); ++x)
	    for (int y = 0; y < img.getHeight(); ++y)
	    {
	        int rgb = img.getRGB(x, y);
	        int r = (rgb >> 16) & 0xFF;
	        int g = (rgb >> 8) & 0xFF;
	        int b = (rgb & 0xFF);

	        int grayLevel = (r + g + b) / 3;
	        int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel; 
	        img.setRGB(x, y, gray);
	    }
	}

	@Override
	public Rectangle2D getBounds2D(BufferedImage srcPt) {
		// TODO Auto-generated method stub
		return(new Rectangle2D.Double(0, 0, srcPt.getWidth(), srcPt.getHeight()));
	}

	@Override
	public Point2D getPoint2D(Point2D srcPt, Point2D dstPt) {
		// TODO Auto-generated method stub
		if (dstPt == null)
			dstPt =new Point2D.Double();
			dstPt.setLocation(srcPt);
		
		return (dstPt);
	}

	@Override
	public RenderingHints getRenderingHints() {
		// TODO Auto-generated method stub
		return (null);
	}
	
	public Kernel createKernel(double sigma, int kernelsize)
	{
		Kernel filter;
		float[] kernel = new float[kernelsize*kernelsize];
		for(int i = 0;  i < kernelsize; i++)
		{
			double x = i - (kernelsize -1) / 2;
			for(int j = 0; j < kernelsize; j++)
			{
				double y = j - (kernelsize -1)/2;
				kernel[j + i*kernelsize] = (float) (1 / (2 * Math.PI * sigma * sigma) * Math.exp(-(x*x + y*y) / (2 * sigma *sigma)));
			}
		}
		float sum = 0;
		
		//wyliczanie sumy wspó³czynników do normalizacji
		for(int i = 0; i < kernelsize; i++)
		{
			for(int j = 0; j < kernelsize; j++)
			{
				sum += kernel[j + i*kernelsize];
			}
		}
		
		//normalizacja
		for(int i = 0; i < kernelsize; i++)
		{
			for(int j = 0; j < kernelsize; j++)
			{
				kernel[j + i*kernelsize] /= sum;
			}
		}
		
		filter = new Kernel(kernelsize, kernelsize, kernel);
		 return filter;
	}
	
	/*private Kernel gaussianFilter(final float sigma, final int k)// do wrzucenia danych o masce filtru
	{
		 final int n = 2*k+1;
		 float kernel[][] = new float[n][n];
		 float[] kernelData = new float[n * n];
		 
		 int a = 0; // indeksy pomocnicze do przepisywania kernela z 2d na 1d
		 int b = 0;
		 
		 int i = 0; // do iterowania po petlach
		 int j = 0;
		 
		 Kernel filter;
		 float factor = 0; // do normalizacji maski filtru
		 
		 //budowa macierzy filtru i wyliczanie wspó³czynnika normalizacji
		 for(i = 0; i < n; i++)
		 {
			 for(j = 0; j < n; j++)
			 {
				 kernel[i][j] = (float) ((1.0/(2*Math.PI*sigma*sigma)*Math.exp(-((((i-(k+1))*(i-(k+1)))   +   ((j-(k+1))*(j-(k+1)))) / (2* sigma * sigma)))));   
				 factor+=kernel[i][j];
			 }
		 }
		 
		 //normalizacja maski filtru gaussowskiego - w celu zapobiegania zaciemnieniu obrazu
		 
		 //normalizacja macierzy - o ile wspó³czynnik ró¿ny od 0
		 if(factor != 0)
		 {
			 for( i = 0; i<n; i++)
			 {
				 for(j=0; j<n; j++)
				 {
					 kernel[i][j] = (kernel[i][j]/factor);
				 }
			 }
		 }
		 
		 
		 //przepisanie kernela 2D na kernela 1D
		 for(i = 0; i< n*n; i++)
		 {
			
			 kernelData[i] = kernel[a][b];
			 //zmiana indeksów przepisywanego kernela
			 if(a == (n-1))
			 {
				 a=0;
				 b=b+1;
			 }
			 else if(a!=(n-1))
			 {
				 a++; // przesuwanie kolumn
			 }
			 else if((a == (n-1)) & (b == (n-1)))
			 {
				 break;
			 }
			 
		 }
		 
		 filter = new Kernel(n, n, kernelData);
		 return filter;
	}*/
	
	//do wyznaczenia rezultatów z filtracji Sobela - dla wspó³rzêdnej x i y, na razie niewykorzystywane
	/*private BufferedImage SobelXFiltration(Kernel SobelX, BufferedImage src)
	{
		BufferedImage Result;
		ConvolveOp op1 = new ConvolveOp(SobelX);
		Result = op1.filter(src, null);
		return Result;
	}
	
	private BufferedImage SobelYFiltration(Kernel SobelY, BufferedImage src)
	{
		BufferedImage Result;
		ConvolveOp op1 = new ConvolveOp(SobelY);
		Result = op1.filter(src, null);
		return Result;
	}*/
	
	//do przekszta³cenia obrazu na skalê szaroœci
	private BufferedImage grayscaling(BufferedImage src)
	{
		ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null); 
		BufferedImage image = op.filter(src, null);
		return image;
	}
	
	//do wyci¹gniêcia Amplitudy gradientu
	private double[][] getGradientsMagnitude(BufferedImage SobelXImage, BufferedImage SobelYImage) throws IllegalArgumentException
	{
		double[][] gradients;
		int[] tmp = new int[1] ;
		
		if(SobelXImage.getWidth() != SobelYImage.getWidth())
		{
			throw new IllegalArgumentException("ró¿ne szerokoœci obrazow");
		}
		
		if(SobelXImage.getHeight() != SobelYImage.getHeight())
		{
			throw new IllegalArgumentException("ró¿ne wysokoœci obrazow");
		}
		
		gradients = new double[SobelXImage.getWidth()][SobelXImage.getHeight()];
		
		for(int i = 2; i < SobelXImage.getWidth()-2; i++)
		{
			for(int j = 2; j < SobelXImage.getHeight()-2; j++)
			{
				gradients[i][j] = Math.hypot(SobelXImage.getRaster().getPixel(i, j, tmp)[0], SobelYImage.getRaster().getPixel(i, j, tmp)[0]);
			}
		}
				
		return gradients;	
	}
	
	//do wyci¹gniêcia k¹ta gradientu
	private double[][] getGradientsDirection(BufferedImage SobelXImage, BufferedImage SobelYImage) throws IllegalArgumentException
	{
		double[][] gradients;
		int[] tmp = new int[1];

		if(SobelXImage.getWidth() != SobelYImage.getWidth())
		{
			throw new IllegalArgumentException("ró¿ne wymiary obrazow");
		}
		
		if(SobelXImage.getHeight() != SobelYImage.getHeight())
		{
			throw new IllegalArgumentException("ró¿ne wymiary obrazow");
		}
		
		gradients = new double[SobelXImage.getWidth()][SobelXImage.getHeight()];
		
		for(int i = 0; i < SobelXImage.getWidth(); i++)
		{
			for(int j = 0; j < SobelXImage.getHeight(); j++)
			{
				if(SobelXImage.getRaster().getPixel(i, j, tmp)[0] != 0)
				{
					gradients[i][j] = Math.atan2((SobelYImage.getRaster().getPixel(i, j, tmp)[0]),SobelXImage.getRaster().getPixel(i, j, tmp)[0]) * 180 / Math.PI;
					if (gradients[i][j] < 0) gradients[i][j] +=180;
				}
				else
					gradients[i][j] = 90;
			}
		}
		
		return gradients;	
		
	}
	
	private void edgeThinning(BufferedImage src, double[][] magnitudes, double[][] directions)
	{
		
		isEdge = new int[src.getWidth()][src.getHeight()];
	    for (int x = 0; x < src.getWidth(); x++) 
	    {
            src.getRaster().setPixel(x, 0, new int[]{255});
            src.getRaster().setPixel(x, src.getHeight() - 1, new int[]{255});
        }
	    
        for (int y = 0; y < src.getHeight(); y++) 
        {
            src.getRaster().setPixel(0, y, new int[]{255});
            src.getRaster().setPixel(src.getWidth() - 1, y, new int[]{255});
        }
		
        for (int x = 1; x < src.getWidth()-1; x++)
        {
            for (int y = 1; y < src.getHeight()-1; y++) 
            {
            	isEdge[x][y] = 0;
            	
           
                if(directions[x][y] > 22.5 && directions[x][y] <= 67.5) // po prostej y= -x
                {
                    if (magnitudes[x][y] > magnitudes[x - 1][y - 1] && magnitudes[x][y] > magnitudes[x + 1][y + 1])
                    {
                    	setPixel(x, y, src, magnitudes[x][y]);
                    }
                        
                    else
                    {
                    	src.getRaster().setPixel(x, y, tmp0);
                    }    
                } 
                else if(directions[x][y]>67.5 && directions[x][y] <= 112.5) // po prostej x = const
                {
                    if (magnitudes[x][y] > magnitudes[x][y + 1] && magnitudes[x][y] > magnitudes[x][y - 1])
                    {
                    	 setPixel(x, y, src, magnitudes[x][y]);
                    }
                    else
                    {
                    	src.getRaster().setPixel(x, y, tmp0);
                    }        
                }
                else if(directions[x][y]>112.5 && directions[x][y] <=157.5) // po prostej y = x
                {
                    if (magnitudes[x][y] > magnitudes[x + 1][y - 1] && magnitudes[x][y] > magnitudes[x - 1][y + 1])
                    {
                    	  setPixel(x, y, src, magnitudes[x][y]);
                    }  
                    else
                    {
                    	 src.getRaster().setPixel(x, y, tmp0);
                    }
                } 
                else  // po prostej y = const
                {
                    if (magnitudes[x][y] > magnitudes[x + 1][y] && magnitudes[x][y] > magnitudes[x - 1][y])
                    {
                    	setPixel(x, y, src, magnitudes[x][y]);
                    }
                    else
                    {
                    	 src.getRaster().setPixel(x, y, tmp0);
                    } 
                } 
            }
        }
	}

	private void setPixel(int x, int y, BufferedImage src, double d)
	{
		 if (d > thresholdHigh)
		 {
			 src.getRaster().setPixel(x, y, tmp255);
			 isEdge[x][y] = 2;
		 }
		 else 
		 {
			 src.getRaster().setPixel(x, y, tmp0);
		 }
	}	
	
	private void thresholdingWithHysteresis(BufferedImage src)
	{
		Boolean imageChanged = true;
		int i = 0;
		while(imageChanged)
		{
			i++;
			System.out.println(i);
			imageChanged = false;
			
			for (int x = 2; x < src.getWidth()-2; x++)
			{
				 for (int y = 2; y < src.getHeight()-2; y++)
				 {
					 if(isEdge[x][y] == 2) 
					 {
						 src.getRaster().setPixel(x, y, tmp127);
						 isEdge[x][y]=1;
					 }
					 
					 if (directions[x][y] > 112.5  && directions[x][y] <= 157.5) //y = -x
					 {
						 if(magnitudes[x-1][y-1] >= thresholdLow && 
							isEdge[x-1][y-1] != 1 &&
							directions[x-1][y-1] > 112.5 &&
							directions[x-1][y-1] <= 157.5 &&
							magnitudes[x-1][y-1] > magnitudes[x][y-2] &&
						 	magnitudes[x-1][y-1] > magnitudes[x-2][y])
						 	{
							 	src.getRaster().setPixel(x-1, y-1, tmp255);
							 	isEdge[x-1][y-1] = 2;
							 	imageChanged = true;
						 	}
						 
						 if(magnitudes[x+1][y+1] >= thresholdLow && 
							isEdge[x+1][y+1] != 1 &&
							directions[x+1][y+1] > 112.5 &&
							directions[x+1][y+1] <= 157.5 &&
							magnitudes[x+1][y+1] > magnitudes[x][y+2] &&
							magnitudes[x+1][y+1] > magnitudes[x+2][y])
							{
								src.getRaster().setPixel(x+1, y+1, tmp255);
								isEdge[x+1][y+1] = 2;
								imageChanged = true;
							}
					}
					 
					 else if (directions[x][y] > 22.5 && directions[x][y] <= 67.5) //y = x
					 {
						 if(magnitudes[x+1][y-1] >= thresholdLow && 
							isEdge[x+1][y-1] != 1 &&
							directions[x+1][y-1] > 22.5 &&
							directions[x+1][y-1] >= 67.5 &&
							magnitudes[x+1][y-1] > magnitudes[x][y-2] &&
						 	magnitudes[x+1][y-1] > magnitudes[x+2][y])
						 	{
							 	src.getRaster().setPixel(x+1, y-1, tmp255);
							 	isEdge[x+1][y-1] = 2;
							 	imageChanged = true;
						 	}
						 
						 if(magnitudes[x-1][y+1] >= thresholdLow && 
							isEdge[x-1][y+1] != 1 &&
							directions[x-1][y+1] > 22.5 &&
							directions[x-1][y+1] <= 67.5 &&
							magnitudes[x-1][y+1] > magnitudes[x][y+2] &&
							magnitudes[x-1][y+1] > magnitudes[x-2][y])
							{
								src.getRaster().setPixel(x-1, y+1, tmp255);
								isEdge[x-1][y+1] = 2;
								imageChanged = true;
							}
					}
					 
					 else if (directions[x][y] > 67.5 && directions[x][y] <= 112.5) // po prostej y = const
					 {
						 if(magnitudes[x-1][y] >= thresholdLow && 
							isEdge[x-1][y] != 1 &&
							directions[x-1][y] > 67.5 &&
							directions[x-1][y] <= 112.5 &&
							magnitudes[x-1][y] > magnitudes[x-1][y-1] &&
						 	magnitudes[x-1][y] > magnitudes[x-1][y+1])
						 	{
							 	src.getRaster().setPixel(x-1, y, tmp255);
							 	isEdge[x-1][y] = 2;
							 	imageChanged = true;
						 	}
						 
						 if(magnitudes[x+1][y] >= thresholdLow && 
							isEdge[x+1][y] != 1 &&
							directions[x+1][y] > 67.5 &&
							directions[x+1][y] <= 112.5 &&
							magnitudes[x+1][y] > magnitudes[x+1][y-1] &&
							magnitudes[x+1][y] > magnitudes[x+1][y+1])
							{
								src.getRaster().setPixel(x+1, y, tmp255);
								isEdge[x+1][y] = 2;
								imageChanged = true;
							}
					}
					 
					 else
					 {
						 if(magnitudes[x][y-1] >= thresholdLow && 
							isEdge[x][y-1] != 1 &&
							directions[x][y-1] < 22.5 &&
							directions[x][y-1] >= 157.5 &&
							magnitudes[x][y-1] > magnitudes[x-1][y-1] &&
						 	magnitudes[x][y-1] > magnitudes[x+2][y-1])
						 	{
							 	src.getRaster().setPixel(x, y-1, tmp255);
							 	isEdge[x][y-1] = 2;
							 	imageChanged = true;
						 	}
						 
						 if(magnitudes[x][y+1] >= thresholdLow && 
							isEdge[x][y+1] != 1 &&
							directions[x][y+1] < 22.5 &&
							directions[x][y+1] >= 157.5 &&
							magnitudes[x][y+1] > magnitudes[x-1][y+1] &&
							magnitudes[x][y+1] > magnitudes[x+1][y+1])
							{
								src.getRaster().setPixel(x, y+1, tmp255);
								isEdge[x][y+1] = 2;
								imageChanged = true;
							}
					}
				 }
			}
			
		}
		for(int n = 2; n<src.getWidth()-2; n++)
		{
			for (int m = 2; m<src.getHeight()-2; m++)
			{
				 if(isEdge[n][m] == 1) 
				 {
					 src.getRaster().setPixel(n, m, tmp255);
					 isEdge[n][m]=2;
				 }
			}
		}
	}
	
	
	/*private void trackWeakOnes(int x, int y, BufferedImage src)
	{
		for (int i = x-1; i <= x+1; i++)
		{
			for (int j = y-1; j <= y+1; j++)
			{
	            if (isWeak(i, j, src))
	            {
	                src.getRaster().setPixel(i, j, tmp0);
	                trackWeakOnes(i, j, src);
	            }
			}
		}          
	 }*/
	 
	/* private boolean isWeak(int x, int y, BufferedImage src) 
	 {
	      return (src.getRaster().getPixel(x, y, tmpPixel)[0] > 0 && src.getRaster().getPixel(x, y, tmpPixel)[0] < 255);
     }*/
	 
	 /*private void HysteresisAndRemoving(BufferedImage src)
	 {
		 int[] tmp = {0};
	     for (int x = 1; x < src.getWidth() - 1; x++)
	     {
             for (int y = 1; y < src.getHeight() - 1; y++)
             {
            	 if (src.getRaster().getPixel(x, y, tmp)[0] < 50)
	             {
	                // It's a strong pixel, lets find the neighbouring weak ones.
	                trackWeakOnes(x, y, src);
	             }
	         }
	     }
	        // removing the single weak pixels.
	     for (int x = 2; x < src.getWidth() - 2; x++)
         {
	    	 for (int y = 2; y < src.getHeight() - 2; y++)
	         {
	             if (src.getRaster().getPixel(x, y, tmp)[0] > 50)
	             {
                    src.getRaster().setPixel(x, y, tmp255);
                 }
	         }
	     }
	 }*/
	 
}


