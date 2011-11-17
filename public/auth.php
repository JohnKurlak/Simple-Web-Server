<?php
if (!isset($PHP_AUTH_USER))
{
	header("WWW-Authenticate: Basic realm=\"Server Login\"");
	header("HTTP/1.0 401 Unauthorized");

	include 'header.php';
	echo "<h2>Authorization</h2>";
	echo "<p>Access Denied.</p>";
	include 'footer.php';
}
else if($PHP_AUTH_USER == "root" && $PHP_AUTH_PW == "john")
{
	include 'header.php';
	echo "<h2>Authorization</h2>";
	echo "<p>Access Granted!</p>";
	include 'footer.php';
}
else
{
	include 'header.php';
	echo "<h2>Authorization</h2>";
	echo "<p>Access Denied!</p>";
	include 'footer.php';
}
?>