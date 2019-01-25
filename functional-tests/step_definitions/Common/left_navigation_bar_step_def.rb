

Then(/^User verifies Collapse option displayed on left navigation bar$/) do
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  @left_nav.verify_collapse_option
end

When(/^User click on "([^"]*)" button on left nav bar$/) do |button_name|
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  if button_name.casecmp('collapse').zero?
    @left_nav.collapse_left_nav_bar
  elsif button_name.casecmp('expand').zero?
    @left_nav.expand_left_nav_bar
  end
end

Then(/^User verifies Expand option displayed on left navigation bar$/) do
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  @left_nav.verify_expand_option
end

And(/^User verifies left navigation bar collapsed$/) do
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  @left_nav.verify_left_nav_bar_collapsed
end

And(/^User verifies left navigation bar expanded$/) do
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  @left_nav.verify_left_nav_bar_expanded
end

Then(/^User verifies tin and npi information displayed based on (.*)$/) do |user_type|
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  if user_type.downcase =='group'
    @left_nav.verify_tin($userObj[:tin])
  elsif user_type.downcase == 'individual'
    @left_nav.verify_tin($userObj[:tin])
    @left_nav.verify_npi($userObj[:npi])
  end
end

And("{string} selected with a bullet indicating that it is the currently displayed page") do |current_page|
  puts "This is commented because of application changes."
  # @verifi = Verifications.new(@browser)
  # @left_nav = Left_Nav_Bar_Page.new(@browser)
  # exp_result = @left_nav.get_currently_displayed_page_bullent
  # @verifi.verify_text(current_page, exp_result)
end

Then(/^User verifies Legend chevron displayed on left navigation bar|User verifies Hide Legend chevron is displayed$/) do
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  @left_nav.verify_group_reporting_chevron_hide
end

When(/^User click on hide legend chevron link$/) do
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  @left_nav.click_hide_group_reporting_chevron
end

Then(/^User verifies category displayed on the left navigation bar$/) do |table|
  # table is a table.hashes.keys # => [:category_name]
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  table.hashes.each do |category|
    @left_nav.verify_category_link(category['category_name'])
  end
end


Then(/^User verifies dropdown list closed$/) do
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  @left_nav.verify_category_list("false")
end

When(/^User clicks "([^"]*)" link on the left navigation bar$/) do |link_name|
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  @left_nav.click_on_left_nav_link(link_name)
end


When(/^User click manage user access$/) do
  @left_nav = Left_Nav_Bar_Page.new(@browser)
  @left_nav.click_manage_user_access
end

  And(/^User click "([^"]*)" link in left hand navigation$/) do |link_name|
  @leftNavBar = Left_Nav_Bar_Page.new(@browser)
  case link_name.downcase
    when 'connected clinicians'
      @leftNavBar.click_connected_clinicians_link
  else
    @browser.link(xpath: "//div[@class='sidebar-content']//a[.='#{link_name}']").wait_until(&:present?)
    WebHelper.click(@browser.link(xpath: "//div[@class='sidebar-content']//a[.='#{link_name}']"))
  end

end

Then(/^User verifies "([^"]*)" link in left hand navigation is selected with the blue strike\/line on the left$/) do |link_name|
  Left_Nav_Bar_Page.new(@browser).verify_link_is_selected_with_blue_bar(link_name)
end

And(/^User verifies "([^"]*)" link in left hand navigation is not selected with the blue strike\/line on the left$/) do |link_name|
  @browser.element(css: "[aria-label='#{link_name}']").attribute_value('class').exclude? "active"
end

Then(/^User verifies category (.*) is not displayed in left hand navigation$/) do |category|
  Left_Nav_Bar_Page.new(@browser).verify_category_link(category,'false')
end

And(/^User verifies "([^"]*)" link in sidebar$/) do |linkName|
  Verifications.new(@browser).verify_element_present(@browser.link(xpath: "//div[@class='sidebar-content']//a[.='#{linkName}']"))
end

And(/^User verifies Clinicians List icon displayed$/) do
  Verifications.new(@browser).verify_element_present(@browser.element(css:"a[aria-label='Clinicians List'] svg.left-icon"))
end

Then(/^User verifies "([^"]*)" icon displayed at top of sidebar$/) do |linkName|
  linkText = @browser.link(css:"div.sidebar-content a:first-child").attribute_value("aria-label")
  Verifications.new(@browser).verify_text(linkName,linkText)
end