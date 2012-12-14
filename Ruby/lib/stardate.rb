require 'time'

class StarDate

  attr_accessor :stardate

  def initialize(t = Time.now)
    case t.class.to_s
    when "Float"
      @stardate = t
      return
    when "DateTime"
      datetime = t.to_time.utc
    when "Time"
      datetime = t.utc
    when "ActiveSupport::TimeWithZone"
      datetime = t.utc
    else
      raise "Unknown conversion: #{t.class}"
    end
    y0 = datetime.year
    t0 = Time.utc(y0).to_f
    t1 = Time.utc(y0 + 1).to_f
    @stardate = y0 + (datetime.to_f - t0)/(t1 - t0)
  end
  
  def to_f
    @stardate
  end

  def to_s
    sprintf("%.15f", @stardate)
  end

  def to_time
    y0 = @stardate.to_i
    t0 = Time.utc(y0).to_f
    t1 = Time.utc(y0+1).to_f
    Time.at(t0 + (@stardate-y0)*(t1-t0))
  end

  def to_localdate
    to_time.to_date
  end
  
  def to_utcdate
    to_time.utc.to_date
  end

  def to_datetime
    to_time.to_datetime
  end

  def to_dts
    to_time.utc.strftime("%Y%m%d_%H%M%SZ")
  end

end

class DateTime

  def to_stardate
    StarDate.new self
  end

end

class Time

  def to_stardate
    StarDate.new self
  end

end
