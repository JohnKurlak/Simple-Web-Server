/******************************************************************************\
	Base 64 Module from:
	http://www.koders.com/java/fid2B76F86B8F8FF984EFE3C04AC6DE198E40D8EF41.aspx

		Permission is hereby granted, free of charge, to any person obtaining
		a copy of this software and associated documentation files (the
		"Software"), to deal in the Software without restriction, including
		without limitation the rights to use, copy, modify, merge, publish,
		distribute, sublicense, and/or sell copies of the Software, and to
		permit persons to whom the Software is furnished to do so, subject to
		the following conditions:

		The above copyright notice and this permission notice shall be included
		in all copies or substantial portions of the Software.

		THE SOFTWARE IS PROVIDED "AS IS," WITHOUT WARRANTY OF ANY KIND, EXPRESS
		OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
		MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
		IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
		CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
		TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
		SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

	Author:		Russell Gold
	Filename:	Base64.java
	Purpose:	This class encodes and decodes text using the Base64 algorithm.
\******************************************************************************/

public class Base64
{
	// The alphabet to use for encoding
	final static String encodingChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/";

	// This method Base64 encodes a string
	public static String encode(String source)
	{
		// Declare variables
		char[] sourceBytes = getPaddedBytes(source);
		int numGroups = (sourceBytes.length + 2) / 3;
		char[] targetBytes = new char[4];
		char[] target = new char[4 * numGroups];

		// Loop through each group
		for (int group = 0; group < numGroups; group++)
		{
			// Convert the group to four bytes instead of three
			convert3To4(sourceBytes, group * 3, targetBytes);

			// Append the new group to the output variable
			for (int i = 0; i < targetBytes.length; i++)
			{
				target[i + 4 * group] = encodingChar.charAt(targetBytes[i]);
			}
		}

		// Determine how many bytes must be appended
		int numPadBytes = sourceBytes.length - source.length();

		// Append the appropriate number of equals signs (=)
		for (int i = target.length - numPadBytes; i < target.length; i++)
		{
			target[i] = '=';
		}

		// Convert the character array to a string and return it
		return new String(target);
	}

	// This method adding "padding" to the bytes passed to it
	private static char[] getPaddedBytes(String source)
	{
		// Declare variables
		char[] converted = source.toCharArray();
		int requiredLength = 3 * ((converted.length + 2) /3);
		char[] result = new char[requiredLength];

		// Copy the new array into the outputted character array
		System.arraycopy(converted, 0, result, 0, converted.length);

		// Return the result
		return result;
	}


	// This method converts a set of three bytes into a set of four bytes
	private static void convert3To4(char[] source, int sourceIndex, char[] target)
	{
		// Generate the four bytes from the three bytes
		target[0] = (char)(source[sourceIndex] >>> 2);
		target[1] = (char)(((source[sourceIndex] & 0x03) << 4) | (source[sourceIndex + 1] >>> 4));
		target[2] = (char)(((source[sourceIndex + 1] & 0x0f) << 2) | (source[sourceIndex + 2] >>> 6));
		target[3] = (char)(source[sourceIndex + 2] & 0x3f);
	}

	// This method Base64 decodes a string
	public static String decode(String source)
	{
		// Make sure the string to decode is valid
		if (source.length() % 4 != 0)
		{
			throw new RuntimeException("Valid Base64 codes have a multiple of 4 characters");
		}

		// Declare variables
		int numGroups = source.length() / 4;
		int numExtraBytes = source.endsWith( "==" ) ? 2 : (source.endsWith( "=" ) ? 1 : 0);
		byte[] targetBytes = new byte[3 * numGroups];
		byte[] sourceBytes = new byte[4];

		// Loop through each byte group
		for (int group = 0; group < numGroups; group++)
		{
			// Get each equivalent byte value
			for (int i = 0; i < sourceBytes.length; i++)
			{
				sourceBytes[i] = (byte) Math.max(0, encodingChar.indexOf(source.charAt(4 * group + i)));
			}

			// Convert the group of four bytes into the original three bytes
			convert4To3(sourceBytes, targetBytes, group * 3);
		}

		// Return the decoded string
		return new String(targetBytes, 0, targetBytes.length - numExtraBytes);
	}

	// This method converts a set of four bytes into a set of three bytes
	private static void convert4To3(byte[] source, byte[] target, int targetIndex)
	{
		// Decode the four bytes back into the original three byte set
		target[targetIndex]  = (byte) ((source[0] << 2) | (source[1] >>> 4));
		target[targetIndex + 1] = (byte) (((source[1] & 0x0f) << 4) | (source[2] >>> 2));
		target[targetIndex + 2] = (byte) (((source[2] & 0x03) << 6) | (source[3]));
	}
}