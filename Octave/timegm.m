#
# timegm unit tests
#
# invoke from octave with:
#
#   test timegm
#

%!shared t0, t1, g
%!test
%!  t0 = time();
%!  g = gmtime(t0);
%!  t1 = timegm(g);
%!assert(t0, t1, 1e-4);
%!  for i = 1:10000
%!    t0 = time()*rand();
%!    t1 = timegm(gmtime(t0));
%!    assert(t0,t1,3e-6);
%!  endfor
