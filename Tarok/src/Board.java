import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;

import javax.swing.JPanel;

//drawBotHands(g2);
public class Board extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private boolean inmenu;
	private boolean ingame;
	private boolean shuffled;
	private boolean contractsRemoved;
	private boolean annReady;
	private boolean kingPicked;
	private boolean annClosed;
	private boolean scoreGame;
	private boolean gameScored;
	private List<Integer> cards = new ArrayList<>();;
	private CardTranslator ct;
	private GameLogic gl;
	private Timer timer = new Timer(100, this);;
	ImageLoader il = new ImageLoader();
	private int cardWidth, LRplayerHeight, topPlayerWidth, startGameWidth, contractWidth, closeAnnWidth;
	private int talonSwap = 0;
	private int selectedCard = -1, rSelectedCard = -1, tSelectedCard = -1, lSelectedCard = -1;
	private int excludeList[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private int timerCounter = 0;
	private int bottomScore = 0, leftScore = 0, topScore = 0, rightScore = 0;
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
		if (gl.contractNotPicked()) {
			showContracts(g2);
		} else if (gl.pickKing() && !kingPicked) {
			showKings(g2);
		} else if (gl.openTalon() && !annReady) {
			showTalon(g2);
		} else if (gl.getGamePicked() == "SoloWithout") {
			annReady = true;
		}
		if (gl.normalGames()) {
			if (annReady && !annClosed) {
				showAnnouncements(g2);
				gl.orderPlayerCards();
			} else if (annClosed && !scoreGame) {
				drawMainGame(g2);
			} else if (scoreGame && !gameScored) {
				threeTwoOneScoring();
			} else if (gameScored) {
				replay(g2);
			}
		}

		drawScores(g2);
		drawPlayerHand(g2);
		drawBotHands(g2);

		boundsRemover();
	}

	private void replay(Graphics2D g2) {
		drawClickable(g2, 49, "/MenuComponents/playButton.png", ResolutionScaler.percentToWidth(20), 40, 40);
		if (il.isFlag(49)) {
			initDeck();
			contractsRemoved = false;
			annReady = false;
			kingPicked = false;
			annClosed = false;
			scoreGame = false;
			gameScored = false;
			talonSwap = 0;
			rSelectedCard = -1;
			tSelectedCard = -1;
			lSelectedCard = -1;
			selectedCard = -1;
			for (int i = 0; i < 12; i++)
				excludeList[i] = 0;
			gl.reset();
			il.resetAllFlags();
			timer.stop();
			timerCounter = 0;
			repaint();
		}
	}

	private void threeTwoOneScoring() {
		gl.normalScoring();
		bottomScore += gl.getBottomScore();
		leftScore += gl.getLeftScore();
		topScore += gl.getTopScore();
		rightScore += gl.getRightScore();
		gameScored = true;
		repaint();
	}

	private void boundsRemover() {
		if (!gl.contractNotPicked() && !contractsRemoved)
			removeContractBounds();

	}

	private void showContracts(Graphics2D g2) {
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
				annReady = true;
				repaint();
			}
			talonSwitcher();
		} else if (talonSwap == gl.talonSplitter()) {
			annReady = true;

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
					gl.addKeptCards(cards.get(cardIndexes[talonSwap]));
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

	private void drawMainGame(Graphics2D g2) {
		if (gl.getlPlayerCards().isEmpty() && gl.gettPlayerCards().isEmpty() && gl.getrPlayerCards().isEmpty()
				&& gl.getPlayerCards().isEmpty()) {
			timer.stop();
			scoreGame = true;
			repaint();
		} else if (timerCounter == 6) {
			timerCounter = 0;
			timer.stop();
			System.out.println(ct.cardToImage(selectedCard) + " bot" + ct.cardToImage(lSelectedCard) + " left"
					+ ct.cardToImage(tSelectedCard) + " top" + ct.cardToImage(rSelectedCard) + " right");
			System.out.println(ct.cardToImage(gl.getPlayedCard()) + " played Card");
			gl.roundWinner();
			gl.setPlayedCard(-1);
			rSelectedCard = -1;
			tSelectedCard = -1;
			lSelectedCard = -1;
			selectedCard = -1;

		} else if (timer.isRunning()) {
			timerCounter++;

		}

		if (gl.roundStart() == "BOTTOM") {
			if (timerCounter == 0) {
				selectCard();
				gl.setPlayedCard(selectedCard);
			} else if (timerCounter == 1)
				lSelectedCard = gl.cardComparatorBot(gl.getlPlayerCards());
			else if (timerCounter == 2)
				tSelectedCard = gl.cardComparatorBot(gl.gettPlayerCards());
			else if (timerCounter == 3)
				rSelectedCard = gl.cardComparatorBot(gl.getrPlayerCards());
		}
		if (gl.roundStart() == "LEFT") {
			if (timerCounter == 0)
				timer.start();
			else if (timerCounter == 1)
				lSelectedCard = gl.cardPlayBot(gl.getlPlayerCards());
			else if (timerCounter == 2)
				tSelectedCard = gl.cardComparatorBot(gl.gettPlayerCards());
			else if (timerCounter == 3)
				rSelectedCard = gl.cardComparatorBot(gl.getrPlayerCards());
			else if (timerCounter == 4) {
				timer.stop();
				selectCard();
			}
		}
		if (gl.roundStart() == "TOP") {
			if (timerCounter == 0)
				timer.start();
			else if (timerCounter == 1)
				tSelectedCard = gl.cardPlayBot(gl.gettPlayerCards());
			else if (timerCounter == 2)
				rSelectedCard = gl.cardComparatorBot(gl.getrPlayerCards());
			else if (timerCounter == 3) {
				timer.stop();
				selectCard();
			} else if (timerCounter == 4)
				lSelectedCard = gl.cardComparatorBot(gl.getlPlayerCards());

		}
		if (gl.roundStart() == "RIGHT") {
			if (timerCounter == 0)
				timer.start();
			else if (timerCounter == 1)
				rSelectedCard = gl.cardPlayBot(gl.getrPlayerCards());
			else if (timerCounter == 2) {
				timer.stop();
				selectCard();
			} else if (timerCounter == 3)
				lSelectedCard = gl.cardComparatorBot(gl.getlPlayerCards());
			else if (timerCounter == 4)
				tSelectedCard = gl.cardComparatorBot(gl.gettPlayerCards());

		}

		drawCard(g2, 45, selectedCard, 47, 46);
		drawCard(g2, 46, rSelectedCard, 55, 34);
		drawCard(g2, 47, tSelectedCard, 47, 21);
		drawCard(g2, 48, lSelectedCard, 39, 34);

	}

	private void drawScores(Graphics2D g2) {
		System.setProperty("file.encoding", "UTF-8");
		Font scoreFont = new Font("Helvetica", Font.PLAIN, ResolutionScaler.percentToHeight(2));
		FontMetrics fm = getFontMetrics(scoreFont);
		g2.setColor(Color.WHITE);
		g2.setFont(scoreFont);

		String bScore = "Vaše Točke: " + bottomScore;
		String lScore = "Levi Točke: " + leftScore;
		String tScore = "Zgornji Točke: " + topScore;
		String rScore = "Desni Točke: " + rightScore;

		g2.drawString(bScore, ResolutionScaler.percentToWidth(90), ResolutionScaler.percentToHeight(96));
		g2.drawString(lScore, ResolutionScaler.percentToWidth(5) - fm.stringWidth(lScore) / 2,
				ResolutionScaler.percentToHeight(2));
		g2.drawString(tScore, ResolutionScaler.percentToWidth(50) - fm.stringWidth(tScore) / 2,
				ResolutionScaler.percentToHeight(2));
		g2.drawString(rScore, ResolutionScaler.percentToWidth(95) - fm.stringWidth(rScore) / 2,
				ResolutionScaler.percentToHeight(2));

	}

	private int selectCard() {
		for (int i = 0; i < gl.getPlayerCards().size(); i++) {
			if (il.isFlag(1 + i)) {
				selectedCard = gl.getPlayerCards().get(i);
				il.resetFlag(1 + i);
				gl.setTempBottom(selectedCard);
				if (gl.getPlayerCards().size()==1)
					gl.addLastRound(selectedCard);
				gl.getPlayerCards().remove(i);
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

		repaint();
//		System.out.println(timerCounter);

	}

}
