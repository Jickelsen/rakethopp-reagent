<?php
// error_reporting(E_ALL);
//  ini_set('display_errors', 1);
/**
 * Connect to mysql server
 * @param bool
 * @use true to connect false to close
 */
function dbConnect($close=true){

	if (!$close) {
		mysql_close($link);
		return true;
	}

// TODO:  $db = new PDO('mysql:host=localhost;dbname=testdb;charset=utf8', 'username', 'password');
	$link = mysql_connect(DB_HOST, DB_USER, DB_PASS) or die('Could not connect to MySQL DB ') . mysql_error();
	if (!mysql_select_db(DB_NAME, $link))
		return false;
}

?>
