# use -DCYGWIN for cygwin

CKOEI_MULTIDIGEST_VERSION=$(shell git describe --always --tags)

CFLAGS=-O3 -DCKOEI_MAIN -std=c99

all: stardate

clean:
	rm -f *.o stardate

deploy: stardate
	cp stardate ~/Sync/OS_X/bin/

stardate: stardate.c
	gcc $(CFLAGS) -o stardate stardate.c

# vim: noet sts=2 sw=2 ts=2
