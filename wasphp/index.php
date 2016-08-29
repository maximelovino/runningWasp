<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <script src="http://maps.googleapis.com/maps/api/js"></script>
        <script>
            var x = new google.maps.LatLng(52.495715, 4.888916);
            var stavanger = new google.maps.LatLng(58.983991, 5.734863);
            var amsterdam = new google.maps.LatLng(52.395715, 4.888916);
            var london = new google.maps.LatLng(51.508742, -0.120850);

            function initialize()
            {
                var mapProp = {
                    center: x,
                    zoom: 8,
                    mapTypeId: google.maps.MapTypeId.ROADMAP
                };

                var map = new google.maps.Map(document.getElementById("googleMap"), mapProp);

                var myTrip = [x, stavanger, amsterdam, london];
                var flightPath = new google.maps.Polyline({
                    path: myTrip,
                    strokeColor: "#FF0000",
                    strokeOpacity: 0.8,
                    strokeWeight: 2
                });

                flightPath.setMap(map);
            }

            google.maps.event.addDomListener(window, 'load', initialize);
        </script>

        <title>Wasp Runner</title>
    </head>
    <body>
    <center>
        <div id="googleMap" style="width:1024px;height:768px;"></div>
    </center>
</body>
</html>
