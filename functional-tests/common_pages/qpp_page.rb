class QPP_Page
  include PageObject
  image(:qpp_header_logo, css: "div.navbar-header img.qpp-logo")
  image(:hhs_logo,css: "footer div.hhs-logo-container>img[alt='Center for Medicaid & Medicare Services']")
  #//div[starts-with(@class,'dropdown-') and .='Alternative Payment Models']
  div(:mips_hdr_lnk, text: 'MIPS')
  div(:apms_lnk, text: 'APMs')
  div(:about_lnk, text: 'About')
  # div(:signin_lnk, :text => 'Sign In')
  div(:signin_lnk, xpath: "//div[@class='nav-title' and text()='Sign In']")
  li(:account, class: 'account')
  button(:logout, class: 'logout')
  button(:delete_category_data, xpath: "//button[normalize-space(.)='Delete' or normalize-space(.)='Delete Measures']")
  div(:react_modal, class: 'ReactModalPortal', visible: true)
  button(:signout_btn) { react_modal_element.button(class: 'btn primary-action') }

  element(:attestation_tab, xpath: "//ul[@class='submission-tabs-nav-list']//h2[normalize-space(.)='Attestation']")
  element(:ehr_tab, xpath: "//ul[@class='submission-tabs-nav-list']//h2[normalize-space(.)='EHR']")
  element(:registry_tab, xpath: "//ul[@class='submission-tabs-nav-list']//h2[normalize-space(.)='Registry']")

  link(:mips_overview_lnk, id: 'mips-overview')
  link(:aci_info_link, xpath: "//div[@class='sidebar-content' or @class='sidebar-content alt-style']//a[.='Promoting Interoperability']")
  link(:ia_link, xpath: "//div[@class='sidebar-content' or @class='sidebar-content alt-style']//a[text()='Improvement Activities']")
  link(:qm_link, xpath: "//div[@class='sidebar-content' or @class='sidebar-content alt-style']//a[text()='Quality']")
  span(:aci_ehr_link, xpath: "//div[@id='ariaJumpToLink']//div[@class='aci-page']//section[@class='submission-method-tabs-section']//div//tabset//ul/li//a/span[text()='EHR']")
  span(:cpia_ehr_link, xpath: "//div[@id='ariaJumpToLink']//div[@class='cpia-page']//section[@class='submission-method-tabs-section']//div//tabset//ul/li//a/span[text()='EHR']")
  link(:account_dash_link, class: 'my-account')
  span(:header_message, css:"span.message-description")

  def expand_measure_chevron(measure_id)
    measure_chevron_ele = @browser.button(xpath: "//span[.='#{measure_id}']/ancestor::div[@class='measure-detail-col-details']/preceding-sibling::div[@class='measure-detail-col-chevron']/button[@class='fa info-button pt5 fa-chevron-right']")
    WebHelper.click(measure_chevron_ele)
  end

  def collapse_measure_chevron(measure_id)
    measure_chevron_ele = @browser.div(xpath: "//span[.='#{measure_id}']/ancestor::div[@class='measure-detail-col-details']/preceding-sibling::div[@class='measure-detail-col-chevron']/button[@class='fa info-button pt5 fa-chevron-down']")
    WebHelper.click(measure_chevron_ele)
  end

  def verify_measure_details_displayed(measure_id,exp_status = true)
    @verifi = Verifications.new(@browser)
    actual = @browser.div(xpath: "//span[.='#{measure_id}']/ancestor::div[@class='panel card panel-default']//div[@class='measure-detail']").exists?
    @browser.div(xpath: "//span[.='#{measure_id}']/ancestor::div[@class='panel card panel-default']//div[@class='measure-detail']").flash
    @verifi.verify_text(exp_status.to_s, actual.to_s,measure_id)
  end

  def verify_measure_specification_download_link_color(measure_id,exp_color)
    measure_ele = @browser.element(xpath: "//span[.='#{measure_id}']/ancestor::div[@class='panel-title']//a[@download='measureSpecLink']//span[@aria-label='Download Measure Specification']")
    measure_ele.flash
    puts "style :" +  measure_ele.style.to_s
    actual = measure_ele.style
    @verifi = Verifications.new(@browser)
    @verifi.verify_text(exp_color, actual,measure_id)
  end

  def mouse_over_on_measure_specification_download_link(measure_id)
    @browser.element(xpath: "//span[.='047']/ancestor::div[@class='panel-title']//*[@alt = 'download']").fire_event :onmouseover
  end

  def verify_qpp_logo_in_header(exp_status=true)
    @verifi = Verifications.new(@browser)
    actual = qpp_header_logo_element.exists?
    @verifi.verify_text(exp_status.to_s,actual.to_s, "QPP Logo in Header")
  end

  def verify_menu_item_in_header(menu_item)
    @verifi = Verifications.new(@browser)
    actual = @browser.div(xpath: "//div[starts-with(@class,'dropdown-') and .='#{menu_item}']").exists?
    @verifi.verify_text('true',actual.to_s)
  end

  def verify_hhs_logo_in_footer(exp_status=true)
    @verifi = Verifications.new(@browser)
    actual = hhs_logo_element.exists?
    @verifi.verify_text(exp_status.to_s,actual.to_s, "HHS Logo in footer")
  end

  def verify_link_in_footer(footer_item)
    @verifi = Verifications.new(@browser)
    @verifi.verify_element_present(@browser.li(xpath: "//footer//li[.='#{footer_item}']"),true)
  end

  def verify_menu_item(menu_item, exp_status)
    @verifi = Verifications.new(@browser)
    actual = @browser.li(xpath: "//ul[@class='dropdown-menu']/li[.='#{menu_item}']").exists?
    @verifi.verify_text(exp_status.to_s, actual.to_s)
  end

  def navigate_to_attestation_tab
    attestation_tab_element.wait_until(&:present?).click
  end

  def delete_caterogy_data
    @browser.span(xpath: "//span[@class='toast-title']").wait_while(&:present?)
    sleep 1
    if self.delete_category_data_element.exists?
      if self.delete_category_data_element.disabled?
        # click on ACI Measure
        ACI_Page.new(@browser).get_available_measure_tracks.each do |measure_set|
          measure_set.wait_until(&:present?).fire_event :onclick
          # select measure
          unless @browser.button(xpath: "//button[starts-with(@aria-label,'Select Yes')][1]").disabled?
            @browser.button(xpath: "//button[starts-with(@aria-label,'Select Yes')][1]").wait_until(&:present?).fire_event :onclick
          end
        end
        if @browser.button(xpath: "//button[@class='measure-action measure-action-yes IA_PCMH']").exists?
          unless @browser.button(xpath: "//button[@class='measure-action measure-action-yes IA_PCMH']").disabled?
            WebHelper.click(@browser.button(xpath: "//button[@class='measure-action measure-action-yes IA_PCMH']"))
            sleep 2
          end
        end
      end
      sleep 1
      unless self.delete_category_data_element.disabled?
        self.delete_category_data_element.fire_event :onclick
        yes_delete_submission_data_element.wait_until(&:present?).fire_event :onclick
        sleep 2
        @browser.span(xpath: "//span[@class='toast-title']").wait_while(timeout:60, &:present?)
      end
    end
  end

  def delete_category_data_from_all_tabs
    delete_caterogy_data
    # get the tabs
    @browser.lis(css: ".submission-tabs-nav-list li").each do |tab|
      if tab.exist?
        tab.click
      end
      delete_caterogy_data
    end
    # if registry_tab_element.exists?
    #   registry_tab_element.fire_event :onclick
    #   delete_caterogy_data
    # end
    # if attestation_tab_element.exists?
    #   attestation_tab_element.fire_event :onclick
    #   delete_caterogy_data
    # end
    #
    # if ehr_tab_element.exist?
    #   ehr_tab_element.fire_event :onclick
    #   delete_caterogy_data
    # end
    # sleep 1
    delete_caterogy_data
  end

  def delete_all_categories_data
    @aci = ACI_Page.new(@browser)
    # Make sure the side nav bar is expanded
    if @browser.button(css: "button.link-expand").exists?
      WebHelper.click( @browser.button(css: "button.link-expand"))
    end
    sleep 2
    active_link = @browser.link(xpath: "//div[@class='sidebar-content' or @class='sidebar-content alt-style']//li[starts-with(@class,'active-link')]/a").wait_until(timeout:120, &:present?).text
    rpt_type = ['Improvement Activities', 'Promoting Interoperability', 'Quality']
    #rpt_type = ['Improvement Activities', 'Quality Measures']
    rpt_type.each do |rpt_type|
      @browser.link(xpath: "//div[@class='sidebar-content' or @class='sidebar-content alt-style']//a[.='#{rpt_type}']").wait_until(&:present?).fire_event :onclick
      sleep 1
      if @aci.is_reweight_modal_displayed
        @aci.accept_reweight
      end
      @browser.element(xpath: "//div[contains(@class,'title-container')]//h1|//div[contains(@class,'title-container')]|//*[contains(@class,'page-title')]").wait_until(&:present?)
      delete_category_data_from_all_tabs
    end
    @browser.link(xpath: "//div[@class='sidebar-content' or @class='sidebar-content alt-style']//a[.='#{active_link}']").wait_until(&:present?).fire_event :onclick
    sleep 2
  end

  def click_mips_lnk
    mips_hdr_lnk_element.fire_event :onclick
  end

  def click_apms_lnk
    apms_lnk_element.fire_event :onclick
  end

  def click_about_lnk
    about_lnk_element.fire_event :onclick
  end

  def click_home_page_signin
    if account_element.exists?
      signout
    end
    WebHelper.click(signin_lnk_element)
  end

  def click_aci_info_link
    aci_info_link_element.wait_until(&:present?).fire_event :onclick
  end

  def click_ia_link
    ia_link_element.flash
    ia_link_element.fire_event :onclick
  end

  def click_qm_link
    qm_link_element.wait_until(&:present?).fire_event :onclick
    sleep 2
    #@browser.span(xpath: "//h1[@class='page-title']/span[text()='Quality']").wait_until(&:present?)
  end

  def click_aci_ehr_link
    aci_ehr_link_element.wait_until(&:present?).fire_event :onclick
    # wait_until(100,"EHR element not present with in the time"){self.aci_ehr_link_element.click}
  end

  def click_account_link
    WebHelper.click(account_element)
    account_dash_link_element.fire_event :onclick
  end

  def navigate_to_tab(tab_name)
    case tab_name.downcase
    when 'attestation'
      tab_name = 'Attestation'
    when 'ehr'
      tab_name = 'EHR'
    when 'registry'
      tab_name = 'Registry'
    when 'manually enter'
      tab_name = 'Manually Enter'
    end
    @browser.element(xpath: "//ul[@class='submission-tabs-nav-list']//h2[normalize-space(.)='#{tab_name}']").wait_until(&:present?)
    @browser.element(xpath: "//ul[@class='submission-tabs-nav-list']//h2[normalize-space(.)='#{tab_name}']").fire_event :onclick
  end

  def navigate_to_group(group_name)
    case group_name.downcase
    when 'aci'
      group_name = 'Advancing Care Information'
    when 'ia'
      group_name = 'Improvement Activities'
    when 'qm'
      group_name = 'Quality Measures'
    end
    grp_ele = @browser.a(xpath: "//div[@class='qpp-style-sidebar-container']//div[@class='link-drawer']//ul/li//a[text()='#{group_name}']")
    # grp_ele.flash
    grp_ele.scroll.to :bottom
    grp_ele.fire_event :onclick
    sleep 2
    # @browser.element(:xpath => "//div[contains(@class,'title-container')]//h1|/div[contains(@class,'title-container')]|//div[contains(@class,'page-title')]//h2").wait_until(&:present?)
  end

  def click_cpia_ehr_link
    cpia_ehr_link_element.wait_until(&:present?).fire_event :onclick
    # wait_until(100,"EHR element not present with in the time"){self.cpia_ehr_link_element.click}
  end

  # this method is used to Sign Out the current user
  #
  def signout
    begin  # do not fail on logout
      WebHelper.click(account_element)
      logout_element.fire_event :onclick
      #signout_btn_element.fire_event :onclick
    rescue Exception => e
      puts e.message
    end

  end

  def get_header_message
    header_message
  end

  def wait_for_loading(second_to_wait)
    @browser.driver
  end
end
