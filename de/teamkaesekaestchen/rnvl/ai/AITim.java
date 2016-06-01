package de.teamkaesekaestchen.rnvl.ai;

import java.util.List;

import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

public class AITim implements Player {

	@Override
	public MoveMessageType getZug(BoardType bt, TreasureType tt,
			List<TreasureType> foundTT, List<TreasuresToGoType> togoTT) {
		
		for (int i = 0; i < 7; i++){
			for (int j = 0; j < 7; j++){
				System.out.println(bt.getRow().get(i).getCol().get(j).getTreasure());
				if (bt.getRow().get(i).getCol().get(j).getTreasure().equals(tt)){
					System.out.println("Gleich bei i: "+i+" und j:"+j);
				}
			}
		}
		
		
		// TODO Auto-generated method stub
		return null;
	}

}
