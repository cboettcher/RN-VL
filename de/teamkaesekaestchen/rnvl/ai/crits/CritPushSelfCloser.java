package de.teamkaesekaestchen.rnvl.ai.crits;

import java.util.List;

import de.teamkaesekaestchen.rnvl.ai.AIBase;
import de.teamkaesekaestchen.rnvl.ai.ICriteriasBoard;
import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class CritPushSelfCloser implements ICriteriasBoard {
	
	public final static int COULDFINDTREASURENEXT = 10000;
	@Override
	public int getPoints(MoveMessageType mmt, TreasureType tt, Board bt,
			Position treasurepos, Position aktpos, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		// TODO Auto-generated method stub
		List<PositionType> positions = bt.getAllReachablePositions(aktpos);
		for(PositionType e : positions) {
			mmt.setNewPinPos(e);
			Board newBoard = bt.fakeShift(mmt);
			PositionType newPos = newBoard.findPlayer(Main.id);
			PositionType treasuretemp = newBoard.findTreasure(tt);
			List<PositionType> newpos = newBoard.getAllReachablePositions(newPos);
			for(PositionType pp : newpos){
				if(pp.getCol() == treasuretemp.getCol() && pp.getRow() == treasuretemp.getRow()){
					mmt.setNewPinPos(e);
					return COULDFINDTREASURENEXT;
				}
			}
		}
		return 0;
		
		
	}
	
}
