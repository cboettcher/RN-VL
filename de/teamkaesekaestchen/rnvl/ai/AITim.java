package de.teamkaesekaestchen.rnvl.ai;

import java.util.List;
import java.util.logging.Logger;

import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.*;

public class AITim implements Player {
	
	private static final Logger logger  = Logger.getLogger("AITim");
	
	private int[] aktpos = new int[2];
	private int[] treasurepos = new int[2];

	@Override
	public MoveMessageType getZug(BoardType bt, TreasureType tt,
			List<TreasureType> foundTT, List<TreasuresToGoType> togoTT) {
		
		logger.info("Ai Tim ist gestartet");
		
		if(Main.getFailedMoves()>5){
			System.exit(1);
		}
		
		for (int i = 0; i < 7; i++){
			for (int j = 0; j < 7; j++){
				if(bt.getRow().get(i).getCol().get(j).getTreasure() != null){
					if (bt.getRow().get(i).getCol().get(j).getTreasure().equals(tt)){
						treasurepos[0] = i;
						treasurepos[1] = j;
						
					}
				}
				if(bt.getRow().get(i).getCol().get(j).getPin().getPlayerID().size() != 0){
					for(Integer id :bt.getRow().get(i).getCol().get(j).getPin().getPlayerID()){
						if(id == Main.id){
							aktpos[0] = i;
							aktpos[1] = j;
						}
					}
				}
				
			}
		}
		if(bt.getForbidden() != null){
			System.out.println("Forbidden: "+bt.getForbidden().getCol() + "  -  "+bt.getForbidden().getRow());
		}
		
		System.out.println(aktpos[0]+"  -  "+aktpos[1]+"\n"+treasurepos[0]+"  -  -  "+treasurepos[1]);
		
		
		MoveMessageType mmt = new MoveMessageType();
		
		PositionType pt = new PositionType();
		pt.setCol(aktpos[1]);
		pt.setRow(aktpos[0]);
		mmt.setNewPinPos(pt);
		
		pt = new PositionType();
		pt.setCol(0);
		pt.setRow(1);
		mmt.setShiftPosition(pt);
		
		mmt.setShiftCard(bt.getRow().get(6).getCol().get(1));
		
		System.out.println(mmt.getShiftCard().getOpenings().isTop());
		System.out.println(mmt.getShiftCard().getOpenings().isRight());
		System.out.println(mmt.getShiftCard().getOpenings().isBottom());
		System.out.println(mmt.getShiftCard().getOpenings().isLeft());
		
		
		
		return mmt;
	}

}
