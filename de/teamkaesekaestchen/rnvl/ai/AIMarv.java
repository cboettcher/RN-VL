package de.teamkaesekaestchen.rnvl.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.teamkaesekaestchen.rnvl.ai.crits.CritCanPickUpTreasure;
import de.teamkaesekaestchen.rnvl.ai.crits.CritMostPositionsReachable;

import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class AIMarv extends AIBase {
	private static final Logger logger  = Logger.getLogger("AITim");
	
	public AIMarv() {
		
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
		
		List<ICriteriasBoard> allCriterias = new ArrayList<ICriteriasBoard>();
		allCriterias.add(new CritCanPickUpTreasure());
		allCriterias.add(new CritMostPositionsReachable());
		
		int highestScore = -1;
		MoveMessageType currBestMove = null;
		List<MoveMessageType> allMoves = super.getAllMoves(mmt);
		for(MoveMessageType move : allMoves) {
			int currscore = 0;
			for(ICriteriasBoard crit : allCriterias) {
				currscore+= crit.getPoints(move, tt, bt, treasurepos, aktpos, foundTT, togoTT);
			}
			if(currscore > highestScore) {
				highestScore = currscore;
				currBestMove = move;
			}
		}
		if(currBestMove == null) {
			System.out.println("AiMarv failed drastically");
			System.exit(1);
		}
		
		
		super.doOutput(mmt);
		return currBestMove;//Nimm den ersten von allen die uebrig geblieben sind
	}
}
