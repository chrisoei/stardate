package main

import (
	"flag"
	"github.com/chrisoei/go-stardate"
	"github.com/chrisoei/oei"
	"os"
	"time"
)

func printtln(t time.Time) {
	sd := stardate.New(t)
	println(sd.String())
}

func main() {
	var email = flag.Bool("email", false, "Use email date format")
	var git = flag.Bool("git", false, "Use git date format")
	var mtime = flag.Bool("mtime", false, "Use modification time of files")
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
			oei.ErrorHandler(err)
			printtln(t)
		}
	} else {
		printtln(time.Now())
	}
}
