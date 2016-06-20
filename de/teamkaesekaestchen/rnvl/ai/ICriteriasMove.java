package de.teamkaesekaestchen.rnvl.ai;

import java.util.List;

import de.teamkaesekaestchen.rnvl.impl.*;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public interface ICriteriasMove {
	int getPoints(PositionType pos, TreasureType tt, Board bt, Position treasurepos, Position aktpos,
			List<TreasureType> foundTT, List<TreasuresToGoType> togoTT);
}
