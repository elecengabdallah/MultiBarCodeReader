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
	
	private InputStream in;
	private OutputStream out;
	private TelnetClient telnetSocket;
	
	private String serverAddress = null;
	private int serverPort = 23;
	
	private String MAC = null;
	
	
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
	
	public boolean start() {
		
		telnetSocket = new TelnetClient();
		
		TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
		EchoOptionHandler echoopt = new EchoOptionHandler(true, false, true, false);
		SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(true, true, true, true);
		
		try {
			telnetSocket.addOptionHandler(ttopt);
			telnetSocket.addOptionHandler(echoopt);
			telnetSocket.addOptionHandler(gaopt);
		} catch (InvalidTelnetOptionException e) {
			System.out.println("Error registering option handlers1: " + e.getMessage()); ///****must be modified****
		} catch (IOException e) {
			System.out.println("Error registering option handlers2: " + e.getMessage());///****must be modified****
		} 
		
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
