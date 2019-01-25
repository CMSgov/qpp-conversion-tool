When(/^User uploads submission file (.*) through modal$/) do |file_name|
  @file_upload_modal = FileUploadModal.new(@browser)
  @file_upload_modal.click_file_upload_button
                    .browse_file_to_upload(file_name)
                    .click_upload_all_files_button
end

And(/^User uploads submission file through modal$/) do |table|
  @file_upload_modal = FileUploadModal.new(@browser)
  @file_upload_modal.click_file_upload_button
  table.hashes.each do |uploadFile|
    @file_upload_modal.browse_file_to_upload(uploadFile["file_name"])
  end
  @file_upload_modal.click_upload_all_files_button
end

Then(/^User verifies Upload "([^"]*)" message is displayed$/) do |status|
  actual = FileUploadModal.new(@browser).get_upload_status
  @verifi = Verifications.new(@browser)
  if status.downcase == 'successful'
    @verifi.verify_text("Upload successful",actual)
  elsif  status.downcase == 'error'
    @verifi.verify_text('An Upload Error Occured', actual)
  end
end

And(/^User clicks on "([^"]*)" button in file upload modal$/) do |button_text|
  @file_upload_modal = FileUploadModal.new(@browser)
  case   button_text
  when "View Submission"
    @file_upload_modal.click_view_submission_button
    sleep 5
  end
end

And(/^User clicks "([^"]*)" link in file upload modal$/) do |linkName|
  @file_upload_modal = FileUploadModal.new(@browser)
  case linkName.downcase
  when 'download report'
    @file_upload_modal.click_download_report_button
  end
  FileUploadModal.new(@browser).click_download_report_button
end

And(/^User verifies (.*) is displayed under Files\(s\) Uploaded section$/) do |file_name|
  FileUploadModal.new(@browser).verify_uploaded_file_name(file_name,true)
end

When(/^User click the delete icon next to the file$/) do
  FileUploadModal.new(@browser).click_delete_file_button
end

Then(/^User verifies the (.*) file was deleted$/) do |file_name|
  FileUploadModal.new(@browser).verify_uploaded_file_name(file_name,false)
end

