/******************************************************************************\
	Author:		John Kurlak
	Filename:	Config.java
	Purpose:	This class loads the settings defined in the configuration file.
\******************************************************************************/

class Config
{
	// This method reads the configuration files into an associative array and starts the timer
	public static void load()
	{
		// Read the configuration information, the MIME types, and the start time into the global variable class
		Global.config = Utils.associativeList(Utils.fileContents(".." + Global.slash + "config" + Global.slash + "config.txt"));
		Global.mimeTypes = Utils.associativeList(Utils.fileContents(".." + Global.slash + "config" + Global.slash + "mime.txt"), "\t", "[\\t]+");
		Global.timeStart = System.currentTimeMillis();
	}

	// This method returns the port at which the server is configured to run
	public static int getPort()
	{
		return Integer.parseInt(Global.config.get("port"));
	}

	// This method returns the path of the public files
	public static String getPath()
	{
		// Get the base path
		String ret = Global.config.get("path");

		// Remove the slash from the end of the path if it is present
		if (Utils.lastChar(ret).equals(Global.slash))
		{
			ret = Utils.substr(ret, 0, -1);
		}

		// Return the path
		return ret;
	}

	// This method returns an array of default index file names
	public static String[] getIndexPage()
	{
		// Get the value for the index pages
		String ret = Global.config.get("index");

		// Get each index page individually
		String[] parts = ret.split(" ");

		// Return the ordered array of index pages
		return parts;
	}
}