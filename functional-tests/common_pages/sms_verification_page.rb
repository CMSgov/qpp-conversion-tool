# Page object representing the login page for the QPP application.

class SMSVerificationPage < QPP_Page
  #---------------------------------------------------------------------------------------
  #

  text_field(:verification_field, :id => 'callPassCode')
  button(:submit_code_btn, :class => 'btn btn-primary login-btn js-login-submit')

  #link(:account_dashboard, :xpath=>"//a[@data-track-label='Account Dashboard']")

  #---------------------------------------------------------------------------------------
  #   Method implementations
  #---------------------------------------------------------------------------------------
  def click_submit_code
    self.submit_code_btn_element.scroll.to :bottom
    self.submit_code_btn_element.focus
    self.submit_code_btn_element.flash
    self.submit_code_btn_element.fire_event :onclick
    sleep 6
    $browser.div(:xpath=>"//div[@class='dropdown-description' and text()='My Account']").wait_until(&:present?)
    return Dashboard_Page.new($browser)
  end

  def input_smstext(text)
    self.verification_field = text
    return Login_Page.new($browser)
  end


  #
  # Enter sms text value and click on the sign in button
  #
  # @Usage
  #         enter_sms_text(text)
  # @param [String] text
  #         text value from sms message
  # @return [PageObject]
  #         Returns Dashboard_Page object. [you should be able to call methods from Dashboard_page in the same step(if required/needed)]
  def enter_sms_text(text)
    input_smstext text
    sleep 15
    click_submit_code
    return Dashboard_Page.new($browser)
  end
end