import java.awt.EventQueue;
import javax.swing.JFrame;

public class TarokExec extends JFrame {
	private static final long serialVersionUID = 1L;
	public TarokExec() {
		initUI();
	}
	public static  Board board = new Board();
	private void initUI() {
		add(board);
		addMouseListener(new ClickAdapter());
		setTitle("Best Tarok Computer Game In The Universe*                     *patent pending");
		setSize(ResolutionScaler.getScreenResolution());
		setExtendedState(this.getExtendedState() | JFrame.MAXIMIZED_BOTH);
		setUndecorated(true);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		setLocationRelativeTo(null);
		
	}
	public static void main(String[] args) {
		EventQueue.invokeLater(()->{
			TarokExec te = new TarokExec();
			te.setVisible(true);
		});
	}

}
