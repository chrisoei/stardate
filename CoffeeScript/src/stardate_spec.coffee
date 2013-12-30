StarDate = require './stardate'

new StarDate(1)

describe "Conversions", ->
  it "should convert an ordinary date to a StarDate", ->
    expect(new StarDate(
      new Date(Date.UTC(2012, 0, 2, 3, 4, 5))).canonical()).toBe \
        "2012.003081518164436"
  it "should convert a StarDate to an ordinary date", ->
    expect(new StarDate(2012.5)).toEqual \
      new Date(2012, 6, 1, 17, 0, 0)
  it "should error if converting a non-number to a date", ->
    (expect -> new StarDate("2012.5")).toThrow \
      new Error "Unable to convert 2012.5 to StarDate"
  it "should error if converting a non-Date to a StarDate", ->
    (expect -> new StarDate("2012-07-01")).toThrow \
      new Error "Unable to convert 2012-07-01 to StarDate"
