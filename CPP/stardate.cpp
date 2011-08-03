#include <time.h>
#include <sys/time.h>
#include <stdio.h>

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

double StarDate(struct tm* x) {
	double y0 = (double)getYearInSeconds(x->tm_year);
	double y1 = (double)getYearInSeconds(x->tm_year + 1);
	double yn = timegm(x);
	return 1900 + x->tm_year + (yn - y0)/(y1 - y0);
}

double StarDate(time_t t) {
	return StarDate(gmtime(&t));
}

double StarDate(timeval t) {
	double s0 = StarDate(t.tv_sec);
	double s1 = StarDate(t.tv_sec + 1);
	return s0 + (s1 - s0) * t.tv_usec / 1.0e6;
}

double StarDate() {
	timeval tv;
	gettimeofday(&tv,NULL);
	return StarDate(tv);
	//return StarDate(time(NULL));
}

#ifdef CKOEI_MAIN
int main(int argc, char* argv[]) {
	printf("%0.16lf\n", StarDate());
}
#endif

