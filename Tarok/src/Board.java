import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JPanel;

public class Board extends JPanel {
	private static final long serialVersionUID = 1L;
	private boolean inmenu;
	private boolean ingame;
	private boolean shuffled;
	private List<Integer> cards = new ArrayList<>();;
	private CardTranslator ct;
	private GameLogic gl;
	ImageLoader il = new ImageLoader();
	private int cardWidth, LRplayerHeight, topPlayerWidth, startGameWidth, contractWidth;
	private final int BACKGROUNDINDEXL = 13;
	private final int BACKGROUNDINDEXR = 14;
	private final int BACKGROUNDINDEXT = 15;
	private final int STARTGAMEINDEX = 28;

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
		for (int i : cards)
			System.out.print(i + " ");
		System.out.println();
		ct = new CardTranslator();
		gl = new GameLogic(il, cards);
		System.out.println(il.toString() + " " + cards.toString());

		shuffled = true;
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
		if (gl.showTalon()) {
			removeContractBounds();
			showTalon(g2);
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
				il.setImgIndex(16 + index);
				il.loadImage(pngName[index]);
				il.setDimensions(-1, contractWidth, il.getNewImageHeight(contractWidth));
				il.setCoordinates(-1, ResolutionScaler.percentToWidth(30 + j * 10),
						ResolutionScaler.percentToHeight(20 + i * 20));
				g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
				index++;
			}
		}

		gl.contractPicker();
		repaint();
	}

	private void showTalon(Graphics2D g2) {
		if (!il.isFlag(STARTGAMEINDEX)) {
			il.setImgIndex(STARTGAMEINDEX);
			il.loadImage("/GameComponents/button1.png");
			int startGameHeight = il.getNewImageHeight(startGameWidth);
			il.setDimensions(-1, startGameWidth, startGameHeight);
			il.setCoordinates(-1, ResolutionScaler.percentToWidth(50) - startGameWidth / 2,
					ResolutionScaler.percentToHeight(50) - startGameHeight / 2);
			g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
		} else if (il.isFlag(STARTGAMEINDEX)) {
			if (gl.talonSplitter() == 1)
				for (int i = 0; i < 6; i++) {
					il.setImgIndex(32 + i);
					il.loadImage(ct.cardToImage(cards.get(48 + i)));
					il.setDimensions(-1, cardWidth, il.getNewImageHeight(cardWidth));
					il.setCoordinates(-1, ResolutionScaler.percentToWidth(20 + 10 * i),
							ResolutionScaler.percentToHeight(45));
					g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);

				}
			if (gl.talonSplitter() == 3) {
				int j = 0;
				for (int i = 0; i < 6; i++) {

					il.setImgIndex(32 + i);
					il.loadImage(ct.cardToImage(cards.get(48 + i)));
					il.setDimensions(-1, cardWidth, il.getNewImageHeight(cardWidth));
					if (i == 3)
						j += 6;
					il.setCoordinates(-1, ResolutionScaler.percentToWidth(29 + j + 6 * i),
							ResolutionScaler.percentToHeight(45));
					g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);

				}
			}
			if (gl.talonSplitter() == 2) {
				int j = 0;
				for (int i = 0; i < 6; i++) {

					il.setImgIndex(32 + i);
					il.loadImage(ct.cardToImage(cards.get(48 + i)));
					il.setDimensions(-1, cardWidth, il.getNewImageHeight(cardWidth));
					if (i == 2||i==4)
						j += 6;
					il.setCoordinates(-1, ResolutionScaler.percentToWidth(26 + j + 6 * i),
							ResolutionScaler.percentToHeight(45));
					g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);

				}
			}
		}
	}

	private void drawPlayerHand(Graphics2D g2) {
		for (int i = 0; i < 12; i++) {
			il.setImgIndex(1 + i);
			il.loadImage(ct.cardToImage(cards.get(i)));
			il.setDimensions(-1, cardWidth, il.getNewImageHeight(cardWidth));
			il.setCoordinates(-1, ResolutionScaler.percentToWidth(14 + 6 * i), ResolutionScaler.percentToHeight(70));
			g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
		}
	}

	private void drawBotHands(Graphics2D g2) {
		// Left Player
		il.setImgIndex(BACKGROUNDINDEXL);
		il.loadImage("/GameComponents/cardBackgroundL.png");
		il.setDimensions(-1, il.getNewImageWidth(LRplayerHeight), LRplayerHeight);
		for (int i = 0; i < 12; i++) {
			il.setCoordinates(-1, ResolutionScaler.percentToWidth(2), ResolutionScaler.percentToHeight(28 + 3 * i));
			g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
		}
		// Right Player
		il.setImgIndex(BACKGROUNDINDEXR);
		il.loadImage("/GameComponents/cardBackgroundL.png");
		il.setDimensions(-1, il.getNewImageWidth(LRplayerHeight), LRplayerHeight);
		for (int i = 0; i < 12; i++) {
			il.setCoordinates(-1, ResolutionScaler.percentToWidth(90), ResolutionScaler.percentToHeight(28 + 3 * i));
			g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
		}

		// Top Player
		il.setImgIndex(BACKGROUNDINDEXT);
		il.loadImage("/GameComponents/cardBackgroundP.png");
		il.setDimensions(-1, topPlayerWidth, il.getNewImageHeight(topPlayerWidth));
		for (int i = 0; i < 12; i++) {
			il.setCoordinates(-1, ResolutionScaler.percentToWidth(32 + 3 * i), ResolutionScaler.percentToHeight(5));
			g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
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
