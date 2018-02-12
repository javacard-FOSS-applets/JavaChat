import java.io.*;
import java.net.*;

public class ServerChat{

	public static void main(String args[]){
		int port = 2222;
		ServerSocket listener;

		try{
			if(args.length == 1){
				port = Integer.parseInt(args[0]);
			}
			listener = new ServerSocket(port);
			System.out.println("ServerChat is listening on port " + port);

			while(true){
				System.out.println("Listening...");
				Socket socket = listener.accept();
				System.out.println("Trying to connect...");
				if(ServiceChat.nbClients < ServiceChat.NBCLIENTSMAX){
					System.out.println("Connection accepted");
					new ServiceChat(socket);
				}
				else{
					System.out.println("Connection refused: max number of connections reached");
					PrintStream output = new PrintStream(socket.getOutputStream());
					output.println("Maximum number of connections reached... Please wait for someone to disconnect\r");
					socket.close();
				}
			}
		}
		catch (IOException e){
			System.out.println("IOException raised: connection issue");
		}
	}
}