require 'aesstrict'
encrypted = []
command = ''

# Enter each tin
while command != 'DONE'
  puts "Enter string to encrypt or DONE:"
  command = gets.chomp
  if command != 'DONE'
    encrypted.push Aesstrict.encrypt(command,ENV["QPP_AES_KEY"])
  end
end

puts '----------------- Examples Table Below -------------------'
encrypted.each do |enc|
  puts "| #{enc} |"
end


