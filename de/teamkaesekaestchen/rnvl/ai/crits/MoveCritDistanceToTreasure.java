package de.teamkaesekaestchen.rnvl.ai.crits;

import java.util.List;

import de.teamkaesekaestchen.rnvl.ai.ICriteriasMove;
import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class MoveCritDistanceToTreasure implements ICriteriasMove {

	@Override
	public int getPoints(PositionType pos, TreasureType tt, Board bt,
			Position treasurepos, Position aktpos, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		// TODO Auto-generated method stub
		if(treasurepos == null)
			return 0;
		int distcurrent = Math.abs(pos.getCol()-treasurepos.getCol())+Math.abs(pos.getRow()-treasurepos.getRow());
		return (14-distcurrent)*10;
	}
}
