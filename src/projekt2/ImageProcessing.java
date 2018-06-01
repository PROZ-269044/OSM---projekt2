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
	final int n = 3;
	float[] sobelXData = {1, 0, -1, 2, 0, -2, 1, 0, -1};
	float[] sobelYData = {1, 2, 1, 0, 0, 0, -1, -2, -1};
	private Kernel sobelX = new Kernel(n,n, sobelXData);
	private Kernel sobelY = new Kernel(n,n, sobelYData);
	
	
	//dla filtru gaussowsiego
	private float Sigma;//parametry filtru gaussowskiego
	private int K;
	private Kernel kernelFilter;//filtr gaussowski, tworzony w metodzie gaussianFilter
	
	//dla obliczania amplitud i kierunku gradientu
	private double[][] magnitudes;
	private double[][] directions;
	
	
	//do przechowywania informacji o wielkoœci obrazu
	private int Height;
	private int Width;
	
	// dla ustalenia progow pikseli
	final int[] tmp255 = {255};
	final int[] tmp127 = {127};
	final int[] tmp0 = {0};
	int[] tmpPixel = {0};
	
	//dla ustalenia poziomów wykrywania pikseli
	double ThresholdLow = 20;
	double ThresholdHigh = 100;
	
	
	//gettery i settery dla progów
	public double getThresholdLow() {
		return ThresholdLow;
	}

	public void setThresholdLow(double thresholdLow) {
		ThresholdLow = thresholdLow;
	}

	
	public double getThresholdHigh() {
		return ThresholdHigh;
	}

	public void setThresholdHigh(double thresholdHigh) {
		ThresholdHigh = thresholdHigh;
	}

	// gettery i settery dla parametrów filtra gaussowskiego
	public float getSigma() {
		return Sigma;
	}

	public int getK() {
		return K;
	}

	public void setK(int k) {
		K = k;
	}

	public void setSigma(float sigma) {
		Sigma = sigma;
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
			kernelFilter = gaussianFilter(Sigma, K);
			System.out.println("przeszlo tworzenie filtru gaussowskiego");
			
			//tworzenie splotów obrazu z filtrami
			ConvolveOp op= new ConvolveOp(kernelFilter);
			ConvolveOp op1= new ConvolveOp(sobelX);
			ConvolveOp op2= new ConvolveOp(sobelY);
			
						
			transferImage = Grayscaling(src);
			transferImage = op.filter(transferImage, null);
			
			SobelXImage = op1.filter(transferImage, null);
			SobelYImage = op2.filter(transferImage, null);
			
			magnitudes = getGradientsMagnitude(SobelXImage, SobelYImage);
			directions = getGradientsDirection(SobelXImage, SobelYImage);
			
			edgeThinning(transferImage, magnitudes, directions);
			HysteresisAndRemoving(transferImage);
			
		
		return transferImage;
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
	
	private Kernel gaussianFilter(final float sigma, final int k)// do wrzucenia danych o masce filtru
	{
		 final int n = 2*k+1;
		 float kernel[][] = new float[n][n];
		 float[] kernelData = new float[n * n];
		 
		 int a = 0; // indeksy pomocnicze do przepisywania kernela z 2d na 1d
		 int b = 0;
		 
		 int i = 0; // do iterowania po petlach
		 int j = 0;
		 
		 Kernel Filter;
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
		/* if(factor != 0)
		 {
			 for( i = 0; i<n; i++)
			 {
				 for(j=0; j<n; j++)
				 {
					 kernel[i][j] = (kernel[i][j]/factor);
				 }
			 }
		 }*/
		 
		 
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
		 
		 Filter = new Kernel(n, n, kernelData);
		 return Filter;
	}
	
	//do wyznaczenia rezultatów z filtracji Sobela - dla wspó³rzêdnej x i y, na razie niewykorzystywane
	private BufferedImage SobelXFiltration(Kernel SobelX, BufferedImage src)
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
	}
	
	//do przekszta³cenia obrazu na skalê szaroœci
	private BufferedImage Grayscaling(BufferedImage src)
	{
		BufferedImage Result;
		ColorConvertOp grayscale = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
		Result = grayscale.filter(src, null);
		return Result;
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
		
		for(int i = 0; i < SobelXImage.getWidth(); i++)
		{
			for(int j = 0; j < SobelXImage.getHeight(); j++)
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
					gradients[i][j] = Math.atan((SobelYImage.getRaster().getPixel(i, j, tmp)[0])/SobelXImage.getRaster().getPixel(i, j, tmp)[0]);
				}
				else
				{
					gradients[i][j] = Math.PI /2d;
				}
			}
		}
		
		return gradients;	
		
	}
	
	private void edgeThinning(BufferedImage src, double[][] magnitudes, double[][] directions)
	{
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
                if (directions[x][y] < (Math.PI / 8d) && directions[x][y] >= (-Math.PI / 8d)) // po prostej x = const
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
                else if (directions[x][y] < (3d * Math.PI / 8d) && directions[x][y] >= (Math.PI / 8d)) // po prostej y=x
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
                else if (directions[x][y] < (-3d * Math.PI / 8d) || directions[x][y] >= (3d * Math.PI / 8d)) // po prostej x = const
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
                else if (directions[x][y] < (-Math.PI / 8d) && directions[x][y] >= (-3d * Math.PI / 8d)) // po prostej y = -x
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
                else 
                {
                    src.getRaster().setPixel(x, y, tmp255);
                }
            }
        }
	}

	private void setPixel(int x, int y, BufferedImage src, double d)
	{
		
		 if (d > ThresholdHigh)
		 {
			 src.getRaster().setPixel(x, y, tmp0);
		 }
		 else if (d > ThresholdLow) 
		 {
			 src.getRaster().setPixel(x, y, tmp127);
		 }
		 else 
		 {
			 src.getRaster().setPixel(x, y, tmp255);
		 }
	}	
	
	private void trackWeakOnes(int x, int y, BufferedImage src)
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
	 }
	 
	 private boolean isWeak(int x, int y, BufferedImage src) 
	 {
	      return (src.getRaster().getPixel(x, y, tmpPixel)[0] > 0 && src.getRaster().getPixel(x, y, tmpPixel)[0] < 255);
     }
	 
	 private void HysteresisAndRemoving(BufferedImage src)
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
	 }
	 
}
