require 'httparty'

print 'Enter metricType: '; type = gets.chomp
JSON.parse(HTTParty.get("https://raw.githubusercontent.com/CMSgov/qpp-measures-data/master/measures/2017/measures-data.json", headers: {:Accept => 'application/json'}).read_body).each do | msr |
  puts "| #{msr["measureId"]} |" if msr["metricType"].eql? type
end