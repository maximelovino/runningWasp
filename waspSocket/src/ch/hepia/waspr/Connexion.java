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
	
	private final String URL_CO = "http://127.0.0.1:80/run.php";
	private final String CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();

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
					
					
					
					String[] argument = message.split(";");
					if(argument[0].matches("start"))
					{
						String query = String.format("uid=%s&start", URLEncoder.encode(argument[1], CHARSET));
						URLConnection connection = new URL(URL_CO + "?" + query).openConnection();
						connection.getInputStream();
						
						System.out.println(connection.getURL().toString());
						System.out.println("RUN STARTED with UID : " + argument[1]);
					}
					else if(argument[0].matches("run"))
					{
						String query = String.format("x=%s&y=%s&cnt=%s&time=%s&uid=%s", 
								URLEncoder.encode(argument[1], CHARSET), 
								URLEncoder.encode(argument[2], CHARSET), 
								URLEncoder.encode(argument[3], CHARSET), 
								URLEncoder.encode(argument[4], CHARSET), 
								URLEncoder.encode(argument[5], CHARSET));
						URLConnection connection = new URL(URL_CO + "?" + query).openConnection();
						connection.getInputStream();

						System.out.println(connection.getURL().toString());
						System.out.println("NEW POINT with :");
						System.out.println("X : " + argument[1]);
						System.out.println("Y : " + argument[2]);
						System.out.println("COUNT : " + argument[3]);
						System.out.println("TIME : " + argument[4]);
						System.out.println("UID : " + argument[5]);
					}
					else if(argument[0].matches("end"))
					{
						String query = String.format("uid=%s&time=%s&end", URLEncoder.encode(argument[1], CHARSET), URLEncoder.encode(argument[2], CHARSET));
						URLConnection connection = new URL(URL_CO + "?" + query).openConnection();
						connection.getInputStream();

						System.out.println(connection.getURL().toString());
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
