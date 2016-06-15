package de.teamkaesekaestchen.rnvl.ai;

import java.util.List;

import de.teamkaesekaestchen.rnvl.impl.*;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public interface CritAi {
	int getPoints(MoveMessageType mmt, TreasureType tt, Board bt, Position treasurepos, Position aktpos,
			List<TreasureType> foundTT, List<TreasuresToGoType> togoTT);
}
