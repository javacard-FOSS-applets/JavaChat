import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class ServiceChat extends Thread{
	public static int nbClients = 0;
	static final int NBCLIENTSMAX = 21;
	public static PrintStream outputs[] = new PrintStream[NBCLIENTSMAX];
	public static String usersList[] = new String[NBCLIENTSMAX];
	public Socket socket;
	public BufferedReader input;
	public PrintStream output;
	public String userName;
	public boolean running;
	
	public ServiceChat(Socket socket){
		this.socket = socket;
		start();
	}

	// TODO: handle instructions for disconnection
	// TODO: users database

	public void run(){
		init();
		System.out.println(nbClients + " client(s) connected");
		setUserName();
		String message = "";
		sendMessage("Welcome to this chat, " + userName + "!", NBCLIENTSMAX, getThreadId());
		sendMessage(userName + " has joined the chat", NBCLIENTSMAX);
		while(running){
			try{
				message = input.readLine();
				if(message == null){
					disconnect();
				}
				else{
					if(message.startsWith("/")){
						doCommand(message);
					}
					else{
						sendMessage(message, getThreadId());
					}
				}
			}
			catch (IOException e){
				System.out.println();
			}
		}
	}

	public synchronized void disconnect(){
		sendMessage("Bye " + userName + "!", NBCLIENTSMAX, getThreadId());
		try{
			socket.close();
			System.out.println("Connection closed");
		}
		catch (IOException e){
			System.out.println("IOException caught in disconnect()");
		}
		popUser();
		nbClients -= 1;
		sendMessage(userName + " has left the chat", NBCLIENTSMAX);
		running = false;
		System.out.println(nbClients + " client(s) connected");
	}

	public void popUser(){
		int threadId = getThreadId();
		for(int i = threadId; i < nbClients - 1; i++){
			outputs[i] = outputs[i+1];
			usersList[i] = usersList[i+1];
		}
		outputs[nbClients - 1] = null;
		usersList[nbClients -1] = null;
	}

	public synchronized void init(){
		try{
			input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			output = new PrintStream(socket.getOutputStream());
			outputs[nbClients] = output;
			nbClients += 1;
			running = true;
		}
		catch(IOException e){
			System.out.println("IOException caught in init()");
		}
	}

	public void setUserName(){
		output.println("Please state your name\r");
		try{
			userName = input.readLine();
			if(userName == ""){
				output.println("You need to identify yourself!\r");
				setUserName();
			}
			if(getUserId(userName) != -1){
				output.println("This user name is already being used on the chat... You need to pick another one.\r");
				setUserName();
			}
			usersList[getThreadId()] = userName;
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

	public int getUserId(String userName){
		for(int i = 0; i < nbClients; i++){
			if(usersList[i] != null && usersList[i].equals(userName)){
				return i;
			}
		}
		return -1;
	}

	public void sendMessage(String message, int sender, int dest){
		String messageFrom = "";
		if(sender == NBCLIENTSMAX){
			messageFrom = "[SERVER] ";
		}
		else{
			messageFrom = "<" + userName + "> ";
		}
		outputs[dest].println(messageFrom.concat(message)+"\r");
	}
	
	public void sendMessage(String message, int sender){
		int threadId = getThreadId();
		for(int i = 0; i < nbClients; i++){
			if(i != threadId){
				sendMessage(message, sender, i);
			}
		}
	}

	public void sendMessage(String message, String destUserName){
		String privMessage = "(private) ".concat(message);
		for(int i = 0; i < nbClients; i++){
			if(usersList[i].equals(destUserName)){
				sendMessage(privMessage, getThreadId(), i);
				return;
			}
		}
		output.println("User " + destUserName + "does not exist...\r");
	}

	public void listUsers(){
		for(int i = 0; i < nbClients; i++){
			output.println(usersList[i]);
		}
	}

	public void doCommand(String command){
		StringTokenizer tokens = new StringTokenizer(command);
		switch(tokens.nextToken()){
			case "/list": listUsers();
				break;
			case "/quit": disconnect();
				break;
			case "/msg": String destUser = tokens.nextToken();
				String message = "";
				while(tokens.hasMoreTokens()){
					message += " ".concat(tokens.nextToken());
				}
				sendMessage(message, destUser);
				break;
			default: output.println("Unknown command\r");
				break;
		}
	}
}