function y = stardate(tm)
  if (strcmp(class(tm),"struct"))
    y0 = tm.year;
    y1 = y0 + 1;
    dummy.year = y0;
    dummy.mon = 0;
    dummy.mday = 1;
    t0 = timegm(dummy);
    dummy.year++;
    t1 = timegm(dummy);
    y = 1900 + y0 + (timegm(tm) - t0)/(t1 - t0);
  else
    y0 = floor(tm);
    dummy.year = y0 - 1900;
    dummy.mon = 0;
    dummy.mday = 1;
    dummy.hour = 0;
    dummy.min = 0;
    dummy.sec = 0;
    dummy.usec = 0;
    t0 = timegm(dummy);
    dummy.year++;
    t1 = timegm(dummy);
    tx = t0 + (t1 - t0) * (tm - y0);
    y = gmtime(tx);
  endif
endfunction

%!shared x, z
%!test
%!  z = stardate(2010.2431089644851);
%!  x = stardate(z);
%!assert(z.year, 110);
%!assert(z.mon, 2);
%!assert(z.mday, 30);
%!assert(z.hour, 17);
%!assert(z.min, 38);
%!assert(z.sec, 4);
%!assert(x, 2010.2431089644851);
