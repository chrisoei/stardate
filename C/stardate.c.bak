#include <stdlib.h>
#include <time.h>
#include <sys/time.h>
#include <stdio.h>
#include <getopt.h>

// y is # of years since 1900 UTC
time_t getYearInSeconds(int y) {
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

double getStarDateFromTM(struct tm* x) {
	double y0 = (double)getYearInSeconds(x->tm_year);
	double y1 = (double)getYearInSeconds(x->tm_year + 1);
	double yn = timegm(x);
	return 1900 + x->tm_year + (yn - y0) / (y1 - y0);
}

double getStarDateFromTimeVal(struct timeval t) {
	struct tm mytime;
	time_t tsec;

	tsec = t.tv_sec;
	mytime = *gmtime(&tsec);
	double s0 = getStarDateFromTM(&mytime);
	tsec++;
	mytime = *gmtime(&tsec);
	double s1 = getStarDateFromTM(&mytime);
	return s0 + (s1 - s0) * t.tv_usec / 1.0e6;
}

double getStarDateFromString(const char* fmt, const char* s) {
	struct tm mytime;
	char* rc = strptime(s, fmt, &mytime);
	return getStarDateFromTM(&mytime);
}

double getStarDateFromTimestamp(const char* ts) {
  return getStarDateFromString("%Y-%m-%d %H:%M:%S", ts);
}

#ifdef CKOEI_MAIN
int main(int argc, char* argv[]) {
	int set_flag = 0;
	int nl_flag = 0;
	int short_flag = 0;
  char c;
  double sd;
  while ((c = getopt(argc, argv, "e:g:ns")) != -1) {
    switch(c) {
      case 'e':
        sd = getStarDateFromString("%a, %d %b %Y %H:%M:%S %z", optarg);
				set_flag++;
        break;
      case 'g':
				sd = getStarDateFromString("%a %b %d %H:%M:%S %Y %z", optarg);
				set_flag++;
        break;
      case 'n':
        nl_flag++;
        break;
      case 's':
			  short_flag++;
        break;
      default:
        printf("Incorrect usage\n");
        exit(1);
    }
  }
	if (!set_flag) {
		struct timeval tv;
		gettimeofday(&tv, NULL);
		sd = getStarDateFromTimeVal(tv);
	}
	if (short_flag) {
		printf("%0.3lf", sd);
	} else {
		printf("%0.15lf", sd);
	}
	if (nl_flag) printf("\n");
 	return 0;
}
#endif
