package multiBarCodeReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.InvalidTelnetOptionException;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

public class BCReader implements TelnetNotificationHandler {
	
	private TelnetClient telnetSocket;
	private InputStream in;
	private OutputStream out;
	
	private String serverAddress = null;
	private int serverPort = 23;
	private String userName = "admin\r\n";
	private String password = "\r\n";
	
	private String MAC = null;
	
	private final static String GET_MAC_COMMAND = "||>get device.mac-address\r\n";
	
	/***
	 * Default constructor, sets the serverAddress and use default Telnet port 23;
	 * @param serverAddress
	 */
	public BCReader(String serverAddress) {
		this.serverAddress = serverAddress;
	}
	
	/***
	 * Construct an instance with the specified serverAddress and serverPort
	 * @param serverAddress
	 * @param serverPort
	 */
	public BCReader(String serverAddress, int serverPort) {
		this.serverAddress = serverAddress;
		this.serverPort = serverPort;
	}
	
	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}

	/***
	 * send messages to Barcode Reader
	 * @param msg input ... string to be sent.
	 */
	public void sendMsg(String msg) {
		
		byte[] outstr = new byte[1024];
		int outstrLength = 0;
		
		outstr = msg.getBytes();
		outstrLength = outstr.length;
		
		try {
			out.write(outstr, 0, outstrLength);
			out.flush();
		} catch (Exception e) {
			System.out.println("Error sending msg to server!!!!"); ///***must be modified***
		}
	}
	
	public boolean start() {
		
		//create telnetSocket instance
		telnetSocket = new TelnetClient();
		
		//set option handlers
		TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
		EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
		SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);
		
		try {
			telnetSocket.addOptionHandler(ttopt);
			telnetSocket.addOptionHandler(echoopt);
			telnetSocket.addOptionHandler(gaopt);
		} catch (Exception e) {
			System.out.println("Error registering option handlers!!!"); ///****must be modified****
			return false;
		}
		
		//connect to barcode reader
		try {
			telnetSocket.connect(serverAddress, serverPort);
		} catch (Exception e) {
			System.out.println("Error connecting to Barcode Reader at IP: " + serverAddress + " !!!"); ///***must be modified***
			return false;
		}
		
		//get input and output streams
		try {
			in = telnetSocket.getInputStream();
			out = telnetSocket.getOutputStream();
		} catch (Exception e) {
			System.out.println("Error getting input and output streams for Barcode reader at IP: " + serverAddress + " !!!"); ///***must be modified***
			return false;
		}
		
		//send user name and password
		sendMsg(userName);
		sendMsg(password);
		
		
		return true;
	}
	
	
	
	@Override
	public void receivedNegotiation(int negotioation_code, int option_code) {
		String command = null;
		switch (negotioation_code) {
		case TelnetNotificationHandler.RECEIVED_DO:
			command = "DO";
			break;
		case TelnetNotificationHandler.RECEIVED_DONT:
			command = "DONT";
			break;
		case TelnetNotificationHandler.RECEIVED_WILL:
			command = "WILL";
			break;
		case TelnetNotificationHandler.RECEIVED_WONT:
			command = "WONT";
			break;
		case TelnetNotificationHandler.RECEIVED_COMMAND:
			command = "COMMAND";
			break;
		default:
			command = Integer.toString(negotioation_code);
			break;
		}
		System.out.println("Received " + command + " for option code " + option_code); //*********must be modified*********
	}
	
	

}
