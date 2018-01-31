import java.io.*;
import java.net.*;

public class ServiceChat extends Thread{
	public static int nbClients = 0;
	static final int NBCLIENTSMAX = 21;
	public static PrintStream outputs[] = new PrintStream[NBCLIENTSMAX];
	public int id;
	public Socket socket;
	public BufferedReader input;
	
	public ServiceChat(Socket socket, int id){
		this.socket = socket;
		this.id = id;
		start();
	}

	public void run(){
		init();
		String message = "";
		sendMessage("Welcome to this chat, User#" + id + "!", NBCLIENTSMAX, id);
		sendMessage("User#" + id + " has joined the chat", NBCLIENTSMAX);
		while(true){
			try{
				message = input.readLine();
				sendMessage(message, id);
			}
			catch (IOException e){
				System.out.println();
			}
		}
	}

	public void init(){
		try{
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outputs[id] = new PrintStream(socket.getOutputStream());
			nbClients += 1;
		}
		catch(IOException e){
			System.out.println("ID#" + id + ": IOException caught in init()");
		}
	}

	public static int getNbClients(){
		return nbClients;
	}

	public void sendMessage(String message, int sender, int dest){
		String messageFrom = "";
		if(sender == NBCLIENTSMAX){
			messageFrom = "[SERVER] ";
		}
		else{
			messageFrom = "[User#" + id + "] ";
		}
		outputs[dest].println(messageFrom.concat(message));
	}
	
	public void sendMessage(String message, int sender){
		for(int i = 0; i < NBCLIENTSMAX; i++){
			if(i != id){
				sendMessage(message, sender, i);
			}
		}
	}
}