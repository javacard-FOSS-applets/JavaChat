import java.io.*;
import java.net.*;

public class ServiceChat extends Thread{
	public static int nbClients = 0;
	static final int NBCLIENTSMAX = 21;
	public static PrintStream outputs[] = new PrintStream[NBCLIENTSMAX];
	public Socket socket;
	public BufferedReader input;
	public PrintStream output;
	public String userName;
	
	//TODO: Handle disconnections
	//TODO: Allow pseudonyms
	public ServiceChat(Socket socket){
		this.socket = socket;
		start();
	}

	public void run(){
		init();
		setUserName();
		String message = "";
		sendMessage("Welcome to this chat, " + userName + "!", NBCLIENTSMAX, getThreadId());
		sendMessage(userName + " has joined the chat", NBCLIENTSMAX);
		while(true){
			try{
				message = input.readLine();
				sendMessage(message, getThreadId());
			}
			catch (IOException e){
				System.out.println();
			}
		}
	}

	public synchronized void init(){
		try{
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintStream(socket.getOutputStream());
			outputs[nbClients] = output;
			nbClients += 1;
		}
		catch(IOException e){
			System.out.println("IOException caught in init()");
		}
	}

	public void setUserName(){
		output.println("Please state your name");
		try{
			userName = input.readLine();
		}
		catch (IOException e){
			System.out.println("IOException caught in setUserName()");
		}
	}

	public int getThreadId(){
		for(int i = 0; i < nbClients; i++){
			if(this.output == outputs[i]){
				return i;
			}
		}
		return -1;
	}

	//TODO: Look if the PrintStream exists before sending
	public void sendMessage(String message, int sender, int dest){
		String messageFrom = "";
		if(sender == NBCLIENTSMAX){
			messageFrom = "[SERVER]\t";
		}
		else{
			messageFrom = "[" + userName + "]\t";
		}
		outputs[dest].println(messageFrom.concat(message));
	}
	
	public void sendMessage(String message, int sender){
		int threadId = getThreadId();
		for(int i = 0; i < nbClients; i++){
			if(i != threadId){
				sendMessage(message, sender, i);
			}
		}
	}
}