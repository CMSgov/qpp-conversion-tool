require 'ringcentral'
require 'aesstrict'
#require 'YAML'


module SMSUtil
  def self.getMFA()
    if ENV['ENV']=='PROD'
      app_key = $userObj[:appKey]
      app_secret = Aesstrict.decrypt($userObj[:appSecretE],ENV['QPP_AES_KEY'])
      server = $userObj[:server]
      account = $userObj[:username]

      puts $userObj[:username]
      puts $userObj[:extension]
      puts Aesstrict.decrypt($userObj[:passwordE],ENV['QPP_AES_KEY'])

      rc = RingCentral.new(app_key, app_secret, server)
      rc.authorize(username: $userObj[:username], extension: $userObj[:extension], password: Aesstrict.decrypt($userObj[:passwordE],ENV['QPP_AES_KEY']))
      r = rc.get('/restapi/v1.0/account/~/extension/~/message-store?perPage=10')
      message = JSON.parse(r.body)['records']

    else
      app_key = $user_yml['RINGCENTRAL']['appKey']
      app_secret = Aesstrict.decrypt($user_yml['RINGCENTRAL']['appSecretE'],ENV['QPP_AES_KEY'])
      server = $user_yml['RINGCENTRAL']['server']
      account = $user_yml['RINGCENTRAL']['username']
      rc = RingCentral.new(app_key, app_secret, server)
      rc.authorize(username: $user_yml['RINGCENTRAL']['username'], extension: $user_yml['RINGCENTRAL']['extension'], password: Aesstrict.decrypt($user_yml['RINGCENTRAL']['passwordE'],ENV['QPP_AES_KEY']))

      # get
      r = rc.get('/restapi/v1.0/account/~/extension/~/message-store?perPage=10')
      message = JSON.parse(r.body)['records']

      # r = rc.get('/restapi/v1.0/account/~/extension/~/message-store/'+message[0]['id'].to_s) # to get out of sandbox

    end

    subject = message[0]['subject']
    subject = subject.split(' - ')[1].chomp #for sandbox
    subject[30...36]
  end

end

# $user_yml = YAML.load_file("../data/user.base.yml")
# puts SMSUtil.getMFA