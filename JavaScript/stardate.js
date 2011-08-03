#!/usr/bin/rhino

function StarDate(d) {
	var y = d.getUTCFullYear();
	var t0 = Date.UTC(y,  0,1,0,0,0,0);
	var t1 = Date.UTC(y+1,0,1,0,0,0,0);
	var t = Date.UTC(y,d.getUTCMonth(),d.getUTCDate(),d.getUTCHours(),
			d.getUTCMinutes(),d.getUTCSeconds(),d.getUTCMilliseconds());
	return y + (t-t0)/(t1-t0);
}

print(StarDate(new Date()));