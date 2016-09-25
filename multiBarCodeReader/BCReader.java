package multiBarCodeReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TelnetNotificationHandler;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;

public class BCReader implements TelnetNotificationHandler {
	
	private TelnetClient telnetSocket;
	private InputStream in;
	private OutputStream out;
	
	private String serverAddress = null;	
	private int serverPort;
	private String userName;
	private String password;
	
	private String MAC = null;
	
	private final static String GET_MAC_COMMAND = "||>get device.mac-address\r\n";
	
	private static String defaultUserName = "admin";
	private static String defaultPassword = "";
	private static int defaultServerPort = 23;
	
	/***
	 * Default constructor, sets the serverAddress and use default Telnet port 23, username
	 * and password.
	 * @param serverAddress
	 */
	public BCReader(String serverAddress) {
		
		this(serverAddress, defaultUserName, defaultPassword, defaultServerPort);
	}
	
	/***
	 * Construct an instance with the specified serverAddress and serverPort and use the
	 * default username and password.
	 * @param serverAddress
	 * @param serverPort
	 */
	public BCReader(String serverAddress, int serverPort) {
		
		this(serverAddress, defaultUserName, defaultPassword, serverPort);
	}
	
	/***
	 * Construct an instance with the specified serverAddress, username and password. And use 
	 * the default server port.
	 * @param serverAddress
	 * @param userName
	 * @param password
	 */
	public BCReader(String serverAddress, String userName, String password) {
		
		this(serverAddress, userName, password, defaultServerPort);
	}
	
	/***
	 * Construct an instance with the specified serverAddress, username, password and 
	 * server port.
	 * @param serverAddress
	 * @param userName
	 * @param password
	 * @param serverPort
	 */
	public BCReader(String serverAddress, String userName, String password, int serverPort) {
		this.serverAddress = serverAddress;
		this.userName = userName + "\r\n";
		this.password = password + "\r\n";
		this.serverPort = serverPort;
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
	
	//start method
	
	/***
	 * Creates telnet clinet socket, gets input and output streams and sends username, password
	 * and get MAC address command.
	 * @return true if no error is happened while establishing connection.
	 */
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
		
		//send get MAC address command
		sendMsg(GET_MAC_COMMAND);
		
		return true;
	}
	
	/***
	 * disconnects the socket and its input and output streams
	 */
	public void disconnect() {
		
		try {
			telnetSocket.disconnect();
		} catch (IOException e) {
			// nothing to do
		}
	}
	
	/***
	 * Received negotiation method
	 */
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
