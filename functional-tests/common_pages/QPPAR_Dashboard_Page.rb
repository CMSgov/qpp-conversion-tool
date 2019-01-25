# This comment should describe what page or page section the class represents in the application
require_relative 'qpp_page'
class QPPAR_Dashboard_Page < QPP_Page
  include PageObject

  # Interactive web elements associated with this page
  # For more details see, https://github.com/cheezy/page-object/wiki/Elements

  element(:file_upload, xpath: "//input[@id='form-control-input']")
  button(:upload_button, xpath: "//button[text()='upload']")
  div(:impl_verification, xpath: "//div[text()='IMPL_verification']")
  button(:file_download_button, xpath: "//button[text()='test.txt']")

  element(:file_list, xpath:"//table[@class='table']/tbody/tr")


  button(:file_delete_button, xpath: "//button[text()='test.txt']/../../../td[7]/div/button")

  button(:delete_button, xpath: "//button[@id='delete-button']")

  div(:upload_success, xpath: "//*[@id='upload-success']")

  button(:user_logout, xpath: "//button[@class='current-user dropdown-toggle btn btn-link']")
  button(:logout_button, xpath: "//button[text()='logout']")
  link(:logout_goBack_link, xpath: "(//a[@href='/'])[2]")
  element(:submission_report_text ,xpath: "//p[text()='This report provides an overview of submissions volume, submissions over time, and submissions composition. The metrics provided in this report reflect total submission transactions for the purpose of monitoring system performance during the QPP submissions window. Note: Total Unique Submissions provides information on submissions unique to TIN/NPI participants.']")

  element(:hamburger_menu_open, xpath: "(//*[local-name() = 'svg'])[1]")

  button(:open_workbench_button, xpath: "//button[text()='OPEN WORKBENCH']")

  link(:workbench_return_link, xpath: "//a[@class='navbar-brand']")

  element(:workbench_nav_bar, xpath: "//ul[@class='nav navbar-nav']")

  div(:mips_eligible, xpath: "//div[text()='Are MIPS eligible']/../span")

  link(:file_manager, xpath: "//a[@href='/ar/filemanagement']")

  link(:clinician_lookup_tool_link, xpath: "//a[@href='/ar/clinicianlookup']")

  button(:upload_IMPLverification, xpath: "//div[text()='IMPL_verification']/../..//BUTTON[@type='button'][text()='Upload'][text()='Upload']")

  span(:x_out, xpath: "//span[text()='×']")

  # Interactive methods used to hide implementation details
  # http://www.rubydoc.info/github/cheezy/page-object/PageObject
  #--------------------------------------------------//*[@class='title']-------------------------------------
  #   Method implementations
  #---------------------------------------------------------------------------------------

  def click_impl_verification_file_list
    impl_verification_element.wait_until(&:present?).fire_event :onclick
  end

  def click_x_out
    x_out_element.wait_until(&:present?).fire_event :onclick
  end

  def click_file_delete_button
    file_delete_button_element.wait_until(&:present?).fire_event :onclick
  end

  def click_file_delete_confirmation
    delete_button_element.wait_until(&:present?).fire_event :onclick
  end

  def check_file_list

    file_list_array = Array["meeret.pdf", "test.txt"]

    newArray = file_list_array.sort

    if file_list_array == newArray
      return true
    else
      return false
    end

  end


  def get_file_download_button
    sleep 5
    return file_download_button_element
  end

  def click_file_download_button
    file_download_button_element.wait_until(&:present?).fire_event :onclick
  end

  def get_workbench_nav_bar
    return workbench_nav_bar_element
  end

  def click_upload_IMPLverification
    upload_IMPLverification_element.wait_until(&:present?).fire_event :onclick
  end

  def upload_file
    @file_name = "D:\\test.txt"
    file_upload_element.wait_until(&:present?).send_keys @file_name
    upload_button_element.wait_until(&:present?).fire_event :onclick
  end

  def upload_second_file
    @file_name = "D:\\meeret.pdf"
    file_upload_element.wait_until(&:present?).send_keys @file_name
    upload_button_element.wait_until(&:present?).fire_event :onclick

  end

  def get_upload_success_div
    upload_success_element.wait_until(&:present?)
  end

  def click_file_manager
    file_manager_element.wait_until(&:present?).fire_event :onclick
  end

  def get_file_manager
    file_manager_element.wait_until(&:present?)
  end

  def get_hamburger_open_element
    return hamburger_menu_open
  end

  def click_logout_goBack_link
    logout_goBack_link_element.wait_until(&:present?).fire_event :onclick
  end

  def get_mips_eligible_text
    mips_eligible_element.wait_until(&:present?).text
  end


  def click_return_to_report
    workbench_return_link_element.wait_until(&:present?).fire_event :onclick
  end

  def click_open_workbench_button
    open_workbench_button_element.wait_until(&:present?).fire_event :onclick
  end

  def open_hamburger_menu
    hamburger_menu_open_element.wait_until(&:present?).fire_event :onclick
  end

  def get_submission_report
    return submission_report_text_element
  end

  def click_user_logout
    #logout_goBack_link_element.wait_until(&:present?).fire_event :onclick
    user_logout_element.wait_until(&:present?).fire_event :onclick
    logout_button_element.wait_until(&:present?).fire_event :onclick
  end

  def click_clinician_lookup_tool
    clinician_lookup_tool_link_element.wait_until(&:present?).fire_event :onclick
  end


end
