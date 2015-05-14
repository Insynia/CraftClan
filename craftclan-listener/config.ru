require './server.rb'
require 'rack/attack'

class Rack::Attack
  blacklist('Secure by IP') do |req|
    ENV['CRAFTCLAN_WEB_IP'] != req.ip
  end
end

use Rack::Attack

run Sinatra::Application