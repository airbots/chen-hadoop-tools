/**
 * This is the client super class for CSE952 course hw3
 */
import java.net.Socket;
import java.net.UnknownHostException;


public abstract class Client {
	
	String serverIP;
	String protocol;
	int port;
	Socket s1;
	public Client(String server, String protocol, int port){
		this.serverIP = server;
		this.protocol = protocol;
		this.port = port;
	}
	
	public void send(String server, int port, String message) throws UnknownHostException{}
	
}
