/******************************************************************************\
	Author:		John Kurlak
	Filename:	ShutdownHook.java
	Purpose:	This class makes sure that the termination message is displayed
				before the server is shut down.
\******************************************************************************/

public class ShutdownHook extends Thread
{
	// This method creates a new thread that calls the server shutdown method
	public void run()
	{
		Server.terminate();
	}
}