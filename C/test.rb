require 'test/unit'

class TestStardate < Test::Unit::TestCase

  def test_time_zone
    x = `./stardate -e 'Sat, 29 Dec 2012 10:52:24 -0800'`.to_f
    assert_in_delta 2012.993951882210013, x, 0.000001
  end

end

