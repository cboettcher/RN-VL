package de.teamkaesekaestchen.rnvl.ai;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import de.teamkaesekaestchen.rnvl.ai.crits.CritCanPickUpTreasure;
import de.teamkaesekaestchen.rnvl.ai.crits.CritMostPositionsReachable;
import de.teamkaesekaestchen.rnvl.ai.crits.CritNextPlayerLessPositionsReachable;

import de.teamkaesekaestchen.rnvl.impl.Board;
import de.teamkaesekaestchen.rnvl.impl.Position;
import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.PositionType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class AIMarv extends AIBase {
	private static final Logger logger  = Logger.getLogger("AITim");
	List<ICriteriasBoard> BoardCriterias;
	List<ICriteriasMove> MoveCriterias;
	public AIMarv() {
		BoardCriterias = new ArrayList<ICriteriasBoard>();

		BoardCriterias.add(new CritCanPickUpTreasure());
		BoardCriterias.add(new CritMostPositionsReachable());
		BoardCriterias.add(new CritNextPlayerLessPositionsReachable());
		
		MoveCriterias = new ArrayList<ICriteriasMove>();
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
		
		int highestScore = -1;
		MoveMessageType currBestMove = null;
		List<MoveMessageType> allMoves = super.getAllMoves(mmt);
		for(MoveMessageType move : allMoves) {
			int currscore = 0;
			for(ICriteriasBoard crit : BoardCriterias) {
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
		
		mmt = currBestMove;
		Board newBoard = bt.fakeShift(mmt);
		
		aktpos = new Position(newBoard.findPlayer(Main.id));
		treasurepos = new Position(newBoard.findTreasure(tt));
		System.out.println(highestScore);
		
		if(highestScore < CritCanPickUpTreasure.FINDTREASUREVAL) {
			List<PositionType> positionList = newBoard.getAllReachablePositions(aktpos);
			PositionType tmppos = aktpos;
			for(PositionType pt : positionList){
				int distcurrent = Math.abs(pt.getCol()-treasurepos.getCol())+Math.abs(pt.getRow()-treasurepos.getRow());
				int best = Math.abs(tmppos.getCol()-treasurepos.getCol())+Math.abs(tmppos.getRow()-treasurepos.getRow());
				if(distcurrent < best) {
					tmppos = pt;
				}
			}
			mmt.setNewPinPos(tmppos);
		}
		
		super.doOutput(mmt);
		return currBestMove;//Nimm den ersten von allen die uebrig geblieben sind
	}
}
