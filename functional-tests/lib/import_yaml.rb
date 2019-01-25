require 'bundler/setup'
require 'user_manager_client'
require 'aesstrict'
require 'yaml'

# This utility can be used to populate the User Manager from a yml file. DO NOT RUN until hard-coded values are updated
# as needed
config = UserManagerClient::Configuration.new
config.host = # ip address
api = UserManagerClient::ApiClient.new config
$umClient = UserManagerClient::UsersApi.new api

base_yml = YAML.load_file("T:\\users\\user3.yml")
aut = 'QPPWI'
env = 'IMP'
users = base_yml[aut][env]["Users"]
users.each do|user|
  du = $umClient.get_user('7')
  du[:role] = user[0]
  du[:aut] = aut
  du[:env] = env
  h = user[1]

  du.delete(:id)
  du[:properties]= []
  h.each do |key, value|
    case key
      when 'uid'
        du[:uid] = value #Aesstrict.decrypt(value,ENV["QPP_AES_KEY"])
      when 'pwd'
        du[:pwdE] = value
      else
        prop = Hash.new
        prop[:key]= key
        prop[:value]= value
        du[:properties].push(prop)
    end
  end
  begin
    resp = $umClient.create_user(du)
    puts "updated"
  rescue => e
    puts e
  end
end
