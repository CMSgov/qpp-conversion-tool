Then(/^User verified "([^"]*)" measures uploaded in (.*) with appropriate score$/) do |group_name, file_name|
  @js = JSON_METHODS.new(@browser)
  case group_name.downcase
  when 'quality'
    @js.validate_quality_measures(file_name)
  when 'aci'
    @js.validate_aci_measures(file_name)
  when 'ia'
    @js.validate_ia_measures(file_name)
  when 'all'
    @js.validate_ia_measures(file_name)
    @js.validate_aci_measures(file_name)
    @js.validate_quality_measures(file_name)
  end
end

Then(/^User verifies measures uploaded in file with appropriate score$/) do |table|
  @js = JSON_METHODS.new(@browser)
  table.hashes.each do |grp|
    group_name = grp['group_name']
    case group_name.downcase
    when 'quality'
      @js.validate_quality_measures(grp['file_name'])
    when 'aci', 'pi'
      @js.validate_aci_measures(grp['file_name'])
    when 'ia'
      @js.validate_ia_measures(grp['file_name'])
    when 'all'
      @js.validate_ia_measures(grp['file_name'])
      @js.validate_aci_measures(grp['file_name'])
      @js.validate_quality_measures(grp['file_name'])
    end
  end
end

And(/^User verifies that uploaded files are listed on page that successfully uploaded$/) do |table|
  # table is a table.hashes.keys # => [:file_name]
  @js = JSON_METHODS.new(@browser)
  @verifi = Verifications.new(@browser)
  table.hashes.each do |json_file|
    tin = @js.get_matching_elements(json_file['file_name'], '$..taxpayerIdentificationNumber')[0]
    @verifi.verify_element_present(@browser.span(xpath: "//span[contains(text(),#{tin})]"))
  end
  # //span[contains(text(),000839449)]  - table tin
  #
end

And(/^User verifies upload successful by opening each upload listed$/) do |table|
  @js = JSON_METHODS.new(@browser)
  @verifi = Verifications.new(@browser)
  # click on expand all in the application
  if @browser.i(xpath: "//i[@class='fa expand-all-arrow fa-angle-right']").exists?
    @browser.i(xpath: "//i[@class='fa expand-all-arrow fa-angle-right']").fire_event :onclick
    @browser.divs(xpath: "//div[@class='expand-arrow nested']").each do |tin_expand|
      tin_expand.fire_event :onclick
    end
  end
  table.hashes.each do |file|
    # get tin
    tin = @js.get_matching_elements(file['file_name'], '$..taxpayerIdentificationNumber')[0]
    # get categories
    categories = @js.get_matching_elements(file['file_name'], '$..measurementSets')[0]
    categories.each do |category|
      # get category name
      case category['category']
      when 'ia'
        exp_cat = 'Improvement Activities'
      when 'aci'
        exp_cat = 'Advancing Care Information'
      when 'quality'
        exp_cat = 'Quality'
      end

      # get submission method
      case category['submissionMethod']
      when 'registry'
        sub_method = 'Registry'
      when 'ehr'
        sub_method = 'EHR'
      end
      # get actual values from application
      act_sub_methods = @browser.span(xpath: "//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//div[@class='panel card category-row-heading #{category['category']} open']//p[text()='SUBMISSION METHOD']/following-sibling::span").text
      act_num_measures = @browser.span(xpath: "//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//div[@class='panel card category-row-heading #{category['category']} open']//p[text()='MEASURES SUBMITTED']/following-sibling::span").text
      act_cat_score = @browser.div(xpath: "//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//div[@class='panel card category-row-heading #{category['category']} open']//p[text()='CATEGORY SCORE']/following-sibling::div").text
      # verify the values
      @verifi.verify_element_present(@browser.span(xpath: "//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//p[text()='CATEGORY']/following-sibling::span[text()='#{exp_cat}']"))
      @verifi.verify_text(sub_method, act_sub_methods)
      @verifi.verify_text(category['measurements'].length.to_s, act_num_measures)

      case category['category']
      when 'ia'
        category['measurements'].each do |measure|
          # puts '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
          # use the below xpath to get the measure attestation value (for now using the hard coded column numbers)
          # @browser.cell(:xpath=>"//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//td[count(//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//div[@class='desktop-view table-responsive']//th[.='ATTESTATION'])+1]")
          button = measure['value'].to_s == 'true' ? 'YES' : 'NO'
          act_attestation = @browser.td(xpath: "//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//td[text()='#{measure['measureId']}']/parent::tr/td[3]").text
          act_contribution_2_cat_score = @browser.td(xpath: "//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//td[text()='#{measure['measureId']}']/parent::tr/td[5]").text
          @verifi.verify_text(button, act_attestation)
          # @verifi.verify_text("40", act_contribution_2_cat_score)
          # puts "TIN :" + tin + "\n Category:" + exp_cat + "\n SubmissionType:" + sub_method + "\n Measure:" + measure['measureId'] + "\n Attestation :" + button
        end
      when 'aci'
        category['measurements'].each do |measure|
          # puts '!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!'
          act_submission = @browser.td(xpath: "//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//div[@class='panel card category-row-heading aci open']//td[.='#{measure['measureId']}']/parent::tr/td[3]").text
          if !(measure['value'].to_s == 'true' || measure['value'].to_s == 'false')
            exp_submisstion = measure['value']['numerator'].to_s + ' / ' + measure['value']['denominator'].to_s
            @verifi.verify_text(exp_submisstion, act_submission)
            # puts "TIN :" + tin + "\n Category:" + exp_cat + "\n SubmissionType:" + sub_method + "\n Measure:" + measure['measureId'] + "\n Submission :" + act_submission
          else
            button = measure['value'].to_s == 'true' ? 'YES' : 'NO'
            @verifi.verify_text(button, act_submission)
            # puts "TIN :" + tin + "\n Category:" + exp_cat + "\n SubmissionType:" + sub_method + "\n Measure:" + measure['measureId'] + "\n Attestation :" + button
          end
        end
      end
    end
  end
end

And(/^User verifies the category scores$/) do |table|
  table.hashes.each do |cat|
    tin = cat['tin']
    if tin.chars.last(5).join == '.json'
      @json_methods = JSON_METHODS.new(@browser)
      data = @json_methods.read_json_file(tin)
      temp_data = JSON.parse(data)
      tin = temp_data['taxpayerIdentificationNumber']
    end
    category = cat['category_name']
    cat_score = cat['category_score']
    # puts "Tin:" + tin + "\n category:" + category + "\n category Score:" + cat_score
    @verifi = Verifications.new(@browser)
    act_cat_score = @browser.span(xpath: "//span[starts-with(.,'#{tin}')]/ancestor::div[@class='panel card submission-row-heading open']//div[@class='panel card category-row-heading #{category} open']//p[text()='CATEGORY SCORE']//following-sibling::div/span").text
    @verifi.verify_text(cat_score, act_cat_score)
  end
end

When(/^User update the tin in json files$/) do |table|
  # table is a table.hashes.keys # => [:file_name]
  @json_methods = JSON_METHODS.new(@browser)
  table.hashes.each do |item|
    data = @json_methods.read_json_file(item['file_name'])
    temp_data = JSON.parse(data)
    puts 'old_tin :' + temp_data['taxpayerIdentificationNumber']
    temp_data['taxpayerIdentificationNumber'] = $userObj[:tin]
    puts 'new_tin:' + temp_data['taxpayerIdentificationNumber']
    if ($userObj[:npi] == '') || !$userObj[:npi].nil?
      temp_data['nationalProviderIdentifier'] = $userObj[:npi]
      temp_data['entityType'] = 'individual'
    else
      temp_data['entityType'] = 'group'
      temp_data.delete('nationalProviderIdentifier')
    end
    temp_file_path = Dir.pwd + '/data/json_files/' + item['file_name']
    File.open(temp_file_path, 'w') do |f|
      f.puts JSON.pretty_generate(temp_data)
    end
  end
end

When(/^User update the tin in (.*) json file$/) do |file_name|
  # table is a table.hashes.keys # => [:file_name]
  @json_methods = JSON_METHODS.new(@browser)
  data = @json_methods.read_json_file(file_name)
  temp_data = JSON.parse(data)
  if !temp_data['submission'].nil?
       puts 'old_tin :' + temp_data['submission']['taxpayerIdentificationNumber']
    temp_data['submission']['taxpayerIdentificationNumber'] = $userObj[:tin]
    puts 'new_tin:' + temp_data['submission']['taxpayerIdentificationNumber']
    if ($userObj[:npi] == '') || !$userObj[:npi].nil?
      temp_data['submission']['nationalProviderIdentifier'] = $userObj[:npi]
      temp_data['submission']['entityType'] = 'individual'
    else
      temp_data['submission']['entityType'] = 'group'
      temp_data['submission'].delete('nationalProviderIdentifier')
    end
  else
    puts 'old_tin :' + temp_data['taxpayerIdentificationNumber']
    temp_data['taxpayerIdentificationNumber'] = $userObj[:tin]
    puts 'new_tin:' + temp_data['taxpayerIdentificationNumber']
    # update the NPI if it's individual user
    if ($userObj[:npi] == '') || !$userObj[:npi].nil?
      temp_data['nationalProviderIdentifier'] = $userObj[:npi]
      temp_data['entityType'] = 'individual'
    else
      temp_data['entityType'] = 'group'
      temp_data.delete('nationalProviderIdentifier')
    end
  end
  temp_file_path = if File.exist?(file_name)
                     file_name
                   else # get the complete path of the file location
                     Dir.glob(Dir.pwd + '/**/' + File.basename(file_name))[0]
                   end

  File.open(temp_file_path, 'w') do |f|
    f.puts JSON.pretty_generate(temp_data)
  end
end

When(/^User update the tin in json files for other user$/) do |table|
  # table is a table.hashes.keys # => [:file_name,:role]
  @json_methods = JSON_METHODS.new(@browser)
  table.hashes.each do |item|
    data = @json_methods.read_json_file(item['file_name'])
    temp_data = JSON.parse(data)
    puts 'old_tin :' + temp_data['taxpayerIdentificationNumber']
    temp_user_data = UserUtil.get_user('QPPWI', item['role'])
    temp_data['taxpayerIdentificationNumber'] = temp_user_data['tin'.to_sym]
    puts 'new_tin:' + temp_data['taxpayerIdentificationNumber']
    if (temp_user_data['npi'] == '') || !temp_user_data['npi'].nil?
      temp_data['nationalProviderIdentifier'] = temp_user_data['npi'.to_sym]
    end
    #temp_file_path = Dir.pwd + '/data/json_files/' + item['file_name']
    temp_file_path = if File.exist?(file_name)
                       file_name
                     else # get the complete path of the file location
                       Dir.glob(Dir.pwd + '/**/' + File.basename(file_name))[0]
                     end
    File.open(temp_file_path, 'w') do |f|
      f.puts JSON.pretty_generate(temp_data)
    end
  end
end

# When(/^User update the tin in (.*) json file for "(.*)" user$/) do |file_name, role|
#   # table is a table.hashes.keys # => [:file_name]
#   data = UpdateUserInfo.new().read_json_file(file_name)
#   temp_data = JSON.parse(data)
#   #puts 'old_tin :' + temp_data['taxpayerIdentificationNumber']
#   puts 'old tin :' + JsonPath.new("$..taxpayerIdentificationNumber").on(temp_data)[0]
#   temp_user_data = UserUtil.get_user_no_lock('QPPWI', role)
#   JsonPath.new("$..taxpayerIdentificationNumber").on(temp_data)[0] = temp_user_data['tin']
#   puts 'new_tin:' + JsonPath.new("$..taxpayerIdentificationNumber").on(temp_data)[0]
#   if (JsonPath.new("$..nationalProviderIdentifier").on(temp_data)[0] == '') || !temp_user_data['npi'].nil?
#     JsonPath.new("$..nationalProviderIdentifier").on(temp_data)[0] = temp_user_data['npi']
#   end
#
#   if (temp_user_data[:npi] == '') || !temp_user_data[:npi].nil?
#     JsonPath.new("$..nationalProviderIdentifier").on(temp_data)[0] = temp_user_data[:npi]
#     JsonPath.new("$..entityType").on(temp_data)[0] = "individual"
#
#   else
#     JsonPath.new("$..entityType").on(temp_data)[0] = "group"
#     temp_data.delete('nationalProviderIdentifier')
#   end
#
#   temp_file_path = Dir.glob(Dir.pwd + '/**/' + File.basename(file_name))[0]
#   File.open(temp_file_path, 'w') do |f|
#     f.puts JSON.pretty_generate(temp_data)
#   end
# end

When(/^User update the tin in (.*) json file for "(.*)" user$/) do |file_name, role|
  data = UpdateUserInfo.new().read_json_file(file_name)
  temp_data = JSON.parse(data)
  temp_user_data = UserUtil.get_user_no_lock('QPPWI', role)

  if !temp_data['submission'].nil?
    puts 'old_tin :' + temp_data['submission']['taxpayerIdentificationNumber']
    temp_data['submission']['taxpayerIdentificationNumber'] = temp_user_data[:tin]
    puts 'new_tin:' + temp_data['submission']['taxpayerIdentificationNumber']
    if (temp_user_data[:npi] == '') || !temp_user_data[:npi].nil?
      temp_data['submission']['nationalProviderIdentifier'] = temp_user_data[:npi]
      temp_data['submission']['entityType'] = 'individual'
    else
      temp_data['submission']['entityType'] = 'group'
      temp_data['submission'].delete('nationalProviderIdentifier')
    end
  else
    puts 'old_tin :' + temp_data['taxpayerIdentificationNumber']
    temp_data['taxpayerIdentificationNumber'] = temp_user_data[:tin]
    puts 'new_tin:' + temp_data['taxpayerIdentificationNumber']
    # update the NPI if it's individual user
    if (temp_user_data[:npi] == '') || !temp_user_data[:npi].nil?
      temp_data['nationalProviderIdentifier'] = temp_user_data[:npi]
      temp_data['entityType'] = 'individual'
    else
      temp_data['entityType'] = 'group'
      temp_data.delete('nationalProviderIdentifier')
    end
  end
  temp_file_path = Dir.glob(Dir.pwd + '/**/' + File.basename(file_name))[0]
  File.open(temp_file_path, 'w') do |f|
    f.puts JSON.pretty_generate(temp_data)
  end

end

And(/^User verified end2end bonus displays for measures uploaded in (.*) file$/) do |file_name|
  @json_methods = JSON_METHODS.new(@browser)
  @js.validate_e2e_bonus_points_for_measures(file_name)
end
