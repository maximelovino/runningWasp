<?php
include_once 'stats.php';
try {
    $db = new PDO('mysql:host=localhost;dbname=waspgps;charset=utf8', 'wasper', 'issou');
} catch (Exception $e) {
    die($e->getMessage());
}
$running = $db->query("SELECT running FROM t_user WHERE idUser LIKE ".$_GET["uid"])->fetchAll()[0][0];
$setrunning = $db->prepare("UPDATE `t_user` SET `running`= :run WHERE `idUser` LIKE :uid");
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
    if ($start && !$end && $running == 0) {
        $newRun->execute(array(
            "uid" => $uid,
            "date" => date('Y-m-d H:i:s')
        ));
        $es = $db->lastInsertId();
        $setrunning->execute(array(
            "run" => $es,
            "uid" => $uid
        ));
        echo $es;
    } else if ($end && !$start && $running != 0) {
        $updateTime->execute(array(
            "sec" => $ts,
            "idr" => $running
        ));
        computeStats($running);
        $setrunning->execute(array(
            "run" => 0,
            "uid" => $uid
        ));
    }
    if ($x != null && $y != null && $ts != null) {
        if ($running != 0) {
            $newPoint->execute(array(
                "idRun" => $running,
                "xcoord" => $x,
                "ycoord" => $y,
                "count" => $ts
            ));
        }
    }
}
?>

