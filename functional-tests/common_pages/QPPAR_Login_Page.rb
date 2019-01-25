require 'rspec'

class QPPAR_Login_Page < QPP_Page
  include Test::Unit::Assertions
#---------------------------------------------------------------------------------------
#

  text_field(:username_field, :name => 'username')
  text_field(:password_field, :name => 'password')
  button(:submit_button, :xpath => "//button[text()='SUBMIT']")

  div(:header_quality_text, :xpath => "//div[@class='quality-text']")

  element(:warning_text, :xpath => "//h4[text()='FOR INTERNAL CMS USE ONLY']")

  div(:show_password, :xpath => "//*[@id='root']/div/div/div[2]/div[2]/form/div[2]")

  label(:job_code, :xpath => "//label[@class='job-code']")

  p(:disclaimer_title, :xpath => "//p[@class='disclaimer-title']")
  p(:disclaimer_text, :xpath => "//p[@class='disclaimer-text']")

  div(:mid_section_text_1, :xpath => "//*[@id='root']/div/div/div[3]/div[2]")
  div(:mid_section_text_2, :xpath => "//*[@id='root']/div/div/div[3]/div[3]")

  element(:hhs_logo, :xpath => "//img[@alt='Center for Medicaid & Medicare Services']")

  element(:qpp_logo, :xpath => "//img[@alt='qpp logo']")

  link(:privacy_notice, :xpath => "//a[text()='CMS Privacy Notice']")

  link(:accessibility_link, :xpath => "//a[text()='Accessibility']")


  def get_accessibility_link
    return accessibility_link_element
  end

  def get_cms_privacy_notice
    return privacy_notice_element
  end

  def get_qpp_logo
    return qpp_logo_element
  end

  def get_hhs_logo
    return hhs_logo_element
  end

  def get_midsection_text_two
    return mid_section_text_2_element
  end

  def get_midsection_text_one
    return mid_section_text_1_element
  end

  def get_disclaimer_title
    return disclaimer_title_element
  end

  def get_disclaimer_text
    return disclaimer_text_element
  end

  def get_job_code_label
    return job_code_element
  end

  def get_submit_button
    return submit_button_element
  end

  def get_show_password
    return show_password_element
  end

  def get_username_field
    return username_field_element
  end

  def get_password_field
    return password_field_element
  end

  def get_warning_text
    return warning_text_element
  end

  def qppar_input_username(username)
    self.username_field = username
    return QPPAR_Login_Page.new(@browser)
  end

  def get_qpp_header_text
    return header_quality_text_element
  end


  def qppar_input_password(password)
    self.password_field = password
    return QPPAR_Login_Page.new(@browser)
  end

  def click_submit_button
    submit_button_element.wait_until(&:present?).fire_event :onclick
  end

  def qppar_login_with(username, password)
    qppar_input_username username
    qppar_input_password password
    click_submit_button

    return QPPAR_Dashboard_Page.new(@browser)
  end


end