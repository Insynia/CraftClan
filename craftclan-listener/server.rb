require 'sinatra/base'

class Listener < Sinatra::Base
  post '/cmd' do
    puts params[:cmd]
  end

  get '/test' do
    system "screen -S #{ENV['SCREEN_NAME']} -X stuff \"say Hello\n\""
  end
end