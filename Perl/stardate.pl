#!/usr/bin/perl


if ($#ARGV >= 0) {
	$now = `date -d '$ARGV[0]'`;
} else {
	$now = `date --utc`;
}
chomp($now);
$yp = 1 + ($y = 0 + `date +"%Y" -d '$now'`);

$y0 = `date -d '$y-01-01 00:00:00 UTC' +'%s'`;
$y1 = `date -d '$yp-01-01 00:00:00 UTC' +'%s'`;

$s=`date -d '$now' +'%s'`;
print $y+($s-$y0)/($y1-$y0)."\n";


