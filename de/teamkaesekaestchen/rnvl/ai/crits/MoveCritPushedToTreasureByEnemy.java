package de.teamkaesekaestchen.rnvl.ai.crits;

import java.util.List;

import de.teamkaesekaestchen.rnvl.ai.AIBase;
import de.teamkaesekaestchen.rnvl.ai.ICriteriasMove;
import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class MoveCritPushedToTreasureByEnemy implements ICriteriasMove {

	@Override
	public int getPoints(PositionType pos, TreasureType tt, Board bt,
			Position treasurepos, Position aktpos, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		MoveMessageType mmt = new MoveMessageType();
		mmt.setNewPinPos(pos);
		List<MoveMessageType> b = AIBase.getAllMoves(mmt, bt);
		for(MoveMessageType m : b) {
			Board newBoard = bt.fakeShift(m);
			if(newBoard.findTreasure(tt) == null)
				continue;
			PositionType temppos = newBoard.findPlayer(Main.id);
			List<PositionType> newPosList = newBoard.getAllReachablePositions(temppos);
			for(PositionType e : newPosList) {
				if(e.getCol() == newBoard.findTreasure(tt).getCol() && e.getRow() == newBoard.findTreasure(tt).getRow())
					return 40;
			}	
			
		}
		return 0;
	}

}
