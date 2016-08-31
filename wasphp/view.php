<?php
if (!isset($_GET["runid"])) {
    header("location: index.php");
} else {
    try {
        $db = new PDO('mysql:host=localhost;dbname=waspgps;charset=utf8', 'wasper', 'issou');
    } catch (Exception $e) {
        die($e->getMessage());
    }
    //get all point from the requested runid
    $runp = $db->query("SELECT DISTINCT xcoord, ycoord FROM t_rundata WHERE idRun LIKE " . $_GET["runid"] . " ORDER BY `count`")->fetchAll();
    $runs = $db->query("SELECT * FROM t_runstats WHERE idRun LIKE " . $_GET["runid"])->fetchAll();
    $time = $runs[0]["distance"] / $runs[0]["speed"];
}
?>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Wasp Runner Viewer</title>
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
        <link rel="stylesheet" href="assets/css/main.css" />
        <script src="http://maps.googleapis.com/maps/api/js"></script>
        <script>
            var pathArray = [];
            <?php
            $xsum = 0.0;
            $ysum = 0.0;
            $count = 0;
            foreach ($runp as $p) {
                $xsum += $p["xcoord"];
                $ysum += $p["ycoord"];
                $count++;
                echo 'pathArray.push(new google.maps.LatLng(' . $p["xcoord"] . ',' . $p["ycoord"] . '));' . "\n";
            }
            $centerx = $xsum / $count;
            $centery = $ysum / $count;
            ?>
            var x = new google.maps.LatLng(<?php echo $centerx ?>, <?php echo $centery ?>);

            function initialize()
            {
                var mapProp = {
                    center: x,
                    zoom: 6,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };
                var map = new google.maps.Map(document.getElementById("googleMap"), mapProp);

                var flightPath = new google.maps.Polyline({
                    path: pathArray,
                    strokeColor: "#FF0000",
                    strokeOpacity: 0.8,
                    strokeWeight: 2
                });
                flightPath.setMap(map);
            }
            google.maps.event.addDomListener(window, 'load', initialize);
        </script>
    </head>
    <body>
        <header id="header">
            <div class="inner">
                <a href="index.php" class="logo">Wasp Runner</a>
                <nav id="nav">
                    <a href="index.php">Accueil</a>
                </nav>
            </div>
        </header>
        <a href="#menu" class="navPanelToggle"><span class="fa fa-bars"></span></a>
        <div id="googleMap" style="width:100%;height:600px;"></div><br/>
        <table>
            <tr><th colspan="2">Statistiques</th></tr>
            <tr><td>Distance</td><td><?php echo round($runs[0]["distance"] / 1000, 2) ?> Km</td></tr>
            <tr><td>Vitesse</td><td><?php echo round($runs[0]["speed"] * 3.6, 2) ?> Km/h</td></tr>
            <tr><td>Vitesse maximale</td><td><?php echo round($runs[0]["maxSpeed"] * 3.6, 2) ?> Km/h</td></tr>
            <tr><td>Temps</td><td><?php echo floor($time / 3600) != 0 ? floor($time / 3600) .' H' :""?>  <?php echo ($time / 60) % 60 ?> Min</td></tr>
        </table>
        <section id="footer">
            <div class="inner">
                <div class="copyright">
                    &copy; Untitled Design: <a href="https://templated.co/">TEMPLATED</a>. Images <a href="https://unsplash.com/">Unsplash</a>
                </div>
            </div>
        </section>
    </body>
</html>
