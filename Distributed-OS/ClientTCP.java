import java.io.BufferedReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.Socket;


/**
 * This is a client program that create a TCP client and send a message to server on port 6543
 * User can input message after -m parameter
 * @author yanjun
 *
 */

public class ClientTCP extends Client {

	public ClientTCP(String server, String protocol, int port) {
		super(server, protocol, 6543);
		// TODO Auto-generated constructor stub
	}
    
	@Override
	public void send(String server, int port, String mes){
		try{
		    Socket socket = new Socket(server, port);  
 
		    OutputStream out = socket.getOutputStream();  
		    out.write(mes.getBytes());  	
		    out.flush();
		    System.out.println("Message Sent!");
		    socket.close();
		}catch (Exception e){
			System.out.println("Error during connect to server!");
			e.printStackTrace();
		}
	}
	public static void main(String[] args){
		
		String server = null;
		String protocol = "TCP";
		String message = null;
		//parse input parameter
		for(int i=0;i<args.length;i++){
			if(args[i].equals("-s")){
				server = args[++i];
			} else if(args[i].equals("-m")){
				message = args[++i];
			}
		}
		//check user input
		if (server.equals(null)){
			System.out.println("Please input server address!");
			System.exit(0);
		}
		//connect to server using TCP
		ClientTCP ctcp = new ClientTCP(server, protocol, 6543);
		if (message.equals(null)){
			System.out.println("NULL message input, program will stop!");
			System.exit(0);
		}
		//send message to server
		ctcp.send(server, 6543, message);
	}
}
