import java.util.List;

public class GameLogic {
	private static final long serialVersionUID = 1L;
	private ImageLoader il;
	private List<Integer> cards;
	private String gamePicked = null;
	private int kingPicked = -1;
	
	public GameLogic(ImageLoader il, List<Integer> cards) {
		this.il = il;
		this.cards = cards;
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
//		System.out.println(talonSplitter());
//		for(int i = 28;i<34;i++)
//			System.out.println(il.isFlag(i));
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

	public int getKingPicked() {
		return kingPicked;
	}

	public void setKingPicked(int kingPicked) {
		this.kingPicked = kingPicked;
	}

}
