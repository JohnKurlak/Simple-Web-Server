/******************************************************************************\
	Author:		John Kurlak
	Filename:	Global.java
	Purpose:	This class stores the configuration information so that it can
				be accessed globally.
\******************************************************************************/

// Import the Map package
import java.util.Map;

class Global
{
	// Declare variables
	static Map<String, String> config;
	static Map<String, String> mimeTypes;
	static long timeStart;
	static String slash = Utils.contains(System.getProperty("os.name"), "Windows") ? "\\" : "/";
}