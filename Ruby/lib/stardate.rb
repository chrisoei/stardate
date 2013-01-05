require 'time'

require 'stardate_interval'

class Stardate

  attr_accessor :stardate

  class << self

    # Based on 365.2425 days/Gregorian year

    def millisecond
      1.0/31556952000.0
    end

    def second
      1.0/31556952.0
    end

    def minute
      1.0/525949.2
    end

    def hour
      1.0/8765.82
    end

    def day
      1.0/365.2425
    end

    def week
      7.0/365.2425
    end

    def fortnight
      14.0/365.2425
    end

    def month
      1.0/12.0
    end

    def rename(filename, mode = :mtime)
      case mode
      when :mtime
        datetime = File.mtime filename
      when :ctime
        datetime = File.ctime filename
      else
        raise "Unknown mode: #{mode}"
      end
      File.rename filename, datetime.to_stardate.to_filename(filename)
    end

  end

  def initialize(t = Time.now)
    case t.class.to_s
    when "Stardate"
      @stardate = t.stardate
      return
    when "Fixnum"
      @stardate = t.to_f
      return
    when "Float"
      @stardate = t
      return
    when "DateTime"
      datetime = t.to_time.utc
    when "Time"
      datetime = t.utc
    when "ActiveSupport::TimeWithZone"
      datetime = t.utc
    when "String"
      datetime = Time.parse(t).utc
    else
      raise "Unknown conversion: #{t.class}"
    end
    y0 = datetime.year
    t0 = Time.utc(y0).to_f
    t1 = Time.utc(y0 + 1).to_f
    @stardate = y0 + (datetime.to_f - t0)/(t1 - t0)
  end

  def -(arg)
    StardateInterval.new arg, self
  end

  def inspect
    [
      to_s,
      ' [',
      to_rfc2822,
      ']'
    ].join
  end

  def to_datetime
    to_time.to_datetime
  end

  def to_dts
    to_time.utc.strftime("%Y%m%d_%H%M%SZ")
  end

  def to_f
    @stardate
  end
  
  def to_femtoyears
    (@stardate * 1e15).to_i
  end

  def to_filename(filename)
    ext = File.extname filename
    [
      filename.chomp(ext),
      '-',
      to_s,
      ext
    ].join
  end

  # Round to the nearest second
  def to_iso8601
    Time.at(to_time.to_f + 0.5).iso8601
  end

  def to_localdate
    to_time.to_date
  end

  # Round to the nearest second
  def to_rfc2822
    Time.at(to_time.to_f + 0.5).rfc2822
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

  def to_utcdate
    to_time.utc.to_date
  end

end

class DateTime

  def to_stardate
    Stardate.new self
  end

end

class Time

  def to_stardate
    Stardate.new self
  end

end
