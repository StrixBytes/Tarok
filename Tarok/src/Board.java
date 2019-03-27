import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;
import javax.xml.ws.RespectBindingFeature;

public class Board extends JPanel {
	private static final long serialVersionUID = 1L;
	private boolean inmenu;
	private boolean ingame;
	private boolean shuffled;
	private boolean contractsRemoved;
	private boolean talonSwitched;
	private boolean kingPicked;
	private List<Integer> cards = new ArrayList<>();;
	private CardTranslator ct;
	private GameLogic gl;
	ImageLoader il = new ImageLoader();
	private int cardWidth, LRplayerHeight, topPlayerWidth, startGameWidth, contractWidth;
	private int talonSwap = 0;
	int excludeList[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private final int BACKGROUNDINDEXL = 13;
	private final int BACKGROUNDINDEXR = 14;
	private final int BACKGROUNDINDEXT = 15;

	public Board() {
		initBoard();
	}

	private void initBoard() {
		addKeyListener(new ClickAdapter());
		setFocusable(true);
		setBackground(Color.BLACK);
		inmenu = true;

	}

	private void setElementSize() {
		cardWidth = ResolutionScaler.percentToWidth(6);
		LRplayerHeight = ResolutionScaler.percentToHeight(12);
		topPlayerWidth = ResolutionScaler.percentToWidth(6);
		startGameWidth = ResolutionScaler.percentToWidth(20);
		contractWidth = ResolutionScaler.percentToWidth(10);

	}

	private void initDeck() {
		cards.clear();
		for (int i = 0; i < 54; i++)
			cards.add(i);
		Collections.shuffle(cards);
		printDeckOrder();
		System.out.println();
		ct = new CardTranslator();
		gl = new GameLogic(il, cards);

		shuffled = true;
	}

	private void printDeckOrder() {
		System.out.print("DeckOrder: ");
		for (int i : cards)
			System.out.print(i + " ");
	}

	private void drawClickable(Graphics g2, int index, String imgName, int width, int xInPercent, int yInPercent) {
		il.setImgIndex(index);
		il.loadImage(imgName);
		il.setDimensions(-1, width, il.getNewImageHeight(width));
		il.setCoordinates(-1, ResolutionScaler.percentToWidth(xInPercent),
				ResolutionScaler.percentToHeight(yInPercent));
		g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
	}

	private void drawClickableReverse(Graphics g2, int index, String imgName, int height, int xInPercent,
			int yInPercent) {
		il.setImgIndex(index);
		il.loadImage(imgName);
		il.setDimensions(-1, il.getNewImageWidth(height), height);
		il.setCoordinates(-1, ResolutionScaler.percentToWidth(xInPercent),
				ResolutionScaler.percentToHeight(yInPercent));
		g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
	}

	private void drawCard(Graphics g2, int index, int cardIndex, int xInPercent, int yInPercent) {
		il.setImgIndex(index);
		il.loadImage(ct.cardToImage(cardIndex));
		il.setDimensions(-1, cardWidth, il.getNewImageHeight(cardWidth));
		il.setCoordinates(-1, ResolutionScaler.percentToWidth(xInPercent),
				ResolutionScaler.percentToHeight(yInPercent));
		g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);

	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (il.isFlag(0)) {
			inmenu = false;
			ingame = true;
			removeMenuBounds();
			il.resetFlag(0);
		}
		removeAll();
		if (inmenu) {
			setElementSize();
			drawMenu(g);
		} else if (ingame)
			drawGame(g);
	}

	private void drawGame(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		if (!shuffled)
			initDeck();
		setBackground(Color.GRAY);
		if (gl.contractNotPicked())
			showContracts(g2);
		if (gl.showTalon() && !talonSwitched) {
			if (!contractsRemoved)
				removeContractBounds();
			showTalon(g2);
		}
		if (gl.pickKing() && talonSwitched && !kingPicked) {
			showKings(g2);
		}

		drawPlayerHand(g2);
		drawBotHands(g2);

	}

	private void showContracts(Graphics g2) {
		int index = 0;
		String[] pngName = { "/GameComponents/contractButtonThree.png", "/GameComponents/contractButtonTwo.png",
				"/GameComponents/contractButtonOne.png", "/GameComponents/contractButtonKlop.png",
				"/GameComponents/contractButtonSoloThree.png", "/GameComponents/contractButtonSoloTwo.png",
				"/GameComponents/contractButtonSoloOne.png", "/GameComponents/contractButtonSoloWithout.png",
				"/GameComponents/contractButtonBeggar.png", "/GameComponents/contractButtonOpenBeggar.png",
				"/GameComponents/contractButtonColorValat.png", "/GameComponents/contractButtonValat.png" };
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 4; j++) {
				drawClickable(g2, 16 + index, pngName[index], contractWidth, 30 + j * 10, 20 + i * 20);
				index++;
			}
		}

		gl.contractPicker();
		repaint();

	}

	private void showTalon(Graphics2D g2) {

		if (gl.talonSplitter() == 1)
			for (int i = 0; i < 6; i++) {
				drawCard(g2, 28 + i, cards.get(48 + i), 22 + 10 * i, 45);
			}
		if (gl.talonSplitter() == 3) {
			int j = 0;
			for (int i = 0; i < 6; i++) {
				if (i == 3)
					j = 6;
				drawCard(g2, 28 + i, cards.get(48 + i), 29 + j + 6 * i, 45);
			}
		}
		if (gl.talonSplitter() == 2) {
			int j = 0;
			for (int i = 0; i < 6; i++) {
				if (i == 2 || i == 4)
					j += 6;
				drawCard(g2, 28 + i, cards.get(48 + i), 26 + j + 6 * i, 45);
			}
		}
		if (talonSwap < gl.talonSplitter()) {
			drawClickable(g2, 34, "/GameComponents/button1.png", startGameWidth, 40, 30);
			if (il.isFlag(34)) {
				talonSwitched = true;
				repaint();
			}
			talonSwitcher();
		} else if (talonSwap == gl.talonSplitter()) {
			talonSwitched = true;
			repaint();
		}
	}

	private void talonSwitcher() {

		int cardIndexes[] = gl.switchWithTalon();
		for (int j = 1; j < 13; j++) {
			if (il.isFlag(j) && j != excludeList[j - 1] && cards.get(j - 1) > 21 && cards.get(j - 1) != 29
					&& cards.get(j - 1) != 37 && cards.get(j - 1) != 45 && cards.get(j - 1) != 53) {
				il.resetFlag(j);
				excludeList[j - 1] = j;
				Collections.swap(cards, j - 1, cardIndexes[talonSwap]);
				talonSwap++;
				repaint();
			}
		}

	}

	private void showKings(Graphics2D g2) {
		for (int i = 0; i < 4; i++)
			drawCard(g2, 35 + i, 29 + i * 8, 17 + 20 * i, 45);

		if (il.isFlag(35))
			gl.setKingPicked(29);
		if (il.isFlag(36))
			gl.setKingPicked(37);
		if (il.isFlag(37))
			gl.setKingPicked(45);
		if (il.isFlag(38))
			gl.setKingPicked(53);
		if (gl.getKingPicked() != -1) {
			kingPicked = true;
			repaint();
		}
		System.out.println("King Picked " + ct.cardToImage(gl.getKingPicked()));

	}

	private void drawPlayerHand(Graphics2D g2) {
		for (int i = 0; i < 12; i++) {
			drawCard(g2, 1 + i, cards.get(i), 14 + 6 * i, 70);

		}
	}

	private void drawBotHands(Graphics2D g2) {
		// Left Player
		for (int i = 0; i < 12; i++) {
			drawClickableReverse(g2, BACKGROUNDINDEXL, "/GameComponents/cardBackgroundL.png", LRplayerHeight, 2,
					28 + 3 * i);
		}
		// Right Player
		for (int i = 0; i < 12; i++) {
			drawClickableReverse(g2, BACKGROUNDINDEXR, "/GameComponents/cardBackgroundL.png", LRplayerHeight, 88,
					28 + 3 * i);
		}

		// Top Player
		for (int i = 0; i < 12; i++) {
			drawClickable(g2, BACKGROUNDINDEXT, "/GameComponents/cardBackgroundP.png", topPlayerWidth, 32 + 3 * i, 3);
		}
	}

	private void drawMenu(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		setBackground(Color.decode("#6E8970"));
		il.setImgIndex(0);
		il.loadImage("/MenuComponents/playButton.png");
		il.setDimensions(-1, ResolutionScaler.percentToWidth(40), ResolutionScaler.percentToHeight(40));
		il.setCoordinates(-1, ResolutionScaler.percentToWidth(50) - il.getWidth(-1) / 2,
				ResolutionScaler.percentToHeight(50) - il.getHeight(-1) / 2);
		g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
	}

	private void removeMenuBounds() {
		il.clearBounds(0); // Play Button
		System.out.println("Menu Flags Removed");
	}

	private void removeContractBounds() {
		contractsRemoved = true;
		for (int i = 0; i < 12; i++)
			il.clearBounds(16 + i);
		System.out.println("Contract Flags Removed");
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("Point " + e.getPoint());
		for (int i = 0; i <= il.getMaxIndex(); i++) {
			int cX = e.getX(), cY = e.getY();
			if (il.getX(i) < cX && cX < (il.getX(i) + il.getWidth(i)))
				if (il.getY(i) < cY && cY < (il.getY(i) + il.getHeight(i))) {
					il.triggerFlag(i);
					System.out.println(
							il.isFlag(i) + " at Flag " + i + " at " + il.getX(i) + " - " + (il.getX(i) + il.getWidth(i))
									+ " and " + il.getY(i) + " - " + (il.getY(i) + il.getHeight(i)));
				}
		}
		repaint();
	}

}
