<?php include 'header.php'; ?>
	<h2>PHP Test</h2>
<?php
function AssignCookie($name, $value, $days)
{
	setcookie($name, $value, time() + ( 60 * 60 * 24 * $days), '/');
}

AssignCookie('count', intval($_COOKIE['count']) + 1, 7);

echo '<p>The counter is at ' . intval($_COOKIE['count']) . '.</p>';
echo '<p>The time is ' . date('g:i:s A', time()) . '.</p>';
?>
	<p>Try the <a href="auth.php">authorization page</a>!</p>
<?php include 'footer.php'; ?>