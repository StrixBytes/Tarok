import java.awt.Dimension;
import java.awt.Toolkit;

// Razred, za postavitev elementov glede na delež zaslona namesto na fiksno x in y pozicijo.
public class ResolutionScaler {
	private static Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static int screenWidth = screenSize.width;
	private static int screenHeight = screenSize.height;
	private static Dimension screenResolution = new Dimension(screenWidth, screenHeight);

	// S podanim deležom, nam vrne pozicijo glede na resolucijo.
	public static int percentToWidth(int percent) {
		double toPercent = (double) percent / 100.0;
		return (int) (screenWidth * toPercent);
	}

	// S podanim deležom, nam vrne pozicijo glede na resolucijo.
	public static int percentToHeight(int percent) {
		double toPercent = (double) percent / 100.0;
		return (int) (screenHeight * toPercent);
	}

	// Getterji za podatkovne tipe razreda.
	public static Dimension getScreenResolution() {

		return screenResolution;
	}

	public static int getScreenWidth() {

		return screenWidth;
	}

	public static int getScreenHeight() {
		return screenHeight;
	}
}
