import java.awt.Dimension;
import java.awt.Toolkit;

public class ResolutionScaler {
	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static int screenWidth = screenSize.width;
	private static int screenHeight = screenSize.height;
	private static Dimension screenResolution = new Dimension(screenWidth, screenHeight);

	public static int percentToWidth(int percent) {
		double toPercent = (double) percent / 100.0;
		return (int) (screenWidth * toPercent);
	}

	public static int percentToHeight(int percent) {
		double toPercent = (double) percent / 100.0;
		return (int) (screenHeight * toPercent);
	}
	
	public static int keepRatio(int sourceWidth, int sourceHeight, int desiredHeight) {
		double ratio = (double) sourceWidth / (double) sourceHeight;
		return (int) (desiredHeight * ratio);
	}

	public static Dimension getScreenResolution() {
		System.out.println(screenWidth);
		System.out.println(screenHeight);
		return screenResolution;
	}

	public static int getScreenWidth() {

		return screenWidth;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}
}
