<?php
error_reporting(E_ALL);
$ch = curl_init();
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
curl_setopt($ch, CURLOPT_URL, 'http://localhost/run.php?uid=1&start'
);
$content = curl_exec($ch);
echo $content;
for ($i = 0; $i < 100; $i++) {
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_URL, 'http://localhost/run.php?uid=1&x='. (3 + rand(-10, 10)/20) .'&y=' . ($i / 100 + rand(-10, 10)/20) . '&time=' . ($i + 1)
    );
    $content = curl_exec($ch);
    echo $content."<br/>";
}
curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);

curl_setopt($ch, CURLOPT_URL, 'http://localhost/run.php?uid=1&time=314159&end'
);
$content = curl_exec($ch);
curl_close($ch);
?>

