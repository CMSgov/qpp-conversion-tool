# This comment should describe what page or page section the class represents in the application
require_relative 'qpp_page'
class Dashboard_Page < QPP_Page
  include PageObject
  # Interactive web elements associated with this page
  # For more details see, https://github.com/cheezy/page-object/wiki/Elements
  div(:variable_name1, :locator_type => 'locator_value')
  button(:variable_name2, :locator_type => 'locator_value')
  text_field(:variable_name3, :locator_type => 'locator_value')
  link(:qm_start_reporting, :xpath => "//a[@aria-label='start reporting quality measures']")
  link(:view_performance_feedback_button, xpath: "//a[contains(text(), 'VIEW PERFORMANCE FEEDBACK')]")
  link(:go_to_web_interface_button, xpath: "//a[contains(text(), 'GO TO CMS WEB INTERFACE')]")
  span(:loading_spinner, css: "span.ds-c-spinner ds-c-spinner--small")
  element(:yes_I_agree_checkbox, xpath: "//div[@class='modal-body']//label//input")
  buttons(:agreement_ok_button, xpath: "//div[@class='modal-footer']//button[text()='OK']")
  
  link(:account_dashboard, :xpath => "//a[@data-track-label='Account Dashboard']")
  link(:start_reporting_IA, :xpath => "//a[@aria-label='start reporting improvement activities']")
  link(:start_reporting_ACI, :xpath => "//a[@aria-label='start reporting advancing care information']")
  link(:start_reporting_QM, :xpath => "//a[@aria-label='start reporting quality measures']")
  span(:page_title,:xpath => "//h1[@class='page-title']/span")
  
  p(:milestone_status_paragraph, xpath: "(//div[@class='milestone-tracker']//img[@class='complete'])[last()]/following-sibling::p")
  h2(:qpp_dashboard_header_greeting, css:"div.wrap-container h2:nth-child(1)")

  link(:next_page, css: "a[aria-label = 'Next Page']")

  def click_next_page
    WebHelper.click_without_javascript(next_page_element)
  end

  def search_clinician(npi)
    @browser.div(xpath: "(//div[@class='clinician-info grid-elig-info'])[1]").wait_until(timeout:30,&:present?)
    unless @browser.div(xpath: "//p[contains(.,'#{npi}')]/ancestor::div[@class='clinician-info grid-elig-info']").exists?
      while next_page_element.exist?
        click_next_page
        loading_spinner_element.wait_while(&:present?)
        break if @browser.div(xpath: "//p[contains(.,'#{npi}')]/ancestor::div[@class='clinician-info grid-elig-info']").exists?
      end
    end
  end

  #
  # This method will return milestone_status paragraph text 
  # author : supputuri
  # date   : 7/20/2018
  
  def milestone_status_text
    return milestone_status_paragraph_element.text
  end

  # Interactive methods used to hide implementation details
  # http://www.rubydoc.info/github/cheezy/page-object/PageObject
  #---------------------------------------------------------------------------------------
  #   Method implementations
  #---------------------------------------------------------------------------------------
  def get_page_title
    page_title
  end

  def get_page_greeting
    qpp_dashboard_header_greeting_element.wait_until(&:present?)
  end


  def click_on_button
    self.variable_name2_element.fire_event :onclick
  end

  def click_qm_start_reporting
    self.qm_start_reporting_element.wait_until(&:present?).fire_event :onclick
  end

  def click_view_performance_feedback_button
    view_performance_feedback_button_element.wait_until(&:present?).fire_event :onclick
  end

  def click_performance_detail_button(tin)
    @browser.button(xpath:"//*[contains(text(), '#"+tin+ "')]/../../../div[@class='buttons-container']/a").wait_until(&:present?).fire_event :onclick
  end

  def click_go_to_web_interface_button
    go_to_web_interface_button_element.wait_until(&:present?).fire_event :onclick
  end

  #
  # This method will navigate the user to account dashboard from any page
  #
  def navigate_to_account_dashboard
    dashboar_url = @browser.url.split('user')[0] + "user/dashboard"
    puts dashboar_url
    @browser.driver.navigate.to(dashboar_url)
    #@browser.h1(xpath: "//div/h1[.='Account Dashboard']").wait_until(&:present?)
  end

  #
  # This method will navigate the user to group reporting page from any page using
  # the first available TIN
  #
  def navigate_to_grp_rpt
    navigate_to_account_dashboard
    # click on the 'Report as a group' link associated with the first available TIN
    @browser.li(xpath: "(//li[div/p[starts-with(.,'TIN:')] ])[1]").link(text: "Report as a group").fire_event :onclick
    Dashboard_Page.new(@browser)
  end

  #
  # This method will navigate the user to group reporting page from any page
  # using user provided TIN
  #
  # @param [String] tin
  #         Provide the TIN for which user has to click 'Report as a group'
  def navigate_to_grp_rpt_with_TIN(tin)
    navigate_to_account_dashboard
    # click on the 'Report as a group' link associated with the user specified TIN
    @browser.li(id: tin).link(text: "Report as a group").fire_event :onclick
  end

  #
  # Clicks on the start reporting IA button
  #
  def start_reporting_on_IA
    self.start_reporting_IA_element.wait_until(timeout:120, &:present?).fire_event :onclick
    @browser.span(:xpath=>"//div[@class='page-title-header']//span[.='Improvement Activities']").wait_until(&:present?)
    Improvement_Activities.new(@browser)
  end

  #
  # Clicks on the start reporting IA button
  #
  def start_reporting_on_ACI
    start_reporting_ACI_element.wait_until(&:present?).fire_event :onclick
    @browser.span(:xpath=>"//div[@class='page-title-header']//span[.='Advancing Care Information']|a[.='Advancing Care Information']").wait_until(&:present?)
    ACI_Page.new(@browser)
  end


  #
  # Clicks on the start reporting IA button
  #
  def start_reporting_on_QM
    WebHelper.click(start_reporting_QM_element)
    @browser.span(:xpath=>"//div[@class='page-title-header']//span[.='Quality']").wait_until(timeout:180, &:present?)
  end

  #
  # This method will navigate the user to IA from
  # any page using the first TIN
  #
  def navigate_to_IA
    navigate_to_account_dashboard
    @browser.li(xpath: "(//li[div/p[starts-with(.,'TIN:')] ])[1]").link(text: "Report as a group").fire_event :onclick
    start_reporting_on_IA
    Dashboard_Page.new(@browser)
  end

  #
  # This method will navigate the user to IA from
  # any page using user provided TIN
  #
  def navigate_to_IA_with_TIN(tin)
    navigate_to_account_dashboard
    @browser.li(id: tin).link(text: "Report as a group").fire_event :onclick
    start_reporting_on_IA
  end


  def navigate_to_APM_with_TIN(tin)
    navigate_to_account_dashboard
    @browser.link(xpath:"//li[@id='" + tin + "']//a").fire_event :onclick
  end

  #
  # This method will check if the Performance Feedback still pending dialog
  # is shown and confirm and close it
  #
  def confirm_feedback_still_pending_modal
    sleep(5)

    # if @browser.h4(:xpath => "//div[@class='modal-header']//h4[text()='Performance Feedback Data Is Still Pending' and not(following-sibling::button)]").exists?
    modal_header_element = @browser.h4(:xpath => "//div[@class='modal-header']//h4[text()='Performance Feedback Data Is Still Pending' and not(following-sibling::button)]")
    if modal_header_element.exists? && modal_header_element.visible?
      yes_I_agree_checkbox_element.wait_until(&:present?).fire_event :onclick

      agreement_ok_button_elements.each { |item|
        if item.visible?
          item.wait_until(&:present?).fire_event :onclick
        end
      }
    end
  end
end
