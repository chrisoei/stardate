#!/usr/bin/ruby1.9.1

require 'date'

dt = DateTime.now.new_offset(0)

y = dt.year

y0 = DateTime.new(y, 1, 1).new_offset(0)
y1 = DateTime.new(y + 1, 1, 1).new_offset(0)

stardate = y + (dt - y0)/(y1 - y0)
printf("%.13f\n", stardate)


y = stardate.to_i
y0 = DateTime.new(y, 1, 1).new_offset(0)
y1 = DateTime.new(y + 1, 1, 1).new_offset(0)
dt = y0 + (stardate - y) * (y1 - y0)
printf("%s\n", dt)

