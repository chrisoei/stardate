class StarDateInterval

  attr_accessor :start, :stop

  def initialize(start, stop)
    @start = StarDate.new start
    @stop = StarDate.new stop
  end

  def -@
    StarDateInterval.new @stop, @start
  end

  def years
    @stop.stardate - @start.stardate
  end

  def months
    years / StarDate.month
  end

  def weeks
    years / StarDate.week
  end

  def days
    years / StarDate.day
  end

  def hours
    years / StarDate.hour
  end

  def minutes
    years / StarDate.minute
  end

  def seconds
    years / StarDate.second
  end

  def milliseconds
    years / StarDate.millisecond
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