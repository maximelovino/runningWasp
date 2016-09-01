<?php
try {
    $db = new PDO('mysql:host=localhost;dbname=waspgps;charset=utf8', 'wasper', 'issou');
} catch (Exception $e) {
    die($e->getMessage());
}
$listrun = $db->prepare("SELECT idRun, Date FROM t_run WHERE idUser LIKE :uid");
$rundata = $db->prepare("SELECT `xcoord`, `ycoord`, `count` FROM `t_rundata` WHERE `idRun` LIKE :idr ORDER BY `count`");

if (!isset($_GET["uid"])) {
    header("location: index.php");
} else {
    if (isset($_GET["listrun"])) {
        $listrun->bindParam(':uid', $_GET["uid"]);
        $r = $listrun->execute();
        $a = $listrun->fetchAll();
        foreach ($a as $line) {
            //ID;Date
            echo $line["idRun"].";".$line["Date"]."\n";
        }
    } else if(isset($_GET["rundata"]) && isset($_GET["idRun"])) {
        $rundata->bindParam(":idr", $_GET["idRun"]);
        $rundata->execute();
        $a = $rundata->fetchAll();
        foreach ($a as $line) {
            //X;Y;C
            echo $line["xcoord"].";".$line["ycoord"].";".$line["count"]."\n";
        }
    }
}
?>

