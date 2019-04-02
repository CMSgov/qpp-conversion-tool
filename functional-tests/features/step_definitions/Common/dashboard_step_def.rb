Given(/^User is on the Account Dashboard$/) do
  puts 'Currently this step def is commented due to the application change.'
  # @dashboard_page = Dashboard_Page.new(@browser)
  # @dashboard_page.navigate_to_account_dashboard()
end

When(/^User selects ([^"]*) next to the desired TIN$/) do |link_name|
  # puts 'Currently using work around for this due to the applciation changes, hence the code is commented.'
  Dashboard_Page.new(@browser).navigate_to_account_dashboard
  if link_name.downcase.include? "individual"
    link_name = "Individual"
  elsif link_name.downcase.include? "group"
    link_name = "Group"
  end
  link_ele = @browser.link(xpath: "(//p[contains(.,'#{$userObj[:tin]}')]/ancestor::li[@class='practice']//a[contains(.,'#{link_name}')])[last()]").wait_until(&:present?)
  # link_ele.scroll.to :bottom
  link_ele.fire_event :onclick
  #@browser.element(css: ".breadcrumb.dashboard").wait_until(&:present?)
end

When(/^User select ([^"]*) next to the desired clinician$/) do |link|
  @dashboad_page = Dashboard_Page.new(@browser)
  if $userObj[:npi].nil?
    puts "please update the step to use the correct step, this step is modified to work with the Report as an individual user link for the npi"
  else
    @dashboad_page.search_clinician($userObj[:npi])
    rpt_ele = @browser.link(xpath: "//p[contains(.,'#{$userObj[:npi]}')]/ancestor::div[@class='clinician-info grid-elig-info']//a[contains(.,'individual')]").wait_until(timeout:30, &:present?)
    WebHelper.click(rpt_ele)
  end
end

Given(/^User is on the Group Reporting Dashboard$/) do
  @dashboard_page = Dashboard_Page.new(@browser)
  @dashboard_page.navigate_to_grp_rpt()
end

Then("User sees links in the left navigation bar") do |table|
  data = table.raw
  @verifi = Verifications.new(@browser)
  data.each do |link|
    # Assert the link is displayed
    link_element = @browser.link(:xpath, "//a[@data-track-category='SidebarNav' and @data-track-label='" + link[0] + "']")
    @verifi.verify_element_present(true, link_element, link[0])
  end
end

And("User will see the TIN and Name displayed") do
  @verifi = Verifications.new(@browser)
  @verifi.verify_text("TIN# " + $userObj[:tin], @browser.element(xpath: "//p[@class='practice-tin']").text)
  if (is_feature_turned_on)
    puts "needs to implement this once the feature is turned on."
  end
end

When("User selects the {string} button for the Improvement Activities") do |string|
  @dashboard_page = Dashboard_Page.new(@browser)
  @dashboard_page.start_reporting_on_IA()
end

And("User sees {string} in the left navigation bar") do |link|
  link_element = @browser.span(:xpath, "//button[@class='link-inline ']//span[text()='" + link + "']")
  @verifi.verify_element_present(true, link_element, link)
end


And(/^User selects the Start Reporting button for the group (.*)$/) do |group_name|

  @dashboard_page = Dashboard_Page.new(@browser)
  if (group_name == 'Advancing Care Information' or group_name == 'Promoting Interoperability')
    @dashboard_page.click_aci_info_link
  elsif group_name == 'Improvement Activities'
    @dashboard_page.click_ia_link
  elsif group_name == 'Quality Measures'
    @dashboard_page.click_qm_link
  end

end

Then(/^User verifies that tabs are displayed$/) do |table|
  @verifi = Verifications.new(@browser)
  table.hashes.each do |tab|
    @verifi.verify_element_present(@browser.span(xpath: "//li[starts-with(@class,'nav-item')]//span[text()= '#{tab['tab_name']}']"))
  end
end

Then(/^User verifies the page title ([^"]*) is displayed$/) do |page_title|
  @verifi = Verifications.new(@browser)
  @dashboard_page = Dashboard_Page.new(@browser)
  @verifi.verify_text(page_title, @dashboard_page.get_page_title)
end


And(/^User verifies left navigation bar is not displayed$/) do
  @verifi = Verifications.new(@browser)
  @verifi.verify_element_not_present(@browser.div(xpath: "//div[@class='sidenav-container']"))

end

And(/^User navigates to "([^"]*)" tab$/) do |tab_name|
  @dashboard_page = Dashboard_Page.new(@browser)
  @dashboard_page.navigate_to_tab(tab_name)
end

And(/^User navigates to "([^"]*)"  group$/) do |group_name|
  @dashboard_page = Dashboard_Page.new(@browser)
  @dashboard_page.navigate_to_group(group_name)
end

And(/^User clicks on the Account Dashboard link$/) do
  @qpp_page = QPP_Page.new(@browser)
  @qpp_page.click_account_link
end

Then(/^User verifies the QPP Logo in the header$/) do
  @dashboard_page = Dashboard_Page.new(@browser)
  @dashboard_page.verify_qpp_logo_in_header
end

And(/^User verifies menu items in the header$/) do |table|
  # table is a table.hashes.keys # => [:menu_item]
  @dashboard_page = Dashboard_Page.new(@browser)
  table.hashes.each do |menu_item|
    @dashboard_page.verify_menu_item_in_header(menu_item['menu_item_name'])
  end
end

And(/^User verifies the HHS Logo in the footer$/) do
  @dashboard_page = Dashboard_Page.new(@browser)
  @dashboard_page.verify_hhs_logo_in_footer
end

And(/^User verifies footer anchor links displayed$/) do |table|
  # table is a table.hashes.keys # => [:link_name]
  @dashboard_page = Dashboard_Page.new(@browser)
  table.hashes.each do |item|
    @dashboard_page.verify_link_in_footer(item['footer_item'])
  end
end

And(/^User confirms Performance Feedback still pending modal$/) do
  @dashboard_page = Dashboard_Page.new(@browser)
  @dashboard_page.confirm_feedback_still_pending_modal
end

Then(/^User ensures the number of displayed practices matches the number in the header$/) do
  num_practices_text = @browser.element(xpath: "//*[contains(text(),'PRACTICES')]/span").text.gsub("(", "").gsub(")", "").to_s
  num_practice_results = @browser.elements(xpath: "//div[contains(@class,'list-item-container')]").count.to_s

  @verifi = Verifications.new(@browser)
  @verifi.verify_text(num_practices_text, num_practice_results)
end

Then(/^User verifies check mark for "([^"]*)" should be filled$/) do |milestone_status|
  actual = Dashboard_Page.new(@browser).milestone_status_text.gsub("\n", " ")
  Verifications.new(@browser).verify_text(milestone_status, actual, "Milestone Status")
end

And(/^User verifies correct header text displayed$/) do |table|
  # table is a table.hashes.keys # => [:header_text]
  table.hashes.each do |headerText|
    actual = QPP_Page.new(@browser).get_header_message
    Verifications.new(@browser).verify_text(headerText["header_text"], actual, "header text")
  end
end

And(/^User verifies "([^"]*)" panel is not displayed$/) do |panel|
  if panel == 'Request a Targeted Review'
    Verifications.new(@browser).verify_element_not_present(@browser.div(xpath: "//div[@class='help-card-title' and .='Request a Targeted Review']"))
  end
end


And(/^User navigates to '(.*)' tab$/) do|tab_name|
  QPP_Page.new(@browser).navigate_to_tab(tab_name)
end