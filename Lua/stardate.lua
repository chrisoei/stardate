-- ***********************************************************************
-- *                                                                     *
-- * THIS IMPLEMENTATION IS BUGGY, OR AT LEAST HAS NOT BEEN VALIDATED!!! *
-- *                                                                     *
-- ***********************************************************************


local function tyear(y)
  return os.difftime(os.time{year=y,month=1,day=1},os.time{year=1970,month=1,day=1})
end

t = os.time()
y0 = os.date("%Y",t)
y1 = y0 + 1
--t0 = utctime{year = y0, month = 1, day = 1}
--t1 = utctime{year = y1, month = 1, day = 1}
t0 = tyear(y0)
t1 = tyear(y1)
print("t1 = "..t0)
print("t1-t0 = "..(t1-t0))
m = 1.0/(t1 - t0)
b = y0 - m * t0
print("m = " .. m .. " b = " .. b)
print(y0 + (t - t0)/(t1 - t0))
print(m * t + b)


