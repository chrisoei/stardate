#!/usr/bin/env dart

double StarDate(DateTime d) {
  int y = d.toUtc().year;
  int t0 = new DateTime.utc(y).millisecondsSinceEpoch;
  int t1 = new DateTime.utc(y + 1).millisecondsSinceEpoch;
  return y + (d.millisecondsSinceEpoch - t0).toDouble()/(t1 - t0);
}

void main() {
  print(StarDate(new DateTime.now()));
}


