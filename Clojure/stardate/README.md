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
```

## License

Copyright © 2014 Chris Oei

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
