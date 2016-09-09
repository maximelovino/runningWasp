<?php
define("R", 6371e3);

/**
 * Calculate the statistical data from any run
 * @param integer $id the id of the run to analyse
 */
function computeStats($id) {
    try {
        $db = new PDO('mysql:host=localhost;dbname=waspgps;charset=utf8', 'wasper', 'issou');
    } catch (Exception $e) {
        die($e->getMessage());
    }
    $check = $db->query("SELECT COUNT(*) FROM t_runstats WHERE idRun LIKE " . $id);
    $ins = $db->prepare("INSERT INTO `t_runstats`(`distance`, `speed`, `maxSpeed`, `idRun`) VALUES (:dist, :speed, :maxsp, :idRun)");
    if ($check->fetchAll()[0][0] == 0) {
        $runp = $db->query("SELECT xcoord, ycoord, time FROM t_rundata WHERE idRun LIKE " . $id . " ORDER BY `count`")->fetchAll();
        $runt = $db->query("SELECT Seconds FROM t_run WHERE idRun LIKE ". $id)->fetchAll();
        $dist = 0;
        $count = 0;
        $mdist = 0;
        $ltime = -1;
        $maxsp = 0;
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
                $mdist = R * $c;
                $dist += $mdist;
                $time = $p["time"]-$ltime;
                if($mdist / $time > $maxsp) {
                    $maxsp = $mdist / $time;
                }
            }
            
            $oldx = $p["xcoord"];
            $oldy = $p["ycoord"];
            $ltime = $p["time"];
            $count++;
        }
        
        $speed = $dist / $runt[0][0];
        $maxsp = $mdist / 5;
        $ins->execute(array(
            "dist" => $dist,
            "speed" => $speed,
            "maxsp" => $maxsp,
            "idRun" => $id
        ));
    }
}
?>

