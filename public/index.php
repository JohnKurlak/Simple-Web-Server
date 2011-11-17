<?php include 'header.php'; ?>
				<h2>Test Page</h2>
				<img src="simple-web-server.png" alt="Simple Web Server" />
				<p>
					This page is a demonstration page for the Simple Web Server software.
					It, along with the Simple Web Server software, was developed by John
					Kurlak.  The server has many features, such as:
				</p>
				<ul>
					<li>PHP Support</li>
					<li>Multi-threading</li>
					<li>Logging</li>
					<li>GZip Compression</li>
					<li>HTTP Authentication</li>
					<li>Custom Error Pages</li>
					<li>Operating System Independence</li>
					<li>And More!</li>
				</ul>
				<br />
				<h1>Examples</h1>
				<h2>&nbsp;</h2>
				<p>
					With Simple Web Server, you can host large files for downloading.
				</p>
				<p>
					You can also make websites in PHP and Simple Web Server will run them.  An example PHP page can be seen <a href="php-ex.php">here</a>!
				</p>
				<p>
					Also, Simple Web Server supports forms:
				</p>
				<form action="post.php" method="post">
					<label for="name">What's your name?</label>
					<input type="text" name="user" />
					<input type="submit" value="Submit" />
				</form>
<?php include 'footer.php'; ?>