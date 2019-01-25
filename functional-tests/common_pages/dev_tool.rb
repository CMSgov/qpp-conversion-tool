require 'net/http'
require 'uri'
require_relative 'qpp_page'

class Dev_Tool
  def get_authToken(role = '')
    #rootURL = $user_yml['QPPWI'][ENV['ENV']]['URL']
    rootURL = UserUtil.get_homepage("QPPWI")
    # removes cookie from URL if it exists
    $url = if rootURL.include? '?'
             rootURL.slice(0..(rootURL.index('?') - 1))
           else
             rootURL
           end
    url = URI($url + '/api/auth/authn')
    http = Net::HTTP.new(url.host, url.port)
    http.use_ssl = true
    request = Net::HTTP::Post.new(url)
    $userObj = UserUtil.get_user('QPPWI', role) if role != ''
    uid = $userObj[:uid]
    pwd = Aesstrict.decrypt($userObj[:pwdE],ENV["QPP_AES_KEY"])

    request['cookie'] = 'ACA=z3DUR2WH3Y' if $url.include? 'imp'
    request['content-type'] = 'application/json'
    request['accept'] = 'application/vnd.qpp.cms.gov.v1+json'
    request['cache-control'] = 'no-cache'
    request.body = "{\r\n  \"username\": \"" + uid + "\",\r\n  \"password\": \"" + pwd + "\"\r\n}"
    response = http.request(request)
    #puts response.read_body
    result = JSON.parse(response.body)
    result['auth']['text']
  end

  def get_data_from_api(api_url,role='')
    authToken = get_authToken(role = '')
    url = URI(api_url)
    http = Net::HTTP.new(url.host, url.port)
    http.use_ssl = true
    request = Net::HTTP::Get.new(url)
    request['content-type'] = 'application/json'
    request['cache-control'] = 'no-cache'
    request['authorization'] = authToken
    request['cookie'] = 'ACA=z3DUR2WH3Y' if api_url.include? 'imp'
    request['qpp_auth_token'] = authToken.sub('Bearer ', '')
    request['content-type'] = 'application/json'
    request['accept'] = 'application/vnd.qpp.cms.gov.v1+json'
    request['cache-control'] = 'no-cache'
    response_out = http.request(request)
    JSON.parse(response_out.body)
  end
end
