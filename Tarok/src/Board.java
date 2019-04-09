import java.awt.Color;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.FontMetrics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

//drawBotHands(g2);
public class Board extends JPanel implements ActionListener {
	private static final long serialVersionUID = 1L;
	private boolean ingame;
	private boolean shuffled;
	private boolean contractsRemoved;
	private boolean annReady;
	private boolean kingPicked;
	private boolean annClosed;
	private boolean scoreGame;
	private boolean gameScored;
	private boolean firstRound = true;
	private List<Integer> cards = new ArrayList<>();;
	private CardTranslator ct;
	private GameLogic gl;
	private Timer timer = new Timer(100, this);;
	ImageLoader il = new ImageLoader();
	Font gameFont = new Font("Georgia", Font.ITALIC, ResolutionScaler.percentToHeight(2));
	FontMetrics fm = getFontMetrics(gameFont);
	private int cardWidth, LRplayerWidth, topPlayerWidth, buttonWidth,  menuButtonWidth;
	private int talonSwap = 0;
	private int selectedCard = -1, rSelectedCard = -1, tSelectedCard = -1, lSelectedCard = -1;
	private int previousCard = -1, rPrevious = -1, tPrevious = -1, lPrevious = -1;
	private int excludeList[] = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	private int timerCounter = 0;
	private int bottomScore = 0, leftScore = 0, topScore = 0, rightScore = 0;

	public Board() {
		initBoard();
	}

	private void initBoard() {
		addKeyListener(new ClickAdapter());
		setFocusable(true);
		setBackground(Color.BLACK);
	}

	private void setElementSize() {
		cardWidth = ResolutionScaler.percentToWidth(6);
		LRplayerWidth = ResolutionScaler.percentToWidth(12);
		topPlayerWidth = ResolutionScaler.percentToWidth(6);
		buttonWidth = ResolutionScaler.percentToWidth(10);
		menuButtonWidth=ResolutionScaler.percentToWidth(16);

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

		removeAll();
		if (!ingame) {
			setElementSize();
			drawMenu(g);
		} else
			drawGame(g);
	}

	private void drawGame(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		g2.setFont(gameFont);
		drawBackgound(g2, "Game");

		if (!shuffled)
			initDeck();

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
		} else if (gl.getGamePicked() == "Valat") {
			if (!annClosed) {
				gl.orderPlayerCards();
				annClosed = true;
			} else if (gl.roundStart() == "BOTTOM" && !scoreGame) {
				drawMainGame(g2);
			} else if (gl.roundStart() != "BOTTOM" && !scoreGame) {
				gl.setSpecialGameMade(false);
				scoreGame = true;
			} else if (scoreGame && !gameScored) {
				valatScoring();
			} else if (gameScored) {
				replay(g2);
			}

		} else if (gl.isBeggarGame()) {
			if (!annClosed) {
				gl.orderPlayerCards();
				annClosed = true;
			} else if (gl.roundStart() == "BOTTOM" && !scoreGame) {
				if (firstRound) {
					drawMainGame(g2);
				} else {
					gl.setSpecialGameMade(false);
					scoreGame = true;
					repaint();
				}
			} else if (gl.roundStart() != "BOTTOM" && !scoreGame) {
				drawMainGame(g2);
			} else if (scoreGame && !gameScored) {
				beggarGamesScoring();
			} else if (gameScored) {
				replay(g2);
			}
		} else if (gl.getGamePicked() == "Klop") {
			if (!annClosed) {
				gl.orderPlayerCards();
				annClosed = true;
			} else if (annClosed && !scoreGame) {
				drawMainGame(g2);
			} else if (scoreGame && !gameScored) {
				klopScoring();
			} else if (gameScored) {
				replay(g2);
			}
		} else if (gl.getGamePicked() == "ColorValat") {
			if (!annClosed) {
				gl.orderPlayerCards();
				annClosed = true;
			} else if (gl.roundStart() == "BOTTOM" && !scoreGame) {
				drawMainGame(g2);
			} else if (gl.roundStart() != "BOTTOM" && !scoreGame) {
				gl.setSpecialGameMade(false);
				scoreGame = true;
			} else if (scoreGame && !gameScored) {
				colorValatScoring();
			} else if (gameScored) {
				replay(g2);
			}
		}

		drawScores(g2);
		drawPlayerHand(g2);
		drawBotHands(g2);

		boundsRemover();
	}

	private void drawBackgound(Graphics2D g2, String scene) {
		Image temp = null;
		if (scene == "Game") {
			URL imageUrl = this.getClass().getResource("/GameComponents/GameBackground.png");
			ImageIcon ii = new ImageIcon(imageUrl);
			temp = ii.getImage();
		} else if (scene == "Menu") {
			URL imageUrl = this.getClass().getResource("/MenuComponents/MenuBackground.png");
			ImageIcon ii = new ImageIcon(imageUrl);
			temp = ii.getImage();
		}
		g2.drawImage(temp, 0, 0, ResolutionScaler.getScreenWidth(), ResolutionScaler.getScreenHeight(), this);

	}

	private void replay(Graphics2D g2) {
		drawClickable(g2, 14, "/GameComponents/SelectionBackground.png", ResolutionScaler.percentToWidth(12), 44, 45);
		String replayMsg = "Konec Igre";
		g2.setColor(Color.decode("#f4b942"));
		g2.drawString(replayMsg, ResolutionScaler.percentToWidth(50) - fm.stringWidth(replayMsg) / 2,
				ResolutionScaler.percentToHeight(48));
		drawClickable(g2, 49, "/GameComponents/playAgain.png", buttonWidth, 45, 51);
		if (il.isFlag(49)) {
			shuffled = false;
			contractsRemoved = false;
			annReady = false;
			kingPicked = false;
			annClosed = false;
			scoreGame = false;
			gameScored = false;
			firstRound = true;
			talonSwap = 0;
			rSelectedCard = -1;
			tSelectedCard = -1;
			lSelectedCard = -1;
			selectedCard = -1;
			previousCard = -1;
			rPrevious = -1;
			tPrevious = -1;
			lPrevious = -1;
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

	private void valatScoring() {
		gl.ValatGameScoring();
		bottomScore += gl.getBottomScore();
		leftScore += gl.getLeftScore();
		topScore += gl.getTopScore();
		rightScore += gl.getRightScore();
		gameScored = true;
		repaint();
	}

	private void beggarGamesScoring() {
		gl.beggarGamesScoring();
		bottomScore += gl.getBottomScore();
		leftScore += gl.getLeftScore();
		topScore += gl.getTopScore();
		rightScore += gl.getRightScore();
		gameScored = true;
		repaint();

	}

	private void klopScoring() {
		gl.klopGameScoring();
		bottomScore += gl.getBottomScore();
		leftScore += gl.getLeftScore();
		topScore += gl.getTopScore();
		rightScore += gl.getRightScore();
		gameScored = true;
		repaint();
	}

	private void colorValatScoring() {
		gl.colorValatGameScoring();
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
		il.clearBounds(13);
		il.clearBounds(14);
		il.clearBounds(15);

	}

	private void showContracts(Graphics2D g2) {
		drawClickable(g2, 14, "/GameComponents/SelectionBackground.png", ResolutionScaler.percentToWidth(32), 34, 35);
		String contractMsg = "Izberi Igro";
		g2.setColor(Color.decode("#f4b942"));
		g2.drawString(contractMsg, ResolutionScaler.percentToWidth(50) - fm.stringWidth(contractMsg) / 2,
				ResolutionScaler.percentToHeight(37));
		int index = 0;
		String[] pngName = { "/GameComponents/contractButtonThree.png", "/GameComponents/contractButtonTwo.png",
				"/GameComponents/contractButtonOne.png", "/GameComponents/contractButtonKlop.png",
				"/GameComponents/contractButtonSoloThree.png", "/GameComponents/contractButtonSoloTwo.png",
				"/GameComponents/contractButtonSoloOne.png", "/GameComponents/contractButtonSoloWithout.png",
				"/GameComponents/contractButtonBeggar.png", "/GameComponents/contractButtonOpenBeggar.png",
				"/GameComponents/contractButtonColorValat.png", "/GameComponents/contractButtonValat.png" };
		for (int i = 0; i < 4; i++) {
			for (int j = 0; j < 3; j++) {
				drawClickable(g2, 16 + index, pngName[index], buttonWidth, 35 + j * 10, 38 + i * 6);
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
			drawClickable(g2, 14, "/GameComponents/SelectionBackground.png", ResolutionScaler.percentToWidth(16), 42,
					29);
			String talonMsg1 = "Izberi del talona za menjavo";
			String talonMsg2 = "in zamenjaj karte s svojimi";
			g2.setColor(Color.decode("#f4b942"));
			g2.drawString(talonMsg1, ResolutionScaler.percentToWidth(50) - fm.stringWidth(talonMsg1) / 2,
					ResolutionScaler.percentToHeight(32));
			g2.drawString(talonMsg2, ResolutionScaler.percentToWidth(50) - fm.stringWidth(talonMsg2) / 2,
					ResolutionScaler.percentToHeight(35));
			drawClickable(g2, 34, "/GameComponents/closeTalon.png", buttonWidth, 45, 37);
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
					gl.addKeptCards(cards.get(j - 1));
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
		drawClickable(g2, 14, "/GameComponents/SelectionBackground.png", ResolutionScaler.percentToWidth(32), 34, 35);
		String annMsg = "Izberi Napovedi";
		g2.setColor(Color.decode("#f4b942"));
		g2.drawString(annMsg, ResolutionScaler.percentToWidth(50) - fm.stringWidth(annMsg) / 2,
				ResolutionScaler.percentToHeight(37));
		int index = 0;
		String[] pngName = { "/GameComponents/announceButtonKings.png", "/GameComponents/announceButtonKUltimo.png",
				"/GameComponents/announceButtonPagat.png", "/GameComponents/announceButtonTrula.png",
				"/GameComponents/announceButtonValat.png" };
		for (int i = 0; i < 5; i++) {
			if (i < 3)
				drawClickable(g2, 39 + i, pngName[index], buttonWidth, 35 + i * 10, 40);
			else
				drawClickable(g2, 39 + i, pngName[index], buttonWidth, 40 + (i - 3) * 10, 50);
			index++;

		}
		drawClickable(g2, 44, "/GameComponents/closeAnnouncements.png", buttonWidth, 45, 58);

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
			if (gl.getGamePicked() == "ColorValat") {
				gl.colorValatRoundWinner();
			} else
				gl.roundWinner();
			storePrevious();
			gl.setPlayedCard(-1);
			rSelectedCard = -1;
			tSelectedCard = -1;
			lSelectedCard = -1;
			selectedCard = -1;
			firstRound = false;
			repaint();
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

		drawCard(g2, 45, selectedCard, 47, 52);
		drawCard(g2, 46, rSelectedCard, 55, 40);
		drawCard(g2, 47, tSelectedCard, 47, 27);
		drawCard(g2, 48, lSelectedCard, 39, 40);
		if (gl.isBeggarGame())
			drawPrevious(g2);

	}

	private void storePrevious() {
		previousCard = selectedCard;
		rPrevious = rSelectedCard;
		tPrevious = tSelectedCard;
		lPrevious = lSelectedCard;
	}

	private void drawPrevious(Graphics2D g2) {
		drawCard(g2, 49, previousCard, 16, 26);
		drawCard(g2, 50, rPrevious, 16, 46);
		drawCard(g2, 51, tPrevious, 22, 26);
		drawCard(g2, 52, lPrevious, 22, 46);
	}

	private void drawScores(Graphics2D g2) {

		Font scoreFont = new Font("Georgia", Font.ITALIC, ResolutionScaler.percentToHeight(2));
		FontMetrics fm = getFontMetrics(scoreFont);
		g2.setColor(Color.WHITE);
		g2.setFont(scoreFont);

		String bScore = "Vaše Točke: " + bottomScore;
		String lScore = "Levi Točke: " + leftScore;
		String tScore = "Zgornji Točke: " + topScore;
		String rScore = "Desni Točke: " + rightScore;

		g2.drawString(bScore, ResolutionScaler.percentToWidth(90), ResolutionScaler.percentToHeight(98));
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
				if (gl.getPlayerCards().size() == 1)
					gl.addLastRound(selectedCard);
				gl.getPlayerCards().remove(i);
				timer.start();
			}
		}

		return selectedCard;
	}

	private void drawPlayerHand(Graphics2D g2) {
		for (int i = 0; i < gl.getPlayerCards().size(); i++) {
			drawCard(g2, 1 + i, gl.getPlayerCards().get(i), 14 + 6 * i, 76);

		}
	}

	private void drawBotHands(Graphics2D g2) {
		// Left Player
		for (int i = 0; i < gl.getlPlayerCards().size(); i++) {
			drawClickable(g2, 13, "/GameComponents/cardBackgroundL.png", LRplayerWidth, 3, 28 + 3 * i);
		}
		// Right Player
		for (int i = 0; i < gl.getrPlayerCards().size(); i++) {
			drawClickable(g2, 14, "/GameComponents/cardBackgroundL.png", LRplayerWidth, 85, 28 + 3 * i);
		}

		// Top Player
		for (int i = 0; i < gl.gettPlayerCards().size(); i++) {
			drawClickable(g2, 15, "/GameComponents/cardBackgroundP.png", topPlayerWidth, 36 + 2 * i, 4);

		}
	}

	private void drawMenu(Graphics g) {
		Graphics2D g2 = (Graphics2D) g;
		drawBackgound(g2, "Menu");

			drawClickable(g2, 0, "/MenuComponents/startGameButton.png", menuButtonWidth, 42, 46);
			drawClickable(g2, 1, "/MenuComponents/rulesButton.png", menuButtonWidth, 42, 52);
		
		if (il.isFlag(0)) {
			ingame = true;
			removeMenuBounds();
			il.resetFlag(0);
			repaint();
		}else if (il.isFlag(1)) {
			Desktop d = Desktop.getDesktop();
			try {
				d.browse(new URI("https://valat.si/pravila"));
			} catch (IOException e) {
				e.printStackTrace();
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			il.resetFlag(1);
		}

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

	}

}
