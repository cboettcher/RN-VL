package de.teamkaesekaestchen.rnvl.ai.crits;

import java.util.List;

import de.teamkaesekaestchen.rnvl.ai.ICriteriasMove;
import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.prot.CardType.Openings;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class MoveCritSavePosition implements ICriteriasMove{

	@Override
	public int getPoints(PositionType pos, TreasureType tt, Board bt,
			Position treasurepos, Position aktpos, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		// TODO Auto-generated method stub
		Openings i = bt.getCard(pos.getRow(), pos.getCol()).getOpenings();
		return numOpenings(i)*20;
		/*if(pos.getCol()%2 == 0 && pos.getRow()%2 == 0)
			return 30;
		if(pos.getCol()%2 == 0 || pos.getRow()%2 == 0)
			return 20;
		return 0;*/
	}
	
	private int numOpenings(Openings i) {
		int counter = 0;
		if(i.isBottom())
			counter++;
		if(i.isLeft())
			counter++;
		if(i.isRight())
			counter++;
		if(i.isTop())
			counter++;
		return counter;
	}

}
