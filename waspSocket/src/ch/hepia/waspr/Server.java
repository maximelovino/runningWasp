package ch.hepia.waspr;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Thomas on 6 sept. 2016
 */
public class Server {

	public final static int PORT = 8173;

	private boolean running = false;
	private List<Connexion> connexions;

	private ServerSocket coSocket;

	public Server() {
		try {
			coSocket = new ServerSocket(PORT);
			connexions = new LinkedList<Connexion>();
			running = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void start() {
		while(running) {
			try {
				Socket clientSocket = coSocket.accept();
				Connexion co = new Connexion(clientSocket);
				connexions.add(co);
				co.start();
				System.out.println("[Server] Connected with "+clientSocket.getInetAddress());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void Dispose() {
		running = false;
		for (Connexion connexion : connexions) {
			connexion.ShutDown();
		}
		try {
			coSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Server s = new Server();
		s.start();
		
		System.out.println("SERVER STARTED, AWAITING CONNEXIONS.");
	}

}
