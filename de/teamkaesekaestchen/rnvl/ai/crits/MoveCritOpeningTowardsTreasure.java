package de.teamkaesekaestchen.rnvl.ai.crits;

import java.util.List;

import de.teamkaesekaestchen.rnvl.ai.ICriteriasMove;
import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;
import de.teamkaesekaestchen.rnvl.prot.CardType.Openings;

public class MoveCritOpeningTowardsTreasure implements ICriteriasMove {

	@Override
	public int getPoints(PositionType pos, TreasureType tt, Board bt,
			Position treasurepos, Position aktpos, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		// TODO Auto-generated method stub
		if(bt.findTreasure(tt) == null)
			return 0;
		PositionType tpos = bt.findTreasure(tt);
		Openings i = bt.getCard(pos.getRow(), pos.getCol()).getOpenings();
		int counter = 0;
		if(i.isLeft() && pos.getCol() > tpos.getCol())
			counter+=15;
		if(i.isRight() && pos.getCol() < tpos.getCol())
			counter+=15;
		if(i.isTop() && pos.getRow() > tpos.getRow())
			counter+=15;
		if(i.isBottom() && pos.getRow() < tpos.getRow())
			counter+=15;
		return counter;
	}

}
