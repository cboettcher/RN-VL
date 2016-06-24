package de.teamkaesekaestchen.rnvl.ai;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.teamkaesekaestchen.rnvl.ai.crits.MoveCritOpeningTowardsTreasure;
import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.CardType;
import de.teamkaesekaestchen.rnvl.prot.CardType.Openings;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

/**
 * TODO MoveCritSavePosition
 * 
 * @author Tobias
 *
 * Spielder id unter Main.id
 * BoardType board enthaellt das gesammte bord, den schiebestein und die verbotene position
 * TreasureType treasure enthaellt infos ueber den naechsten zu findenden schatz
 * List<TreasureType> foundTreasures enthaellt alle schaetze die schon gefunden wurden (vom spieler? oder allen spielern)
 * List<TreasuresToGoType> die anzahl an schaetzen die alle spieler noch brauchen um das spiel abzuschliessen
 * 
 * KI Zug Berechnung:
 * 1. Erstelle in einer Liste alle 48 gueltigen Zuege
 * 2. Filtere alle Zuege die nicht gueltig sind (gibt es ungueltige zuege?)
 * 3. Bewerte alle Zuege und waehle den Besten zug aus:
 * 		Zugbewertung:
 * 			- Schatz erreicht: +1000 //muss immer vorgezogen werden
 * 			- Kuerzeste moegliche Distanz zum Schatz nach move: 
 * 				Distanz zum Schatz in Manhattan Norm: dist
 * 				Punkte: y = 1.02040816327*Math.pow(dist, 2) + -50.0*dist + 500.0 
 * 			- Erreichbare Felder fuer den eigenen Spieler: +5
 * 			- Erreichbare Schaetze fuer den eigenen Spieler: schon gefunden +0, noch nicht gefunden +10
 * 			- Erreichbare Felder fuer den naechsten Spieler: -4
 * 			- Erreichbare Schaetze fuer den naechsten Spieler: -8
 * 			- Erreichbare Felder fuer die weiteren Spieler: -2
 * 			- Erreichbare Schaetze fuer die weiteren Spieler: -4
 */
public class AITobias implements IPlayer {
	
	private static final int maxValue = 100000;
	
	//{row, col}
	private static final int[][] shiftPositions = new int[][] {{0, 1}, {0, 3}, {0, 5}, {1, 6}, {3, 6}, {5, 6}, 
		{6, 1}, {6, 3}, {6, 5}, {1, 0}, {3, 0}, {5, 0}};
	
	private BoardType board;
	
	private TreasureType searchingTreasure;
	
	private List<TreasureType> found;
	
	private int myId;
	
	@Override
	public MoveMessageType getZug(BoardType board, TreasureType treasure, List<TreasureType> foundTreasures,
			List<TreasuresToGoType> treasuresToGo) {
		myId = Main.id;
		searchingTreasure = treasure;
		found = foundTreasures;
		this.board = board;
		List<ShiftRating> shiftRatings = getPossibleShifts();
		Collections.sort(shiftRatings);
		MoveMessageType move = new MoveMessageType();
		PositionType shift = new PositionType();
		PositionType pin = new PositionType();
		shift.setRow(shiftRatings.get(0).getShiftPosition()[0]);
		shift.setCol(shiftRatings.get(0).getShiftPosition()[1]);
		pin.setRow(shiftRatings.get(0).getMove().getPinPos()[0]);
		pin.setCol(shiftRatings.get(0).getMove().getPinPos()[1]);
		move.setShiftCard(shiftRatings.get(0).getMove().getCard());
		move.setShiftPosition(shift);
		move.setNewPinPos(pin);
		Openings open = move.getShiftCard().getOpenings();
		System.out.println("Move: \nShift: " + shift.getRow() + " " + shift.getCol() + 
				"\nPin: " + pin.getRow() + " " + pin.getCol() + "\nCard: " + 
				open.isTop() + " " + open.isRight() + " " + open.isBottom() + " " + open.isLeft());
		return move;
	}
	
	private int getNumPlayers() {
		int players = 0;
		for (BoardType.Row row : board.getRow()) {
			for (CardType card : row.getCol()) {
				players += card.getPin().getPlayerID().size();
			}
		}
		return players;
	}
	
	private int getNextPlayerId(int myId, int players) {
		return (myId+1)%players;
	}
	
	private Map<Integer, int[]> getPlayersPositions() {
		return getPlayersPositions(board);
	}
	private Map<Integer, int[]> getPlayersPositions(BoardType board) {
		Map<Integer, int[]> positions = new HashMap<Integer, int[]>(4);
		for (int i = 0; i < board.getRow().size(); i++) {
			for (int j = 0; j < board.getRow().get(i).getCol().size(); j++) {
				for (int id : board.getRow().get(i).getCol().get(j).getPin().getPlayerID()) {
					positions.put(id, new int[] {j, i});
				}
			}
		}
		return positions;
	}
	
	private int[] getNextTreasurePos(TreasureType treasure) {
		for (int i = 0; i < board.getRow().size(); i++) {
			for (int j = 0; j < board.getRow().get(i).getCol().size(); j++) {
				if (board.getRow().get(i).getCol().get(j).getTreasure().equals(treasure)) {
					return new int[] {j, i};
				}
			}
		}
		return null;
	}
	
	private List<ShiftRating> getPossibleShifts() {
		List<ShiftRating> shifts = new ArrayList<ShiftRating>(48);
		List<CardType> rotatedCards = new ArrayList<CardType>(4);
		CardType shiftCard = board.getShiftCard();
		for (int i = 0; i < 4; i++) {
			rotatedCards.add(rotateCard(shiftCard, i));
		}
		for (CardType card : rotatedCards) {
			for (int[] shift : shiftPositions) {
				if (board.getForbidden() == null || board.getForbidden().getRow() != shift[0] && board.getForbidden().getCol() != shift[1]) {
					RevertableBoard tmpBoard = shiftCard(card, shift);
					try {
						Move rating = getBoardRating(tmpBoard.getBoard());
						rating.setCard(card);
						ShiftRating sr = new ShiftRating(shift, rating.getRating(), rating);
						shifts.add(sr);
						tmpBoard.revert();
					}
					catch (NullPointerException npe) {
						npe.printStackTrace();
						tmpBoard.revert();
					}
				}
			}
		}
		return shifts;
	}
	
	private static CardType rotateCard(CardType card, int rotations) {
		CardType rotated = new CardType();
		rotated.setPin(card.getPin());
		rotated.setTreasure(card.getTreasure());
		CardType.Openings open = card.getOpenings();
		CardType.Openings newOpenings = new CardType.Openings();
		boolean openTop = open.isTop();
		boolean openRight = open.isRight();
		boolean openBottom = open.isBottom();
		boolean openLeft = open.isLeft();
		switch (rotations) {
			case 0:
				//don't rotate
				break;
			case 1:
				newOpenings.setTop(openLeft);
				newOpenings.setRight(openTop);
				newOpenings.setBottom(openRight);
				newOpenings.setLeft(openBottom);
				break;
			case 2:
				newOpenings.setTop(openBottom);
				newOpenings.setRight(openLeft);
				newOpenings.setBottom(openTop);
				newOpenings.setLeft(openRight);
				break;
			case 3:
				newOpenings.setTop(openRight);
				newOpenings.setRight(openBottom);
				newOpenings.setBottom(openLeft);
				newOpenings.setLeft(openTop);
				break;
			default:
				throw new IllegalArgumentException("Error: Unknown rotation number (" + rotations + ") should be between 0 and 3");
		}
		rotated.setOpenings(open);
		return rotated;
	}
	
	/**
	 * Zugbewertung:
	 * 	- Schatz erreicht: +1000 //muss immer vorgezogen werden
	 * 	- Kuerzeste moegliche Distanz zum Schatz nach move: 
	 * 			Distanz zum Schatz in Manhattan Norm: dist
	 * 		Punkte: y = -3.84615384615*dist + 53.8461538462 (zwischen 0 und 50 punkte)
	 * 	- Erreichbare Felder fuer den eigenen Spieler: +7
	 * 	- Erreichbare Schaetze fuer den eigenen Spieler: schon gefunden +0, noch nicht gefunden +3
	 * 	- Erreichbare Felder fuer die weiteren Spieler: -5
	 * 	- Erreichbare Schaetze fuer die weiteren Spieler: -2
	 * 	- Vom Schatz aus erreichbare Felder: +5
	 */
	private Move getBoardRating(BoardType board) {
		int rating = 0;
		int[][] graph = buildGraph(board);
		//run floyd warshall on the graph
		for (int i = 0; i < graph.length; i++) {
			for (int j = 0; j < graph.length; j++) {
				for (int k = 0; k < graph.length; k++) {
					graph[j][k] = Math.min(graph[j][k], graph[j][i] + graph[i][k]);
				}
			}
		}
		int[] startPosition = getMyPosition();
		int startingIndex = startPosition[0]*7+startPosition[1];
		int[] movePosition = new int[2];
		boolean treasureFound = false;
		List<int[]> reachable = getReachableFields(board, graph, myId);
		//Schatz erreichbar:
		int[] treasurePosition = getTreasurePosition(searchingTreasure);
		if (graph[startingIndex][treasurePosition[0]*7+treasurePosition[1]] != maxValue) {
			rating += 1000;
			movePosition[0] = treasurePosition[0];
			movePosition[1] = treasurePosition[1];
			treasureFound = true;
		}
		else {
			//geringste distanz zum schatz finden
			int[] shortestDistance = new int[] {-1, -1, maxValue};
			for (int[] field : reachable) {
				int dist = Math.abs(treasurePosition[0]-field[0]) + Math.abs(treasurePosition[1]-field[1]);
				if (dist < shortestDistance[2]) {
					shortestDistance[0] = field[0];
					shortestDistance[1] = field[1];
					shortestDistance[2] = dist;
				}
			}
			rating += -3.84615384615*shortestDistance[2] + 53.8461538462;
		}
		//erreichbare Felder
		rating += reachable.size()*7;
		//erreichbare Schaetze
		rating += getReachableTreasures(board, graph, myId).size()*3;
		//erreichbare Felder/Schaetze fuer andere Spieler
		for (int i = 0; i < getNumPlayers(); i++) {
			if (i != myId && getPlayerPosition(i) != null) {
				rating -= getReachableFields(board, graph, i).size()*5;
				rating -= getReachableTreasures(board, graph, i).size()*2;
			}
		}
		//erreichbare Felder vom Schatz aus:
		rating += getReachableFields(board, graph, treasurePosition).size()*5;
		if (!treasureFound) {
			movePosition = findBestPosition(board, reachable, treasurePosition);
		}
		return new Move(new int[] {movePosition[0], movePosition[1]}, rating, null);
	}
	
	private int[] findBestPosition(BoardType board, List<int[]> reachable, int[] treasurePosition) {
		int[] points = new int[reachable.size()];
		for (int i = 0; i < reachable.size(); i++) {
			int[] pos = reachable.get(i);
			int score = 0;
			int dist = Math.abs(treasurePosition[0]-pos[0]) + Math.abs(treasurePosition[1]-pos[1]);
			Openings open = board.getRow().get(pos[0]).getCol().get(pos[1]).getOpenings();
			if(open.isLeft() && pos[1] > treasurePosition[1]) {
				score += 15;
			}
			if(open.isRight() && pos[1] < treasurePosition[1]) {
				score += 15;
			}
			if(open.isTop() && pos[0] > treasurePosition[0]) {
				score += 15;
			}
			if(open.isBottom() && pos[0] < treasurePosition[0]) {
				score += 15;
			}
			if (open.isLeft()) {
				score += 20;
			}
			if (open.isBottom()) {
				score += 20;
			}
			if (open.isRight()) {
				score += 20;
			}
			if (open.isTop()) {
				score += 20;
			}
			score += (14-dist)*10;
			points[i] = score;
		}
		int[] maxScore = new int[] {-1, -1};
		for (int i = 0; i < points.length; i++) {
			if (points[i] > maxScore[0]) {
				maxScore[0] = points[i];
				maxScore[1] = i;
			}
		}
		return reachable.get(maxScore[1]);
	}
	
	private List<int[]> getReachableFields(BoardType board, int[][] graph, int player) {
		int[] startPosition = getPlayerPosition(player);
		return getReachableFields(board, graph, startPosition);
	}
	private List<int[]> getReachableFields(BoardType board, int[][] graph, int[] startPosition) {
		int startingIndex = startPosition[0]*7+startPosition[1];
		List<int[]> reachable = new ArrayList<int[]>(49);
		for (int i = 0; i < graph.length; i++) {
			if (graph[startingIndex][i] != maxValue) {
				reachable.add(new int[] {i/7, i%7});
			}
		}
		return reachable;
	}
	private List<TreasureType> getReachableTreasures(BoardType board, int[][] graph, int player) {
		int[] startPosition = getPlayerPosition(player);
		int startingIndex = startPosition[0]*7+startPosition[1];
		List<TreasureType> reachable = new ArrayList<TreasureType>(49);
		for (int i = 0; i < graph.length; i++) {
			if (graph[startingIndex][i] != maxValue) {
				TreasureType treasure = board.getRow().get(i/7).getCol().get(i%7).getTreasure();
				if (treasure != null && !found.contains(treasure)) {
					reachable.add(treasure);
				}
			}
		}
		return reachable;
	}
	
	private int[] getMyPosition() {
		for (int i = 0; i < board.getRow().size(); i++) {
			for (int j = 0; j < board.getRow().size(); j++) {
				if (board.getRow().get(i).getCol().get(j).getPin().getPlayerID().contains(myId)) {
					return new int[] {i, j};
				}
			}
		}
		return null;
	}
	
	private int[] getPlayerPosition(int id) {
		for (int i = 0; i < board.getRow().size(); i++) {
			for (int j = 0; j < board.getRow().size(); j++) {
				if (board.getRow().get(i).getCol().get(j).getPin().getPlayerID().contains(id)) {
					return new int[] {i, j};
				}
			}
		}
		return null;
	}
	
	private int[] getTreasurePosition(TreasureType treasure) {
		for (int i = 0; i < board.getRow().size(); i++) {
			for (int j = 0; j < board.getRow().size(); j++) {
				TreasureType t = board.getRow().get(i).getCol().get(j).getTreasure(); 
				if (t != null && t.equals(treasure)) {
					return new int[] {i, j};
				}
			}
		}
		return null;
	}
	
	public int findStartingDistance(TreasureType treasure) {
		int[] treasurePos = getTreasurePosition(treasure);
		int[] pinPos = getMyPosition();
		return Math.abs(treasurePos[0]-pinPos[0]) + Math.abs(treasurePos[1]-pinPos[0]);
	}
	
	/**
	 * Build a graph if the board for a floyd-warshall algroithm.
	 */
	private int[][] buildGraph(BoardType board) {
		int[][] graph = new int[49][49];
		for (int i = 0; i < graph.length; i++) {
			for (int j = 0; j < graph[i].length; j++) {
				graph[i][j] = maxValue;
			}
		}
		for (int i = 0; i < graph.length; i++) {
			graph[i][i] = 0;
		}
		for (int i = 0; i < graph.length; i++) {
			for (int[] connection : getConnectedFields(board, i/7, i%7)) {
				graph[i][connection[0]*7 + connection[1]] = 1;
			}
		}
		return graph;
	}
	
	private List<int[]> getConnectedFields(BoardType board, int row, int col) {
		List<int[]> connections = new ArrayList<int[]>(4);
		int[][] possibleConnections = new int[][] {{row-1, col}, {row, col+1}, {row+1, col}, {row, col-1}};
		for (int i = 0; i < possibleConnections.length; i++) {
			if (possibleConnections[i][0] >= 0 && possibleConnections[i][0] <= 6 && 
					possibleConnections[i][1] >= 0 && possibleConnections[i][1] <= 6) {
				CardType conCard = board.getRow().get(possibleConnections[i][0]).getCol().get(possibleConnections[i][1]);
				CardType currentCard = board.getRow().get(row).getCol().get(col);
				switch (i) {
					case 0:
						if (conCard.getOpenings().isBottom() && currentCard.getOpenings().isTop()) {
							connections.add(possibleConnections[i]);
						}
						break;
					case 1:
						if (conCard.getOpenings().isLeft() && currentCard.getOpenings().isRight()) {
							connections.add(possibleConnections[i]);
						}
						break;
					case 2:
						if (conCard.getOpenings().isTop() && currentCard.getOpenings().isBottom()) {
							connections.add(possibleConnections[i]);
						}
						break;
					case 3:
						if (conCard.getOpenings().isRight() && currentCard.getOpenings().isLeft()) {
							connections.add(possibleConnections[i]);
						}
						break;
				}
			}
		}
		return connections;
	}
	
	private RevertableBoard shiftCard(CardType card, int[] shiftPosition) {
		CardType newShift = null;
		if (shiftPosition[1] % 6 == 0 && shiftPosition[0] % 2 == 1) {
			int row = shiftPosition[0];
			int start = (shiftPosition[1] + 6) % 12;
			newShift = getCard(board, row, start);
			if (start == 6) {
				for (int i = 6; i > 0; i--) {
					setCard(board, row, i, getCard(board, row, i-1));
				}
			}
			else {
				for (int i = 0; i < 6; i++) {
					setCard(board, row, i, getCard(board, row, i + 1));
				}
			}
		}
		else if (shiftPosition[0] % 6 == 0 && shiftPosition[1] % 2 == 1) {
			int col = shiftPosition[1];
			int start = (shiftPosition[0] + 6) % 12;
			newShift = getCard(board, start, col);
			if (start == 6) {
				for (int i = 6; i > 0; --i) {
					setCard(board, i, col, getCard(board, i - 1, col));
				}
			}
			else {
				for (int i = 0; i < 6; ++i) {
					setCard(board, i, col, getCard(board, i + 1, col));
				}
			}
		}
		if (!newShift.getPin().getPlayerID().isEmpty()) {
			CardType.Pin temp = newShift.getPin();
			newShift.setPin(new CardType.Pin());
			card.setPin(temp);
		}
		setCard(board, shiftPosition[0], shiftPosition[1], card);
		int[] reverseShift = new int[2];
		if (shiftPosition[0] % 6 == 0) {
			reverseShift[0] = (shiftPosition[0]+6)%12;
			reverseShift[1] = shiftPosition[1];
		}
		else {
			reverseShift[0] = shiftPosition[0];
			reverseShift[1] = (shiftPosition[1]+6)%12;
		}
		return new RevertableBoard(board, newShift, reverseShift);
	}
	
	private static void setCard(BoardType board, int row, int col, CardType c) {
		board.getRow().get(row).getCol().remove(col);
		board.getRow().get(row).getCol().add(col, c);
	}
	private static CardType getCard(BoardType board, int row, int col) {
		return board.getRow().get(row).getCol().get(col);
	}
	
	private class ShiftRating implements Comparable<ShiftRating> {
		
		private int[] shiftPosition;
		private Move move;
		private int rating;
		
		public ShiftRating(int[] shiftPosition, int rating, Move move) {
			this.shiftPosition = shiftPosition;
			this.rating = rating;
			this.move = move;
		}

		@Override
		public int compareTo(ShiftRating o) {
			if (getRating() > o.getRating()) {
				return -1;
			}
			else if (getRating() < o.getRating()) {
				return 1;
			}
			return 0;
		}
		
		public int[] getShiftPosition() {
			return shiftPosition;
		}
		public void setShiftPosition(int[] shiftPosition) {
			this.shiftPosition = shiftPosition;
		}
		
		public int getRating() {
			return rating;
		}
		public void setRating(int rating) {
			this.rating = rating;
		}
		
		public Move getMove() {
			return move;
		}
	}
	
	private class Move {
		
		private int[] pinPos;
		private int rating;
		private CardType card;
		
		public Move(int[] pinPos, int rating, CardType card) {
			this.pinPos = pinPos;
			this.rating = rating;
			this.card = card;
		}
		
		public int[] getPinPos() {
			return pinPos;
		}
		public void setPinPos(int[] pinPos) {
			this.pinPos = pinPos;
		}
		
		public CardType getCard() {
			return card;
		}
		public void setCard(CardType card) {
			this.card = card;
		}
		
		public int getRating() {
			return rating;
		}
		public void setRating(int rating) {
			this.rating = rating;
		}
	}	
	
	private class RevertableBoard {
		
		private BoardType board;
		private CardType outshiftedCard;
		private int[] reverseShift;
		
		public RevertableBoard(BoardType board, CardType outshiftedCard, int[] reverseShift) {
			this.board = board;
			this.outshiftedCard = outshiftedCard;
			this.reverseShift = reverseShift;
		}
		
		public BoardType revert() {
			return shiftCard(outshiftedCard, reverseShift).getBoard();
		}
		
		public BoardType getBoard() {
			return board;
		}
		public void setBoard(BoardType board) {
			this.board = board;
		}
	}
}