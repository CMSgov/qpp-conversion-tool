require_relative 'qpp_page'
class Left_Nav_Bar_Page < QPP_Page
  # ----------------------------------------------------------------------------
  # div elements
  # ----------------------------------------------------------------------------
  div(:left_nav_bar, xpath: "//div[@id='qpp-nav-sidebar']")

  # ----------------------------------------------------------------------------
  # button elements
  # ----------------------------------------------------------------------------
  button(:collapse, xpath: "//button[@aria-label='Click to collapse the sidebar menu']")
  button(:expand, xpath: "//button[@aria-label='Click to expand the sidebar menu']")
  button(:group_reporting_chevron_hide, xpath: "//button[.='Group Reporting']")

  # ----------------------------------------------------------------------------
  # Paragraph elements
  # ----------------------------------------------------------------------------
  p(:left_nav_tin, xpath: "//div[@class='practice-container']//p[@class='practice-tin']")
  p(:left_nav_npi, xpath: "//div[@class='individual-container']//p[@class='individual-npi']")

  # ----------------------------------------------------------------------------
  # link elements
  # ----------------------------------------------------------------------------
  link(:left_nav_acc_dashboard, xpath: "//div[@class='sidebar-content']//a[.='Account Dashboard']")
  link(:group_dashboard, xpath: "//div[@class='sidebar-content']//a[.='Group Dashboard']")
  link(:left_nav_QM, xpath: "//div[@class='currentPage drawer open']//a[.='Quality Measures' or .='Quality']")
  link(:left_nav_ACI, xpath: "//div[@class='currentPage drawer open']//a[.='Promoting Interoperability']")
  link(:left_nav_IA, xpath: "//div[@class='currentPage drawer open']//a[.='Improvement Activities']")
  link(:active_link, xpath: "//li[@class='active-link']//a")
  link(:connected_clinicians_link, css: ".sidebar-content a[aria-label='Connected Clinicians']")
  link(:fb_overview, xpath: "//div[@class='sidebar-content']//a[.='Overview']")
  link(:fb_cost, xpath: "//div[@class='sidebar-content']//a[.='Cost']")
  link(:manage_user_access, css: ".qpp-side-nav a[href= '/user/manage-access']")
  link(:left_nav_quality_link, xpath: "//a[text()='Quality']")

  # ----------------------------------------------------------------------------
  # unordered list elements
  # ----------------------------------------------------------------------------
  # Feedback links
  ul(:group_reporting_cats, xpath: "//div[@class='currentPage drawer open']/ul")

  # ----------------------------------------------------------------------------
  # all other elements
  # ----------------------------------------------------------------------------
  element(:expanded_nav_bar, xpath: "//aside[@class='sidebar']")
  element(:collapsed_nav_bar, xpath: "//aside[@class='sidebar closed']")

  def verify_link_is_selected_with_blue_bar(link_name)
    actual = @browser.element(css: "[aria-label='#{link_name}']").style('border-left')
    Verifications.new(@browser).verify_text('3px solid rgb(128, 203, 196)', actual, link_name)
  end

  def click_left_nav_quality_link
    left_nav_quality_link_element.wait_until(&:present?).fire_event :onclick
  end

  def verify_group_reporting_chevron_hide(exp_status = 'true')
    @verifi = Verifications.new(@browser)
    actual = WebHelper.get_attribute(group_reporting_chevron_hide_element, 'aria-pressed').include? 'true'
    @verifi.verify_text(exp_status.to_s, actual.to_s)
  end

  def verify_hide_group_reporting_chevron(exp_status = 'true')
    @verifi = Verifications.new(@browser)
    actual = WebHelper.get_attribute(group_reporting_chevron_element, 'class')
    @verifi.verify_text(exp_status.to_s, actual.to_s)
  end

  def click_hide_group_reporting_chevron
    WebHelper.click(group_reporting_chevron_hide_element)
  end

  def click_manage_user_access
    WebHelper.click(manage_user_access_element)
  end

  def get_manage_user_access_link
    manage_user_access_element
  end

  def verify_category_list(exp_status = 'true')
    @verifi = Verifications.new(@browser)
    actual = group_reporting_cats_element.exists?
    @verifi.verify_text(exp_status.to_s, actual.to_s)
  end

  def verify_category_link(link_name, exp_status = 'true')
    @verifi = Verifications.new(@browser)
    case link_name.downcase
    when 'ia', 'improvement activities'
      ele = left_nav_IA_element
    when 'aci', 'advancing care information', 'promoting interoperability.'
      ele = left_nav_ACI_element
    when 'qm', 'quality measures','quality'
      ele = left_nav_QM_element
    when 'feedback overview'
      ele = fb_overview_element
    when 'group dashboard'
      ele = group_dashboard_element
    when 'account dashboard'
      ele = left_nav_acc_dashboard_element
    when 'cost'
      ele = fb_cost_element
    end
    ele.wait_until(&:present?)
    actual = ele.exists?
    @verifi.verify_text(exp_status.to_s, actual.to_s, link_name)
  end

  def click_on_left_nav_link(link_name)
    case link_name.downcase
    when 'ia', 'improvement activities'
      ele = left_nav_IA_element
    when 'aci', 'advancing care information', 'promoting interoperability'
      ele = left_nav_ACI_element
    when 'qm', 'quality measures',"quality"
      ele = left_nav_QM_element
    when 'feedback overview'
      ele = fb_overview_element
    when 'account dashboard'
      ele = left_nav_acc_dashboard_element
    when 'group dashboard'
      ele = group_dashboard_element
    when 'cost'
      ele = fb_cost_element
    end
    WebHelper.click(ele.wait_until(&:present?))
    sleep 2
  end

  #
  # This method will click on the connected_clinicians link
  # author : supputuri
  # date   : 6/13/2018

  def click_connected_clinicians_link
    WebHelper.click(connected_clinicians_link_element)
    self
  end

  def verify_tin(exp_tin)
    @verifi = Verifications.new(@browser)
    actual = left_nav_tin_element.text.sub('TIN# ', '')
    @verifi.verify_text(exp_tin.to_s, actual.to_s)
  end

  def verify_npi(exp_npi)
    @verifi = Verifications.new(@browser)
    actual = left_nav_npi_element.text.sub('NPI# ', '')
    @verifi.verify_text(exp_npi.to_s, actual.to_s)
  end

  def verify_left_navigation_bar(exp_status = 'true')
    @verifi = Verifications.new(@browser)
    actual = left_nav_bar_element.exists?
    @verifi.verify_text(exp_status.to_s, actual.to_s)
  end

  def verify_collapse_option(exp_status = 'true')
    @verifi = Verifications.new(@browser)
    sleep 2
    actual = collapse_element.exists?
    @verifi.verify_text(exp_status.to_s, actual.to_s)
  end

  def verify_expand_option(exp_status = 'true')
    @verifi = Verifications.new(@browser)
    actual = expand_element.exists?
    @verifi.verify_text(exp_status.to_s, actual.to_s)
  end

  def collapse_left_nav_bar
    WebHelper.click(collapse_element)
  end

  def expand_left_nav_bar
    WebHelper.click(expand_element)
  end

  def verify_left_nav_bar_collapsed
    @verifi = Verifications.new(@browser)
    @verifi.verify_element_present(collapsed_nav_bar_element)
  end

  def verify_left_nav_bar_expanded
    @verifi = Verifications.new(@browser)
    @verifi.verify_element_present(expanded_nav_bar_element)
  end

  #
  # This method will get the selected left navigation link with a bullet
  #
  # @return [String] Name of the link
  def get_currently_displayed_page_bullent
    active_link_element.text
  end
end
