require 'aesstrict'
require 'readline'

puts "Enter password to decrypt:"
p = Readline::readline
puts '---------Decrypted Value------------'
puts Aesstrict.decrypt(p,ENV["QPP_AES_KEY"])