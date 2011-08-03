<?php
function the_stardate( ) { 
	$yn = strtotime(get_the_time('c'));
        $year = date("Y", $yn);
        $y0 = gmmktime(0, 0, 0, 1, 1, $year);
        $y1 = gmmktime(0, 0, 0, 1, 1, $year + 1);
        echo $year + ($yn - $y0) / ($y1 - $y0);
	echo strftime(" (%Y-%m-%d %H:%M:%S UTC)",$yn);
	return '';
}
?>
