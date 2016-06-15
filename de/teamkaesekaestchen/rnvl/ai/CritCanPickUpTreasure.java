package de.teamkaesekaestchen.rnvl.ai;

import java.util.List;

import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class CritCanPickUpTreasure implements CritAi {

	@Override
	public int getPoints(MoveMessageType mmt, TreasureType tt, Board bt, Position treasurepos, Position aktpos,
			List<TreasureType> foundTT, List<TreasuresToGoType> togoTT) {
		
		Board newBoard = bt.fakeShift(mmt);//Spalten
		aktpos = new Position(newBoard.findPlayer(Main.id));
		if(newBoard.findTreasure(tt) != null){
			treasurepos = new Position(newBoard.findTreasure(tt));
			for(PositionType pp : newBoard.getAllReachablePositions(aktpos)){
				if(pp.getCol() == treasurepos.getCol() && pp.getRow() == treasurepos.getRow()){
					mmt.setNewPinPos(new Position(treasurepos));
					return 1000;
				}
			}
		
		}
		return 0;
	}
}
