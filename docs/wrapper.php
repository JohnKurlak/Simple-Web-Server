<?php
// Loop through all arguments
for ($i = 1; $i < count($argv); $i++)
{
	// Make sure the current argument has an equals sign
	if (strpos($argv[$i], '=') !== false)
	{
		// Parse the current argument
		$parts = explode('=', $argv[$i]);

		// Check to see what kind of information is being sent
		if ($parts[0] == 'page')
		{
			// The address of the page has been sent
			$pageSPECIAL_HIDE = base64_decode($parts[1]);
		}
		elseif ($parts[0] == 'get')
		{
			// The GET variables have been sent
			$get = base64_decode($parts[1]);
		}
		elseif ($parts[0] == 'post')
		{
			// The POST variables have been sent
			$post = base64_decode($parts[1]);
		}
		elseif ($parts[0] == 'cookie')
		{
			// The cookies have been sent
			$cookie = base64_decode($parts[1]);
		}
		elseif ($parts[0] == 'auth')
		{
			// The authorization credentials have been sent
			$auth = base64_decode($parts[1]);
		}
	}
}

$_GET = Array();
$_POST = Array();
$_COOKIE = Array();
$_SERVER = Array();

// Check to see if GET variables have been set
if (strpos($get, '=') !== false)
{
	// Get each GET variable
	$vars = explode('&', $get);

	// Loop through the GET variables
	foreach ($vars as $value)
	{
		// Make sure the GET variable has a value
		if (strpos($value, '=') !== false)
		{
			// Store the GET variable
			$parts = explode('=', $value);
			$_GET[$parts[0]] = $parts[1];
		}
	}
}

// Check to see if POST variables have been set
if (strpos($post, '=') !== false)
{
	// Get each POST variable
	$vars = explode('&', $post);

	// Loop through the POST variables
	foreach ($vars as $value)
	{
		// Make sure the POST variable has a value
		if (strpos($value, '=') !== false)
		{
			// Store the POST variable
			$parts = explode('=', $value);
			$_POST[$parts[0]] = $parts[1];
		}
	}
}

// Check to see if the cookies have been set
if (strpos($cookie, '=') !== false)
{
	// Get each cookie
	$vars = explode(';', $cookie);

	// Loop through the cookies
	foreach ($vars as $value)
	{
		// Make sure the cookie has a value
		if (strpos($value, '=') !== false)
		{
			// Store the cookie
			$parts = explode('=', $value);
			$_COOKIE[$parts[0]] = $parts[1];
		}
	}
}

// Check to see if the authorization credentials have been set
if (strpos($auth, ':') !== false)
{
	// Store the authorization credentials
	$parts = explode(':', $auth);
	$PHP_AUTH_USER = $parts[0];
	$PHP_AUTH_PW = $parts[1];
}

// Erase all variables that were used
unset($i);
unset($argc);
unset($argv);
unset($parts);
unset($get);
unset($post);
unset($cookie);
unset($auth);
unset($vars);
unset($value);

// Interpret the PHP of the requested page
require_once($pageSPECIAL_HIDE);

// Erase the last variable
unset($pageSPECIAL_HIDE);
?>