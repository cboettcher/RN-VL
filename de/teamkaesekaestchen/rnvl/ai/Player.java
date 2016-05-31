package de.teamkaesekaestchen.rnvl.ai;

import java.util.List;

import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public interface Player {
	public MoveMessageType getZug(BoardType bt, TreasureType tt, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT);
}
