<?php
define("R", 6371e3);
if (!isset($_GET["runid"])) {
    header("location: index.php");
} else {
    try {
        $db = new PDO('mysql:host=localhost;dbname=waspgps;charset=utf8', 'wasper', 'issou');
    } catch (Exception $e) {
        die($e->getMessage());
    }
    $check = $db->query("SELECT COUNT(*) FROM t_runstats WHERE idRun LIKE " . $_GET["runid"]);
    $ins = $db->prepare("INSERT INTO `t_runstats`(`distance`, `speed`, `maxSpeed`, `idRun`) VALUES (:dist, :speed, :maxsp, :idRun)");
    if ($check->fetchAll()[0][0] == 0) {
        $runp = $db->query("SELECT xcoord, ycoord FROM t_rundata WHERE idRun LIKE " . $_GET["runid"] . " ORDER BY `count`")->fetchAll();
        $runt = $db->query("SELECT Seconds FROM t_run WHERE idRun LIKE ". $_GET["runid"])->fetchAll();
        $dist = 0;
        $count = 0;
        $oldx = 0.0;
        $oldy = 0.0;
        foreach ($runp as $p) {
            if ($count > 0) {
                $phi1 = $p["xcoord"] * pi() / 180;
                $phi2 = $oldx * pi() / 180;
                $dphi = ($phi1 - $phi2);
                $dlam = ($p["ycoord"] - $oldy) * pi() / 180;
                $a = sin($dphi / 2) * sin($dphi / 2) + cos($phi1) * cos($phi2) * sin($dlam / 2) * sin($dlam / 2);
                $c = 2 * atan2(sqrt($a), sqrt(1 - $a));
                $dist += R * $c;
            }
            $oldx = $p["xcoord"];
            $oldy = $p["ycoord"];
            $count++;
        }
        
        $speed = $dist / $runt[0][0];
        $maxsp = 0;
        $ins->execute(array(
            "dist" => $dist,
            "speed" => $speed,
            "maxsp" => $maxsp,
            "idRun" => $_GET["runid"]
        ));
    }
}
header('location: index.php');      
?>

