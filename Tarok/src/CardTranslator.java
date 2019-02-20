public class CardTranslator {
	
	public CardTranslator() {
	}

	public String cardToImage(int i) {
		String imgName = null;
		if (i < 22)
			imgName = "/Cards/" + (i + 1) + ".jpg";
		else if (22 <= i && i < 30)
			imgName = "/Cards/C" + (i - 21) + ".jpg";
		else if (30 <= i && i < 38)
			imgName = "/Cards/H" + (i - 29) + ".jpg";
		else if (38 <= i && i < 46)
			imgName = "/Cards/S" + (i - 37) + ".jpg";
		else if (46 <= i && i < 54)
			imgName = "/Cards/D" + (i - 45) + ".jpg";
//		System.out.println(imgName);
		return imgName;
	}

	public String cardSuit(int i) {
		String suit = null;
		if (i < 22)
			suit = "SUIT.TAROK";
		else if (22 <= i && i < 30)
			suit = "SUIT.CLUBS";
		else if (30 <= i && i < 38)
			suit = "SUIT.HEARTS";
		else if (38 <= i && i < 46)
			suit = "SUIT.SPADES";
		else if (46 <= i && i < 54)
			suit = "SUIT.DIAMONDS";
		return suit;
	}

	public int cardStrength(int i) {
		int strength = 0;
		if (i < 22)
			strength = i + 1;
		else if (22 <= i && i < 30)
			strength = i - 21;
		else if (30 <= i && i < 38)
			strength = i - 29;
		else if (38 <= i && i < 46)
			strength = i - 37;
		else if (46 <= i && i < 54)
			strength = i - 45;
		return strength;
	}

}
