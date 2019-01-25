
class FileUpload < QPP_Page
  include PageObject
  button(:file_upload, xpath: "//button[@aria-label='File Upload']")
  file_field(:file_uploader, id: 'xmlUpload')
  button(:upload_all, xpath: "//button[@class='btn btn-success btn-s upload-all']")
  button(:upload_complete,xpath:"//button[@class='btn btn-success btn-s upload-all disabled']")
  button(:show_details, xpath: "//buton[@class='error-status']")
  li(:error_text, xpath: "//li[@class='error-text']")
  button(:export_file_messages,xpath:"//button[@class='btn btn-default btn-s export-all-messages']")
  button(:close,xpath:"//button[@aria-label='Close File Upload Modal' and text()='Close']")
  div(:upload_pending,xpath:"(//div[@class='uploading-status'])[1]")
  buttons(:upload_failed,xpath:"//button[@class='error-status']")

  #l
  # This method will click on the "File Upload" button.
  #
  def click_file_upload
    #file_upload_element.flash
    #file_upload_element.when_present(30).flash
    file_upload_element.when_present(30).fire_event :onclick
    close_element.when_present(30)
  end

  # @param [String] file_name name of the file to be uploaded
  # Note: Do not provide the complete file path. This method requires only file name
  def browse_file_to_upload(file_name)
    # need to implement handling absolute and dyn paths
    # if file_name.split('/').last
    #puts file_name.chars.last(5).join
    if file_name.chars.last(5).join== '.json'
      temp_file_path = Dir.pwd + '/data/json_files/' + file_name
      #puts temp_file_path
    end
    if file_name.chars.last(4).join== '.xml'
      temp_file_path = Dir.pwd + '/data/xml_files/' + file_name
      puts temp_file_path
    end
    sleep 1
    #puts temp_file_path
    # field = @browser.driver.find_element(:id, 'xmlUpload')
    # field.send_keys 'D:\\Users\\test6\\Documents\\GitHub\\qpp-hivvs-test\\data\\json_files\\C152827_Valid_Quality.json'#temp_file_path
    file_uploader_element.value= temp_file_path

  end

  #
  # This method will click on the "Upload All" button on file upload window
  #
  def click_on_upload_all
    ele = upload_all_element
    ele.wait_until(&:present?).scroll.to :bottom
    ele.fire_event :onclick
    sleep 1
    @browser.span(xpath: "//div[normalize-space(.)='Uploading...']").wait_while_present(120)
    @browser.button(xpath: "//button[@class='btn btn-danger btn-s remove-all']").wait_until(&:present?)
    upload_all_element.wait_while_present(120)
  end

  #
  # This method will click on the show details in the file upload window
  #
  def click_on_show_details
    show_details
  end

  #
  # This method will return the error text
  #
  def get_error_text
    error_text.text
  end

  def close_file_upload
    close_element.fire_event :onclick
    sleep 2
  end

  def show_upload_failed_details
    failed_files = upload_failed_elements
    failed_files.each do |failed_file|
      failed_file.fire_event :onclick
    end
  end

end
