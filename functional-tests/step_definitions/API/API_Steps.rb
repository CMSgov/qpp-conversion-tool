require 'jsonpath'
require 'roo'

Given(/^User starts(?: (.*))? API test$/) do |app|
  $response = nil
  $userObj = nil
  $authToken = nil

  app ? @app = app : @app = 'QPPWI'
  rootURL = UserUtil.get_homepage(@app)

  # removes cookie from URL if it exists
  if rootURL.include? "?"
    $url = rootURL.slice(0..(rootURL.index('?') - 1))
  else
    $url = rootURL
  end
end

And(/^User switches to the (DEV|IMP|PROD|DEVPREV) environment$/) do |env|
  ENV['ENV'] = env
end

Given(/^User receives valid JWT$/) do

end

Given(/^User has logged in to API and has valid JWT$/) do

end

When(/^User makes GET request to "(.*)"(?: with Parameter (.*) and Value (.*))?$/) do |endpoint, param, value|
  endpoint = JsonSpec.remember(endpoint).tr('""', '')
  value = JsonSpec.remember(value).tr('"', '') if value
  GET(endpoint, param, value)
end

Then(/^User can verify the (.*) scores for (.*) directory are valid$/) do |measure_type, directory|
  dir = Pathname.new(__FILE__).join("..")
  Dir.glob("#{dir}/" + "Data Files/" + directory + "/*.json") do |mset|
    # POST measurement set, check for 201, and keep submissionId, submissionMethod, and isEndToEndReported
    step "User makes POST request to \"/api/submissions/measurement-sets with QPPA/Public/#{File.basename(directory)}/#{File.basename(mset)}\" in json format"
    step 'User receives 201 response code'
    step "I keep the JSON response at \"data/measurementSet/submissionId\" as \"Submission_ID\""
    step "I keep the JSON response at \"data/measurementSet/submissionMethod\" as \"Submission_Method\""
    step "I keep the JSON response at \"data/measurementSet/measurements/0/value/isEndToEndReported\" as \"isE2E\""
    step "I keep the JSON response at \"data/measurementSet/measurements/0/measureId\" as \"Measure_ID\""

    # GET the score for the measurement set using submissionId and check for 200 response
    step "User makes GET request to \"/api/submissions/submissions/%{Submission_ID}/score\""
    step 'User receives 200 response code'

    # Save the benchmarkType and validate it against the rules of the measurement type
    validate_score(JsonPath.new('$..benchmarkType').first($response.body), measure_type, File.basename(mset))

    # DELETE the measurement set to prevent duplicate entry errors
    step 'User does not have any measurement-sets posted'
  end
end

def validate_score(benchmarkType, measure_type, mset)
  submission_method = JsonSpec.remember("%{Submission_Method}").tr('"', '')
  is_e2e = JsonSpec.remember("%{isE2E}").tr('"', '')

  # check the response based on the measurement's submission method and endToEndReported values
  case measure_type

  when 'eCQM Only'
    if submission_method == 'registry' and is_e2e == 'false'
      raise StandardError, "Invalid benchMarkType \"#{benchmarkType}\" for measureType #{measure_type} in #{mset}" if invalid_benchmark?(submission_method) unless benchmarkType == nil
    else
      raise StandardError, "Invalid benchMarkType \"#{benchmarkType}\" for measureType #{measure_type} in #{mset}" if invalid_benchmark?(submission_method) unless benchmarkType == 'electronicHealthRecord'
    end

  when 'historical'
    if submission_method == 'registry' and is_e2e == 'false'
      raise StandardError, "Invalid benchMarkType \"#{benchmarkType}\" for measureType #{measure_type} in #{mset}" if invalid_benchmark?(submission_method, true) and benchmarkType != 'registry'
    else
      raise StandardError, "Invalid benchMarkType \"#{benchmarkType}\" for measureType #{measure_type} in #{mset}"
    end

  when 'MIPS Without eCQM', 'QCDR'
    raise StandardError, "Invalid benchMarkType \"#{benchmarkType}\" for measureType #{measure_type} in #{mset}" if invalid_benchmark?(submission_method) unless benchmarkType == 'registry'

  when 'MIPS With eCQM'
    if submission_method == 'registry' and is_e2e == 'false'
      raise StandardError, "Invalid benchMarkType \"#{benchmarkType}\" for measureType #{measure_type} in #{mset}" if invalid_benchmark?(submission_method) unless benchmarkType == 'registry'
    else
      raise StandardError, "Invalid benchMarkType \"#{benchmarkType}\" for measureType #{measure_type} in #{mset}" if invalid_benchmark?(submission_method) unless benchmarkType == 'electronicHealthRecord'
    end

  else
    raise ArgumentError, 'Measure type is not "eCQM Only", "MIPS Without eCQM", "MIPS With eCQM", nor "QCDR"'
  end
end

def invalid_benchmark?(submission_method, historical = nil)
  # GET Benchmark for measure ID of measurement-set posted
  step "User makes GET request to \"/api/submissions/public/benchmarks?performanceYear=2017&measureId=%{Measure_ID}\""
  step 'User receives 200 response code'

  # Find the submission methods and status codes for the benchmark
  statuses = JsonPath.new('$..status').on($response.body)

  if historical
    statuses[JsonPath.new('$..submissionMethod').on($response.body).index(submission_method)] != 'historical'
  else
    statuses[JsonPath.new('$..submissionMethod').on($response.body).index(submission_method)] != 'currentInsufficientData'
  end
end

When(/User receives (.*) response code$/) do |expectedCode|
  expect($response.code).to eq(expectedCode)
end

When(/User receives response message "(.*)"$/) do |text|
  expect($response.read_body).to include(text)
end

Then(/^the (?:JSON|json)(?: response)? should( not)? include the (key|value) "(.*)"$/) do |negative, searchFor, val|
  json = JSON.parse($response.read_body)
  if searchFor == 'key'
    if negative
      refute(json.keys.include? val)
    else
      assert(json.keys.include? val)
    end
  else
    if negative
      refute(json.values.include? val)
    else
      assert(json.values.include? val)
    end
  end
end

When(/^User DELETES (?:provider|clinic) (.*) for organization (.*)$/) do |id, orgID|
  if id.include? "provider"
    providerID = JsonSpec.remember(id)

    endpoint = "/api/submissions/web-interface/providers/organization/" + orgID + "/provider/" + providerID
  else
    clinID = JsonSpec.remember(id)
    endpoint = "/api/submissions/web-interface/clinics/organization/" + orgID + "/clinic/" + clinID
  end
  DELETE(endpoint)
end

When(/^User makes POST request to "(.*)" with (.*) in (.*) format(?: with Parameter (.*) and Value (.*))?$/) do |endpoint, file_path, file_format, param, value|
  endpoint = JsonSpec.remember(endpoint).tr('""', '')
  value = JsonSpec.remember(value).tr('"', '') if value
  POST(endpoint, file_path, file_format, param, value)
end

When(/^User makes POST request to "(.*)" with:$/) do |endpoint, json|
  POST(JsonSpec.remember(endpoint).tr('""', ''), JsonSpec.remember(json))
end

When(/^User makes POST request to "(.*?)"(?: with Parameter (.*) and Value (.*))? with raw:$/) do |endpoint, param, value, text|
  endpoint = JsonSpec.remember(endpoint).tr('""', '')
  value = JsonSpec.remember(value).tr('"', '') if value
  POST(endpoint, text, nil, param, value)
end

When(/^User makes PUT request to "(.*)" with:$/) do |endpoint, json|
  PUT(JsonSpec.remember(endpoint).tr('""', ''), JsonSpec.remember(json))
end

When(/^User makes PUT request to "(.*)" with (.*) in (.*) format$/) do |endpoint, file_path, file_format|
  PUT(JsonSpec.remember(endpoint).tr('""', ''), file_path, file_format)
end

When(/^User makes PUT request to "(.*)"(?: with Parameter (.*) and Value (.*))? and JSON:$/) do |endpoint, param, value, json|
  endpoint = JsonSpec.remember(endpoint).tr('""', '')
  value = JsonSpec.remember(value).tr('"', '') if value
  PUT(endpoint, json, nil, param, value)
end

When(/^User makes PATCH request to "(.*)" with:$/) do |endpoint, json|
  PATCH(JsonSpec.remember(endpoint).tr('""', ''), JsonSpec.remember(json))
end

When(/^User makes PATCH request to "(.*)" with (.*) in (.*) format$/) do |endpoint, file_path, file_format|
  PATCH(JsonSpec.remember(endpoint).tr('""', ''), file_path, file_format)
end

When(/^User makes DELETE request to "(.*)"(?: with Parameter (.*) and Value (.*))?$/) do |endpoint, param, value|
  endpoint = JsonSpec.remember(endpoint).tr('""', '')
  value = JsonSpec.remember(value).tr('"', '') if value
  DELETE(endpoint, param, value)
end

When(/^User has no authorization$/) do
  $userObj = nil
end

When(/^User authenticates the(?: (.*))? API with role=([^\s]+)(?: (with|without) lock)?$/) do |app, role, lock|
  app == nil ? @app = 'QPPWI' : @app = app

  if ENV['ENV'] == 'PROD' && @app != 'QPPA' && @app != 'QPPCT'
    step "User opens a browser"
    step "User visit QPP home page"
    step "User click sign in link on the top right of the page"
    step "User logs in to #{@app} with role=#{role}"
  end

  lock == 'with' ? $userObj = UserUtil.get_user(@app, role) : $userObj = UserUtil.get_user_no_lock(@app, role)
  rootURL = UserUtil.get_homepage(@app)

  # removes cookie from URL if it exists
  if rootURL.include? "?"
    $url = rootURL.slice(0..(rootURL.index('?') - 1))
  else
    $url = rootURL
  end

  if ENV['ENV'] == 'PROD' && @app != 'QPPA' && @app != 'QPPCT'
    $authToken = 'Bearer ' + $qpp_auth_token
  else
    pwd = Aesstrict.decrypt($userObj[:pwdE], ENV["QPP_AES_KEY"])
    if !role.include? "JWT"

      uid = $userObj[:uid]
      url = URI($url + "/api/auth/authn")
      http = Net::HTTP.new(url.host, url.port)
      http.use_ssl = true
      request = Net::HTTP::Post.new(url)
      if $url.include? "imp"
        request["cookie"] = 'ACA=z3DUR2WH3Y'
      end
      request["content-type"] = 'application/json'
      request["accept"] = 'application/vnd.qpp.cms.gov.v1+json'
      request["cache-control"] = 'no-cache'
      request.body = "{\r\n  \"username\": \"" + uid + "\",\r\n  \"password\": \"" + pwd + "\"\r\n}"
      response = http.request(request)
      # puts response.read_body
      result = JSON.parse(response.body)
      $authToken = result["auth"]["text"]
      # puts $authToken
    else
      $authToken = 'Bearer ' + pwd
      # puts $authToken
    end
  end
end

And (/^The logged in user TIN is memorized$/) do
  if $userObj != nil and $userObj[:tin] != nil
    JsonSpec.memorize("TIN", $userObj[:tin])
  end
end

And (/^The logged in user NPI is memorized$/) do
  if $userObj != nil and ($userObj.has_key? :npi) and $userObj[:npi] != nil
    JsonSpec.memorize("NPI", $userObj[:npi])
  else
    JsonSpec.memorize("NPI", nil)
  end
end

And (/^The logged in user EntityId is memorized$/) do
  if $userObj != nil and ($userObj.has_key? :entityId) and $userObj[:entityId] != nil
    JsonSpec.memorize("EntityId", $userObj[:entityId])
  else
    JsonSpec.memorize("EntityId", nil)
  end
end

def GET(endpoint = nil, param = nil, value = nil)
  if endpoint != nil and $userObj != nil
    endpoint.gsub!('{org}', $userObj[:org].to_s)
    endpoint.gsub!('{clinic}', $userObj[:clinic].to_s)
  end
  endpoint != nil ? url = URI($url + endpoint) : url = URI($url + '/')

  http = Net::HTTP.new(url.host, url.port)
  http.use_ssl = false
  if url.scheme == 'https'
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_PEER
  end

  request = Net::HTTP::Get.new(url)
  request["authorization"] = $authToken if $authToken != nil
  request["cookie"] = 'ACA=z3DUR2WH3Y' if $url.include? "imp"
  request["content-type"] = 'application/json'
  request["cache-control"] = 'no-cache'
  request[param] = value if param

  $response = http.request(request)
  puts $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body

  def last_json
    $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body
  end
end

def POST(endpoint = nil, file = nil, file_format = nil, param = nil, value = nil)
  if endpoint != nil and $userObj != nil
    endpoint.gsub!('{org}', $userObj[:org].to_s)
    endpoint.gsub!('{clinic}', $userObj[:clinic].to_s)
  end
  endpoint != nil ? url = URI($url + endpoint) : url = URI($url + '/')

  http = Net::HTTP.new(url.host, url.port)
  http.use_ssl = false
  if url.scheme == 'https'
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_PEER
  end

  request = Net::HTTP::Post.new(url)
  request["authorization"] = $authToken if $authToken != nil
  request["cookie"] = 'ACA=z3DUR2WH3Y' if $url.include? "imp"
  if file_format != nil
    request["content-type"] = "application/#{file_format.downcase}"
  else
    request["content-type"] = 'application/json'
  end
  request["cache-control"] = 'no-cache'

  if file_format != nil && (file_format.casecmp?("xml") || file_format.casecmp?("json") || file_format.casecmp?('txt'))
    dir = Pathname.new(__FILE__).join("..")
    dataToSend = IO.read("#{dir}/" + "Data Files/" + file)
    request.body = dataToSend
  else
    unless file.empty?
      begin
        hash = JSON.parse file
        if hash.has_key? 'organizationId'
          if hash.has_value? '{org}'
            hash["organizationId"] = $userObj[:org]
            file = hash.to_json
          end
        end
        request.body = file
      rescue
        if param && param == 'X-QPP-Team'
          begin
            tins = JSON.parse file
            tins.each_with_index do | tin , index|
              tins[index] = Aesstrict.decrypt(tin.tr('"','').chomp, ENV['QPP_AES_KEY'])
            end
            request.body = JSON.dump tins
          rescue JSON::ParserError
            request.body = Aesstrict.decrypt(file.chomp, ENV['QPP_AES_KEY'])
          end
        else
          request.body = file
        end
      end
    end
  end

  request[param] = value if param

  puts request.body

  $response = http.request(request)
  puts $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body

  def last_json
    $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body
  end
end

def PUT(endpoint, file, file_format = nil, param = nil, value = nil)
  if endpoint != nil and $userObj != nil
    endpoint.gsub!('{org}', $userObj[:org].to_s)
    endpoint.gsub!('{clinic}', $userObj[:clinic].to_s)
  end
  endpoint != nil ? url = URI($url + endpoint) : url = URI($url + '/')

  http = Net::HTTP.new(url.host, url.port)
  http.use_ssl = false
  if url.scheme == 'https'
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_PEER
  end

  request = Net::HTTP::Put.new(url)
  request["authorization"] = $authToken if $authToken != nil
  request["cookie"] = 'ACA=z3DUR2WH3Y' if $url.include? "imp"
  if file_format != nil
    request["content-type"] = "application/#{file_format.downcase}"
  else
    request["content-type"] = 'application/json'
  end
  request["cache-control"] = 'no-cache'

  if file_format != nil && file_format == "xml" || file_format == "json"
    dir = Pathname.new(__FILE__).join("..")
    dataToSend = IO.read ("#{dir}/" + "Data Files/" + file)
    request.body = dataToSend
  else
    unless file.empty?
      hash = JSON.parse file
      if hash.has_key? 'organizationId'
        if hash.has_value? '{org}'
          hash["organizationId"] = $userObj[:org]
          file = hash.to_json
        end
      end
    end
    request.body = file
  end

  request[param] = value if param != nil

  $response = http.request(request)
  puts $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body

  def last_json
    $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body
  end
end

def PATCH(endpoint, file, file_format = nil)
  if endpoint != nil and $userObj != nil
    endpoint.gsub!('{org}', $userObj[:org].to_s)
    endpoint.gsub!('{clinic}', $userObj[:clinic].to_s)
  end
  endpoint != nil ? url = URI($url + endpoint) : url = URI($url + '/')

  http = Net::HTTP.new(url.host, url.port)
  http.use_ssl = false
  if url.scheme == 'https'
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_PEER
  end

  request = Net::HTTP::Patch.new(url)
  request["authorization"] = $authToken if $authToken != nil
  request["cookie"] = 'ACA=z3DUR2WH3Y' if $url.include? "imp"
  if file_format != nil
    request["content-type"] = "application/#{file_format.downcase}"
  else
    request["content-type"] = 'application/json'
  end
  request["cache-control"] = 'no-cache'

  if file_format != nil
    dir = Pathname.new(__FILE__).join("..")
    dataToSend = IO.read ("#{dir}/" + "Data Files/" + file)
    request.body = dataToSend
  else
    unless file.empty?
      hash = JSON.parse file
      if hash.is_a?(Hash) && hash.has_key?('organizationId')
        if hash.has_value? '{org}'
          hash["organizationId"] = $userObj[:org]
          file = hash.to_json
        end
        # This condition currently does not support deeply nested lists within an array. Only singly nested lists.
      elsif hash.is_a?(Array) && hash[0].has_key?(:organizationId)
        if hash[0].has_value? '{org}'
          hash[0][:organizationId] = $userObj[:org]
          file = hash.to_json
        end
      end
    end
    request.body = file
  end

  $response = http.request(request)
  puts $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body

  def last_json
    $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body
  end
end

def DELETE(endpoint = nil, param = nil, value = nil)
  if endpoint != nil and $userObj != nil
    endpoint.gsub!('{org}', $userObj[:org].to_s)
    endpoint.gsub!('{clinic}', $userObj[:clinic].to_s)
  end
  endpoint != nil ? url = URI($url + endpoint) : url = URI($url + '/')

  http = Net::HTTP.new(url.host, url.port)
  http.use_ssl = false
  if url.scheme == 'https'
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_PEER
  end

  request = Net::HTTP::Delete.new(url)
  request["cookie"] = 'ACA=z3DUR2WH3Y' if $url.include? "imp"
  request["accept"] = 'application/vnd.qpp.cms.gov.v1+json'
  request["content-type"] = 'application/json'
  request["authorization"] = $authToken
  request["cache-control"] = 'no-cache'
  request[param] = value if param

  $response = http.request(request)
  puts $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body

  def last_json
    $response.read_body ? $response.read_body.force_encoding("UTF-8") : $response.read_body
  end
end

And(/^the JSON response has the correct maxPossPoints$/) do |table|
  table.hashes.each do |cat|
    measure = cat['mName']
    maxPossPoints = cat['maxPossPoints']
    response = JSON.parse($response.read_body)
    response["data"]["items"].each do |items|
      if items["measureName"] == measure
        expect(items["maximumPossiblePoints"]).to eq(maxPossPoints.to_i)
      end
    end
  end
end

And(/^the JSON response has decilePoints for each measure$/) do |table|
  table.hashes.each do |cat|
    measure = cat['mName']
    response = JSON.parse($response.read_body)
    response["data"]["items"].each do |items|
      if items["measureName"] == measure
        expect(items["decilePoints"]).not_to be_nil
      end
    end
  end
end

And(/^the JSON response has partialPoints for each measure$/) do |table|
  table.hashes.each do |row|
    measure = row['mName']
    response = JSON.parse($response.read_body)
    response["data"]["items"].each do |items|
      if items["measureName"] == measure
        items.key?("partialPoints")
      end
    end
  end
end

And(/the JSON response does not include (.*)$/) do |key|
  response = JSON.parse($response.read_body)
  response.extend Hashie::Extensions::DeepFind
  expect(response.deep_find(key)).to be_nil
end


And(/^the JSON response has a score for each measure$/) do |table|
  table.hashes.each do |cat|
    measure = cat['mName']
    response = JSON.parse($response.read_body)
    response["data"]["items"].each do |items|
      if items["measureName"] == measure
        expect(items["score"]).not_to be_nil
      end
    end
  end
end

And (/^User gets a clinic ID that is associated with beneficiaries$/) do
  GET("/api/submissions/web-interface/clinics/organization/{org}")
  response = JSON.parse($response.read_body)
  response["data"]["items"].each do |clinic|
    clinicID = clinic["id"].to_s
    GET("/api/submissions/web-interface/clinics/organization/{org}/clinic/" + clinicID + "/beneficiaries/count")
    countResponse = JSON.parse($response.read_body)
    count = countResponse["data"]["count"]
    if count > 0
      JsonSpec.memorize("ClinicID", clinicID)
      break
    end
  end
end

Then(/^User can verify the Excel spreadsheets at (.*) and (.*) are the same$/) do |file1, file2|
  dir = Pathname.new(__FILE__).join("..")
  file1 = Roo::Excelx.new("#{dir}/Data Files/" + file1)
  file2 = Roo::Excelx.new("#{dir}/Data Files/" + file2)
  failCnt = 0

  (1..file1.sheet(1).last_row).step do |row|
    (1..file1.sheet(1).last_column).step do |col|
      unless row == 554
        if file1.sheet(1).cell(row, col) != file2.sheet(1).cell(row, col)
          puts "Unlucky. Row #{row} Col #{col}: #{file1.sheet(1).cell(row, col)}
does not equal #{file2.sheet(1).cell(row, col)}"
          failCnt += 1
        end
      end
    end
  end
  assert(failCnt == 0, "#{failCnt} inequalities found.")
end

Then(/^User can verify the Excel spreadsheet at (.*) non benchmarks have blank deciles$/) do |sheet|
  dir = Pathname.new(__FILE__).join("..")
  sheet = Roo::Excelx.new("#{dir}/Data Files/" + sheet)

  (1..sheet.sheet(1).last_row).step do |row|
    if sheet.sheet(1).cell(row, 5) == 'N'
      (6..16).step do |col|
        assert(sheet.sheet(1).cell(row, col) == '--', "Inequality found. Row #{row} Col #{col} is #{sheet.sheet(1).cell(row, col)} and not --")
      end
    end
  end
end

Then(/^User can verify the Excel spreadsheet at (.*) has the HCQAR updated AQUA8 measure for 2018 benchmarks included$/) do |sheet|
  dir = Pathname.new(__FILE__).join("..")
  sheet = Roo::Excelx.new("#{dir}/Data Files/" + sheet)

  (1..sheet.sheet(1).last_row).step do |row|
    if sheet.sheet(1).cell(row, 2) == 'AQUA8'
      assert(sheet.sheet(1).cell(row, 5) == 'Y', "Inequality found. Row #{row} Col #{5} is #{sheet.sheet(1).cell(row, 5)} and not --")
      (6..12).step do |col|
        assert(sheet.sheet(1).cell(row, col) != '--', "Inequality found. Row #{row} Col #{col} is #{sheet.sheet(1).cell(row, col)} and not --")
      end
    end
  end
end

Then(/^User can verify the Excel spreadsheet at (.*) has the HCQAR updates for (.*) 2018 benchmarks included$/) do |sheet, measureId|
  dir = Pathname.new(__FILE__).join("..")
  sheet = Roo::Excelx.new("#{dir}/Data Files/" + sheet)

  (1..sheet.sheet(1).last_row).step do |row|
    if sheet.sheet(1).cell(row, 2) == measureId
      assert(sheet.sheet(1).cell(row, 5) == 'N', "Inequality found. Row #{row} Col #{5} is #{sheet.sheet(1).cell(row, 5)} and not --")
      (6..12).step do |col|
        assert(sheet.sheet(1).cell(row, col) == '--', "Inequality found. Row #{row} Col #{col} is #{sheet.sheet(1).cell(row, col)} and not --")
      end
    end
  end
end

def call_api(verb, endpoint, payload = nil, file_format = nil)
  case verb.upcase
  when "POST"
    POST(JsonSpec.remember(endpoint).tr('""', ''), payload, file_format)
  when "PATCH"
    PATCH(JsonSpec.remember(endpoint).tr('""', ''), payload, file_format)
  when "PUT"
    PUT(JsonSpec.remember(endpoint).tr('""', ''), payload, file_format)
  when "DELETE"
    DELETE(JsonSpec.remember(endpoint).tr('""', ''))
  when "MULTI_PART_POST"
    MULTI_PART_POST(JsonSpec.remember(endpoint).tr('""', ''), payload, "")
  when "GET"
    GET(JsonSpec.rememeber(endpoint).tr('""', ''))
  else
    puts "No case requirement met"
  end
end