require 'debugger'
require 'stardate'

describe Stardate do

  it 'should convert Stardates to Time' do
    Stardate.new(2012.5).to_time.utc.to_s.should ==
      '2012-07-02 00:00:00 UTC'
  end

  it 'should convert Time to Stardates' do
    Time.utc(2012,1,2,3,4,5).to_stardate.should
      be_within(3.1688765e-08).of(2012.0030815181644)
  end

end
