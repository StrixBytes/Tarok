import java.awt.Image;
import java.awt.Rectangle;
import java.net.URL;
import java.util.HashMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;

public class ImageLoader {
	private HashMap<Integer, Image> img = new HashMap<Integer, Image>();
	private int imgIndex = -1;
	private int maxIndex = 0;
	private HashMap<Integer, Integer> x = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> y = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> width = new HashMap<Integer, Integer>();
	private HashMap<Integer, Integer> height = new HashMap<Integer, Integer>();
	private HashMap<Integer, Boolean> flag = new HashMap<Integer, Boolean>();

	public ImageLoader() {
	}

	public void loadImage(String imgName) {
//		System.out.println("Loading " + imgIndex + " "+ imgName);
		URL imageUrl = this.getClass().getResource(imgName);
		ImageIcon ii = new ImageIcon(imageUrl);
		img.put(imgIndex, ii.getImage());
		getImageDim(imgIndex);

	}
	
	public boolean skip(int i) {
		boolean temp = false;
		if (x.get(i)==null)
			temp=true;
		return temp;
	}

	private void getImageDim(int index) {
		if (index == -1)
			index = imgIndex;
		width.put(index, img.get(index).getWidth(null));
		height.put(index, img.get(index).getHeight(null));
	}

	public int getNewImageWidth(int height) {
		double ratio = (double) getWidth(-1) / (double) getHeight(-1);
		return (int) (height * ratio);
	}

	public int getNewImageHeight(int width) {
		double ratio = (double) getHeight(-1) / (double) getWidth(-1);
		return (int) (width * ratio);
	}

	public int getMaxIndex() {
		return maxIndex;
	}

	public void triggerFlag(int index) {
		flag.put(index, true);
	}

	public void resetAllFlags() {
		flag.clear();
	}

	public void resetFlag(int index) {
		flag.put(index, null);
	}

	public boolean isFlag(int index) {
		boolean isflag = false;
		if (flag.get(index) != null) {
			isflag = true;
		}
		return isflag;
	}

	public void clearBounds(int index) {
		x.put(index, 0);
		y.put(index, 0);
		width.put(index, 0);
		height.put(index, 0);
	}

	public Rectangle getBounds(int index) {
		if (index == -1)
			index = imgIndex;
		return new Rectangle(x.get(index), y.get(index), width.get(index), height.get(index));
	}

	public void setCoordinates(int index, int x, int y) {
		if (index == -1)
			index = imgIndex;
		this.x.put(index, x);
		this.y.put(index, y);
	}

	public void setDimensions(int index, int width, int height) {
		if (index == -1)
			index = imgIndex;
		this.width.put(index, width);
		this.height.put(index, height);
	}

	public int getImgIndex() {
		return imgIndex;
	}

	public void setImgIndex(int imgIndex) {
		clearBounds(imgIndex);
		if (imgIndex > maxIndex)
			maxIndex = imgIndex;
		this.imgIndex = imgIndex;
	}

	public int getX(int index) {
		if (index == -1)
			index = imgIndex;
		return x.get(index);
	}

	public void setX(int index, int x) {
		if (index == -1)
			index = imgIndex;
		this.x.put(index, x);
	}

	public int getY(int index) {
		if (index == -1)
			index = imgIndex;
		return y.get(index);
	}

	public void setY(int index, int y) {
		if (index == -1)
			index = imgIndex;
		this.y.put(index, y);
	}

	public void setWidth(int index, int width) {
		if (index == -1)
			index = imgIndex;
		this.width.put(index, width);
	}

	public void setHeight(int index, int height) {
		if (index == -1)
			index = imgIndex;
		this.height.put(index, height);
	}

	public int getWidth(int index) {
		if (index == -1)
			index = imgIndex;
		return width.get(index);
	}

	public int getHeight(int index) {
		if (index == -1)
			index = imgIndex;
		return height.get(index);
	}

	public Image getImg(int index) {
		if (index == -1)
			index = imgIndex;
		return img.get(index);
	}

	public Icon getIcon(int index) {
		if (index == -1)
			index = imgIndex;
		return new ImageIcon(img.get(index));
	}

}
