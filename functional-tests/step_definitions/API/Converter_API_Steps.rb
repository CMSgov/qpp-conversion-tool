require 'json'
require 'jsonpath'

#MultiPart Post for QPPCT Tests -- Accpets API enpoint, the file path for the file to be submitted and file name (can be any string) as parameters
def MULTI_PART_POST(endpoint=nil, file_path, file_name)
  dir = Pathname.new(__FILE__).join("..")
  endpoint != nil ? url = URI($url + endpoint) : url = URI($url + '/')

  http = Net::HTTP.new(url.host, url.port)
  http.use_ssl = false
  if url.scheme == 'https'
    http.use_ssl = true
    http.verify_mode = OpenSSL::SSL::VERIFY_PEER
  end
  request = Net::HTTP::Post::Multipart.new(url.path,{'file' => UploadIO.new("#{dir}/" + "Data Files/Integration/QPPCT/" + file_path,"application.xml", file_name)})

  request["authorization"] = $authToken if $authToken != nil
  request["cookie"] = 'ACA=z3DUR2WH3Y' if $url.include? "imp"
  request["cache-control"] = 'no-cache'
  request["Purpose"] = 'HIVVS'
  request["Accept"] = 'application/vnd.qpp.cms.gov.v2+json'
  $response = http.request(request)
  # puts $response.read_body
  puts $response.read_body

  def last_json
    $response.read_body
  end
end

#(.*?) depicts a quantifier that is not greedy, meaning it will not try to capture the third parameter (if it exists).
When(/^User makes a Multipart POST request(?: to (.*))? with (.*?)(?: and (.*))?$/) do |endpoint, file_path, file_name|
  file_name == nil ? MULTI_PART_POST(endpoint, file_path, "") : MULTI_PART_POST(endpoint, file_path, file_name)
end

When(/^User loads the (.*) measures-data.json file$/) do |year |
  # Make GET request to measures data
  $measures = HTTParty.get("https://raw.githubusercontent.com/CMSgov/qpp-measures-data/master/measures/#{year}/measures-data.json", headers: {:Accept => 'application/json'})
  $measures = JSON.parse($measures.read_body)
end

Then(/^User generates an examples table using the (.*) of the response$/) do |key|
  JsonPath.new("$..#{key}").on(JSON.parse($response.read_body)).each do |value|
    print "| #{value} |\n"
  end
end

Then(/^User can verify all eMeasureUuids for the (.*) performance year are valid$/) do |year|
  begin
    $measures = JSON.parse($measures.read_body)
  rescue
    step "User accesses the #{year} measures data JSON in Github"
    $measures = JSON.parse($measures.read_body)
  end

  # Get the lines of the latest valid QRDA III file.
  if year == 2017
    qrda_file  = File.open('features/step_definitions/API/Data Files/Integration/QPPCT/valid-QRDA-III-latest.xml')
  else
    qrda_file  = File.open('features/step_definitions/API/Data Files/Integration/QPPCT/2018/valid-QRDA-III-latest.xml')
  end
  qrda_lines = File.readlines(qrda_file)

  # Open a temporary QRDA III file.
  File.open('features/step_definitions/API/Data Files/Integration/QPPCT/valid-QRDA-III-latest_UUID-Temp.xml', 'w') { |f| f.write(qrda_lines.join) }

  # Store the components used to build an entry. These will be edited and inserted into the temporary file.
  ipop     = File.readlines(File.open('features/step_definitions/API/Data Files/Integration/QPPCT/Uuid_Validation/ipop_component.xml'))
  denex    = File.readlines(File.open('features/step_definitions/API/Data Files/Integration/QPPCT/Uuid_Validation/denex_component.xml'))
  numer    = File.readlines(File.open('features/step_definitions/API/Data Files/Integration/QPPCT/Uuid_Validation/numer_component.xml'))
  denom    = File.readlines(File.open('features/step_definitions/API/Data Files/Integration/QPPCT/Uuid_Validation/denom_component.xml'))
  strat    = File.readlines(File.open('features/step_definitions/API/Data Files/Integration/QPPCT/Uuid_Validation/stratifier_component.xml'))
  denexcep = File.readlines(File.open('features/step_definitions/API/Data Files/Integration/QPPCT/Uuid_Validation/denexcep_component.xml'))
  @components = [ipop, denom, numer, denex, strat, denexcep]

  # Special case: Store the stratifier. Sometimes they exist within the eMeasureUuids and exist as an extra array.
  @stratifier = File.readlines('features/step_definitions/API/Data Files/Integration/QPPCT/Uuid_Validation/stratifier.xml')

  # Iterate over each measure in the measures-data json and POST any entry not in the valid QRDA III file.
  $measures.each do |msr |
    # Skip to next measure if eMeasureUuid is already in the valid QRDA III file
    next if File.foreach(qrda_file).grep(/#{msr['eMeasureUuid']}/).count > 0

    # Cut out the previous entry from the temp file.
    qrda_lines.slice!(751..2642)

    # Replace line 724 of temp file with "eMeasureUuid" and 744 (performanceRate) with "numeratorUuid".
    qrda_lines[723] = "extension=\"#{msr['eMeasureUuid']}\"/>" << $/
    qrda_lines[743] = "<id root=\"#{msr['strata'][0]['eMeasureUuids']['numeratorUuid']}\"/>" << $/

    # Iterate over each component ("eMeasureUuids") and build entry to POST
    entry = []
    msr['strata'].each do |strata |
      strata['eMeasureUuids'].each_with_index do |uuid, i |
        # Build the entry using the eMeasureUuids component
        entry.push(build_ct_entry(uuid, i))
      end
    end

    # Insert the entry into the temp file.
    qrda_lines.insert(751, entry)
    File.open('features/step_definitions/API/Data Files/Integration/QPPCT/valid-QRDA-III-latest_UUID-Temp.xml', 'w') { |f| f.write(qrda_lines.join) }

    # Multipart POST the temp file and check for 201 response code.
    step "User makes a Multipart POST request with valid-QRDA-III-latest_UUID-Temp.xml and \"\""
    step "User receives 201 response code"

    # Rewrite the temp file back to the original valid QRDA III file for next iteration.
    qrda_lines = File.readlines(qrda_file)
  end
end

def build_ct_entry(uuid, i)
  if uuid[0] == 'denominatorExclusionUuid'
    component = @components[3].clone
    component[component.count-5] = "<id root=\"#{uuid[1]}\"/>" << $/
  elsif uuid[0] == 'strata'
    component = @components[4].clone
    uuid[1].each do |id|
      temp_strat = @stratifier.clone
      temp_strat[temp_strat.count-5] = "<id root=\"#{id}\"/>" << $/

      component.insert(31, temp_strat)
    end
  elsif uuid[0] == 'denominatorExceptionUuid'
    component = @components[5].clone
    component[component.count-5] = "<id root=\"#{uuid[1]}\"/>" << $/
  else
    component = @components[i].clone
    component[component.count-5] = "<id root=\"#{uuid[1]}\"/>" << $/
  end
  component
end

$path = Pathname.new(__FILE__).join("..")

# step definition for importing a list off data and reading it
When(/^the User imports a list from (.*)/) do |file_path|
  $List = File.read("#{$path}/" + "Data Files/" + file_path)
end

When(/^the User imports a JSON list from the website (.*)/) do |webAddress|
  response = HTTParty.get(webAddress, headers: {:Accept => 'application/json'})
  $List = JSON.parse(response.read_body)
end

# step definition for replacing a value in a pre-determined file from an existing list
And(/^User replaces the (.*) in (.*) with (.*)/) do | value, file_path, replace|
  temp_path = "#{$path}/Data Files/Integration/QPPCT/2017/temp.xml"
  FileUtils.copy_file(("#{$path}/" + "Data Files/" + file_path), temp_path)
  File.write(temp_path, File.read("#{$path}/" + "Data Files/" + file_path).sub(value, replace))
end

And(/^User can verify the unprocessed files' submission sequence is ordered by date$/) do
  unprocessed_files = JSON.parse($response.body)
  unprocessed_files.each_with_index do |unprocessed_file, index|
    # puts "#{DateTime.parse(unprocessed_file["conversionDate"])} VERSUS #{DateTime.parse(unprocessed_files[index + 1]["conversionDate"])}}" unless index == unprocessed_files.size - 1
    assert(DateTime.parse(unprocessed_file["conversionDate"]) < DateTime.parse(unprocessed_files[index + 1]["conversionDate"]), "#{DateTime.parse(unprocessed_file["conversionDate"])} is later than #{DateTime.parse(unprocessed_files[index + 1]["conversionDate"])}}") unless index == unprocessed_files.size - 1
  end
end