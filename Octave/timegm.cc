// To build this octave extension:
//
//   mkoctfile timegm.cc 
//

#include <octave/oct.h>
#include <error.h>
#include <oct-time.h>
#include <oct-map.h>

static inline octave_base_tm extract_tm (Octave_map &m) {
  octave_base_tm tm;

  tm.usec (m.intfield ("usec"));
  tm.sec (m.intfield ("sec"));
  tm.min (m.intfield ("min"));
  tm.hour (m.intfield ("hour"));
  tm.mday (m.intfield ("mday"));
  tm.mon (m.intfield ("mon"));
  tm.year (m.intfield ("year"));
  tm.wday (m.intfield ("wday"));
  tm.yday (m.intfield ("yday"));
  tm.isdst (m.intfield ("isdst"));
  tm.zone (m.stringfield ("zone"));

  return tm;
}

octave_time octave_timegm (const octave_base_tm& tm)
{
  struct tm t;

  t.tm_sec = tm.sec ();
  t.tm_min = tm.min ();
  t.tm_hour = tm.hour ();
  t.tm_mday = tm.mday ();
  t.tm_mon = tm.mon ();
  t.tm_year = tm.year ();
  t.tm_wday = tm.wday ();
  t.tm_yday = tm.yday ();
  t.tm_isdst = tm.isdst ();

#if defined (HAVE_STRUCT_TM_TM_ZONE)
  std::string s = tm.zone ();
  char *ps = strsave (s.c_str ());
  t.tm_zone = ps;
#endif

  octave_time ot = octave_time(
    (double)timegm(&t) + (double)tm.usec()/(double)1.0e6);

#if defined (HAVE_STRUCT_TM_TM_ZONE)
  delete [] ps;
#endif

  return ot;
}


DEFUN_DLD (timegm, args, ,
  "-*- texinfo -*-\n\
@deftypefn {Loadable Function} {} timegm (@var{tm_struct})\n\
Convert a time structure corresponding to the GMT time to the number\n\
of seconds since the epoch.  For example,\n\
\n\
@example\n\
@group\n\
x = time()\n\
timegm (gmtime (x)) - x\n\
     @result{} 0\n\
@end group\n\
@end example\n\
@seealso{mktime, gmtime}\n\
@end deftypefn")
{
  octave_value retval;

  if (args.length () == 1)
    {
      Octave_map map = args(0).map_value ();

      if (! error_state)
        {
          octave_base_tm tm = extract_tm (map);

          if (! error_state)
            retval = octave_timegm (tm);
          else
            error ("timegm: invalid TMSTRUCT argument");
        }
      else
        error ("timegm: expecting structure argument");
    }
  else
    print_usage ();

  return retval;
}

