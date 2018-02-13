import java.io.*;
import java.net.*;

public class ClientChat extends Thread{
	Socket s;
	BufferedReader inputConsole, inputNetwork;
	PrintStream outputConsole, outputNetwork;
	boolean running;

	public ClientChat(String[] args){
		try{
			if(args.length == 2){
				s = new Socket(args[0], Integer.parseInt(args[1]));	
			}
			s = new Socket("localhost", 2222);
			initInputOutput(s);
			running = true;
			start();
			listenConsole();
		}
		catch (IOException e){
			System.out.println("IOException caught in ClientChat constructor\r");
		}
	}

	public void run(){
		listenNetwork();
	}

	public static void main(String[] args){
		new ClientChat(args);
	}

	void initInputOutput(Socket s){
		// Initialize IO console
		try{
			inputConsole = new BufferedReader(new InputStreamReader(System.in));
			outputConsole = new PrintStream(System.out);
		}
		catch (Exception e){
			System.out.println("Exception caught initializing IO console\r");
		}

		// Initialize IO network
		try{
			inputNetwork = new BufferedReader(new InputStreamReader(s.getInputStream()));
			outputNetwork = new PrintStream(s.getOutputStream());
		}
		catch (IOException e){
			System.out.println("IOException caught initializing IO network\r");
		}
	}

	void listenConsole(){
		String consoleMessage = "";
		while(running){
			try{
				consoleMessage = inputConsole.readLine();
				outputNetwork.println(consoleMessage);
			}
			catch (IOException e){
				outputConsole.println("IOException caught in listenConsole()\r");
			}
		}
	}

	void listenNetwork(){
		String networkMessage = "";
		while(running){
			try{
				networkMessage = inputNetwork.readLine();
				if(networkMessage == null){
					disconnect();
				}
				else{
					outputConsole.println(networkMessage);
				}
			}
			catch (IOException e){
				outputNetwork.println("IOException caught in listenNetwork()\r");
			}
		}
	}

	void disconnect(){
		running = false;
	}
}