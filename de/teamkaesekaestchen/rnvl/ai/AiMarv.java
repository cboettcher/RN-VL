package de.teamkaesekaestchen.rnvl.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class AiMarv extends AIBase {
	private static final Logger logger  = Logger.getLogger("AITim");
	
	public AiMarv() {
		
	}

	@Override
	public MoveMessageType getZug(BoardType btt, TreasureType tt, List<TreasureType> foundTT,
			List<TreasuresToGoType> togoTT) {
		logger.info("Ai Marv ist gestartet");
		
		bt = new Board(btt);
		aktpos = new Position(bt.findPlayer(Main.id));
		
		if(Main.getTotalFailedMoves()>0){
			System.exit(1);
		}
		
		MoveMessageType mmt = new MoveMessageType();
		mmt.setNewPinPos(aktpos);
		
		//enthaelt alle Zuege
		List<MoveMessageType> allMoves = new ArrayList<MoveMessageType>();
		//enthaelt alle Zuege die nicht aussortiert wurden und damit gut sind
		List<MoveMessageType> betterMoves = new ArrayList<MoveMessageType>();
		
		//Fuelle mit allen Zuegen die Moeglich sind
		FillAllPossible(allMoves);
		
		//Fuelle betterMoves mit allen Zuegen wo ein Schatz erreicht werden kann
		for(MoveMessageType e : allMoves) {
			if(CanFindTreasure(e)) {
				betterMoves.add(e);
			}
		}
		
		//Fuelle betterMoves ansonsten mit allen Zuegen die Moeglich sind
		if(betterMoves.size() == 0) {
			betterMoves = allMoves;//TODO: dann zuege machen die die gegner moeglist blockieren
		}
				
		// TODO Auto-generated method stub
		for(int i = 0; i < betterMoves.size(); i++) {
			if(!isValid(betterMoves.get(i))) {//This Should never happen
				System.out.println("One of your guilty moves was found unguilty, this should never happen!");
				betterMoves.remove(i);
				i--;
			}
		}
		if(betterMoves.size() == 0) {//sollte nicht passieren, es gibt keinen Zug fuer mich :C
			System.out.println("Marv Failed :C");
			if(allMoves.size() != 0)
				return allMoves.get(0);
			//This shouldn't happen AT ALL
			System.out.println("Marv Failed very hard to find anything :CC");
			return null;
		}
		return betterMoves.get(0);//Nimm den ersten von allen die uebrig geblieben sind
	}
	
	private boolean CanFindTreasure(MoveMessageType e) {
		// TODO Auto-generated method stub
		return false;
	}

	private void FillAllPossible(List<MoveMessageType> moves) {
		moves.add(null);//TODO: finish this
	}

	//TODO: finish this
	public boolean isValid(MoveMessageType t) {
		return false;
	}
}
