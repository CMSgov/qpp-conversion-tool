# Page object representing the login page for the QPP application.
require 'rspec'

class Login_Page < QPP_Page
  include Test::Unit::Assertions
  #---------------------------------------------------------------------------------------
  text_field(:username_field, :id => 'username')
  text_field(:password_field, :id => 'password')
  text_field(:mfa_field, :id => 'callPassCode')
  checkbox(:agreement_chkbox, :name => 'accuracy-agreement')
  button(:sign_in_btn, :class => 'js-login-submit')
  button(:verify_btn, :class => 'btn btn-primary login-btn js-login-submit')
  #link(:account_dashboard, :xpath=>"//a[@data-track-label='Account Dashboard']")

  #---------------------------------------------------------------------------------------
  #   Method implementations
  #---------------------------------------------------------------------------------------
  def click_signin
    WebHelper.click(sign_in_btn_element)
    if ENV['ENV'] == 'PROD'
      sleep 10
      message = SMSUtil.getMFA
      input_mfa message
      click_verify
      sleep 5
      get_cookies()
    end
    sleep 1
    myAccount = @browser.div(:xpath=>"//div[@class='dropdown-description' and text()='My Account']")
    myAccount.wait_until(timeout:30, &:present?)
    @verify_page = Verifications.new(@browser)
    @verify_page.verify_element_present(myAccount)
    return Dashboard_Page.new(@browser)
  end

  def click_verify
    self.verify_btn_element.fire_event :onclick
  end

  def input_username(username)
    self.username_field = username
    return Login_Page.new(@browser)
  end

  def input_password(password)
    self.password_field = password
    return Login_Page.new(@browser)
  end

  def input_mfa(code)
    self.mfa_field = code
    return Login_Page.new(@browser)
  end

  def check_agreement
    agreement_chkbox_element.fire_event :onclick
    if !self.agreement_chkbox_checked?
      @browser.execute_script("$('[name=\"accuracy-agreement\"]').prop('checked','checked');")
    end
    return Login_Page.new(@browser)
  end

  #
  # Login to the application using decrypted user name and password
  #
  # @Usage
  #         login_with(decrypted UserName, decrypted Password)
  # @param [String] username
  #         User Name [Make sure to enter user names in YML]
  # @param [String] password
  #         Password [Make sure to enter encrypted passwords in YML]
  # @return [PageObject]
  #         Returns Dashboard_Page object. [you should be able to call methods from Dashboard_page in the same step(if required/needed)]
  def login_with(username, password)
    input_username username
    input_password password
    check_agreement
    click_signin
    return Dashboard_Page.new(@browser)
  end

  def get_cookies()
    cookie = @browser.driver.manage.cookie_named("qpp_auth_token")
    puts " value is"+ cookie[:name] + cookie[:value]

    $qpp_auth_token = cookie[:value]

    # @browser.driver.manage.all_cookies.each do |cookie|
    #   puts cookie[:name]
    #   #puts cookie[:value]
    # end
  end
end