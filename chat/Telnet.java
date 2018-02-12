import java.io.*;
import java.net.*;

public class Telnet extends Thread{
	Socket s;
	BufferedReader inputConsole, inputNetwork;
	PrintStream outputConsole, outputNetwork;
	public boolean running;

	public Telnet(String[] args){
		try{
			s = new Socket(args[0], Integer.parseInt(args[1]));
			initInputOutput(s);
			running = true;
			start();
			listenConsole();
		}
		catch (IOException e){
			System.out.println("IOException caught in Telnet constructor\r");
		}
	}

	public void run(){
		listenNetwork();
	}

	public static void main(String[] args){
		new Telnet(args);
	}

	public void initInputOutput(Socket s){
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

	public void listenConsole(){
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

	public void listenNetwork(){
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

	public void disconnect(){
		running = false;
	}
}