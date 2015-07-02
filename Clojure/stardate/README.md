# Stardate

A Clojure library designed to represent time as fractional years.

## Latest version
[![Clojars Project](http://clojars.org/stardate/latest-version.svg)](http://clojars.org/stardate)

## Usage

```
user=> (require '[stardate :as sd])
nil
user=> (sd/of "2014-09-30T17:17:27-07:00")
2014.7479784056316
user=> (sd/short)
"2015.499"
user=> (sd/canonical)
"2015.499250137905800"
user=> (sd/toZonedDateTime 2015.499 "America/Los_Angeles")
#object[java.time.ZonedDateTime 0x4acb8b2d "2015-07-01T20:14:24-07:00[America/Los_Angeles]"]
user=> (sd/toInstant 2015.499)
#object[java.time.Instant 0x20e8802f "2015-07-02T03:14:24Z"]
```

## License

Copyright © 2014 Chris Oei

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
