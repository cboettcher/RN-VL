package de.teamkaesekaestchen.rnvl.net;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.UnmarshalException;

import de.teamkaesekaestchen.rnvl.ai.AITim;
import de.teamkaesekaestchen.rnvl.ai.Player;
import de.teamkaesekaestchen.rnvl.io.XmlInStream;
import de.teamkaesekaestchen.rnvl.io.XmlOutStream;
import de.teamkaesekaestchen.rnvl.prot.AcceptMessageType;
import de.teamkaesekaestchen.rnvl.prot.AwaitMoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.BoardType;
import de.teamkaesekaestchen.rnvl.prot.DisconnectMessageType;
import de.teamkaesekaestchen.rnvl.prot.LoginMessageType;
import de.teamkaesekaestchen.rnvl.prot.LoginReplyMessageType;
import de.teamkaesekaestchen.rnvl.prot.MazeCom;
import de.teamkaesekaestchen.rnvl.prot.MazeComType;
import de.teamkaesekaestchen.rnvl.prot.MoveMessageType;
import de.teamkaesekaestchen.rnvl.prot.TreasureType;
import de.teamkaesekaestchen.rnvl.prot.TreasuresToGoType;
import de.teamkaesekaestchen.rnvl.prot.WinMessageType;

public class Main {

	private static String host = "localhost";
	private static int port = 5123;
	
	private static volatile int countFailedMoves = 0;
	private static volatile int countTotalFailedMoves = 0;
	

	// static communication variables
	private static Player player;
	private static Socket socket;
	private static XmlInStream instream;
	private static XmlOutStream outstream;
	public static int id = -1;

	public static final int MAX_LOGIN_TRIES = 5;
	public static final String TEAM = "Team Kaesekaestchen";
	private static final Logger logger = Logger.getLogger("Main");
	
	
	public static final int UNKNOWN_HOST_CODE = 1000;
	public static final int CONNECTION_FAILED_CODE = 1001;
	public static final int LOGIN_FAILED_CODE = 1002;
	
	private static int login() {
		MazeCom mc;
		boolean loginSuccess = false;
		LoginMessageType lmt;
		LoginReplyMessageType lrmt;
		int c = 0;
		int _id = -1;

		while (!loginSuccess && c < MAX_LOGIN_TRIES) {
			lmt = new LoginMessageType();
			lmt.setName(TEAM);
			mc = new MazeCom();
			mc.setLoginMessage(lmt);
			mc.setMcType(MazeComType.LOGIN);
			outstream.write(mc);
			try {
				mc = instream.readMazeCom();
			} catch (UnmarshalException | IOException e) {
				logger.severe(e.getMessage());
			}
			if (mc.getMcType().equals(MazeComType.LOGINREPLY)) {
				lrmt = mc.getLoginReplyMessage();
				_id = lrmt.getNewID();
				loginSuccess = true;
			} else if (mc.getMcType().equals(MazeComType.ACCEPT)) {
				logger.info("Login failed, trying again.");
				logger.info("ErrorType is " + mc.getAcceptMessage().getErrorCode().value());
			} else {
				break;
			}
			c++;
		}
		
		return _id;
	}

	public static void main(String[] args) {
		if (args.length >= 1) {
			host = args[0];
		}
		if (args.length >= 2) {
			port = Integer.parseInt(args[1]);
		}
		logger.setLevel(Level.INFO);

		// TODO select AI type depending on commandline arguments
		player =  new AITim();

		try {
			socket = new Socket(host, port);
		} catch (UnknownHostException e) {
			logger.warning("Unknown host.");
			logger.warning("Shutting down Program.");
			System.exit(UNKNOWN_HOST_CODE);
		} catch (IOException e) {
			logger.warning("An Exception occured while connecting to the Server");
			logger.warning(e.getMessage());
			logger.warning("Shutting down Program.");
			System.exit(CONNECTION_FAILED_CODE);
		}

		logger.info("Successfully connected to server at " + host + ":" + port);

		try {
			instream = new XmlInStream(new BufferedInputStream(socket.getInputStream()));
		} catch (IOException e) {
			logger.warning("An Exception occured while connecting to the Server");
			logger.warning(e.getMessage());
			logger.warning("Shutting down Program.");
			System.exit(CONNECTION_FAILED_CODE);
		}
		try {
			outstream = new XmlOutStream(new BufferedOutputStream(socket.getOutputStream()));
		} catch (IOException e) {
			logger.warning("An Exception occured while connecting to the Server");
			logger.warning(e.getMessage());
			logger.warning("Shutting down Program.");
			System.exit(CONNECTION_FAILED_CODE);
		}

		logger.info("Successfully attached to the network streams.");

		MazeCom mc = null;

		id = login();

		if (id < 0) {
			logger.warning("Login failed!");
			logger.warning("Shutting down program.");
			System.exit(LOGIN_FAILED_CODE);
		}

		// Now that login was successfull we can start the game loop:

		boolean accepted;
		boolean playing = true;
		boolean move = false;
		int winnerID = -1;
		AcceptMessageType amt;
		DisconnectMessageType dmt;
		AwaitMoveMessageType ammt;
		WinMessageType wmt;
		MoveMessageType mmt;
		BoardType bt = null;
		TreasureType tt = null;
		List<TreasureType> foundTT = null;
		List<TreasuresToGoType> togoTT = null;
		while (playing) {
			accepted = false;

			while (!accepted) {
				try {
					mc = instream.readMazeCom();
				} catch (UnmarshalException | IOException e) {
					logger.severe(e.getMessage());
				}

				switch (mc.getMcType()) {
				case ACCEPT:
					amt = mc.getAcceptMessage();
					if (amt.isAccept()) {
						accepted = true;
						move = false;
						logger.info("Your move was accepted.");
						countFailedMoves = 0;
						break;
					} else {
						logger.warning("Your move was not accepted!");
						countFailedMoves++;
						countTotalFailedMoves++;
						move = true;
					}
					break;

				case AWAITMOVE:
					ammt = mc.getAwaitMoveMessage();
					bt = ammt.getBoard();
					tt = ammt.getTreasure();
					foundTT = ammt.getFoundTreasures();
					togoTT = ammt.getTreasuresToGo();
					move = true;
					break;

				case DISCONNECT:
					dmt = mc.getDisconnectMessage();
					logger.warning("Server closed connection.");
					logger.info("Error of DisconnectMessage: " + dmt.getErrorCode() + " " + dmt.getName());
					playing = false;
					accepted = true;
					move = false;
					break;
				
				case WIN:
					wmt = mc.getWinMessage();
					winnerID = wmt.getWinner().getId();
					if (winnerID == id) {
						logger.info("You have won this game!");
						System.out.println("Spiel gewonnen");
					} else {
						logger.info("You have lost this game.");
						System.out.println("Spiel verloren");
					}
					break;

				default:
					// TODO should never happen
				}
				
				if (move) {
					mmt = player.getZug(bt, tt, foundTT, togoTT);
					mc = new MazeCom();
					mc.setId(id);
					mc.setMcType(MazeComType.MOVE);
					mc.setMoveMessage(mmt);
					outstream.write(mc);
				}
			}

		}

	}
	
	public static int getFailedMoves() {
		return countFailedMoves;
	}
	
	public static int getTotalFailedMoves() {
		return countTotalFailedMoves;
	}

}
