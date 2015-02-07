#include <stdlib.h>
#include <time.h>
#include <sys/stat.h>
#include <sys/time.h>
#include <stdio.h>
#include <getopt.h>

// y is # of years since 1900 UTC
static time_t getYearInSeconds(int y) {
  struct tm mytime;
  mytime.tm_sec = 0;
  mytime.tm_min = 0;
  mytime.tm_hour = 0;
  mytime.tm_mday = 1; // tm_mday range is [1,31]
  mytime.tm_mon = 0; // tm_mon range is [0,11]
  mytime.tm_year = y; // tm_year is years since 1900
  mytime.tm_wday = 0; // this will be set automatically
  mytime.tm_yday = 0; // this will be set automatically
  mytime.tm_isdst = 0;
  return timegm(&mytime);
}

// struct tm does not carry time zone info!
// This function assumes tm refers to UTC
double getStarDateFromTMUTC(struct tm* x) {
  double y0 = (double)getYearInSeconds(x->tm_year);
  double y1 = (double)getYearInSeconds(x->tm_year + 1);
  double yn = timegm(x);
  return 1900 + x->tm_year + (yn - y0) / (y1 - y0);
}

// struct tm does not carry time zone info!
// This function assumes tm refers to local time
double getStarDateFromTMLocal(struct tm* x) {
  double y0 = (double)getYearInSeconds(x->tm_year);
  double y1 = (double)getYearInSeconds(x->tm_year + 1);
  double yn = timelocal(x);
  return 1900 + x->tm_year + (yn - y0) / (y1 - y0);
}


double getStarDateFromTime(time_t t) {
  return getStarDateFromTMUTC(gmtime(&t));
}

double getStarDateFromTimeVal(struct timeval t) {
  double s0 = getStarDateFromTime(t.tv_sec);
  double s1 = getStarDateFromTime(t.tv_sec + 1.0);
  return s0 + (s1 - s0) * t.tv_usec / 1.0e6;
}

double getStarDateFromString(const char* fmt, const char* s, int v) {
  static struct tm mytime = {0};
  char* rc = strptime(s, fmt, &mytime);
  if (rc == NULL) {
    fprintf(stderr, "Unable to parse %s as %s\n", s, fmt);
    exit(1);
  }
  if (v) {
    static char buf[256];
    strftime(buf, 254, "%a, %d %b %Y %H:%M:%S %z", &mytime);
    printf("Parsed time as: %s\n", buf);
  }
  return getStarDateFromTMLocal(&mytime);
}

double getStarDateFromTimestamp(const char* ts, int v) {
  return getStarDateFromString("%Y-%m-%d %H:%M:%S", ts, v);
}

#ifdef CKOEI_MAIN
int main(int argc, char* argv[]) {
  int verbose_flag = 0;
  int set_flag = 0;
  int nl_flag = 0;
  int short_flag = 0;
  int rc = 0;
  char c;
  static struct timeval tv = {0};
  static struct stat st = {0};
  static char buf[256];
  double sd;

  while ((c = getopt(argc, argv, "e:g:m:nsv")) != -1) {
    switch(c) {
      case 'e':
        sd = getStarDateFromString("%a, %d %b %Y %H:%M:%S %z", optarg, verbose_flag);
        set_flag++;
        break;
      case 'g':
        sd = getStarDateFromString("%a %b %d %H:%M:%S %Y %z", optarg, verbose_flag);
        set_flag++;
        break;
      case 'm':
        if ((rc = lstat(optarg ,&st))) {
          fprintf(stderr, "lstat error %d for %s\n", rc, optarg);
          exit(1);
        }
        sd = getStarDateFromTime(st.st_mtime);
        set_flag++;
        break;
      case 'n':
        nl_flag++;
        break;
      case 's':
        short_flag++;
        break;
      case 'v':
        verbose_flag++;
        break;
      default:
        fprintf(stderr, "Incorrect usage\n");
        exit(1);
    }
  }
  if (!set_flag) {
    gettimeofday(&tv, NULL);
    sd = getStarDateFromTimeVal(tv);
  }
  printf(short_flag ? "%0.3lf" : "%0.15lf", sd);
  (verbose_flag || nl_flag) && printf("\n");
  return 0;
}
#endif
