package de.teamkaesekaestchen.rnvl.ai.crits;

import java.util.List;

import de.teamkaesekaestchen.rnvl.ai.ICriteriasBoard;
import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class CritNextPlayerLessPositionsReachable implements ICriteriasBoard {

	@Override
	public int getPoints(MoveMessageType mmt, TreasureType tt, Board bt,
			Position treasurepos, Position enemypos, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		
		Board newBoard = bt.fakeShift(mmt);//Spalten
		enemypos = new Position(newBoard.findPlayer((Main.id%4)+1));//Next Player is "Thisplayer"mod 4 and then +1
		return 49-newBoard.getAllReachablePositions(enemypos).size();
	}
	
	

}
