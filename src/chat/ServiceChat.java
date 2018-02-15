import java.io.*;
import java.net.*;
import java.util.*;

public class ServiceChat extends Thread{
	public static int nbClients = 0;
	static final int NBCLIENTSMAX = 21;
	static PrintStream outputs[] = new PrintStream[NBCLIENTSMAX];
	static String usersList[] = new String[NBCLIENTSMAX];
	Socket socket;
	BufferedReader input;
	PrintStream output;
	String userName;
	boolean running;

	public ServiceChat(Socket socket){
		this.socket = socket;
		start();
	}

	//////////////////////////
	// CONNECTION HANDLING //
	////////////////////////

	public void run(){
		init();
		System.out.println(nbClients + " client(s) connected");
		if(authentication()){
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
	}

	synchronized void init(){
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

	/////////////////////
	// AUTHENTICATION //
	///////////////////

	boolean authentication(){
		boolean ret = false;
		int credsIndex = -1;
		setUserName();
		if((credsIndex = hasCredentials()) != 0){
			if(!checkPassword(credsIndex)){
				disconnect(false);
			}
			else{
				ret = true;
			}
		}
		else{
			sendMessage("You do not have a recorded password attached to your login yet.", NBCLIENTSMAX, getThreadId());
			setPassword();
			ret = true;
		}
		return ret;
	}

	void setUserName(){
		sendMessage("Please state your name", NBCLIENTSMAX, getThreadId());
		try{
			userName = input.readLine();
			if(userName == ""){
				sendMessage("You need to identify yourself!", NBCLIENTSMAX, getThreadId());
				setUserName();
			}
			if(getUserId(userName) != -1){
				sendMessage("This user name is already being used on the chat... You need to pick another one", NBCLIENTSMAX, getThreadId());
				setUserName();
			}
			usersList[getThreadId()] = userName;
		}
		catch (IOException e){
			System.out.println("IOException caught in setUserName()");
		}
	}

	void setPassword(){
		String password = "";
		sendMessage("Please type in your new password", NBCLIENTSMAX, getThreadId());
		try{
			password = input.readLine();
			sendMessage("Please confirm password", NBCLIENTSMAX, getThreadId());
			if(password.equals(input.readLine())){
				recordCredentials(password);
				sendMessage("Your password has properly been changed", NBCLIENTSMAX, getThreadId());
			}
			else{
				sendMessage("The two passwords don't match. You need to type in the same password twice.", NBCLIENTSMAX, getThreadId());
				setPassword();
			}
		}
		catch(IOException e){
			System.out.println("IOException caught in setPassword()");
		}
	}

	synchronized void recordCredentials(String password){
		try{
			PrintWriter recorder = new PrintWriter(new BufferedWriter(new FileWriter("credentials", true)));
			recorder.write(userName + ":" + password + "\n");
			recorder.close();
		}
		catch(IOException e){
			System.out.println("IOException caught in recordCredentials");
		}
	}

	int hasCredentials(){
		int index = 0;
		int count = 0;
		String credentials = "";
		try{
			BufferedReader reader = new BufferedReader(new FileReader("credentials"));
			while((credentials = reader.readLine()) != null){
				count += 1;
				if(credentials.startsWith(userName)){
					index = count;
					break;
				}
			}
			reader.close();
		}
		catch(IOException e){}
		return index;
	}

	boolean checkPassword(int credsIndex){
		boolean ret = false;
		int tries = 3;
		String tentative = "";
		String password = "";
		try{
			BufferedReader reader = new BufferedReader(new FileReader("credentials"));
			for(int i = 0; i < credsIndex; i++){
				password = reader.readLine().substring(userName.length() + 1);
			}
			reader.close();
		}
		catch(FileNotFoundException e){
			System.out.println("Credentials file not found");
		}
		catch(IOException e){
			System.out.println("IOException caught in checkPassword()");
		}

		while(tries > 0){
			sendMessage("Please type in your password", NBCLIENTSMAX, getThreadId());
			try{
				tentative = input.readLine();
				if(password.equals(tentative)){
					ret = true;
					break;
				}
				else{
					tries -= 1;
					if(tries > 0){
						sendMessage("You have entered the wrong login/password combination. Try again (remaining " + tries + " tries)", NBCLIENTSMAX, getThreadId());
					}
					else{
						sendMessage("Too many wrong tries", NBCLIENTSMAX, getThreadId());
					}
				}
			}
			catch(IOException e){
				System.out.println("IOException caught in checkPassword()");
			}
		}
		return ret;
	}

	/////////////////////////////
	// DISCONNECTION HANDLING //
	///////////////////////////

	public synchronized void disconnect(boolean notify){
		if(notify){
			sendMessage("Bye " + userName + "!", NBCLIENTSMAX, getThreadId());
		}
		try{
			socket.close();
			System.out.println("Connection closed");
		}
		catch (IOException e){
			System.out.println("IOException caught in disconnect()");
		}
		popUser();
		nbClients -= 1;
		if(notify){
			sendMessage(userName + " has left the chat", NBCLIENTSMAX);
		}
		running = false;
		System.out.println(nbClients + " client(s) connected");
	}

	public synchronized void disconnect(){
		disconnect(true);
	}

	void popUser(){
		int threadId = getThreadId();
		for(int i = threadId; i < nbClients - 1; i++){
			outputs[i] = outputs[i+1];
			usersList[i] = usersList[i+1];
		}
		outputs[nbClients - 1] = null;
		usersList[nbClients -1] = null;
	}

	//////////////
	// GETTERS //
	////////////

	int getThreadId(){
		for(int i = 0; i < nbClients; i++){
			if(this.output == outputs[i]){
				return i;
			}
		}
		return -1;
	}

	int getUserId(String userName){
		for(int i = 0; i < nbClients; i++){
			if(usersList[i] != null && usersList[i].equals(userName)){
				return i;
			}
		}
		return -1;
	}

	//////////////////////
	// MESSAGE SENDING //
	////////////////////

	void sendMessage(String message, int sender, int dest){
		String messageFrom = "";
		if(sender == NBCLIENTSMAX){
			messageFrom = "[SERVER] ";
		}
		else{
			messageFrom = "<" + userName + "> ";
			System.out.println("Message transmitted: " + userName + " -> " + usersList[dest]);
		}
		outputs[dest].println(messageFrom.concat(message)+"\r");
	}
	
	void sendMessage(String message, int sender){
		int threadId = getThreadId();
		for(int i = 0; i < nbClients; i++){
			if(i != threadId){
				sendMessage(message, sender, i);
			}
		}
	}

	void sendMessage(String message, String destUserName){
		String privMessage = "(private)".concat(message);
		for(int i = 0; i < nbClients; i++){
			if(usersList[i].equals(destUserName)){
				sendMessage(privMessage, getThreadId(), i);
				return;
			}
		}
		sendMessage("User " + destUserName + "does not exist...", NBCLIENTSMAX, getThreadId());
	}

	/////////////////////////////
	// CHAT COMMANDS HANDLING //
	///////////////////////////

	void doCommand(String command){
		StringTokenizer tokens = new StringTokenizer(command);
		switch(tokens.nextToken()){
			case "/list":
				listUsers();
				break;
			case "/quit":
				disconnect();
				break;
			case "/msg":
				String destUser = tokens.nextToken();
				String message = "";
				while(tokens.hasMoreTokens()){
					message += " ".concat(tokens.nextToken());
				}
				sendMessage(message, destUser);
				break;
			default:
				sendMessage("Unknown command", NBCLIENTSMAX, getThreadId());
				break;
		}
	}

	void listUsers(){
		String users = "Users currently connected";
		for(int i = 0; i < nbClients; i++){
			users += "\n".concat(usersList[i]);
		}
		sendMessage(users, NBCLIENTSMAX, getThreadId());
	}
}