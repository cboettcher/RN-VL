package de.teamkaesekaestchen.rnvl.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.teamkaesekaestchen.rnvl.impl.*;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.*;

public class AITim implements Player {
	
	private static final Logger logger  = Logger.getLogger("AITim");
	
	private Position aktpos;
	private Position treasurepos;
	
	private Board bt;

	@Override
	public MoveMessageType getZug(BoardType btt, TreasureType tt,
			List<TreasureType> foundTT, List<TreasuresToGoType> togoTT) {
		
		logger.info("Ai Tim ist gestartet");
		
		this.bt = new Board(btt);
		
		System.out.println(tt.toString());
		
		System.out.println(Main.getTotalFailedMoves());
		if(Main.getTotalFailedMoves()>0){
			System.exit(1);
		}
		
		aktpos = new Position(bt.findPlayer(Main.id));
		treasurepos = new Position(bt.findTreasure(tt));
		
//		for(PositionType poo : bt.getAllReachablePositions(p)){
//			System.out.println("Moeglich: ("+poo.getRow()+"|"+poo.getCol()+")");
//		}
		
		
		MoveMessageType mmt = new MoveMessageType();
		List<MoveMessageType> moveList = new ArrayList<>();
		
		mmt.setNewPinPos(aktpos);
		boolean gefunden = false;
		for(int i : new int[]{0, 6}){
			for(int j : new int[]{1, 3, 5}){
				if(bt.getForbidden()!= null &&bt.getForbidden().getCol() == j && bt.getForbidden().getRow() == i){
					continue;
				}
				Card shiftCard = new Card(bt.getShiftCard());
				for(Card c : shiftCard.getPossibleRotations()){
					mmt.setShiftCard(c);
					mmt.setShiftPosition(new Position(i, j));
					Board newBoard = bt.fakeShift(mmt);//Spalten
					aktpos = new Position(newBoard.findPlayer(Main.id));
					treasurepos = new Position(newBoard.findTreasure(tt));
					for(PositionType pp : newBoard.getAllReachablePositions(aktpos)){
						if(pp.getCol() == treasurepos.getCol() && pp.getRow() == treasurepos.getRow()){
							mmt.setNewPinPos(new Position(treasurepos));
							return mmt;
						}
					}
					moveList.add(new MoveMessage(mmt));
				}
				
				if(bt.getForbidden()!= null &&bt.getForbidden().getCol() == i && bt.getForbidden().getRow() == j){
					continue;
				}
				shiftCard = new Card(bt.getShiftCard());
				for(Card c : shiftCard.getPossibleRotations()){
					mmt.setShiftPosition(new Position(j, i));//Reihen
					Board newBoard = bt.fakeShift(mmt);
					aktpos = new Position(newBoard.findPlayer(Main.id));
					treasurepos = new Position(newBoard.findTreasure(tt));
					for(PositionType pp : newBoard.getAllReachablePositions(aktpos)){
						if(pp.getCol() == treasurepos.getCol() && pp.getRow() == treasurepos.getRow()){
							mmt.setNewPinPos(new Position(treasurepos));
							return mmt;
						}
					}
					moveList.add(new MoveMessage(mmt));
					
				}
			}
		}
		
		mmt = moveList.get((int)(Math.random()*moveList.size()));
		
		aktpos = new Position(bt.findPlayer(Main.id));
		
		mmt.setNewPinPos(aktpos);
		Board newBoard = bt.fakeShift(mmt);
		
		aktpos = new Position(newBoard.findPlayer(Main.id));
		
		List<PositionType> positionList = newBoard.getAllReachablePositions(aktpos);
		mmt.setNewPinPos(positionList.get((int)(Math.random()*positionList.size())));
		
//		mmt.setNewPinPos(pt);
//		
//		pt = new PositionType();
//		pt.setCol(0);
//		pt.setRow(1);
//
//		if(bt.getForbidden() != null){
//			System.out.println("Forbidden: "+bt.getForbidden().getCol() + "  -  "+bt.getForbidden().getRow());
//			if(bt.getForbidden().getCol()==6 && bt.getForbidden().getRow()==1){
//				pt.setRow(3);
//			}
//		}
//		mmt.setShiftPosition(pt);
//		
//		mmt.setShiftCard(bt.getShiftCard());
		System.out.println("aktpos:            "+aktpos.getRow()+"  "+aktpos.getCol());
		System.out.println("pinpos:            "+mmt.getNewPinPos().getRow()+" "+mmt.getNewPinPos().getCol());
		System.out.println("shiftCardPosition: "+mmt.getShiftPosition().getRow()+"  "+mmt.getShiftPosition().getCol());
		
		
		return mmt;
	}
	
	
	
	
}