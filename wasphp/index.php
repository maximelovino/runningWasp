<?php
try {
    $db = new PDO('mysql:host=localhost;dbname=waspgps;charset=utf8', 'wasper', 'issou');
} catch (Exception $e) {
    die($e->getMessage());
}
$runs = $db->query("SELECT * FROM t_run WHERE idUser LIKE 1 ORDER BY Date")->fetchAll();
?>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Wasp Runner</title>
    </head>
    <body>
    <center>
        <form action="view.php" method="GET">
            <select name="runid">
                <?php 
                foreach ($runs as $run) {
                    echo '<option value="'.$run["idRun"].'">'.$run["Date"].'</option>';
                }
                ?>
            </select>
            <input type="submit" />
        </form>
    </center>
</body>
</html>
