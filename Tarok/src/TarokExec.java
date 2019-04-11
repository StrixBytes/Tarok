import java.awt.EventQueue;
import java.net.URL;
import javax.swing.ImageIcon;
import javax.swing.JFrame;

//main razred programa. Nastavi privzete lastnosti JFrame-a, zažene program.

public class TarokExec extends JFrame {
	private static final long serialVersionUID = 1L;

	public TarokExec() {
		initUI();
	}

	public static Board board = new Board();
	
	//Nastavitev lastnosti JFrame-a.
	private void initUI() {
		URL imageUrl = this.getClass().getResource("/icon.png");
		ImageIcon ii = new ImageIcon(imageUrl);
		add(board);
		addMouseListener(new ClickAdapter());
		setTitle("Best Tarok Computer Game In The Universe*                     *patent pending");
		setSize(ResolutionScaler.getScreenResolution());
		setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setIconImage(ii.getImage());
		setLocationRelativeTo(null);

	}
	
	//Main metoda.
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			TarokExec te = new TarokExec();
			te.setVisible(true);
		});
	}

}
