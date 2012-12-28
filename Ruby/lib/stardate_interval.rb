class StardateInterval

  attr_accessor :start, :stop

  def initialize(start, stop)
    @start = Stardate.new start
    @stop = Stardate.new stop
  end

  def -@
    StardateInterval.new @stop, @start
  end

  def years
    @stop.stardate - @start.stardate
  end

  def months
    years / Stardate.month
  end

  def weeks
    years / Stardate.week
  end

  def days
    years / Stardate.day
  end

  def hours
    years / Stardate.hour
  end

  def minutes
    years / Stardate.minute
  end

  def seconds
    years / Stardate.second
  end

  def milliseconds
    years / Stardate.millisecond
  end

  def inspect
    [
      '[',
      @start.to_s,
      ' - ',
      @stop.to_s,
      '] (',
        to_human,
        ')'
    ].join
  end

  def to_human(precision = 3)
    [ :years, :months, :weeks, :days, :hours, :minutes, :seconds, :milliseconds ].each do |scale|
      x = public_send scale
      return "%.#{precision}f %s" % [x, scale] if x.abs >= 1
    end
    return "%.#{precision}e seconds" % seconds
  end

end
