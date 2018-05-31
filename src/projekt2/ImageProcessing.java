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
		BufferedImage transferImage;
		// TODO Auto-generated method stub
		if (dest == null)
			dest = createCompatibleDestImage(src, src.getColorModel());
		
		
		
			//przypisanie wlaœciwego kernela do filtru
			kernelFilter = gaussianFilter(Sigma, K);
			System.out.println("przeszlo tworzenie filtru gaussowskiego");
			ConvolveOp op= new ConvolveOp(kernelFilter);
			
			transferImage = op.filter(src, null);
			
			ConvolveOp op1 = new ConvolveOp(sobelX);
			
			dest = op1.filter(transferImage, null);
		
		return dest;
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
		 
		 Kernel Filter;
		 int licznik = 0;
		 
		 //budowa macierzy filtru
		 for(int i = 0; i < n; i++)
		 {
			 for(int j = 0; j < n; j++)
			 {
				 kernel[i][j] = (float) ((1.0/(2*Math.PI*sigma*sigma)*Math.exp(-((((i-(k+1))*(i-(k+1)))   +   ((j-(k+1))*(j-(k+1)))) / (2* sigma * sigma)))));   
				 System.out.println(kernel[i][j]);
			 }
		 }
		
		 //przepisanie kernela 2D na kernela 1D
		 System.out.println(n);
		 for(int i = 0; i< n*n; i++)
		 {
			 System.out.println(a);
			 System.out.println(b);
			 
			 
			 System.out.println("przepisuje" + licznik);
			 kernelData[i] = kernel[a][b];
			 
			 licznik++;
			 //zmiana indeksów przepisywanego kernela
			 if(a == (n-1))
			 {
				 System.out.println("wszedl do a = n-1" + a);
				 a=0;
				 b=b+1;
			 }
			 else if(a!=(n-1))
			 {
				 a++; // przesuwanie kolumn
			 }
			 else if((a == (n-1)) & (b == (n-1)))
			 {
				 System.out.println("wyszedl z petli");
				 break;
			 }
			 
		 }
		 
		 Filter = new Kernel(n, n, kernelData);
		 return Filter;
	}
	
	//do wyznaczenia rezultatów z filtracji Sobela - dla wspó³rzêdnej x i y
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
		
		if(SobelXImage.getWidth() != SobelYImage.getWidth())
		{
			throw new IllegalArgumentException("ró¿ne szerokoœci obrazow");
		}
		
		if(SobelXImage.getHeight() != SobelYImage.getHeight())
		{
			throw new IllegalArgumentException("ró¿ne szerokoœci obrazow");
		}
		
		
		
		
		
		return gradients;	
	}
	
	//do wyci¹gniêcia k¹ta gradientu
	private double[][] getGradientsDirection(BufferedImage SobelXImage, BufferedImage SobelYImage) throws IllegalArgumentException
	{
		double[][] gradients;

		if(SobelXImage.getWidth() != SobelYImage.getWidth())
		{
			throw new IllegalArgumentException("ró¿ne wymiary obrazow");
		}
		
		if(SobelXImage.getHeight() != SobelYImage.getHeight())
		{
			throw new IllegalArgumentException("ró¿ne wymiary obrazow");
		}
		
		
		
		return gradients;	
	}
	
}
