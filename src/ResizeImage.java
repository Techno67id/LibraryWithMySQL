import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

/*
 * @author mkyong
 *
 */
public class ResizeImage {

	private static final int IMG_WIDTH = 700;//2*350, width size on screen
	private static final int IMG_HEIGHT = 900;//2*450, Height size on screen
	private BufferedImage bufferedImage=null;

	public ResizeImage(String loadFile) {
		
		BufferedImage originalImage=null;
		try {
			originalImage = ImageIO.read(new File(loadFile));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();

		double scaleX = (double) IMG_WIDTH / originalImage.getWidth();
		double scaleY = (double) IMG_HEIGHT / originalImage.getHeight();
		double scaleFactor = Math.min(scaleX, scaleY);

		BufferedImage resizedImage = new BufferedImage((int) (originalImage.getWidth() * scaleFactor),
				(int) (originalImage.getHeight() * scaleFactor), type);

		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, (int) (originalImage.getWidth() * scaleFactor),
				(int) (originalImage.getHeight() * scaleFactor), null);
		g.dispose();

		bufferedImage=resizedImage;
	}
	
	public BufferedImage getBufferedImage()
	{
		return bufferedImage;
		
	}

}