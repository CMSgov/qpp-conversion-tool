require 'jsonpath'

# This utility can be used to test a JSON Path string against JSON to view the results
puts "Enter JSON string, and then hit <Enter> twice:"
all_text = ""
until (text = gets) == "\n"
  all_text << text
end
json_string = all_text.chomp

puts "Enter JSONPath to evaluate:"
path_string = gets.chomp

result = JsonPath.on(json_string, path_string)

puts result