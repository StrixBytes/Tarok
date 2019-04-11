import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

// Razred, ki določa logiko igre.
public class GameLogic {
	
	private ImageLoader il;
	private CardTranslator ct;
	
	private List<Integer> cards;
	private List<Integer> playerCards, lPlayerCards, tPlayerCards, rPlayerCards, talonCards, keptCards;
	private List<Integer> playerScoreCards, leftScoreCards, topScoreCards, rightScoreCards;
	private List<Integer> lastRoundList;
	private HashMap<String, Boolean> announcements;
	
	private String gamePicked = null;
	private String partner = null;
	
	private int kingPicked = -1;
	private int playedCard = -1;
	private int gameRoundCounter = 0;
	private int globalDiff = 0;
	private int tempRight, tempTop, tempLeft, tempBottom;
	private int bottomScore, leftScore, topScore, rightScore;
	
	private boolean valat, specialGameMade = true;

	public GameLogic(ImageLoader il, List<Integer> cards) {
		this.il = il;
		this.cards = cards;
		ct = new CardTranslator();
		dealCards();
		initAnnouncements();
	}

	// Razdeli karte med igralce iz skupnega kupa kart, inicializira ostale
	// ArrayListe.
	private void dealCards() {
		playerCards = new ArrayList<Integer>(cards.subList(0, 12));
		lPlayerCards = new ArrayList<Integer>(cards.subList(12, 24));
		tPlayerCards = new ArrayList<Integer>(cards.subList(24, 36));
		rPlayerCards = new ArrayList<Integer>(cards.subList(36, 48));
		talonCards = new ArrayList<Integer>(cards.subList(48, 54));
		playerScoreCards = new ArrayList<Integer>();
		leftScoreCards = new ArrayList<Integer>();
		topScoreCards = new ArrayList<Integer>();
		rightScoreCards = new ArrayList<Integer>();
		keptCards = new ArrayList<Integer>();
		lastRoundList = new ArrayList<Integer>();
		// listPrint
		System.out.println();
		for (int i : playerCards)
			System.out.print(i + " ");
		System.out.println();
		for (int i : lPlayerCards)
			System.out.print(i + " ");
		System.out.println();
		for (int i : tPlayerCards)
			System.out.print(i + " ");
		System.out.println();
		for (int i : rPlayerCards)
			System.out.print(i + " ");
		System.out.println();
		for (int i : talonCards)
			System.out.print(i + " ");
		System.out.println();
	}

	// Inicializira napovedi.
	private void initAnnouncements() {
		announcements = new HashMap<String, Boolean>();
		announcements.put("Kings", false);
		announcements.put("KUltimo", false);
		announcements.put("Pagat", false);
		announcements.put("Trula", false);
		announcements.put("Valat", false);

	}

	// Uredi igralčeve karte po vrsti.
	public void orderPlayerCards() {
		Collections.sort(playerCards);
	}

	// Preveri katera licitacija/igra je bila izbrana.
	public void contractPicker() {
		if (il.isFlag(16))
			gamePicked = "Three";
		else if (il.isFlag(17))
			gamePicked = "Two";
		else if (il.isFlag(18))
			gamePicked = "One";
		else if (il.isFlag(19))
			gamePicked = "Klop";
		else if (il.isFlag(20))
			gamePicked = "SoloThree";
		else if (il.isFlag(21))
			gamePicked = "SoloTwo";
		else if (il.isFlag(22))
			gamePicked = "SoloOne";
		else if (il.isFlag(23))
			gamePicked = "SoloWithout";
		else if (il.isFlag(24))
			gamePicked = "Beggar";
		else if (il.isFlag(25))
			gamePicked = "OpenBeggar";
		else if (il.isFlag(26))
			gamePicked = "ColorValat";
		else if (il.isFlag(27))
			gamePicked = "Valat";
	}

	// Preveri, če je igra že izbrana.
	public boolean contractNotPicked() {
		boolean contract;
		if (gamePicked == null)
			contract = true;
		else
			contract = false;
		return contract;
	}

	// Vrne true, če je igra igrana in točkovana na normalen način (tri, dva, ena,
	// in solo verzije njih).
	public boolean normalGames() {
		boolean isNormal;
		if (gamePicked == "Three" || gamePicked == "Two" || gamePicked == "One" || gamePicked == "SoloThree"
				|| gamePicked == "SoloTwo" || gamePicked == "SoloOne" || gamePicked == "SoloWithout")
			isNormal = true;
		else
			isNormal = false;
		return isNormal;
	}

	// Vrne true, če igra zahteva partnerja.
	public boolean pickKing() {
		boolean kingApplicable;
		if (gamePicked == "Three" || gamePicked == "Two" || gamePicked == "One")
			kingApplicable = true;
		else
			kingApplicable = false;
		return kingApplicable;
	}

	// Določi na koliko delov bo šel talon.
	public int talonSplitter() {
		int split = 0;
		if (gamePicked == "One" || gamePicked == "SoloOne")
			split = 1;
		else if (gamePicked == "Two" || gamePicked == "SoloTwo")
			split = 2;
		else if (gamePicked == "Three" || gamePicked == "SoloThree")
			split = 3;
		return split;
	}

	// Vrne true, če je igra solo verzija normalne.
	public boolean isSolo() {
		boolean solo = false;
		if (gamePicked == "SoloOne" || gamePicked == "SoloTwo" || gamePicked == "SoloThree")
			solo = true;
		return solo;
	}

	// Vrne tabelo indeksov kart za menjavo pri talonu.
	public int[] switchWithTalon() {
		int[] index = { 0, 0, 0 };
		if (talonSplitter() == 1 && il.isFlag(28))
			index[0] = 48;
		else if (talonSplitter() == 1 && il.isFlag(29))
			index[0] = 49;
		else if (talonSplitter() == 1 && il.isFlag(30))
			index[0] = 50;
		else if (talonSplitter() == 1 && il.isFlag(31))
			index[0] = 51;
		else if (talonSplitter() == 1 && il.isFlag(32))
			index[0] = 52;
		else if (talonSplitter() == 1 && il.isFlag(33))
			index[0] = 53;
		else if (talonSplitter() == 2 && (il.isFlag(28) || il.isFlag(29))) {
			index[0] = 48;
			index[1] = 49;
		} else if (talonSplitter() == 2 && (il.isFlag(30) || il.isFlag(31))) {
			index[0] = 50;
			index[1] = 51;
		} else if (talonSplitter() == 2 && (il.isFlag(32) || il.isFlag(33))) {
			index[0] = 52;
			index[1] = 53;
		} else if (talonSplitter() == 3 && (il.isFlag(28) || il.isFlag(29) || il.isFlag(30))) {
			index[0] = 48;
			index[1] = 49;
			index[2] = 50;
		} else if (talonSplitter() == 3 && (il.isFlag(31) || il.isFlag(32) || il.isFlag(33))) {
			index[0] = 51;
			index[1] = 52;
			index[2] = 53;
		}
		for (int i = 0; i < 3; i++) {
			System.out.print(index[i]);
		}
		return index;
	}

	// Preveri kateri kralj je bil izbran.
	public void kingPicker() {
		if (il.isFlag(35))
			kingPicked = 29;
		if (il.isFlag(36))
			kingPicked = 37;
		if (il.isFlag(37))
			kingPicked = 45;
		if (il.isFlag(38))
			kingPicked = 53;
	}

	// Poišče partnerja glede na klicanega kralja.
	public void findPartner() {
		for (int i = 0; i < 12; i++) {
			if (playerCards.get(i) == kingPicked)
				partner = "SELF";
		}
		for (int i = 0; i < 12; i++) {
			if (lPlayerCards.get(i) == kingPicked)
				partner = "LEFT";
		}
		for (int i = 0; i < 12; i++) {
			if (rPlayerCards.get(i) == kingPicked)
				partner = "RIGHT";
		}
		for (int i = 0; i < 12; i++) {
			if (tPlayerCards.get(i) == kingPicked)
				partner = "TOP";
		}
		for (int i = 0; i < 6; i++) {
			if (talonCards.get(i) == kingPicked)
				partner = "SELF";
		}
	}

	// Vrne true, če igra zahteva odprt talon.
	public boolean openTalon() {
		boolean temp = false;
		if ((gamePicked == "Three" || gamePicked == "Two" || gamePicked == "One") && kingPicked != -1 || isSolo())
			temp = true;
		return temp;
	}

	// Preveri izbrane napovedi.
	public void announcePicker() {
		if (il.isFlag(39))
			announcements.put("Kings", true);
		if (il.isFlag(40))
			announcements.put("KUltimo", true);
		if (il.isFlag(41))
			announcements.put("Pagat", true);
		if (il.isFlag(42))
			announcements.put("Trula", true);
		if (il.isFlag(43))
			announcements.put("Valat", true);
		System.out.print(announcements.entrySet() + "\n");

	}

	// Vrne, kdo začne krog igre.
	public String roundStart() {
		String temp = "";
		if (gameRoundCounter == 0) {
			temp = "BOTTOM";
		} else if (gameRoundCounter == 1) {
			temp = "RIGHT";
		} else if (gameRoundCounter == 2) {
			temp = "TOP";
		} else if (gameRoundCounter == 3) {
			temp = "LEFT";
		}
		return temp;

	}

	// Logika za igralca računalnika, ki vrže prvo karto.
	public int cardPlayBot(List<Integer> bot) {
		int temp = -1;
		if (!bot.isEmpty()) {
			temp = bot.get(0);
			if (bot.size() == 1)
				addLastRound(temp);
			bot.remove(0);
		}
		playedCard = temp;
		if (bot == rPlayerCards) {
			tempRight = temp;
		} else if (bot == tPlayerCards) {
			tempTop = temp;
		} else if (bot == lPlayerCards) {
			tempLeft = temp;
		}
		return temp;
	}

	// Logika igralca bota, ki vrže karto na že igrano karto.
	public int cardComparatorBot(List<Integer> bot) {
		int temp = -1;
		int playedCardStr = ct.cardStrength(playedCard);
		String playedCardSuit = ct.cardSuit(playedCard);
		boolean eligible = false;
		if (bot.size() == 1)
			addLastRound(bot.get(0));
		for (int i = 0; i < bot.size(); i++) {
			String botSuit = ct.cardSuit(bot.get(i));
			int botStr = ct.cardStrength(bot.get(i));
			if (playedCardSuit == botSuit && playedCardStr < botStr && !eligible) {
				temp = bot.get(i);
				eligible = true;
				bot.remove(i);
			}
		}

		for (int i = 0; i < bot.size(); i++) {
			String botSuit = ct.cardSuit(bot.get(i));
			if (playedCardSuit == botSuit && !eligible) {
				temp = bot.get(i);
				eligible = true;
				bot.remove(i);
			}
		}

		for (int i = 0; i < bot.size(); i++) {
			String botSuit = ct.cardSuit(bot.get(i));
			if (botSuit == "SUIT.TAROK" && !eligible) {
				temp = bot.get(i);
				eligible = true;
				bot.remove(i);
			}
		}
		if (!bot.isEmpty() && !eligible) {
			temp = bot.get(0);
			eligible = true;
			bot.remove(0);
		}

		if (bot == rPlayerCards) {
			tempRight = temp;
		} else if (bot == tPlayerCards) {
			tempTop = temp;
		} else if (bot == lPlayerCards) {
			tempLeft = temp;
		}
		return temp;
	}

	// Logika, ki določi zmagovalca kroga, in mu prišteje točke kroga.
	public void roundWinner() {
		int temp = playedCard;

		int[] comparables = { tempBottom, tempLeft, tempRight, tempTop };
		List<Integer> score = Arrays.asList(tempBottom, tempLeft, tempRight, tempTop);
		if (ct.cardSuit(playedCard) == "SUIT.TAROK") {
			for (int i : comparables)
				if (ct.cardSuit(i) == "SUIT.TAROK" && ct.cardStrength(i) > ct.cardStrength(temp))
					temp = i;
		} else if (ct.cardSuit(playedCard) != "SUIT.TAROK"
				&& (ct.cardSuit(tempRight) == "SUIT.TAROK" || ct.cardSuit(tempTop) == "SUIT.TAROK"
						|| ct.cardSuit(tempLeft) == "SUIT.TAROK" || ct.cardSuit(tempBottom) == "SUIT.TAROK")) {
			for (int i : comparables) {
				if (ct.cardSuit(temp) != "SUIT.TAROK" && ct.cardSuit(i) == "SUIT.TAROK")
					temp = i;
				else if (ct.cardSuit(temp) == "SUIT.TAROK" && ct.cardSuit(i) == "SUIT.TAROK"
						&& ct.cardStrength(i) > ct.cardStrength(temp))
					temp = i;
			}
		} else {
			for (int i : comparables)
				if (ct.cardSuit(playedCard) == ct.cardSuit(i) && ct.cardStrength(i) > ct.cardStrength(temp))
					temp = i;

		}
		if (temp == tempRight) {
			gameRoundCounter = 1;
			rightScoreCards.addAll(score);
			if (gamePicked == "Klop" && !talonCards.isEmpty()) {
				rightScoreCards.add(talonCards.get(0));
				talonCards.remove(0);
			}
		} else if (temp == tempTop) {
			gameRoundCounter = 2;
			topScoreCards.addAll(score);
			if (gamePicked == "Klop" && !talonCards.isEmpty()) {
				topScoreCards.add(talonCards.get(0));
				talonCards.remove(0);
			}
		} else if (temp == tempLeft) {
			gameRoundCounter = 3;
			leftScoreCards.addAll(score);
			if (gamePicked == "Klop" && !talonCards.isEmpty()) {
				leftScoreCards.add(talonCards.get(0));
				talonCards.remove(0);
			}
		} else if (temp == tempBottom) {
			gameRoundCounter = 0;
			playerScoreCards.addAll(score);
			if (gamePicked == "Klop" && !talonCards.isEmpty()) {
				playerScoreCards.add(talonCards.get(0));
				talonCards.remove(0);
			}
		} else
			System.out.println("Error");

	}

	// Logika, ki določi zmagovalca kroga na barvnem valatu.
	public void colorValatRoundWinner() {
		int temp = playedCard;
		int[] comparables = { tempBottom, tempLeft, tempRight, tempTop };
		List<Integer> score = Arrays.asList(tempBottom, tempLeft, tempRight, tempTop);

		if (ct.cardSuit(playedCard) == "SUIT.TAROK") {
			for (int i : comparables)
				if (ct.cardSuit(i) == "SUIT.TAROK" && ct.cardStrength(i) > ct.cardStrength(temp))
					temp = i;
		} else {
			for (int i : comparables)
				if (ct.cardSuit(playedCard) == ct.cardSuit(i) && ct.cardStrength(i) > ct.cardStrength(temp))
					temp = i;
		}

		if (temp == tempRight) {
			gameRoundCounter = 1;
			rightScoreCards.addAll(score);

		} else if (temp == tempTop) {
			gameRoundCounter = 2;
			topScoreCards.addAll(score);

		} else if (temp == tempLeft) {
			gameRoundCounter = 3;
			leftScoreCards.addAll(score);

		} else if (temp == tempBottom) {
			gameRoundCounter = 0;
			playerScoreCards.addAll(score);

		} else
			System.out.println("Error");

	}

	// Metoda, ki doda obdržane karte iz talona.
	public void addKeptCards(int cardIndex) {
		keptCards.add(cardIndex);
	}

	// Določi uspele bonuse/napovedi trula, kralji in valat.
	public void globalBonuses() {
		if (partner == "SELF" || isSolo() || gamePicked == "SoloWithout") {
			if (playerScoreCards.contains(29) && playerScoreCards.contains(37) && playerScoreCards.contains(45)
					&& playerScoreCards.contains(53)) {
				if (announcements.get("Kings") == true)
					globalDiff += 20;
				else
					globalDiff += 10;
			}

			if (playerScoreCards.contains(0) && playerScoreCards.contains(20) && playerScoreCards.contains(21)) {
				if (announcements.get("Trula") == true)
					globalDiff += 20;
				else
					globalDiff += 10;
			}

			if (leftScoreCards.isEmpty() && topScoreCards.isEmpty() && rightScoreCards.isEmpty()) {
				valat = true;
				if (announcements.get("Valat") == true)
					globalDiff = 500;
				else
					globalDiff = 250;

			} else if (!leftScoreCards.isEmpty() && !topScoreCards.isEmpty() && !rightScoreCards.isEmpty()
					&& announcements.get("Valat") == true) {
				valat = true;
				globalDiff = -500;
			}

		} else if (partner == "LEFT") {
			if ((playerScoreCards.contains(29) || leftScoreCards.contains(29))
					&& (playerScoreCards.contains(37) || leftScoreCards.contains(37))
					&& (playerScoreCards.contains(45) || leftScoreCards.contains(45))
					&& (playerScoreCards.contains(53) || leftScoreCards.contains(53))) {
				if (announcements.get("Kings") == true)
					globalDiff += 20;
				else
					globalDiff += 10;
			}
			if ((playerScoreCards.contains(0) || leftScoreCards.contains(0))
					&& (playerScoreCards.contains(20) || leftScoreCards.contains(20))
					&& (playerScoreCards.contains(21) || leftScoreCards.contains(21))) {
				if (announcements.get("Trula") == true)
					globalDiff += 20;
				else
					globalDiff += 10;
			}
			if (topScoreCards.isEmpty() && rightScoreCards.isEmpty()) {
				valat = true;
				if (announcements.get("Valat") == true)
					globalDiff = 500;
				else
					globalDiff = 250;
			} else if (!topScoreCards.isEmpty() && !rightScoreCards.isEmpty() && announcements.get("Valat") == true) {
				valat = true;
				globalDiff = -500;
			}

		} else if (partner == "TOP") {
			if ((playerScoreCards.contains(29) || topScoreCards.contains(29))
					&& (playerScoreCards.contains(37) || topScoreCards.contains(37))
					&& (playerScoreCards.contains(45) || topScoreCards.contains(45))
					&& (playerScoreCards.contains(53) || topScoreCards.contains(53))) {
				if (announcements.get("Kings") == true)
					globalDiff += 20;
				else
					globalDiff += 10;
			}
			if ((playerScoreCards.contains(0) || topScoreCards.contains(0))
					&& (playerScoreCards.contains(20) || topScoreCards.contains(20))
					&& (playerScoreCards.contains(21) || topScoreCards.contains(21))) {
				if (announcements.get("Trula") == true)
					globalDiff += 20;
				else
					globalDiff += 10;
			}

			if (leftScoreCards.isEmpty() && rightScoreCards.isEmpty()) {
				valat = true;
				if (announcements.get("Valat") == true)
					globalDiff = 500;
				else
					globalDiff = 250;
			} else if (!leftScoreCards.isEmpty() && !rightScoreCards.isEmpty() && announcements.get("Valat") == true) {
				valat = true;
				globalDiff = -500;
			}
		} else if (partner == "RIGHT") {
			if ((playerScoreCards.contains(29) || rightScoreCards.contains(29))
					&& (playerScoreCards.contains(37) || rightScoreCards.contains(37))
					&& (playerScoreCards.contains(45) || rightScoreCards.contains(45))
					&& (playerScoreCards.contains(53) || rightScoreCards.contains(53))) {
				if (announcements.get("Kings") == true)
					globalDiff += 20;
				else
					globalDiff += 10;
			}
			if ((playerScoreCards.contains(0) || rightScoreCards.contains(0))
					&& (playerScoreCards.contains(20) || rightScoreCards.contains(20))
					&& (playerScoreCards.contains(21) || rightScoreCards.contains(21))) {
				if (announcements.get("Trula") == true)
					globalDiff += 20;
				else
					globalDiff += 10;
			}

			if (leftScoreCards.isEmpty() && topScoreCards.isEmpty()) {
				valat = true;
				if (announcements.get("Valat") == true)
					globalDiff = 500;
				else
					globalDiff = 250;
			} else if (!leftScoreCards.isEmpty() && !topScoreCards.isEmpty() && announcements.get("Valat") == true) {
				valat = true;
				globalDiff = -500;
			}

		}
	}

	// Določi uspela bonusa/napovedi kralj ultimo in pagat ultimo.
	public void lastRoundBonuses() {

		if (lastRoundList.contains(0) && announcements.get("Pagat") == true)
			globalDiff += 50;
		else if (lastRoundList.contains(0) && announcements.get("Pagat") == false)
			globalDiff += 25;
		if (lastRoundList.contains(kingPicked) && announcements.get("KUltimo") == true)
			globalDiff += 20;
		else if (lastRoundList.contains(kingPicked) && announcements.get("KUltimo") == false)
			globalDiff += 10;

	}

	// Točkuje dobljene karte posameznih igralcev, vrne število točk, ki jih je
	// dobil posameznik.
	public int individualCountScoring(String player) {
		List<Integer> temp = null;
		int score = 0;

		if (player == "BOTTOM") {
			temp = playerScoreCards;
		} else if (player == "LEFT") {
			temp = leftScoreCards;
		} else if (player == "TOP") {
			temp = topScoreCards;
		} else if (player == "RIGHT") {
			temp = rightScoreCards;
		} else if (player == "KEPT") {
			temp = keptCards;
		}
		while (!temp.isEmpty()) {
			if (temp.size() >= 3) {
				int tempScore = 0;

				for (int i = 0; i < 3; i++) {
					int tempCard = temp.get(0);
					temp.remove(0);
					if ((ct.cardSuit(tempCard) == "SUIT.TAROK" && (ct.cardStrength(tempCard) != 22
							|| ct.cardStrength(tempCard) != 21 || ct.cardStrength(tempCard) != 1))
							|| (ct.cardSuit(tempCard) != "SUIT.TAROK" && ct.cardStrength(tempCard) < 5)) {
						tempScore++;
					} else if ((ct.cardSuit(tempCard) == "SUIT.TAROK" && (ct.cardStrength(tempCard) == 22
							|| ct.cardStrength(tempCard) == 21 || ct.cardStrength(tempCard) == 1))) {
						tempScore += 5;
					} else {
						tempScore += ct.cardStrength(tempCard) - 3;
					}

				}
				tempScore -= 2;
				score += tempScore;
			} else if (temp.size() < 3) {
				int fullCount = 0;
				int tempScore = 0;

				for (int i = 0; i < temp.size(); i++) {
					int tempCard = temp.get(0);
					temp.remove(0);
					if ((ct.cardSuit(tempCard) == "SUIT.TAROK" && (ct.cardStrength(tempCard) != 22
							|| ct.cardStrength(tempCard) != 21 || ct.cardStrength(tempCard) != 1))
							|| (ct.cardSuit(tempCard) != "SUIT.TAROK" && ct.cardStrength(tempCard) < 5)) {
						tempScore++;
						fullCount++;
					} else if ((ct.cardSuit(tempCard) == "SUIT.TAROK" && (ct.cardStrength(tempCard) == 22
							|| ct.cardStrength(tempCard) == 21 || ct.cardStrength(tempCard) == 1))) {
						tempScore += 5;
						fullCount++;
					} else {
						tempScore += ct.cardStrength(tempCard) - 3;
						fullCount++;
					}

				}
				if (fullCount > 1)
					tempScore -= fullCount - 1;
				score += tempScore;
			}
		}
		System.out.println(score + " " + player);
		return score;
	}

	// Metoda za dodajanje kart na list lastRoundList.
	public void addLastRound(int card) {
		lastRoundList.add(card);
	}

	// Točkovanje normalnih iger.
	public void normalScoring() {
		lastRoundBonuses();
		globalBonuses();

		int diff = 0;
		bottomScore = 0;
		leftScore = 0;
		topScore = 0;
		rightScore = 0;
		if (valat) {
			if (gamePicked == "Three" || gamePicked == "Two" || gamePicked == "One") {
				if (partner == "SELF") {
					bottomScore = globalDiff;
				} else if (partner == "LEFT") {
					bottomScore = globalDiff;
					leftScore = globalDiff;
				} else if (partner == "TOP") {
					bottomScore = globalDiff;
					topScore = globalDiff;
				} else if (partner == "RIGHT") {
					bottomScore = globalDiff;
					rightScore = globalDiff;
				}
			} else {
				bottomScore = globalDiff;
			}

		} else if (gamePicked == "Three" || gamePicked == "Two" || gamePicked == "One") {

			int tempPair = 0;
			if (partner == "SELF") {
				tempPair = individualCountScoring("BOTTOM") + individualCountScoring("KEPT");
			} else if (partner == "LEFT") {
				tempPair = individualCountScoring("BOTTOM") + individualCountScoring("KEPT")
						+ individualCountScoring("LEFT");
			} else if (partner == "TOP") {
				tempPair = individualCountScoring("BOTTOM") + individualCountScoring("KEPT")
						+ individualCountScoring("TOP");
			} else if (partner == "RIGHT") {
				tempPair = individualCountScoring("BOTTOM") + individualCountScoring("KEPT")
						+ individualCountScoring("RIGHT");
			}
			diff = tempPair - 35;
			if (diff >= 0) {
				if (gamePicked == "Three")
					diff += 10 + globalDiff;
				else if (gamePicked == "Two")
					diff += 20 + globalDiff;
				if (gamePicked == "One")
					diff += 30 + globalDiff;
			} else {
				if (gamePicked == "Three")
					diff -= (10 + globalDiff);
				else if (gamePicked == "Two")
					diff -= (20 + globalDiff);
				if (gamePicked == "One")
					diff -= (30 + globalDiff);
			}

			if (partner == "SELF") {
				bottomScore = diff;
			} else if (partner == "LEFT") {
				bottomScore = diff;
				leftScore = diff;
			} else if (partner == "TOP") {
				bottomScore = diff;
				topScore = diff;
			} else if (partner == "RIGHT") {
				bottomScore = diff;
				rightScore = diff;
			}
		} else if (isSolo()) {
			diff = individualCountScoring("BOTTOM") + individualCountScoring("KEPT") - 35;
			if (diff >= 0) {
				if (gamePicked == "SoloThree")
					diff += 40 + globalDiff;
				else if (gamePicked == "SoloTwo")
					diff += 50 + globalDiff;
				else if (gamePicked == "SoloOne")
					diff += 60 + globalDiff;
			} else {
				if (gamePicked == "SoloThree")
					diff -= (40 + globalDiff);
				else if (gamePicked == "SoloTwo")
					diff -= (50 + globalDiff);
				else if (gamePicked == "SoloOne")
					diff -= (60 + globalDiff);

			}
			bottomScore = diff;
		} else if (gamePicked == "SoloWithout") {
			diff = individualCountScoring("BOTTOM") - 35;
			if (diff >= 0)
				diff += 80 + globalDiff;
			else
				diff -= (80 + globalDiff);
			bottomScore = diff;
		}

	}

	// Točkovanje igre valat.
	public void ValatGameScoring() {
		leftScore = 0;
		topScore = 0;
		rightScore = 0;
		if (specialGameMade)
			bottomScore = 500;
		else if (!specialGameMade)
			bottomScore = -500;
	}

	// Vrne true, če je igra ena izmed beračev.
	public boolean isBeggarGame() {
		boolean temp;
		if (gamePicked == "OpenBeggar" || gamePicked == "Beggar")
			temp = true;
		else
			temp = false;
		return temp;
	}

	// Točkovanje beračev.
	public void beggarGamesScoring() {
		leftScore = 0;
		topScore = 0;
		rightScore = 0;
		if (specialGameMade) {
			if (gamePicked == "Beggar")
				bottomScore = 70;
			else if (gamePicked == "OpenBeggar")
				bottomScore = 90;
		} else {
			if (gamePicked == "Beggar")
				bottomScore = -70;
			else if (gamePicked == "OpenBeggar")
				bottomScore = -90;
		}

	}

	// Točkovanje igre klopa.
	public void klopGameScoring() {
		bottomScore = 0;
		leftScore = 0;
		topScore = 0;
		rightScore = 0;
		int tempPlayer = individualCountScoring("BOTTOM");
		int tempRight = individualCountScoring("RIGHT");
		int tempTop = individualCountScoring("TOP");
		int tempLeft = individualCountScoring("LEFT");

		if (tempPlayer > 35)
			bottomScore = -70;
		else if (tempRight > 35)
			rightScore = -70;
		else if (tempTop > 35)
			topScore = -70;
		else if (tempLeft > 35)
			leftScore = -70;
		else {
			bottomScore = -tempPlayer;
			leftScore = -tempLeft;
			topScore = -tempTop;
			rightScore = -tempRight;
		}
	}

	// Točkovanje barvnega valata.
	public void colorValatGameScoring() {
		leftScore = 0;
		topScore = 0;
		rightScore = 0;

		if (specialGameMade)
			bottomScore = 125;
		else if (!specialGameMade)
			bottomScore = -125;
	}

	// Metoda, ki ponastavi vrednosti nazaj na njihove začetne, za igranje več
	// zaporednih iger.
	public void reset() {
		dealCards();
		initAnnouncements();
		gamePicked = null;
		kingPicked = -1;
		partner = null;
		gameRoundCounter = 0;
		globalDiff = 0;
	}

	// Vsi getterji in setterji za spremenljivke razreda.

	public List<Integer> getPlayerCards() {
		return playerCards;
	}

	public void setPlayerCards(List<Integer> playerCards) {
		this.playerCards = playerCards;
	}

	public List<Integer> getlPlayerCards() {
		return lPlayerCards;
	}

	public void setlPlayerCards(List<Integer> lPlayerCards) {
		this.lPlayerCards = lPlayerCards;
	}

	public List<Integer> gettPlayerCards() {
		return tPlayerCards;
	}

	public void settPlayerCards(List<Integer> tPlayerCards) {
		this.tPlayerCards = tPlayerCards;
	}

	public List<Integer> getrPlayerCards() {
		return rPlayerCards;
	}

	public void setrPlayerCards(List<Integer> rPlayerCards) {
		this.rPlayerCards = rPlayerCards;
	}

	public List<Integer> getTalonCards() {
		return talonCards;
	}

	public void setTalonCards(List<Integer> talonCards) {
		this.talonCards = talonCards;
	}

	public String getGamePicked() {
		return gamePicked;
	}

	public int getKingPicked() {
		return kingPicked;
	}

	public void setKingPicked(int kingPicked) {
		this.kingPicked = kingPicked;
	}

	public String getPartner() {
		return partner;
	}

	public void setSpecialGameMade(boolean specialGameMade) {
		this.specialGameMade = specialGameMade;
	}

	public int getPlayedCard() {
		return playedCard;
	}

	public void setPlayedCard(int playedCard) {
		this.playedCard = playedCard;
	}

	public void setTempBottom(int tempBottom) {
		this.tempBottom = tempBottom;
	}

	public int getBottomScore() {
		return bottomScore;
	}

	public int getLeftScore() {
		return leftScore;
	}

	public int getTopScore() {
		return topScore;
	}

	public int getRightScore() {
		return rightScore;
	}

}
