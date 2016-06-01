package de.teamkaesekaestchen.rnvl.ai;

import java.util.List;

import de.teamkaesekaestchen.rnvl.net.Main;
import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;

/**
 * @author Tobias
 *
 * Spielder id unter Main.id
 * BoardType board enth�llt das gesammte bord, den schiebestein und die verbotene position
 * TreasureType treasure enth�llt infos �ber den n�chsten zu findenden schatz
 * List<TreasureType> foundTreasures enth�llt alle sch�tze die schon gefunden wurden (vom spieler? oder allen spielern)
 * List<TreasuresToGoType> die anzahl an sch�tzen die alle spieler noch brauchen um das spiel abzuschlie�en
 * 
 * KI Zug Berechnung:
 * 1. Erstelle in einer Liste alle 48 g�ltigen Z�ge
 * 2. Filtere alle Z�ge die nicht g�ltig sind (gibt es ung�ltige z�ge?)
 * 3. Bewerte alle Z�ge und w�hle den Besten zug aus:
 * 		Zugbewertung:
 * 			- Schatz erreicht: +1000 //muss immer vorgezogen werden
 * 			- K�rzeste m�gliche Distanz zum Schatz nach move: 
 * 				Distanz zum Schatz in Manhattan Norm: dist
 * 				Punkte: y = 1.02040816327*Math.pow(dist, 2) + -50.0*dist + 500.0 
 * 			- Erreichbare Felder f�r den eigenen Spieler: +5
 * 			- Erreichbare Sch�tze f�r den eigenen Spieler: schon gefunden +0, noch nicht gefunden +10
 * 			- Erreichbare Felder f�r den n�chsten Spieler: -4
 * 			- Erreichbare Sch�tze f�r den n�chsten Spieler: -8
 * 			- Erreichbare Felder f�r die weiteren Spieler: -2
 * 			- Erreichbare Sch�tze f�r die weiteren Spieler: -4
 */
public class AITobias implements Player {
	
	@Override
	public MoveMessageType getZug(BoardType board, TreasureType treasure, List<TreasureType> foundTreasures,
			List<TreasuresToGoType> treasuresToGo) {
		int myId = Main.id;
		
		
		
		return null;
	}
	
	private int getNumPlayers() {
		//TODO
		return 0;
	}
	
	private int getNextPlayerId() {
		//TODO
		return 0;
	}
	
	private List<int[]> getPlayersPositions() {
		//TODO
		return null;
	}
	
	private int[] getNextTreasurePos() {
		//TODO
		return null;
	}
}