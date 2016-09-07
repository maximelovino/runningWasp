package ch.hepia.waspr;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Thomas on 6 sept. 2016
 */
public class Connexion extends Thread {

	private Socket socket;
	private BufferedReader inFromClient;
	private DataOutputStream outToClient;

	public Connexion(Socket socket) {
		this.socket = socket;
		this.setName("Connexion with " + socket.getInetAddress());
	}

	@Override
	public void run() {
		try {
			inFromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			outToClient = new DataOutputStream(socket.getOutputStream());
			String message = "";

			while (socket.isConnected()) {
				message = inFromClient.readLine();
				if (message != null) {
					System.out.println("[Server] " + message + " from " + socket.getInetAddress());
					//outToClient.writeBytes(message + "\n");
				}
			}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public synchronized void ShutDown() {
		try {
			inFromClient.close();
			outToClient.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
