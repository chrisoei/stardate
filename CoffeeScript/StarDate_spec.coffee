stardate = require './StarDate.coffee'

describe "Conversions", ->
  it "should convert an ordinary date to a StarDate", ->
    (expect stardate.toStarDate(
      new Date(Date.UTC(2012, 0, 2, 3, 4, 5)))).toBe \
        2012.0030815181644
  it "should convert a StarDate to an ordinary date", ->
    (expect stardate.toDate(2012.5)).toEqual \
      new Date(2012, 6, 1, 17, 0, 0)
  it "should error if converting a non-number to a date", ->
    (expect -> stardate.toDate("2012.5")).toThrow \
      new Error "Unable to convert 2012.5 to Date"
  it "should error if converting a non-Date to a StarDate", ->
    (expect -> stardate.toStarDate("2012-07-01")).toThrow \
      new Error "Unable to convert 2012-07-01 to StarDate"