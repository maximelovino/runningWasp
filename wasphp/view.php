<?php
if (!isset($_GET["runid"])) {
    header("location: index.php");
} else {
    try {
        $db = new PDO('mysql:host=localhost;dbname=waspgps;charset=utf8', 'wasper', 'issou');
    } catch (Exception $e) {
        die($e->getMessage());
    }
    $runp = $db->query("SELECT xcoord, ycoord FROM t_rundata WHERE idRun LIKE " . $_GET["runid"] . " ORDER BY `count`")->fetchAll();
    
}
?>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Wasp Runner Viewer</title>
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
                echo 'pathArray.push(new google.maps.LatLng('.$p["xcoord"].','.$p["ycoord"].'));';
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
        <div id="googleMap" style="width:1024px;height:768px;"></div>
    </body>
</html>
