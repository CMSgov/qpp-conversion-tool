

When(/^User uploads file (.*)$/) do |file_name|
  @file_upload = FileUpload.new(@browser)
  @file_upload.click_file_upload
  @file_upload.browse_file_to_upload(file_name)
  @file_upload.click_on_upload_all
end

# this step will click on the file upload button
#   and uploads all the files specified in the step
#
# multiple file names can be provided in the step as data table

When(/^User uploads file$/) do |table|
  @file_upload = FileUpload.new(@browser)
  # Click on file upload button
  @file_upload.click_file_upload
  # get the files from step
  files = table.hashes
  # upload each file
  files.each do |file_data|
    @file_upload.browse_file_to_upload(file_data['file_name'])
  end
  # click on upload all button
  @file_upload.click_on_upload_all
end

When(/^User selects close button at the bottom of the Upload Submission Data page$/) do
  @file_upload = FileUpload.new(@browser)
   #sleep 10
  @file_upload.close_file_upload
   #??? comment the below line once the page refresh after upload issue is fixed
  #step 'User click Refresh button' #fixed as part of QPPSF-2705
end

Then(/^Files that were uploaded are marked with upload status$/) do |table|
  # table is a table.hashes.keys # => [:file_name, :upload_status]
  @file_upload = FileUpload.new(@browser)
  @verifi = Verifications.new(@browser)
  files = table.hashes
  sleep 5
  files.each do |file_data|
    actual_status = @browser.li(xpath: "//span[text()='#{file_data['file_name']}']/parent::li[starts-with(@class,'file-upload-files-item')]").wait_until(&:present?).attribute_value('class')
    #puts actual_status
    if file_data['upload_status'].downcase == 'failed' || file_data['upload_status'].downcase == 'upload failed' || file_data['upload_status'].downcase == 'fail'
      #@verifi.verify_text('Upload Failed', actual_status)
      @verifi.verify_text("true",(actual_status.include? "error").to_s)
    elsif file_data['upload_status'].downcase == 'complete' || file_data['upload_status'].downcase == 'upload successful' || file_data['upload_status'].downcase == 'success'
      @verifi.verify_text("true",(actual_status.include? "success").to_s)
    end
  end
end

When(/^User clicks on arrow after the words Upload failed$/) do
  @file_upload = FileUpload.new(@browser)
  @file_upload.show_upload_failed_details
end

Then(/^Drop down information with reason why file failed upload is displayed$/) do |table|
  @file_upload = FileUpload.new(@browser)
  @verifi = Verifications.new(@browser)
  files = table.hashes
  files.each do |file_data|
    actual_status = @browser.li(xpath: "//span[starts-with(text(),'#{file_data['file_name']}')]/ancestor::div[starts-with(@class,'table-row upload')]//li[@class='error-text']").text
    #puts actual_status
    expected_result = file_data['error_reason']
    @verifi.verify_text(expected_result, actual_status)
  end
end

#This is for Registry users only
When(/^User delete all submissions$/) do
  if @browser.button(xpath: "//button[@aria-label='Delete submission data']").exists?
    @browser.button(xpath: "//button[@aria-label='Delete submission data']").fire_event :onclick
    @browser.button(xpath: "//button[@aria-label='Delete all submission data on page']").fire_event :onclick
    @browser.button(xpath: "//button[@aria-label='Yes, delete this submission data']").wait_until(timeout:30, &:present?).fire_event :onclick
  end
  @browser.button(xpath: "//button[@aria-label='Yes, delete this submission data']").wait_while(timeout:180, &:present?)
end


And(/^User expands all the uploaded tins$/) do
  sleep 2
  if @browser.i(xpath: "//i[@class='fa expand-all-arrow fa-angle-right']").exists?
    @browser.i(xpath: "//i[@class='fa expand-all-arrow fa-angle-right']").fire_event :onclick
    @browser.divs(xpath: "//div[@class='expand-arrow nested']").each do |tin_expand|
      tin_expand.fire_event :onclick
    end
  end
end

When(/^User selects invalid|valid file (.*)$/) do |file_name|
  @file_upload_modal = FileUploadModal.new(@browser)
  @file_upload_modal.browse_file_to_upload(file_name)
end

When(/^User click on File Upload button$/) do
  @file_upload_modal = FileUploadModal.new(@browser)
  @file_upload_modal.click_file_upload_button
end

And(/^User selects Upload All$/) do
  @file_upload = FileUpload.new(@browser)
  @file_upload.click_on_upload_all
end
