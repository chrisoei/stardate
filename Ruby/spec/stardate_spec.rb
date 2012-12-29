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

  it 'should convert to/from RFC2822' do
    [
      'Sat, 29 Dec 2012 10:52:24 -0800'
    ].each do |t|
      Stardate.new(t).to_rfc2822.should == t
    end
  end

  it 'should form filenames when the filename has an extension' do
    Stardate.new(2012.5).to_filename('foo.txt').should == 'foo-2012.500000000000000.txt'
  end

  it 'should form filenames when the filename does not have an extension' do
    Stardate.new(2012.75).to_filename('README').should == 'README-2012.750000000000000'
  end

end
