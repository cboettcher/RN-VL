package de.teamkaesekaestchen.rnvl.impl;

import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;

public class MoveMessage extends MoveMessageType {
	
	public MoveMessage(MoveMessageType mmt){
		super();
		this.setNewPinPos(mmt.getNewPinPos());
		this.setShiftCard(mmt.getShiftCard());
		this.setShiftPosition(mmt.getShiftPosition());
	}
}
