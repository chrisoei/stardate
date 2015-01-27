require 'stardate'

describe StardateInterval do

  it 'should be created by subtracting Stardates' do
    expect((Stardate.new(2013) - Stardate.new(2012)).years).to eq(1)
  end

end
