<?php
//     ini_set('display_errors', 'On');
//     error_reporting(E_ALL | E_STRICT);
    $projectId = $_POST["stuff"];
//     $gameId = "jgj";

    include('config.php');
    include('function.php');
    dbConnect();

    mysql_query("SET NAMES utf8");

    $result = mysql_query("SELECT num, title, title_short, description, url
    FROM projects
    WHERE title_short = '".$projectId."'
    ORDER BY num DESC");

    $to_encode = array();
    while($row = mysql_fetch_assoc($result)) {
      $to_encode[] = $row;
    }
    echo json_encode($to_encode);
// echo $gameId;
?>
