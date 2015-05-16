require 'sinatra/base'

class Listener < Sinatra::Base
  post '/cmd' do
    system "screen -S #{ENV['SCREEN_NAME']} -X stuff #{params[:cmd]}"
  end

  get '/test' do
    system "screen -S #{ENV['SCREEN_NAME']} -X stuff \"say Hello, this is a test from the server. If you can read this, you are awesome\n\""
  end
end