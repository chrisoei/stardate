require 'time'
require 'erb'

def my_version
	'1.0.0'
end

task :default => :test

desc "Test the code"
task :test do
  sh %{ rspec }
end

desc "Build the gem"
task :build do
	gemspec = ERB.new IO.read 'stardate.gemspec.erb'
	IO.write 'stardate.gemspec', gemspec.result(binding)
  sh %{ gem build stardate.gemspec }
end

desc "Uninstall"
task :uninstall do
  sh %{ gem uninstall stardate --all }
end

desc "Install"
task :install => [ :build, :uninstall ] do
  sh %{ gem install stardate-#{my_version}.gem --local }
end
