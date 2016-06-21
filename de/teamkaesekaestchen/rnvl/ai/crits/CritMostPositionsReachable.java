package de.teamkaesekaestchen.rnvl.ai.crits;

import java.util.List;

import de.teamkaesekaestchen.rnvl.ai.ICriteriasBoard;
import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class CritMostPositionsReachable implements ICriteriasBoard{
	@Override
	public int getPoints(MoveMessageType mmt, TreasureType tt, Board bt,
			Position treasurepos, Position aktpos, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		
		Board newBoard = bt.fakeShift(mmt);//Spalten
		aktpos = new Position(newBoard.findPlayer(Main.id));
		return newBoard.getAllReachablePositions(aktpos).size();
	}

}
