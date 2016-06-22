package de.teamkaesekaestchen.rnvl.ai.crits;

//import java.util.ArrayList;
import java.util.List;

import de.teamkaesekaestchen.rnvl.ai.ICriteriasMove;
import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class MoveCritLastPositions	implements ICriteriasMove {
	
	private PositionType lastPos = null;
	//private List<PositionType> lastPositions = new ArrayList<PositionType>();
	@Override
	public int getPoints(PositionType pos, TreasureType tt, Board bt,
			Position treasurepos, Position aktpos, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		pos = bt.findPlayer(Main.id);
		if(aktpos.getCol() != pos.getCol() || aktpos.getRow() != pos.getRow())
			return 10;
		return 0;
	}

}
