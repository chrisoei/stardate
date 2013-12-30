module.exports = class StarDate

  constructor: (d = new Date()) ->
    switch typeof d
      when 'number'
        @stardate = d
        y = Math.floor d
        t0 = Date.UTC y, 0, 1, 0, 0, 0, 0
        t1 = Date.UTC y + 1, 0, 1, 0, 0, 0, 0
        @date = new Date(t0 + (t1 - t0) * (d - y))
      when 'object'
        if d instanceof Date
          @date = new Date(d)
          y = @date.getUTCFullYear()
          t0 = Date.UTC y, 0, 1, 0, 0, 0, 0
          t1 = Date.UTC y + 1, 0, 1, 0, 0, 0, 0
          t = Date.UTC y,
            @date.getUTCMonth(),
            @date.getUTCDate(),
            @date.getUTCHours(),
            @date.getUTCMinutes(),
            @date.getUTCSeconds(),
            @date.getUTCMilliseconds()
          @stardate = y + (t - t0) / (t1 - t0)
        else if d instanceof StarDate
          @stardate = d.stardate
          @date = new Date(d.date)
        else
          throw "Unable to convert #{d} to StarDate"
      else
        throw "Unable to convert #{d} to StarDate"

  canonical: ->
    @stardate.toFixed(15)

  short: ->
    @stardate.toFixed(3)

# vim: expandtab tabstop=2 shiftwidth=2

