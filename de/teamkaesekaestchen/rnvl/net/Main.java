package de.teamkaesekaestchen.rnvl.net;

import java.awt.TrayIcon.MessageType;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.bind.UnmarshalException;

import de.teamkaesekaestchen.rnvl.ai.Player;
import de.teamkaesekaestchen.rnvl.io.XmlInStream;
import de.teamkaesekaestchen.rnvl.io.XmlOutStream;
import de.teamkaesekaestchen.rnvl.prot.ErrorType;
import de.teamkaesekaestchen.rnvl.prot.LoginMessageType;
import de.teamkaesekaestchen.rnvl.prot.LoginReplyMessageType;
import de.teamkaesekaestchen.rnvl.prot.MazeCom;
import de.teamkaesekaestchen.rnvl.prot.MazeComType;

public class Main {
	
	private static String host = "localhost";
	private static int port = 8080;
	
	//static communication variables
	private static Player player;
	private static Socket socket;
	private static XmlInStream instream;
	private static XmlOutStream outstream;
	private static int id = -1;
	
	
	public static final int MAX_LOGIN_TRIES = 5;
	public static final String TEAM = "team kaesekaestchen";
	private static final Logger logger = Logger.getLogger("Main");
	public static final int UNKNOWN_HOST_CODE = 1000;
	public static final int CONNECTION_FAILED_CODE = 1001;
	public static final int LOGIN_FAILED_CODE = 1002;
	
	
	public static void main(String[] args) {
		if (args.length >= 1) {
			host = args[0];
		}
		if (args.length >= 2) {
			port = Integer.parseInt(args[1]);
		}
		logger.setLevel(Level.INFO);
		
		//TODO setPlayer
		
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
		
		logger.info("Successfully connected to server at " + host + ":"  +port);
		
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
		
		boolean loginSuccess = false;
		LoginMessageType lmt;
		LoginReplyMessageType lrmt;
		int c = 0;
		
		while (!loginSuccess && c < MAX_LOGIN_TRIES) {
			lmt = new LoginMessageType();
			lmt.setName(TEAM);
			mc = new MazeCom();
			mc.setLoginMessage(lmt);
			mc.setId(id);
			mc.setMcType(MazeComType.LOGIN);
			outstream.write(mc);
			try {
				mc = instream.readMazeCom();
			} catch (UnmarshalException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (mc.getMcType().equals(MazeComType.LOGINREPLY)) {
				lrmt = mc.getLoginReplyMessage();
				id = lrmt.getNewID();
				loginSuccess = true;
			} else if (mc.getMcType().equals(MazeComType.ACCEPT)){
				logger.info("Login failed, trying again.");
				logger.info("ErrorType is " + mc.getAcceptMessage().getErrorCode().value());
			} else {
				break;
			}
			c++;
		}
		
		if (!loginSuccess) {
			logger.warning("Login failed!");
			logger.warning("Shutting down program.");
			System.exit(LOGIN_FAILED_CODE);
		}
		
		
		
		
		

	}

}
