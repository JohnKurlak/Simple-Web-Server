/******************************************************************************\
	Author:		John Kurlak
	Filename:	Server.java
	Purpose:	The Server class starts the server and waits until the user
				wants to shut it down.  The WebServer class starts listening
				for connections on the appropriate port and accepts all client
				requests, which are then processed in a separate thread.
\******************************************************************************/

// Import the network and input/output packages
import java.net.*;
import java.io.*;

public class Server
{
	// This method is the first part of the program that is run, and it loads the configuration and starts the server
	public static void main(String[] args)
	{
		// Wait until the user tries to shutdown and display a shutdown message
		ShutdownHook shutdownHook = new ShutdownHook();
		Runtime.getRuntime().addShutdownHook(shutdownHook);

		// Load the configuration and start the server
		Config.load();
		WebServer myServer = new WebServer(Config.getPort());
	}

	// This method is called when the server is shutting down
	public static void terminate()
	{
		// Display how long the server has been running
		Utils.disp("");
		Utils.disp("--------------------------------------------------------------------------------");
		Utils.displn("Session terminated after: " + (System.currentTimeMillis() - Global.timeStart) / 1000 + " second(s)");
		Utils.disp("--------------------------------------------------------------------------------");
		Utils.disp("");
	}
}

class WebServer
{
	// Declare public variables
	boolean running = true;
	int port = 80;
	String serverName = "Simple HTTP Server";
	String serverVersion = "1.0";
	String serverIP = "0.0.0.0";
	ServerSocket serverSocket = null;
	Socket socket = null;

	// This method starts a server on the given port
	public WebServer(int port)
	{
		this.port = port;
		this.start();
	}

	// This method changes the port on which the server is to run
	public void setPort(int port)
	{
		this.port = port;
	}

	// This method displays a message when the server is started
	public void dispIntro()
	{
		Utils.disp("--------------------------------------------------------------------------------");
		Utils.disp("                                " + this.serverName + " " + this.serverVersion + "                          ");
		Utils.disp("                                    By John Kurlak                              ");
		Utils.disp("                                (johnkurlak@gmail.com)                          ");

		Utils.disp("--------------------------------------------------------------------------------");
		Utils.disp("> Running at http://" + this.serverIP + ":" + this.port);
	}

	// This method starts listening for connection requests and handles them as they come
	public void start()
	{
		// Declare variables
		boolean err = false;
		serverSocket = null;

		// Check to see if the port is available
		try
		{
			serverSocket =  new ServerSocket(this.port);
		}
		catch (Exception e)
		{
			err = true;
		}

		// Find the local IP address
		try
		{
			this.serverIP = Utils.getBetween(serverSocket.getInetAddress().getLocalHost().toString(), "/", "");
		}
		catch (Exception e)
		{
			this.serverIP = "0.0.0.0";
		}

		// Display the introduction message
		this.dispIntro();

		// Stop the program is the server cannot run at the defined port
		if (err)
		{
			Utils.disp("> The port at which the server is configured to run is unavailable.\n\tShutting down...");
			System.exit(1);
		}

		// Declare variables
		ThreadGroup threadGroup = new ThreadGroup("HTTP Request Threads");
		boolean gzip = Global.config.get("gzip").toLowerCase().trim().equals("enabled");

		// Continously check for new connection requests
		while (this.running)
		{
			try
			{
				// Accept a connection request
				socket = serverSocket.accept();

				// Send the connection request to a new thread to process it
				Thread t = new RequestHandler(socket, this.port, this.serverName, this.serverVersion, this.serverIP, gzip);
    			Thread thread = new Thread(threadGroup, t);
    			thread.start();
			}
			catch (Exception e)
			{
				e.printStackTrace();
				continue;
			}
		}
	}
}