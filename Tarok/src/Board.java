import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;

import javax.swing.JPanel;

public class Board extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private boolean inmenu;
	private boolean ingame;
	private boolean shuffled;
	private boolean contractsRemoved;
	private boolean talonSwitched;
	private boolean kingPicked;
	private boolean annClosed;
	private List<Integer> cards = new ArrayList<>();;
	private CardTranslator ct;
	private GameLogic gl;
	private Timer timer;
	ImageLoader il = new ImageLoader();
	private int cardWidth, LRplayerHeight, topPlayerWidth, startGameWidth, contractWidth, closeAnnWidth;
	private int talonSwap = 0;
	private int selectedCard = -1, rSelectedCard = -1, tSelectedCard = -1, lSelectedCard = -1;
	private int excludeList[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private int timerCounter = 0;
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
		closeAnnWidth = ResolutionScaler.percentToWidth(4);

	}

	private void initDeck() {
		cards.clear();
		for (int i = 0; i < 54; i++)
			cards.add(i);
		Collections.shuffle(cards);
		printDeckOrder();
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
		if (cardIndex != -1) {
			il.setImgIndex(index);
			il.loadImage(ct.cardToImage(cardIndex));
			il.setDimensions(-1, cardWidth, il.getNewImageHeight(cardWidth));
			il.setCoordinates(-1, ResolutionScaler.percentToWidth(xInPercent),
					ResolutionScaler.percentToHeight(yInPercent));
			g2.drawImage(il.getImg(-1), il.getX(-1), il.getY(-1), il.getWidth(-1), il.getHeight(-1), this);
		}

	}

	private static GraphicsConfiguration getDefaultConfiguration() {
		GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice gd = ge.getDefaultScreenDevice();
		return gd.getDefaultConfiguration();
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

		if (talonSwitched && gl.announce() && !annClosed) {
			showAnnouncements(g2);
		}
		if (annClosed) {
			drawMainGame(g2);
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
		removePlayerBounds();
		if (gl.talonSplitter() == 1)
			for (int i = 0; i < 6; i++) {
				drawCard(g2, 28 + i, gl.getTalonCards().get(i), 22 + 10 * i, 45);
			}
		if (gl.talonSplitter() == 3) {
			int j = 0;
			for (int i = 0; i < 6; i++) {
				if (i == 3)
					j = 6;
				drawCard(g2, 28 + i, gl.getTalonCards().get(i), 29 + j + 6 * i, 45);
			}
		}
		if (gl.talonSplitter() == 2) {
			int j = 0;
			for (int i = 0; i < 6; i++) {
				if (i == 2 || i == 4)
					j += 6;
				drawCard(g2, 28 + i, gl.getTalonCards().get(i), 26 + j + 6 * i, 45);
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
			gl.orderPlayerCards();
			repaint();
		}
	}

	private void talonSwitcher() {

		int cardIndexes[] = gl.switchWithTalon();
		if (cardIndexes[talonSwap] != 0)
			for (int j = 1; j < 13; j++) {
				if (il.isFlag(j) && j != excludeList[j - 1] && cards.get(j - 1) > 21 && cards.get(j - 1) != 29
						&& cards.get(j - 1) != 37 && cards.get(j - 1) != 45 && cards.get(j - 1) != 53) {
					il.resetFlag(j);
					excludeList[j - 1] = j;
					Collections.swap(cards, j - 1, cardIndexes[talonSwap]);
					gl.setPlayerCards(cards.subList(0, 12));
					gl.setTalonCards(cards.subList(48, 54));
					talonSwap++;
					repaint();
				}
			}

	}

	private void showKings(Graphics2D g2) {
		for (int i = 0; i < 4; i++)
			drawCard(g2, 35 + i, 29 + i * 8, 17 + 20 * i, 45);

		gl.kingPicker();
		if (gl.getKingPicked() != -1) {
			gl.findPartner();
			System.out.println(gl.getPartner());
			kingPicked = true;
			repaint();
		}
		System.out.println("King Picked " + ct.cardToImage(gl.getKingPicked()));

	}

	private void showAnnouncements(Graphics2D g2) {
		int index = 0;
		String[] pngName = { "/GameComponents/announceButtonKings.png", "/GameComponents/announceButtonKUltimo.png",
				"/GameComponents/announceButtonPagat.png", "/GameComponents/announceButtonTrula.png",
				"/GameComponents/announceButtonValat.png" };
		for (int i = 0; i < 5; i++) {
			if (i < 3)
				drawClickable(g2, 39 + i, pngName[index], contractWidth, 30 + i * 15, 40);
			else
				drawClickable(g2, 39 + i, pngName[index], contractWidth, 35 + (i - 3) * 20, 55);
			index++;

		}
		drawClickable(g2, 44, "/GameComponents/closeAnnouncements.png", closeAnnWidth, 48, 25);

		gl.announcePicker();

		if (il.isFlag(44)) {
			annClosed = true;
			repaint();
		}
	}

	private void drawMainGame(Graphics g2) {
		timer = new Timer(100, this);
//		if (gl.roundStart() == "BOTTOM") {
			if (timerCounter == 0) {
				selectCard();
				
			}

//		}
		drawCard(g2, 45, selectedCard, 47, 46);
		drawCard(g2, 46, rSelectedCard, 55, 34);
		drawCard(g2, 47, tSelectedCard, 47, 21);
		drawCard(g2, 48, lSelectedCard, 39, 34);

	}

	private int selectCard() {
		for (int i = 0; i < gl.getPlayerCards().size(); i++) {
			if (il.isFlag(1 + i)) {
				selectedCard = gl.getPlayerCards().get(i);
				il.resetFlag(1 + i);
				gl.setPlayedCard(gl.getPlayerCards().get(i));
				gl.getPlayerCards().remove(i);
				rSelectedCard = -1;
				tSelectedCard = -1;
				lSelectedCard = -1;
				timer.start();
			}
		}

		return selectedCard;
	}

	private void drawPlayerHand(Graphics2D g2) {
		for (int i = 0; i < gl.getPlayerCards().size(); i++) {
			drawCard(g2, 1 + i, gl.getPlayerCards().get(i), 14 + 6 * i, 70);

		}
	}

	private void drawBotHands(Graphics2D g2) {
		// Left Player
		for (int i = 0; i < gl.getlPlayerCards().size(); i++) {
			drawClickableReverse(g2, BACKGROUNDINDEXL, "/GameComponents/cardBackgroundL.png", LRplayerHeight, 2,
					28 + 3 * i);
		}
		// Right Player
		for (int i = 0; i < gl.getrPlayerCards().size(); i++) {
			drawClickableReverse(g2, BACKGROUNDINDEXR, "/GameComponents/cardBackgroundL.png", LRplayerHeight, 88,
					28 + 3 * i);
		}

		// Top Player
		for (int i = 0; i < gl.gettPlayerCards().size(); i++) {
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

	private void removePlayerBounds() {
		for (int i = 0; i < 12; i++)
			il.clearBounds(1 + i);
		System.out.println("Player Flags Removed");
	}

	public void mouseReleased(MouseEvent e) {
		System.out.println("Point " + e.getPoint());
		for (int i = 0; i <= il.getMaxIndex(); i++) {

			int cX = e.getX(), cY = e.getY();
			if (il.skip(i) != true)
				if (il.getX(i) < cX && cX < (il.getX(i) + il.getWidth(i)))
					if (il.getY(i) < cY && cY < (il.getY(i) + il.getHeight(i))) {
						il.triggerFlag(i);
						System.out.println(il.isFlag(i) + " at Flag " + i + " at " + il.getX(i) + " - "
								+ (il.getX(i) + il.getWidth(i)) + " and " + il.getY(i) + " - "
								+ (il.getY(i) + il.getHeight(i)));

					}
		}
		repaint();
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		timerCounter++;
		if (timerCounter == 1)
			rSelectedCard = gl.rightBot();
		else if (timerCounter == 2)
			tSelectedCard = gl.topBot();
		else if (timerCounter == 3)
			lSelectedCard = gl.leftBot();
		else if (timerCounter == 4) {
			gl.roundWinner();
		} else if (timerCounter == 5) {
			timerCounter = 0;
			((Timer) e.getSource()).stop();

		}

		repaint();
		System.out.println(timerCounter);

	}

}
