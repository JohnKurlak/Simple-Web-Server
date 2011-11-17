/******************************************************************************\
	Author:		John Kurlak
	Filename:	Mime.java
	Purpose:	This class handles all file type/file extension processing.
\******************************************************************************/

// Import the util package
import java.util.*;

class Mime
{
	// This method returns the file type of the given file
	public static String getType(String fileName)
	{
		// Parse the file extension
		String extension = getExtension(fileName);

		// Make sure the file has an extension
		if (!extension.equals(""))
		{
			// Find the MIME type for the file extension and return it
			return getMime(Global.mimeTypes, extension);
		}

		// If there is no file extension, return no MIME type
		return "";
	}

	// This method returns the file extension of a given file
	public static String getExtension(String fileName)
	{
		// Make sure the file has a file extension
		if (fileName.lastIndexOf(".") == -1)
		{
			// If the file doesn't have an extension, return nothing
			return "";
		}
		else
		{
			// If the file has an extension, parse it and return it
			return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
		}
	}

	// This method returns the MIME type for the given file extension (if one is located in the passed associative array)
	public static String getMime(Map<String, String> associativeArray, String value)
	{
		// Declare variables
		String ret = "text/plain";
		Set vset = associativeArray.entrySet();
		Iterator values = vset.iterator();
		Set kset = associativeArray.keySet();
		Iterator keys = kset.iterator();

		// Loop through all of the MIME types available
		while(values.hasNext())
		{
			// Get the value and key of each MIME type
			String val = values.next().toString();
			String key = keys.next().toString();

			// Remove the equals sign (=) from the values
			val = val.substring(val.indexOf("=") + 1);

			// Check to see if more than one file extension is acceptable
			if (Utils.contains(val, " "))
			{
				// Get each file extension
				String[] parts = val.split(" ");

				// Loop through all of the file extensions
				for (int i = 0; i < parts.length; i++)
				{
					// Remove the period from the file extension if it is present
					if (Utils.substr(parts[i], 0, 1).equals("."))
					{
						parts[i] = Utils.substr(parts[i], 1);
					}

					// The file type was identified; set the MIME type
					if (parts[i].equals(value))
					{
						ret = key;
						break;
					}
				}
			}
			else
			{
				// Remove the period from the file extension if it is present
				if (Utils.substr(val, 0, 1).equals("."))
				{
					val = Utils.substr(val, 1);
				}

				// The file type was identified; set the MIME type
				if (val.equals(value))
				{
					ret = key;
					break;
				}
			}

		}

		// Return the MIME type
		return ret;
	}
}