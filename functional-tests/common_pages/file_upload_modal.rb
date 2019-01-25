class FileUploadModal < QPP_Page

  element(:upload_status, css: "div.modal-dialog h5.file-upload-notification-title")
  spans(:uploaded_files, css: "ul.file-upload-files span.file-upload-files-item-title")

  def get_upload_status
    upload_status_element.text
  end

  # file input feilds
  file_field(:file_uploader, class: 'xmlUpload')
  #
  # This method will browse the file to be uploaded
  # @param [String] file_name - file path
  # author : Sridhar Upputuri
  # date   : 10/22/2018

  def browse_file_to_upload(file_name)
    # verify if the file path is provided as part of file_name
    file_path = if File.exist?(file_name)
                  file_name
                else # get the complete path of the file location
                  Dir.glob(Dir.pwd + '/**/' + File.basename(file_name))[0]
                end
    upload_all_files_button_element.wait_until(&:present?)
    file_uploader_element.value = file_path
    self
  end

  # Buttons
  button(:file_upload_button, xpath: "//button[normalize-space(.)='Upload A File']")
  button(:upload_all_files_button, css: "button[aria-label='Upload Files']")
  button(:cancel_file_upload_button, css: "button[normalize-space(.)='Cancel File Upload Modal']")
  button(:download_report_button, class: 'file-upload-export-button')
  button(:view_submission_button, xpath: "//button[normalize-space(.)='View Submission']")
  button(:delete_file, css: "ul.file-upload-files button.file-upload-files-item-delete")
  button(:delete_file_button, css: "ul.file-upload-files button.file-upload-files-item-delete")

  #
  # This method will click on the delete_file button
  # author : Test16
  # date   : 10/24/2018

  def click_delete_file_button
    WebHelper.click(delete_file_button_element)
    return self
  end

  #
  # This method will click on the view_submission button
  # author : Test16
  # date   : 10/23/2018

  def click_view_submission_button
    view_submission_button_element.wait_until(&:present?)
    WebHelper.click(view_submission_button_element)
    return self
  end

  #
  # This method will click on the file_upload button
  # author : Test16
  # date   : 10/22/2018

  def click_file_upload_button
    WebHelper.click(file_upload_button_element)
    self
  end

  #
  # This method will click on the download_report button
  # author : Sridhar Upputuri
  # date   : 10/22/2018

  def click_download_report_button
    WebHelper.click(download_report_button_element)
    self
  end

  #
  # This method will click on the cancel_file_upload button
  # author : Sridhar Upputuri
  # date   : 10/22/2018

  def click_cancel_file_upload_button
    WebHelper.click(cancel_file_upload_button_element)
    self
  end

  #
  # This method will click on the upload_all_files button
  # author : Sridhar Upputuri
  # date   : 10/22/2018

  def click_upload_all_files_button
    WebHelper.click(upload_all_files_button_element)
    self
  end


  def verify_uploaded_file_name(file_name,expStatus)
    blnFileFound = false
    uploaded_files_elements.each do |uploaded_file_name|
      if uploaded_file_name.text == file_name
        blnFileFound = true
      end
    end
    Verifications.new(@browser).verify_text(expStatus.to_s,blnFileFound.to_s)
  end

end
