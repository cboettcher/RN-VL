package de.teamkaesekaestchen.rnvl.ai;

import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;

public interface Player {
	public MoveMessageType getZug(BoardType bt);
}
