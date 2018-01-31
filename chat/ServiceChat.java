import java.io.*;
import java.net.Socket;

import jdk.internal.jline.internal.InputStreamReader;

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
	}

	public void init(){
		try{
			input = new BufferedReader(new InputStreamReader(System.in));
			outputs[id] = new PrintStream(socket.getOutputStream());
			nbClients += 1;
		}
		catch(IOException e){
			System.out.println("IOException caught in init()");
		}
	}

	public static int getNbClients(){
		return nbClients;
	}
}