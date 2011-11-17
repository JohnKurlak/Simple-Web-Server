/******************************************************************************\
	Author:		John Kurlak
	Filename:	RequestHandler.java
	Purpose:	This class reads all requests, parses them, and returns the
				appropriate information back to the client.
\******************************************************************************/

// Import the network and input/output packages
import java.net.*;
import java.io.*;

class RequestHandler extends Thread
{
	// Declare public variables
	Socket socket = null;
	int port = 0;
	String serverName = "";
	String serverVersion = "";
	String serverIP = "";
	boolean gzip = true;

	// This default constructor accepts and stores all information necessary to process the request
	public RequestHandler(Socket s, int p, String sName, String sVersion, String sIP, boolean gz)
	{
		// Set variables
		socket = s;
		port = p;
		serverName = sName;
		serverVersion = sVersion;
		serverIP = sIP;
		gzip = gz;
	}

	// This method outputs a file to an opened stream as it reads it chunk by chunk
	public static void chunkFile(OutputStream out, String path)
	{
		try
		{
			// Declare variables
			File f = new File(path);
			InputStream is = new FileInputStream(f);
			long length = f.length();
			byte[] bytes = new byte[(int) length];
			int offset = 0;
			int num = 0;

			try
			{
				// Read the file and output it to the stream
				while (offset < bytes.length && (num = is.read(bytes, offset, bytes.length - offset)) >= 0)
				{
					offset += num;
					socketWrite(out, bytes);
					bytes = null;
				}
			}
			catch (Exception e) {}

			// Close the stream
			is.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	// This method writes a byte array to an opened stream
	public static void socketWrite(OutputStream output, byte[] data)
	{
		// Attempt to write the data to the stream
		try
		{
			output.write(data);
		}
		catch (Exception e) {}
	}

	// This method reads, parses, and proccesses an HTTP request
	public void run()
	{
		try
		{
			// Declare variables
			InputStream input = socket.getInputStream();
			OutputStream output = socket.getOutputStream();
			Request request = new Request(input);

			// Check to see if the request is malformed
			if (!request.malformed)
			{
				// The request is valid

				// Declare variables
				long contentLength = Utils.fileSize(request.fileLocation);
				boolean chunkedTransfer = false;

				// Determine whether GZip compression can be used in the response
				try
				{
					gzip = gzip && Utils.contains(request.headers.get("accept-encoding"), "gzip");
				}
				catch (NullPointerException e)
				{
					gzip = false;
				}

				// Display the date, time, and path of the request
				Utils.disp("> " + Utils.getDate() + " -> " + request.type + " " + request.path);

				// Determine whether the output needs to be chunked
				if (contentLength > 1000000)
				{
					chunkedTransfer = true;
				}

				// Check to see if the request page exists
				if (Utils.fileExists(request.fileLocation))
				{
					// The requested page exists

					// Generate the response headers
					String contentType = Mime.getType(request.fileLocation);
					String prepend = "HTTP/1.x 200 OK\r\n";
					prepend += "Date: " + Utils.getDate() + "\r\n";
					prepend += "Server: " + this.serverName + " " + this.serverVersion + "\r\n";

					// Check to see if the page needs PHP
					if (!contentType.equals("application/x-httpd-php"))
					{
						// The page doesn't need php

						// Send the content length of the page
						prepend += "Content-Length: " + contentLength + "\r\n";

						// Set the content type
						prepend += "Content-Type: " + contentType + "\r\n";

						// Check to see if GZIP compression can be used
						if (gzip && !chunkedTransfer)
						{
							// GZip compression is permissible

							// Compress the file
							byte[] b = Utils.GZipCompress(Utils.fileContents(request.fileLocation, true));
							prepend += "Content-Encoding: gzip\r\n";
							prepend += "\r\n";

							// Write the compressed file to the output stream
							socketWrite(output, Utils.concatByteArray(prepend.getBytes(), b));
						}
						else
						{
							// The transfer must be chunked

							// Set the content type
							prepend += "Transfer-Encoding: chunked\r\n";
							prepend += "\r\n";

							// Write the response headers and start sending chunks of the requested file
							socketWrite(output, prepend.getBytes());
							chunkFile(output, request.fileLocation);
						}
					}
					else
					{
						// Declare a variable
						boolean usePHP = Global.config.get("php").toLowerCase().trim().equals("enabled");

						if (usePHP)
						{
							// The page needs PHP

							// Use the PHP interpretter to get the response
							String php = Utils.executeCommand("php -c \"" + System.getProperty("user.dir") + Global.slash + ".." + Global.slash + "config" + Global.slash + "php.ini\" \"" + System.getProperty("user.dir") + Global.slash + ".." + Global.slash + "docs" + Global.slash + "wrapper.php\" page=" + Utils.base64Encode(Utils.urlDecode(request.fileLocation)) + " get=" + Utils.base64Encode(Utils.urlDecode(request.queryString)) + " post=" + Utils.base64Encode(Utils.urlDecode(request.postString)) + " cookie=" + Utils.base64Encode(Utils.urlDecode(request.cookieString)) + " auth=" + Utils.base64Encode(Utils.urlDecode(request.authString)));

							// Change the response code if authentication is required
							if (Utils.getHeaders(php).get("www-authenticate") != null)
							{
								prepend = prepend.replace("HTTP/1.x 200 OK", "HTTP/1.x 401 Unauthorized");
							}

							// Get the current response headers
							String[] parts = php.split("[\r\n]{4}");

							// Check to see if a URL redirect is necessary
							if (Utils.contains(parts[0], "Location: "))
							{
								// Change the headers so that a URL redirect will take place
								php = Utils.replace(php, "Content-type: " + Utils.getBetween(parts[0], "Content-type: ", "\n"), "");
								prepend = Utils.replace(prepend, "HTTP/1.x 200 OK", "HTTP/1.x 302 Found");
								php = Utils.replace(php, "Status: 302\n", "");
							}

							// Write the response to the output stream
							socketWrite(output, Utils.concatByteArray(prepend.getBytes(), php.getBytes()));
						}
						else
						{
							// The server is not using PHP

							// Get the file not found response page
							Utils.disp("> Not Implemented!");
							String notFound = "HTTP/1.x 501 Not Implemented\r\n";
							notFound += "Content-Type: text/html\r\n";
							notFound += "Date: " + Utils.getDate() + "\r\n";
							notFound += "Server: " + this.serverName + " " + this.serverVersion + "\r\n";
							notFound += "\r\n";
							notFound += Utils.fileContents("..\\docs\\501.html");

							// Add the server signature to the bottom of the page
							notFound = Utils.replace(notFound, "$_HOST", this.serverIP);
							notFound = Utils.replace(notFound, "$_PORT", this.port + "");
							notFound = Utils.replace(notFound, "$_SERVER", this.serverName);
							notFound = Utils.replace(notFound, "$_VERSION", this.serverVersion);

							// Write the response to the output stream
							socketWrite(output, notFound.getBytes());
						}
					}
				}
				else
				{
					// The requested page was not found

					// Get the file not found response page
					Utils.disp("> File Not Found!");
					String notFound = "HTTP/1.x 404 File Not Found\r\n";
					notFound += "Content-Type: text/html\r\n";
					notFound += "Date: " + Utils.getDate() + "\r\n";
					notFound += "Server: " + this.serverName + " " + this.serverVersion + "\r\n";
					notFound += "\r\n";
					notFound += Utils.fileContents("..\\docs\\404.html");

					// Add the server signature to the bottom of the page, and add the path of the requested file
					notFound = Utils.replace(notFound, "$_URL", request.path);
					notFound = Utils.replace(notFound, "$_HOST", this.serverIP);
					notFound = Utils.replace(notFound, "$_PORT", this.port + "");
					notFound = Utils.replace(notFound, "$_SERVER", this.serverName);
					notFound = Utils.replace(notFound, "$_VERSION", this.serverVersion);

					// Write the response to the output stream
					socketWrite(output, notFound.getBytes());
				}
			}
			else
			{
				// The client sent a bad request

				// Get the malformed request page
				Utils.disp("> Malformed Request!");
				String notFound = "HTTP/1.x 400 Bad Request\r\n";
				notFound += "Content-Type: text/html\r\n";
				notFound += "Date: " + Utils.getDate() + "\r\n";
				notFound += "Server: " + this.serverName + " " + this.serverVersion + "\r\n";
				notFound += "\r\n";
				notFound += Utils.fileContents("..\\docs\\400.html");

				// Add the server signature to the bottom of the page
				notFound = Utils.replace(notFound, "$_HOST", this.serverIP);
				notFound = Utils.replace(notFound, "$_PORT", this.port + "");
				notFound = Utils.replace(notFound, "$_SERVER", this.serverName);
				notFound = Utils.replace(notFound, "$_VERSION", this.serverVersion);

				// Write the response to the output stream
				socketWrite(output, notFound.getBytes());
			}

			// Close the socket
			socket.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}