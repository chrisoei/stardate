package main

import (
	"flag"
	"github.com/chrisoei/go-stardate"
	"log"
	"os"
	"time"
)

func printtln(t time.Time, nl bool) {
	sd := stardate.New(t)
	if nl {
		os.Stdout.Write([]byte(sd.Canonical() + "\n"))
	} else {
		os.Stdout.Write([]byte(sd.Canonical()))
	}
}

func main() {
	var email = flag.Bool("email", false, "Use email date format")
	var git = flag.Bool("git", false, "Use git date format")
	var mtime = flag.Bool("mtime", false, "Use modification time of files")
	var nl = flag.Bool("nl", false, "Use newline")
	flag.Parse()
	var f string
	if *email {
		f = "Mon, 2 Jan 2006 15:04:05 -0700"
	} else if *git {
		f = "Mon Jan 2 15:04:05 2006 -0700"
	} else {
		f = "2006-01-02 15:04:05 -0700"
	}
	args := flag.Args()
	if len(args) > 0 {
		for i := range args {
			var t time.Time
			var err error
			if *mtime {
				var s os.FileInfo
				s, err = os.Stat(args[i])
				if err == nil {
					t = s.ModTime()
				}
			} else {
				t, err = time.Parse(f, args[i])
			}
			if err != nil {
				log.Fatalln(err)
			}
			printtln(t, *nl)
		}
	} else {
		printtln(time.Now(), *nl)
	}
}

// vim: noet sts=2 sw=2 ts=2
