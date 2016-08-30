<?php
try {
    $db = new PDO('mysql:host=localhost;dbname=waspgps;charset=utf8', 'wasper', 'issou');
} catch (Exception $e) {
    die($e->getMessage());
}
//get all run done be the current user
$runs = $db->query("SELECT * FROM t_run WHERE idUser LIKE 1 ORDER BY Date")->fetchAll();
?>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Wasp Runner</title>
        <meta name="viewport" content="width=device-width, initial-scale=1, user-scalable=no" />
        <link rel="stylesheet" href="assets/css/main.css" />
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

        <section id="one">
            <div class="inner">
                <header>
                    <h2>Courses enregistrées</h2>
                </header>
                <table>
                    <?php
                    //display all runs as a dropdown menu
                    foreach ($runs as $run) {
                        echo '<tr><td><a href="view.php?runid=' . $run["idRun"] . '">' . $run["Date"] . '</a></td></tr>';
                    }
                    ?>
                </table>
            </div>
        </section>

        <section id="footer">
            <div class="inner">
                <div class="copyright">
                    &copy; Untitled Design: <a href="https://templated.co/">TEMPLATED</a>. Images <a href="https://unsplash.com/">Unsplash</a>
                </div>
            </div>
        </section>
    </body>
</html>
