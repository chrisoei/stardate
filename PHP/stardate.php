#!/usr/bin/php5

<?php
$yn = gmmktime();
$year = date("Y", $yn);
$y0 = gmmktime(0, 0, 0, 1, 1, $year);
$y1 = gmmktime(0, 0, 0, 1, 1, $year + 1);
$y = $year + ($yn - $y0) / ($y1 - $y0);
print "y = " + $y;
?>

