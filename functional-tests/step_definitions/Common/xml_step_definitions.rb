

And(/^User update the details in (.*)xml file$/) do |file_name|
  XmlMethods.new().update_user_details(file_name)
end