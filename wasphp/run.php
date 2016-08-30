<?php
session_start();
include_once 'stats.php';
try {
    $db = new PDO('mysql:host=localhost;dbname=waspgps;charset=utf8', 'wasper', 'issou');
} catch (Exception $e) {
    die($e->getMessage());
}
$newRun = $db->prepare("INSERT INTO `t_run`(`idUser`, `Date`) VALUES (:uid, :date)");
$newPoint = $db->prepare("INSERT INTO `t_rundata`(`idRun`, `xcoord`, `ycoord`, `count`) VALUES (:idRun,:xcoord,:ycoord,:count)");
$updateTime = $db->prepare("UPDATE `t_run` SET `Seconds` = :sec WHERE `idRun` = :idr");
$x = isset($_GET["x"]) ? $_GET["x"] : null;
$y = isset($_GET["y"]) ? $_GET["y"] : null;
$ts = isset($_GET["time"]) ? $_GET["time"] : null;

$start = isset($_GET["start"]) ? true : false;
$end = isset($_GET["end"]) ? true : false;

$uid = isset($_GET["uid"]) ? $_GET["uid"] : null;

if ($uid != null) {
    if ($start && !$end && !isset($_SESSION["a".$uid])) {
        $newRun->execute(array(
            "uid" => $uid,
            "date" => date('Y-m-d H:i:s')
        ));
        $es = $db->lastInsertId();
        $_SESSION["a".$uid] = $es;
        echo $es;
    } else if ($end && !$start && isset($_SESSION["a".$uid])) {
        $updateTime->execute(array(
            "sec" => $ts,
            "idr" => $_SESSION["a".$uid]
        ));
        computeStats($_SESSION["a".$uid]);
        unset($_SESSION["a".$uid]);
        
    }
    if ($x != null && $y != null && $ts != null) {
        if (isset($_SESSION["a".$uid])) {
            $newPoint->execute(array(
                "idRun" => $_SESSION["a".$uid],
                "xcoord" => $x,
                "ycoord" => $y,
                "count" => $ts
            ));
        }
    }
}
?>

