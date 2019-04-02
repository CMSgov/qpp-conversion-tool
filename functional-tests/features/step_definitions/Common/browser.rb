Given(/^User opens a browser$/) do
  @browser = SeleniumUtil.open()
  @browser.window.maximize()
  @manage_user_access = Manage_Access.new(@browser)
  @harp_profile_info = HARP_Profile_Info_Page.new(@browser)
  @harp_account_info = HARP_Account_Info_Page.new(@browser)
  @harp_remote_proofing = HARP_Remote_Proofing_Page.new(@browser)
end

Given(/^User opens a browser with directory "([^"]*)"$/) do |directory|
  if ENV['SAUCEY'] == 'false'
    puts "new directory = " + Dir.pwd + directory
    ENV['DOWNLOAD_FOLDER'] = Dir.pwd + directory
  else
    puts "Using Sauce Download Folder"
    ENV['TRANSFER_FOLDER'] = Dir.pwd + directory
    ENV['DOWNLOAD_FOLDER'] = 'C:\\Users\\Administrator\\Downloads'
  end
  @browser = SeleniumUtil.open()
  @browser.window.maximize()
end

And(/^User closes browser$/) do
# this method is empty. We let the selenium_util gem handle it
end

#This method clicks on a link with a certain text
And(/^User click "([^"]*)" link$/) do |arg|
  sleep 4
  #@browser.a(text: arg).wait_until(&:present?).scroll.to :bottom
  @browser.a(:xpath => "//a[normalize-space(.)='#{arg}']").fire_event :onclick
end

And(/^User click on ACO start button for TIN$/) do
  @dashboard_page = Dashboard_Page.new(@browser)
  @dashboard_page.navigate_to_APM_with_TIN($userObj[:tin])
end


And(/^User click view progress link$/) do
  sleep 10
  @left_nav = LeftNav.new(@browser)
  @left_nav.click_View_Progress
end


#This method clicks on a button with a certain text
And(/^User clicks "([^"]*)" button$/) do |arg|
  #@browser.button(text: arg).wait_until(&:present?).scroll.to :bottom
  @browser.button(xpath: "//button[normalize-space(.)='#{arg}']").fire_event :onclick
end

And(/^User clicks "([^"]*)" button in browser "([^"]*)"$/) do |arg, browserIndex|
  @browser.window(:index => browserIndex.to_i - 1).use do
    @browser.button(xpath: "//button[normalize-space(.)='#{arg}']").fire_event :onclick
  end

end


#This method clicks on a button with a certain text
And(/^User checks for button and then User clicks "([^"]*)" button$/) do |arg|
  #@browser.button(text: arg).wait_until(&:present?).scroll.to :bottom
  button_var = @browser.button(text: arg)

  if button_var.exist?
    @browser.button(text: arg).fire_event :onclick
  else
    puts "Measure has no benchmark link"
  end
end

When(/^User click Refresh button$/) do
  @browser.refresh
  @browser.li(class: 'account').wait_until(timeout:120, &:present?)
  sleep 5
end

And(/^User clicks the Back button$/) do
  @browser.back
  @browser.li(class: 'account').wait_until(timeout:120, &:present?)
  sleep 5
end

Given(/^User sets download folder without browser to "([^"]*)"$/) do |directory|

  if ENV['SAUCEY'] == 'false'
    puts "new directory = " + Dir.pwd + directory
    ENV['DOWNLOAD_FOLDER'] = Dir.pwd + directory
  else
    puts "Using Sauce Download Folder"
    ENV['DOWNLOAD_FOLDER'] = 'C:\\Users\\Administrator\\Downloads'
  end
end

And(/^User saves session for sortsite$/) do
  @browser.goto('https://imp.qpp.cms.gov/shazam/?params=set_qpp_imp')
end
And(/^User deletes session for sortsite$/) do
  @browser.goto('https://imp.qpp.cms.gov/shazam/?params=delete_qpp_imp')
end


And(/^User clicks the Download button with tin$/) do
  ele = @browser.button(xpath: "//span[.='TIN: ##{$userObj[:tin]}']//ancestor::div[@class='wrap-container']//button[normalize-space(.)='Download']")
  WebHelper.click(ele)
  sleep 1
end
