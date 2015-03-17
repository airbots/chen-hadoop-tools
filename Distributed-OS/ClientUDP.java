import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * This is a client program that create a TCP client and send a message to server on port 6543
 * User can input message after -m parameter
 * @author yanjun
 *
 */
public class ClientUDP extends Client{

	public ClientUDP(String server, String protocol, int port) {
		super(server, protocol, 7654);
		// TODO Auto-generated constructor stub
	}
    
    @Override
	public void send(String server, int port, String mes)
			throws UnknownHostException {

		int tries = 0;
		try {
			InetAddress serverAdd[] = InetAddress.getAllByName(server);
			DatagramSocket socket = new DatagramSocket();
			socket.setSoTimeout(60000);
			DatagramPacket sendPacket = new DatagramPacket(mes.getBytes(),
					mes.getBytes().length, serverAdd[0], port);
			DatagramPacket receivePacket = new DatagramPacket(
					new byte[mes.getBytes().length], mes.getBytes().length);

			boolean receivedResponse = false;
			// start to connect server and waiting for server's response
			do {
				socket.send(sendPacket);
				socket.receive(receivePacket);

				if (!receivePacket.getAddress().equals(server)) {
					tries += 1;
					//System.out.println("Received packet from an unknown source\n" +
					//		"Timed out, " + (3 - tries) + "");
				}
				receivedResponse = true;

			} while ((!receivedResponse) && (tries < 3));
			// check response from server and decide whether data is received
			if (receivedResponse) {
				System.out.println("Response Received from server!");
			} else {
				System.out.println("No response -- giving up.");
			}

			socket.close();
		} catch (UnknownHostException uhe){
			System.out.println("Unknown or incorrect server IP address");
		} catch (IOException ioe) {
			System.out.println("Socket can not be built!");
		}
	}
    
    
    public static void main(String[] args) throws UnknownHostException{
		
		String server = null;
		String protocol = null;
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
			System.out.println("Please input server address!\nProgram will stop!");
			System.exit(0);
		}
		if (message.equals(null)){
			System.out.println("NULL message input, program will stop!");
			System.exit(0);
		}
		ClientUDP cudp=new ClientUDP(server, protocol,7654);
		//send message to server
		cudp.send(server, 7654, message);
    }
}
