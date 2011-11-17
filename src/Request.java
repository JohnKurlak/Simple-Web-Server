/******************************************************************************\
	Author:		John Kurlak
	Filename:	Request.java
	Purpose:	This class parses all needed information from a HTTP request.
\******************************************************************************/

// Import the util and input/ouput packages
import java.util.*;
import java.io.*;

class Request
{
	// Declare all public variables
	String rawHeaders = "";
	Map<String, String> headers;
	String type = "";
	String path = "";
	InputStream stream;
	String fileLocation = "";
	String queryString = "";
	String uri = "";
	String postString = "";
	boolean malformed = false;
	String cookieString = "";
	String authString = "";

	// This method reads the request, checks for its validity, and stores parses all information from the request
	public Request(InputStream is)
	{
		// Read the stream and check to see if it is malformed
		this.stream = is;
		this.rawHeaders = this.parseOpenIS();
		this.malformed = this.isMalformed();

		// If the stream isn't malformed, parse it
		if (!this.malformed)
		{
			this.headers = this.parseHeaders();
			this.type = this.parseRequestType();
			this.path = this.parseRequestPath();
			this.fileLocation = this.getFileLocation();
			this.queryString = this.parseQueryString();
			this.uri = getURI();
			this.postString = this.getPostString();
			this.cookieString = this.getCookieString();
			this.authString = this.getAuthString();
		}
	}

	// This method checks to see if the request is valid
	public boolean isMalformed()
	{
		// Make sure that the first line of the request is valid
		if (this.rawHeaders.length() > 4 && this.rawHeaders.contains(" ") && this.rawHeaders.toLowerCase().contains("http/"))
		{
			// Make sure that the request is either a GET or POST request
			if (this.parseRequestType().toLowerCase().equals("get") || this.parseRequestType().toLowerCase().equals("post"))
			{
				return false;
			}
			else
			{
				return true;
			}
		}
		else
		{
			return true;
		}
	}

	// This method returns an associative array of all the headers that the user's browser sent
	public Map<String, String> parseHeaders()
	{
		return Utils.associativeList(this.rawHeaders);
	}

	// This method reads the GET or POST request's input stream
	public String parseOpenIS()
	{
		// Declare variables
		byte[] buffer = new byte[32768];
		int len = 0;
		String ret = "";

		// Try to read the stream
		try
		{
			len = this.stream.read(buffer);
		}
		catch (Exception e) {}

		// Add the newly read data into the output variable
		ret += new String(buffer, 0, len);

		// Try to read more from the stream
		try
		{
			// Only read more if the request is a POST request
			if (Utils.substr(ret, 0, 4).toLowerCase().equals("post"))
			{
				/*// Try to read more data into the buffer
				try
				{
					try
					{
						len = this.stream.read(buffer);
					}
					catch (Exception e) {}

					// Add the newly read data into the output variable
					ret += new String(buffer, 0, len);
				}
				catch (Exception e) {}*/
			}
		}
		catch (Exception e) {}

		// Return the data of the stream as a string
		return ret;
	}

	// This method determines whether the request is a GET request or a POST request
	public String parseRequestType()
	{
		// Make sure that there is a space in the request
		if (Utils.contains(this.rawHeaders, " "))
		{
			// Return the first portion of the request
			return this.rawHeaders.split("[ ]")[0].toUpperCase();
		}
		else
		{
			// Return nothing is the request is malformed
			return "";
		}
	}

	// This method returns the path defined in the request
	public String parseRequestPath()
	{
		// Check to see if the request has the end tag
		if (Utils.contains(this.rawHeaders.toLowerCase(), " http"))
		{
			// Parse the file path from the request
			return this.rawHeaders.split("[ ]")[1].split(" [H|h][T|t][T|t][P|p]")[0];
		}
		else
		{
			// Return nothing is the request is malformed
			return "";
		}
	}

	// This method returns the query string, if any, of the defined path
	public String parseQueryString()
	{
		return Utils.getBetween(this.path, "?", "");
	}

	// This method returns the requested path with the query string included
	public String getURI()
	{
		return this.fileLocation + "?" + this.queryString;
	}

	// This method returns the local file location of the requested page
	public String getFileLocation()
	{
		// Get the path without the query string
		String uri = Utils.getBetween(this.path, "", "?");

		// Add a slash to the beginning of the path if there isn't already one there
		if (!Utils.substr(uri, 0, 1).equals(Global.slash))
		{
			uri = Global.slash + uri;
		}

		// Prevent clients from accessing files that they do not have permission to access
		uri = uri.replace("../", "/").replace("/", Global.slash);

		// Check to see if the user requested to view a directory
		if (Utils.directoryExists(Config.getPath() + uri + Global.slash))
		{
			// Get all of the default index pages
			String[] indexes = Config.getIndexPage();

			// Check to see if any of the default index pages exist
			for (String page : indexes)
			{
				// If an index page has been found, use it as the filename
				if (Utils.fileExists(Config.getPath() + uri + Global.slash + page))
				{
					uri = Global.slash + page;
					break;
				}
			}
		}

		// Return the file path
		return Config.getPath() + uri;
	}

	// This method returns the POST variables as a string
	public String getPostString()
	{
		// Declare a variable
		String ret = "";

		// Make sure POST variables have been set
		if (Utils.contains(this.rawHeaders, "\n\n") || Utils.contains(this.rawHeaders, "\r\r") || Utils.contains(this.rawHeaders, "\r\n\r\n"))
		{
			// Make sure the request is a POST request
			if (this.type.equals("POST"))
			{
				// Get the post variables
				String[] parts = this.rawHeaders.split("[\r\n]{4}");

				ret = parts[1];
			}
		}

		// Return the post variables
		return ret;
	}

	// This method returns the cookies as a string
	public String getCookieString()
	{
		// Declare variables
		String ret = "";
		String headers = this.rawHeaders;

		// Check to see if a cookie was set
		while (Utils.contains(headers.toLowerCase(), "cookie:"))
		{
			// Parse all of the cookie information from the headers
			ret += Utils.getBetween(headers, "Cookie: ", "\r\n") + "; ";
			headers = headers.substring(headers.toLowerCase().indexOf("cookie: ") + 8);
		}

		// Combine all of the cookie information into one string
		try
		{
			ret = Utils.substr(ret, 0, -1).replaceAll(";[ ]*", ";");

			// Make sure the last character is not a semicolon (;)
			while (Utils.lastChar(ret).equals(";") || Utils.lastChar(ret).equals(" "))
			{
				ret = Utils.substr(ret, 0, -1);
			}
		}
		catch (Exception e)
		{
			ret = "";
		}

		// Return the cookies
		return ret;
	}

	// This method returns the user authentication information
	public String getAuthString()
	{
		// Check to see if the user is trying to access an authenticated page
		String ret = headers.get("authorization");

		// Try to parse the login credentials
		try
		{
			// Check to see that the client has followed the basic format
			if (Utils.contains(ret.toLowerCase(), "basic "))
			{
				// Get the login credentials and decode them
				String[] parts = ret.split("[Bb][Aa][Ss][Ii][Cc] ");
				ret = Utils.base64Decode(parts[1]);
			}
			else
			{
				ret = "";
			}
		}
		catch (Exception e)
		{
			ret = "";
		}

		// Return the login credentials
		return ret;
	}
}