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
 * BoardType board enthällt das gesammte bord, den schiebestein und die verbotene position
 * TreasureType treasure enthällt infos über den nächsten zu findenden schatz
 * List<TreasureType> foundTreasures enthällt alle schätze die schon gefunden wurden (vom spieler? oder allen spielern)
 * List<TreasuresToGoType> die anzahl an schätzen die alle spieler noch brauchen um das spiel abzuschließen
 * 
 * KI Zug Berechnung:
 * 1. Erstelle in einer Liste alle 48 gültigen Züge
 * 2. Filtere alle Züge die nicht gültig sind (gibt es ungültige züge?)
 * 3. Bewerte alle Züge und wähle den Besten zug aus:
 * 		Zugbewertung:
 * 			- Schatz erreicht: +1000 //muss immer vorgezogen werden
 * 			- Kürzeste mögliche Distanz zum Schatz nach move: 
 * 				Distanz zum Schatz in Manhattan Norm: dist
 * 				Punkte: y = 1.02040816327*Math.pow(dist, 2) + -50.0*dist + 500.0 
 * 			- Erreichbare Felder für den eigenen Spieler: +5
 * 			- Erreichbare Schätze für den eigenen Spieler: schon gefunden +0, noch nicht gefunden +10
 * 			- Erreichbare Felder für den nächsten Spieler: -4
 * 			- Erreichbare Schätze für den nächsten Spieler: -8
 * 			- Erreichbare Felder für die weiteren Spieler: -2
 * 			- Erreichbare Schätze für die weiteren Spieler: -4
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