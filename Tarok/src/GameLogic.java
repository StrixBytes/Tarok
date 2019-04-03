import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class GameLogic {
	private static final long serialVersionUID = 1L;
	private ImageLoader il;
	private List<Integer> cards;
	private CardTranslator ct;
	private String gamePicked = null;
	private int kingPicked = -1;

	private List<Integer> playerCards, lPlayerCards, tPlayerCards, rPlayerCards, talonCards;
	private HashMap<String, Boolean> announcements;
	private String partner;
	private int playedCard = -1;
	private int gameRoundCounter = 0;
	private int tempRight, tempTop, tempLeft;

	public GameLogic(ImageLoader il, List<Integer> cards) {
		this.il = il;
		this.cards = cards;
		ct = new CardTranslator();
		dealCards();
		initAnnouncements();
	}

	private void dealCards() {
		playerCards = new ArrayList<Integer>(cards.subList(0, 12));
		lPlayerCards = new ArrayList<Integer>(cards.subList(12, 24));
		tPlayerCards = new ArrayList<Integer>(cards.subList(24, 36));
		rPlayerCards = new ArrayList<Integer>(cards.subList(36, 48));
		talonCards = new ArrayList<Integer>(cards.subList(48, 54));
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

	private void initAnnouncements() {
		announcements = new HashMap<String, Boolean>();
		announcements.put("Kings", false);
		announcements.put("KUltimo", false);
		announcements.put("Pagat", false);
		announcements.put("Trula", false);
		announcements.put("Valat", false);

	}

	public void orderPlayerCards() {
		Collections.sort(playerCards);
	}

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

	public boolean contractNotPicked() {
		boolean contract;
		if (gamePicked == null)
			contract = true;
		else
			contract = false;
		return contract;
	}

	public boolean showTalon() {
		boolean talonApplicable;
		if (gamePicked == "Three" || gamePicked == "Two" || gamePicked == "One" || gamePicked == "SoloThree"
				|| gamePicked == "SoloTwo" || gamePicked == "SoloOne")
			talonApplicable = true;
		else
			talonApplicable = false;
		return talonApplicable;
	}

	public boolean pickKing() {
		boolean kingApplicable;
		if (gamePicked == "Three" || gamePicked == "Two" || gamePicked == "One")
			kingApplicable = true;
		else
			kingApplicable = false;
		return kingApplicable;
	}

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

	public boolean isSolo() {
		boolean solo = false;
		if (gamePicked == "SoloOne" || gamePicked == "SoloTwo" || gamePicked == "SoloThree")
			solo = true;
		return solo;
	}

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

	public void kingPicker() {
		if (il.isFlag(35))
			setKingPicked(29);
		if (il.isFlag(36))
			setKingPicked(37);
		if (il.isFlag(37))
			setKingPicked(45);
		if (il.isFlag(38))
			setKingPicked(53);
	}

	public int getKingPicked() {
		return kingPicked;
	}

	public void setKingPicked(int kingPicked) {
		this.kingPicked = kingPicked;
	}

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
	}

	public String getPartner() {
		return partner;
	}

	public boolean announce() {
		boolean temp = false;
		if (kingPicked != -1 || isSolo())
			temp = true;
		return temp;
	}

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

	public int getPlayedCard() {
		return playedCard;
	}

	public void setPlayedCard(int playedCard) {
		this.playedCard = playedCard;
	}

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

	public int rightBot() {
		tempRight = -1;
		int playedCardStr = ct.cardStrength(playedCard);
		String playedCardSuit = ct.cardSuit(playedCard);
		boolean eligible = false;
		for (int i = 0; i < rPlayerCards.size(); i++) {
			String rightSuit = ct.cardSuit(rPlayerCards.get(i));
			int rightStr = ct.cardStrength(rPlayerCards.get(i));
			if (playedCardSuit == rightSuit && playedCardStr < rightStr && !eligible) {
				tempRight = rPlayerCards.get(i);
				eligible = true;
				rPlayerCards.remove(i);
				System.out.println("Run t1");
			}
		}

		for (int i = 0; i < rPlayerCards.size(); i++) {
			String rightSuit = ct.cardSuit(rPlayerCards.get(i));
			if (playedCardSuit == rightSuit && !eligible) {
				tempRight = rPlayerCards.get(i);
				eligible = true;
				rPlayerCards.remove(i);
				System.out.println("Run t2");
			}
		}

		for (int i = 0; i < rPlayerCards.size(); i++) {
			String rightSuit = ct.cardSuit(rPlayerCards.get(i));
			if (rightSuit == "SUIT.TAROK" && !eligible) {
				tempRight = rPlayerCards.get(i);
				eligible = true;
				rPlayerCards.remove(i);
				System.out.println("Run t3");
			}
		}
		if (!rPlayerCards.isEmpty() && !eligible) {
			tempRight = rPlayerCards.get(0);
			eligible = true;
			rPlayerCards.remove(0);
			System.out.println("Run t4");
		}
		return tempRight;
	}

	public int topBot() {
		tempTop = -1;
		int playedCardStr = ct.cardStrength(playedCard);
		String playedCardSuit = ct.cardSuit(playedCard);
		boolean eligible = false;
		for (int i = 0; i < tPlayerCards.size(); i++) {
			String topSuit = ct.cardSuit(tPlayerCards.get(i));
			int topStr = ct.cardStrength(tPlayerCards.get(i));
			if (playedCardSuit == topSuit && playedCardStr < topStr && !eligible) {
				tempTop = tPlayerCards.get(i);
				eligible = true;
				tPlayerCards.remove(i);
				System.out.println("Run t1");
			}
		}

		for (int i = 0; i < tPlayerCards.size(); i++) {
			String topSuit = ct.cardSuit(tPlayerCards.get(i));
			if (playedCardSuit == topSuit && !eligible) {
				tempTop = tPlayerCards.get(i);
				eligible = true;
				tPlayerCards.remove(i);
				System.out.println("Run t2");
			}
		}

		for (int i = 0; i < tPlayerCards.size(); i++) {
			String topSuit = ct.cardSuit(tPlayerCards.get(i));
			if (topSuit == "SUIT.TAROK" && !eligible) {
				tempTop = tPlayerCards.get(i);
				eligible = true;
				tPlayerCards.remove(i);
				System.out.println("Run t3");
			}
		}
		if (!tPlayerCards.isEmpty() && !eligible) {
			tempTop = tPlayerCards.get(0);
			eligible = true;
			tPlayerCards.remove(0);
			System.out.println("Run t4");
		}
		return tempTop;
	}

	public int leftBot() {
		tempLeft = -1;
		int playedCardStr = ct.cardStrength(playedCard);
		String playedCardSuit = ct.cardSuit(playedCard);
		boolean eligible = false;
		for (int i = 0; i < lPlayerCards.size(); i++) {
			String leftSuit = ct.cardSuit(lPlayerCards.get(i));
			int leftStr = ct.cardStrength(lPlayerCards.get(i));
			if (playedCardSuit == leftSuit && playedCardStr < leftStr && !eligible) {
				tempLeft = lPlayerCards.get(i);
				eligible = true;
				lPlayerCards.remove(i);
				System.out.println("Run t1");
			}
		}

		for (int i = 0; i < lPlayerCards.size(); i++) {
			String leftSuit = ct.cardSuit(lPlayerCards.get(i));
			if (playedCardSuit == leftSuit && !eligible) {
				tempLeft = lPlayerCards.get(i);
				eligible = true;
				lPlayerCards.remove(i);
				System.out.println("Run t2");
			}
		}

		for (int i = 0; i < lPlayerCards.size(); i++) {
			String leftSuit = ct.cardSuit(lPlayerCards.get(i));
			if (leftSuit == "SUIT.TAROK" && !eligible) {
				tempLeft = lPlayerCards.get(i);
				eligible = true;
				lPlayerCards.remove(i);
				System.out.println("Run t3");
			}
		}
		if (!lPlayerCards.isEmpty() && !eligible) {
			tempLeft = lPlayerCards.get(0);
			eligible = true;
			lPlayerCards.remove(0);
			System.out.println("Run t4");
		}
		return tempLeft;

	}

	public void roundWinner() {
		int temp = playedCard;
		int[] cardTable = { tempRight, tempTop, tempLeft };
		if (ct.cardSuit(playedCard) == "SUIT.TAROK") {
			for (int i = 0; i < 3; i++)
				if (ct.cardSuit(cardTable[i]) == "SUIT.TAROK" && ct.cardStrength(cardTable[i]) > ct.cardStrength(temp))
					temp = cardTable[i];
		} else if(ct.cardSuit(playedCard) != "SUIT.TAROK"&&(ct.cardSuit(tempRight)=="SUIT.TAROK"||ct.cardSuit(tempTop)=="SUIT.TAROK"||ct.cardSuit(tempLeft)=="SUIT.TAROK")){
			for (int i = 0; i < 3; i++)
				if (ct.cardSuit(cardTable[i]) == "SUIT.TAROK" && ct.cardStrength(cardTable[i]) > ct.cardStrength(temp))
					temp = cardTable[i];
		}else if (ct.cardSuit(playedCard) != "SUIT.TAROK") {
			for (int i = 0; i < 3; i++)
				if (ct.cardSuit(playedCard) == ct.cardSuit(cardTable[i])
						&& ct.cardStrength(cardTable[i]) > ct.cardStrength(temp))
					temp = cardTable[i];
		}

		
		
		
		if (temp == tempRight)
			gameRoundCounter = 1;
		else if (temp == tempTop)
			gameRoundCounter = 2;
		else if (temp == tempLeft)
			gameRoundCounter = 3;
		else if (temp == playedCard)
			gameRoundCounter = 0;
		System.out.println(roundStart());
	}
}
