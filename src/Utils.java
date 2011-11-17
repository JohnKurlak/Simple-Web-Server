/******************************************************************************\
	Author:		John Kurlak
	Filename:	Utils.java
	Purpose:	This class contains many "utility" methods that are used
				throughout the web server's code.
\******************************************************************************/

// Import the network, input/output, util, text, and zip packages
import java.net.*;
import java.io.*;
import java.util.*;
import java.util.zip.*;
import java.text.*;

class Utils
{
	// This method looks for a smaller string within a larger string and returns a boolean value based on whether it found it
	public static boolean contains(String haystack, String needle)
	{
		// Check for the needle in the haystack
		if (haystack.indexOf(needle) != -1)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// This method prints text followed by a new line to the screen (and stores it in the log)
	public static void displn(String text)
	{
		System.out.println(text + "\n");
		fileAppend(Global.config.get("path") + Global.slash + ".LOG", text + "\r\n\r\n");
	}

	// This method prints two blank lines to the screen (and to the log)
	public static void displn()
	{
		System.out.println("\n");
		fileAppend(Global.config.get("path") + Global.slash + ".LOG", "\r\n\r\n");
	}

	// This method prints text to the screen (and stores it in the log)
	public static void disp(String text)
	{
		System.out.println(text);
		fileAppend(Global.config.get("path") + Global.slash + ".LOG", text + "\r\n");
	}

	// This method prints a blank line to the screen (and to the log)
	public static void disp()
	{
		System.out.println();
		fileAppend(Global.config.get("path") + Global.slash + ".LOG", "\r\n");
	}

	// This method adds text to the end of a file
	public static void fileAppend(String path, String data)
	{
		try
		{
			// Declare variables
			FileWriter fw = new FileWriter(path, true);
			BufferedWriter out = new BufferedWriter(fw);

			// Write the data to the file, and close the file
			out.write(data);
			out.close();
		}
		catch (Exception e) {}
	}

	// This method generates an "associative array" based on given information that is separated by a delimeter
	public static Map<String, String> associativeList(String input, String delimeter, String regex, boolean lowercase)
	{
		// Declare variables
		String[] lines = input.split("[\n]");
		Map<String, String> out = new HashMap<String, String>();

		// Loop through each line
		for (int i = 0; i < lines.length; i++)
		{
			// Check the current line to make sure that it has the delimeter
			if (Utils.contains(lines[i], delimeter))
			{
				// Split the line at the delimeter
				String[] parts = lines[i].split(regex);

				if (lowercase)
				{
					// Assign the current key (converted to lowercase) to the current value
					out.put(parts[0].toLowerCase(), parts[1]);
				}
				else
				{
					// Assign the current key to the current value
					out.put(parts[0], parts[1]);
				}
			}
		}

		return out;
	}

	// This method overloads the associativeList method, assuming that the array keys can be converted to lowercase values
	public static Map<String, String> associativeList(String input, String delimeter, String regex)
	{
		return associativeList(input, delimeter, regex, true);
	}

	// This method overloads the associativeList method, assuming that the delimeter is a colon (:)
	public static Map<String, String> associativeList(String input)
	{
		return associativeList(input, ": ", "[:][ ]", true);
	}

	// This methods returns the contents of a file as a string
	public static String fileContents(String path)
	{
		// Declare a variable
		String out = "";

		try
		{
			// Declare a variable
			BufferedReader input = new BufferedReader(new FileReader(path));

			try
			{
				// Declare a variable
				String line;

				// Read each line of the file
				while ((line = input.readLine()) != null)
				{
					// Append the line to the end of the output variable
					out += line + "\n";
				}

				// Remove the last newline from the output variable
				out = Utils.substr(out, 0, -1);
			}
			finally
			{
				// Close the input stream
				input.close();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		// Return the file contents
		return out;
	}

	// This method returns the contents of a file as a byte array
	public static byte[] fileContents(String path, boolean returnByteArray)
	{
		// Make sure the user didn't mess up the arguments
		if (returnByteArray)
		{
			try
			{
				// Declare variables
				File f = new File(path);
				InputStream is = new FileInputStream(f);
				long length = f.length();

				// Make sure the file isn't too large
				if (length > Integer.MAX_VALUE)
				{
					return "File is too large.".getBytes();
				}

				// Declare more variables
				byte[] bytes = new byte[(int) length];
				int offset = 0;
				int num = 0;

				// Read the file
				while (offset < bytes.length && (num = is.read(bytes, offset, bytes.length - offset)) >= 0)
				{
					offset += num;
				}

				// Make sure all of the file was read
				if (offset < bytes.length)
				{
					return "Error reading file.".getBytes();
				}

				// Close the stream
				is.close();

				// Return the bytes of the newly read file
				return bytes;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return "Error reading file.".getBytes();
			}
		}
		else
		{
			return "File contents argument error".getBytes();
		}
	}

	// This method returns a portion of a larger string based on a starting position and a length
	public static String substr(String haystack, int start, int length)
	{
		// Determine the appropriate starting position if a negative value is given
		if (start < 0)
		{
			start = haystack.length() - Math.abs(start);
		}

		// Determine the appropriate length if a negative value is given
		if (length < 0)
		{
			length = haystack.length() - start - Math.abs(length);
		}

		// Return the substring
		return haystack.substring(start, start + length);
	}

	// This method returns the portion of a string that comes after the defined starting position
	public static String substr(String haystack, int start)
	{
		// Determine the appropriate starting position if a negative value is given
		if (start < 0)
		{
			start = haystack.length() - Math.abs(start);
		}

		// Return the substring
		return haystack.substring(start);
	}

	// This method returns the last character of a string
	public static String lastChar(String input)
	{
		return substr(input, -1);
	}

	// This method decodes a string
	public static String urlDecode(String input)
	{
		try
		{
			// If the string can be decoded, decode it
			return URLDecoder.decode(input, "UTF-8");
		}
		catch (UnsupportedEncodingException e)
		{
			// Otherwise, return the original string
			return input;
		}
	}

	// This method checks to see if the given file exists
	public static boolean fileExists(String path)
	{
		// Declare a variable
		File f = new File(path);

		// Check to see if the path points to a file
		if (f.isFile())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// This method checks to see if the given directory exists
	public static boolean directoryExists(String path)
	{
		// Declare a variable
		File f = new File(path);

		// Check to see if the path points to a directory
		if (f.isDirectory())
		{
			return true;
		}
		else
		{
			return false;
		}
	}

	// This method returns the file size of the given file
	public static long fileSize(String path)
	{
		// Declare a variable
		File f = new File(path);

		// Return the length of the file
		return f.length();
	}

	// This method replaces all instances of a search string with a new string
	public static String replace(String haystack, String needle, String replace)
	{
		// Make sure the user is looking for something to replace
		if (haystack != null)
		{
			// Declare variables
			int length = needle.length();
			String replaced = "";
			int found = -1;
			int start = 0;

			// Continue until all occurances are gone
			while((found = haystack.indexOf(needle, start)) != -1)
			{
				// Replace the current occurance with the replacement text
				replaced += haystack.substring(start, found) + replace;
				start = found + length;
			}

			// Store the new string in a variable
			replaced += haystack.substring(start);

			// Return the new string
			return replaced;
		}
		else
		{
			return "";
		}
	}

	// This method concatenates two byte arrays into one giant byte array
	public static byte[] concatByteArray(byte[] arr1, byte[] arr2)
	{
		// Declare a variable
		byte[] newArray = new byte[(arr1.length + arr2.length)];

		// Copy each array to the new array
		System.arraycopy((Object) arr1, 0, (Object) newArray, 0, arr1.length);
		System.arraycopy((Object) arr2, 0, (Object) newArray, arr1.length, arr2.length);

		// Return the result
 		return newArray;
	}

	// This method runs an external program and returns the standard output
	public static String executeCommand(String command) throws IOException
	{
		// Declare a variable
		String ret = "";

		// Run the external program and get its output
		Process process = Runtime.getRuntime().exec(command);
		ret = Utils.readInputStream(process.getInputStream());

		// Return the result
		return ret;
	}

	// This method returns the date and time
	public static String getDate()
	{
		// Get the date in the given format
		SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z");
		String d = sdf.format(new Date(System.currentTimeMillis())).toString();

		// Return the date
		return d;
	}

	// This method reads a closed input stream and returns the results as a string
	public static String readInputStream(InputStream is)
	{
		// Declare a variable
		String out = "";

		try
		{
			// Declare a variable
			BufferedReader input = new BufferedReader(new InputStreamReader(is));

			try
			{
				// Declare a variable
				String line;

				// Read each line of the stream
				while ((line = input.readLine()) != null)
				{
					// Append the current line to the output variable
					out += line + "\n";
				}

				// Remove the last newline from the output variable
				out = Utils.substr(out, 0, -1);
			}
			finally
			{
				// Close the stream
				input.close();
			}
		}
		catch (IOException ex)
		{
			ex.printStackTrace();
		}

		// Return the stream contents
		return out;
	}

	// This method GZip compresses a byte array
	public static byte[] GZipCompress(byte[] in)
	{
		// Declare variables
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		GZIPOutputStream zos;

		try
		{
			// Prepare a new stream to encrypt the bytes
			zos = new GZIPOutputStream(baos);

			// Add each byte to the GZip stream
			for(int i = 0; i < in.length; i++)
			{
				zos.write(in[i]);
			}

			// Close the GZip stream and the byte steam
			zos.close();
			baos.close();
		}
		catch (IOException e)
		{
			System.out.println(e.getMessage());
		}

		// Return the bytes of the GZip stream
		return baos.toByteArray();
	}

	// This method parses the headers portion of a response string
	public static Map<String, String> getHeaders(String source)
	{
		// Declare variables
		String[] parts = source.split("[\r|\n]{2}");
		String headers = parts[0];

		// Return the headers as an associative array
		return Utils.associativeList(headers);
	}

	// This method retrieves the text that lies within two boundaries of a larger portion of text
	public static String getBetween(String haystack, String needle1, String needle2)
	{
		// Check to see whether the given needles occur in the text
		if ((Utils.contains(haystack, needle1) && !needle1.equals("")) && (Utils.contains(haystack, needle2) && !needle2.equals("")))
		{
			// Both needles occur; return the text between them
			return haystack.substring(haystack.indexOf(needle1) + needle1.length(), haystack.substring(haystack.indexOf(needle1) + needle1.length()).indexOf(needle2) + haystack.indexOf(needle1) + needle1.length());
		}
		else if (Utils.contains(haystack, needle1) && !needle1.equals(""))
		{
			// Only the first needle was found; return all text after it
			return haystack.substring(haystack.indexOf(needle1) + needle1.length());
		}
		else if (Utils.contains(haystack, needle2) && !needle2.equals(""))
		{
			// Only the second needle was found; return all text before it
			return haystack.substring(0, haystack.indexOf(needle2));
		}
		else
		{
			// No needles were found; return the text as it was given
			return haystack;
		}
	}

	// This method Base64 encodes the given text
	public static String base64Encode(String text)
	{
		return Base64.encode(text).replaceAll("[\r\n]", "");
	}

	// This method Base64 decodes the given text
	public static String base64Decode(String text)
	{
		return Base64.decode(text.replaceAll("[\r\n]", ""));
	}
}