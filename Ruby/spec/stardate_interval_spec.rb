require 'stardate'

describe StardateInterval do

  it 'should be created by subtracting Stardates' do
    (Stardate.new(2013) - Stardate.new(2012)).years.should == 1
  end

end
