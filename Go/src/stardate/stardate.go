package stardate

import (
	"fmt"
	"time"
)

type Stardate float64

func (sd *Stardate) Canonical() string {
	return fmt.Sprintf("%0.15f", float64(*sd))
}

func (sd *Stardate) Short() string {
	return fmt.Sprintf("%0.3f", float64(*sd))
}

func (sd *Stardate) String() string {
	return sd.Canonical()
}

func (sd *Stardate) Time() time.Time {
	y := int((float64(*sd)))
	frac := float64(*sd) - float64(y)
	t0 := time.Date(y, time.January, 1, 0, 0, 0, 0, time.UTC).UnixNano()
	t1 := time.Date(y+1, time.January, 1, 0, 0, 0, 0, time.UTC).UnixNano()
	return time.Date(y, time.January, 1, 0, 0, 0, 0, time.UTC).Add(time.Duration(frac * float64(t1-t0)))
}

func (sd *Stardate) setTime(t time.Time) *Stardate {
	y := t.UTC().Year()
	t0 := time.Date(y, time.January, 1, 0, 0, 0, 0, time.UTC).UnixNano()
	t1 := time.Date(y+1, time.January, 1, 0, 0, 0, 0, time.UTC).UnixNano()
	*sd = (Stardate)(float64(y) + float64(t.UnixNano()-t0)/float64(t1-t0))
	return sd
}

func New(t time.Time) Stardate {
	sd := Stardate(0)
	sd.setTime(t)
	return sd
}
