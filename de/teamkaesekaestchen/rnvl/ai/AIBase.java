package de.teamkaesekaestchen.rnvl.ai;

import java.util.ArrayList;
import java.util.List;

import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Card;
import de.teamkaesekaestchen.rnvl.impl.MoveMessage;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.CardType;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;
import de.teamkaesekaestchen.rnvl.prot.CardType.Openings;

public abstract class AIBase implements IPlayer {

	protected Position aktpos;
	protected Position treasurepos;
	
	protected Board bt;

	List<PositionType> possibleShiftPositions;
	
	public void doOutput(MoveMessageType mmt) {
		System.out.println("aktpos:            "+aktpos.getRow()+"  "+aktpos.getCol());
		System.out.println("pinpos:            "+mmt.getNewPinPos().getRow()+" "+mmt.getNewPinPos().getCol());
		System.out.println("shiftCardPosition: "+mmt.getShiftPosition().getRow()+"  "+mmt.getShiftPosition().getCol());
	}

	/**
	 * creates a PositionType-Object from the given row and column
	 * @param row
	 * @param column
	 * @return
	 */
	public static PositionType createPositionType(int row, int column) {
		PositionType pt = new PositionType();
		pt.setCol(column);
		pt.setRow(row);
		return pt;
	}
	
	/**
	 * returns a new CardType that is the old one rotated 90 degrees to the right
	 * @param ct
	 * @return
	 */
	public static CardType rotateCardRight(CardType ct) {
		CardType ret = new CardType();
		Openings o = new Openings();
		ret.setPin(ct.getPin());
		ret.setTreasure(ct.getTreasure());
		o.setRight(ct.getOpenings().isTop());
		o.setBottom(ct.getOpenings().isRight());
		o.setLeft(ct.getOpenings().isBottom());
		o.setTop(ct.getOpenings().isLeft());
	
		return ret;		
	}

	/**
	 * gets all possible Positions for inserting the card and stores them in a list
	 * @param bt
	 * @param tt
	 * @param foundTT
	 * @param togoTT
	 */
	protected void getShiftPositions(BoardType bt, TreasureType tt, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		possibleShiftPositions = new ArrayList<>();
		PositionType forbidden = bt.getForbidden();
		int[] border = { 0, 6 };
		int[] movable = { 1, 3, 5 };

		for (int i : border) {
			for (int j : movable) {
				possibleShiftPositions.add(createPositionType(i, j));
				possibleShiftPositions.add(createPositionType(j, i));
			}
		}

		// if there is a forbidden move remove it
		if (forbidden != null) {
			for (int i = 0; i < possibleShiftPositions.size(); i++) {
				if (possibleShiftPositions.get(i).getRow() == forbidden.getRow()
						&& possibleShiftPositions.get(i).getCol() == forbidden.getCol()) {
					possibleShiftPositions.remove(i);
					break;
				}
			}
		}
		
		

	}

	/**
	 * gets the current Position and stores it in class Variables
	 * @param bt
	 * @param tt
	 * @param foundTT
	 * @param togoTT
	 */
	protected void currentPosition(BoardType bt, TreasureType tt, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		for (int i = 0; i < 7; i++) {
			for (int j = 0; j < 7; j++) {

				if (bt.getRow().get(i).getCol().get(j).getTreasure() != null) {
					if (bt.getRow().get(i).getCol().get(j).getTreasure().equals(tt)) {
						treasurepos.setCol(i);// = i;
						treasurepos.setRow(j);// = j;

					}
				}

				if (bt.getRow().get(i).getCol().get(j).getPin().getPlayerID().size() != 0) {
					for (Integer id : bt.getRow().get(i).getCol().get(j).getPin().getPlayerID()) {
						if (id == Main.id) {
							aktpos.setCol(i);//[0] = i;
							aktpos.setRow(j);// = j;
						}
					}
				}

			}
		}
	}
	
	protected List<MoveMessageType> getAllMoves(MoveMessageType mmt) {
		ArrayList<MoveMessageType> moveList = new ArrayList<MoveMessageType>(44);
		Card shiftCard;
		for(int i : new int[]{0, 6}){
			for(int j : new int[]{1, 3, 5}){
				if(bt.getForbidden()!= null &&bt.getForbidden().getCol() == j && bt.getForbidden().getRow() == i){
					continue;
				}
				shiftCard = new Card(bt.getShiftCard());
				for(Card c : shiftCard.getPossibleRotations()){
					mmt.setShiftCard(c);
					mmt.setShiftPosition(new Position(i, j));
					moveList.add(new MoveMessage(mmt));
				}
				
				if(bt.getForbidden()!= null &&bt.getForbidden().getCol() == i && bt.getForbidden().getRow() == j){
					continue;
				}
				shiftCard = new Card(bt.getShiftCard());
				for(Card c : shiftCard.getPossibleRotations()){
					mmt.setShiftCard(c);
					mmt.setShiftPosition(new Position(i, j));
					moveList.add(new MoveMessage(mmt));	
				}
				
			}
		}
		return moveList;
	}
}
