
@toStarDate = (d) ->
  if d.getUTCFullYear?
    y = d.getUTCFullYear()
    t0 = Date.UTC y, 0, 1, 0, 0, 0, 0
    t1 = Date.UTC y + 1, 0, 1, 0, 0, 0, 0
    t = Date.UTC y, d.getUTCMonth(), d.getUTCDate(),
      d.getUTCHours(), d.getUTCMinutes(), d.getUTCSeconds(),
      d.getUTCMilliseconds()
    y + (t - t0) / (t1 - t0)
  else
    throw new Error "Unable to convert #{d} to StarDate"

@toDate = (d) ->
  if typeof d is "number"
    y0 = Math.floor d
    y1 = y0 + 1
    t0 = Date.UTC y0, 0, 1, 0, 0, 0, 0
    t1 = Date.UTC y1, 0, 1, 0, 0, 0, 0
    new Date(t0 + (t1 - t0) * (d - y0))
  else
    throw new Error "Unable to convert #{d} to Date"
