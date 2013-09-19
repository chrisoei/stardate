package main

import (
	"flag"
	"github.com/chrisoei/go-stardate"
	"github.com/chrisoei/oei"
	"time"
)

func printtln(t time.Time) {
	sd := stardate.New(t)
	println(sd.String())
}


func main() {
	var email = flag.Bool("email", false, "Use email date format")
	flag.Parse()
        var f string;
	if (*email) {
		f = "Mon, 02 Jan 2006 15:04:05 -0700"
	} else {
		f = "2006-01-02 15:04:05 -0700"
        }
	args := flag.Args()
	if len(args) > 0 {
		for i := range args {
			t, err := time.Parse(f, args[i])
			oei.ErrorHandler(err)
			printtln(t)
		}
	} else {
		printtln(time.Now())
	}
}
