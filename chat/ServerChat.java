import java.io.*;
import java.net.ServerSocket;

public class ServerChat{
		int port;
		ServerSocket listener;

	public ServerChat(int port){
		try{
			this.port = port;
			listener = new ServerSocket(port);
			System.out.println("ServerChat listening on port " + this.port + "...");
			while(true){
				Socket socket = listener.accept();
				System.out.println("Connection accepted");
				new ServiceChat(socket, ServiceChat.getNbClients());
			}
		}
		catch (IOException e){
			System.out.println("IOException raised: connection issue");
		}
	}
	public ServerChat(){
		this(4242);
	}

	public log(String message){
		System.out.println(message);
	}
}