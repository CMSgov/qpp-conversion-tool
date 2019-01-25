require 'aesstrict'
require 'readline'

puts "Enter string to encrypt:"
p = Readline::readline
enc = Aesstrict.encrypt(p,ENV["QPP_AES_KEY"])
puts '---------Encrypted Value------------'
puts enc
puts '---------Decrypted Value------------'
d = Aesstrict.decrypt(enc,ENV["QPP_AES_KEY"])
puts d
puts '---------------------'
