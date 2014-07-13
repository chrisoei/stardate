# use -DCYGWIN for cygwin

CKOEI_MULTIDIGEST_VERSION=$(shell git describe --always --tags)

CFLAGS=-O3 -I. -I../oei-sqlite -I/usr/local/Cellar/openssl/1.0.1h/include -D_FILE_OFFSET_BITS=64 -D_LARGEFILE_SOURCE -DCKOEI_MULTIDIGEST_VERSION=\"$(CKOEI_MULTIDIGEST_VERSION)\" -I../oei-sqlite -std=c99

all: stardate

clean:
	rm -f *.o stardate

deploy: stardate
	cp stardate ~/Sync/OS_X/bin/

stardate: stardate.c
	gcc $(CFLAGS) -o stardate stardate.c

# vim: noet sts=2 sw=2 ts=2
