require 'json'
require 'readline'

puts 'Enter json string below :'
puts JSON.pretty_generate JSON.parse(Readline::readline)