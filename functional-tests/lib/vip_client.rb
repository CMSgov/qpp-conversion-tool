require 'open3'

module VIPClient
  def self.getCode()
    stdout, stderr, status = Open3.capture3(Dir.pwd+"\\bin\\VIPClient\\dist\\VIPClient.exe")
    return stdout
  end
end