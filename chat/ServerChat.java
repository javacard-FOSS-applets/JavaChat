import java.io.*;
import java.net.*;

public class ServerChat{

	public static void main(String args[]){
		int port = 4242;
		ServerSocket listener;

		try{
			if(args.length == 1){
				port = Integer.parseInt(args[0]);
			}
			listener = new ServerSocket(port);
			System.out.println("ServerChat is listening on port " + port + "...");

			while(true){
				Socket socket = listener.accept();
				System.out.println("Connection accepted");
				new ServiceChat(socket);
			}
		}
		catch (IOException e){
			System.out.println("IOException raised: connection issue");
		}
	}
}