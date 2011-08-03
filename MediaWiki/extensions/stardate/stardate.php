<?php
 
 
$wgHooks['ParserFirstCallInit'][] = 'StarDateParserInit';
 
function StarDateParserInit( &$parser ) {
	$parser->setHook( 'StarDate', 'StarDateRender' );
	return true;
}
 
function StarDateRender( $input, $args, $parser ) {
	$y0 = intval($input);
	$y1 = $y0 + 1;
	$x0 = gmmktime(0, 0, 0, 1, 1, $y0);
	$x1 = gmmktime(0, 0, 0, 1, 1, $y1);
	$x = $x0 + ($input - $y0) * ($x1 - $x0);
	return htmlspecialchars("StarDate: " .
		$input . gmstrftime(" (%Y-%m-%d %H:%M:%S UTC)" ,$x));
}