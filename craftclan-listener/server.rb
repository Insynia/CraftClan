require 'sinatra/base'

class Listener < Sinatra::Base
  post '/cmd' do
    puts params[:cmd]
  end
end