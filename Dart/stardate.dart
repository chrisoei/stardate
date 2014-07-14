#!/usr/bin/env dart

double StarDate(d) {
  var y = d.toUtc().year;
  var t0 = new DateTime.utc(y).millisecondsSinceEpoch;
  var t1 = new DateTime.utc(y + 1).millisecondsSinceEpoch;
  return y + (d.millisecondsSinceEpoch - t0).toDouble()/(t1 - t0);
}

void main() {
  print(StarDate(new DateTime.now()));
}


