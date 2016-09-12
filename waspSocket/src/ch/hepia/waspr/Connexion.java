package ch.hepia.waspr;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

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
				if (message != null && message.contains("/")) 
				{
					message = message.split("/")[1];
					System.out.println("[Server] " + message + " from " + socket.getInetAddress());
					
					String url = "http://127.0.0.1:8080";
					String charset = java.nio.charset.StandardCharsets.UTF_8.name();
					
					String[] argument = message.split(";");
					if(argument[0].matches("start"))
					{
						String query = String.format("uid=%s&start", URLEncoder.encode(argument[1], charset));
						URLConnection connection = new URL(url + "?" + query).openConnection();
						
						System.out.println("RUN STARTED with UID : " + argument[1]);
					}
					else if(argument[0].matches("run"))
					{
						String query = String.format("x=%s&y=%s&time=%s&count=%s&uid=%s", 
								URLEncoder.encode(argument[1], charset), 
								URLEncoder.encode(argument[2], charset), 
								URLEncoder.encode(argument[3], charset), 
								URLEncoder.encode(argument[4], charset), 
								URLEncoder.encode(argument[5], charset));
						URLConnection connection = new URL(url + "?" + query).openConnection();
						
						System.out.println("NEW POINT with :");
						System.out.println("X : " + argument[1]);
						System.out.println("Y : " + argument[2]);
						System.out.println("COUNT : " + argument[3]);
						System.out.println("TIME : " + argument[4]);
						System.out.println("UID : " + argument[5]);
					}
					else if(argument[0].matches("end"))
					{
						String query = String.format("uid=%s&time=%s&end", URLEncoder.encode(argument[1], charset), URLEncoder.encode(argument[2], charset));
						URLConnection connection = new URL(url + "?" + query).openConnection();
						
						System.out.println("RUN ENDED with UID : " + argument[1] + ", duration " + argument[2] + " sec");
					}
					else
					{
						outToClient.writeBytes("ERROR");
					}
					
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
