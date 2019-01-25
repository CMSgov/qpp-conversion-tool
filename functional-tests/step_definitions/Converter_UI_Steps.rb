When(/^User navigates to the Conversion Tool Simple UI page$/) do
  visit_page Simple_UI_Page
  sleep 3
end

And(/^User uploads a (.*) file to the Simple UI$/) do |file1|
  dir = Pathname.new(__FILE__).join("../API")
  @browser.input(:xpath => "id('main-content')/app-root/section/div/div/div/input").send_keys "#{dir}/" + "Data Files/" + file1
  sleep 1
  @browser.button(:xpath => "id('main-content')/app-root/section/div/div/div/table/tbody/tr/td[5]/button[1]").click
end

Then(/^User receives Successful Upload message$/) do
  @browser.link(:xpath => "id('main-content')/app-root/section/div/div/div/div[2]/div/div/div/a").wait_until(&:present?).click
  sleep 3
end

And(/^User uploads another (.*) file to the Simple UI$/) do |file2|
  dir = Pathname.new(__FILE__).join("../API")
  @browser.input(:xpath => "id('main-content')/app-root/section/div/div/div/input").send_keys "#{dir}/" + "Data Files/" + file2
  sleep 1
  @browser.button(:xpath => "id('main-content')/app-root/section/div/div/div/table/tbody/tr[2]/td[5]/button[1]").click
end

And(/^User Drops a file into the Drag and Drop box at (.*)$/) do |file|
  dir = Pathname.new(__FILE__).join("../API")
  file = "#{dir}/" + "Data Files/" + file
  dropbox = @browser.div(xpath: "//div[contains(@class, 'well my-drop-zone')]")
  dropboxHover = @browser.div(xpath: "//div[contains(@class, 'well my-drop-zone')]")
  sleep 2
  dropbox.flash
  sleep 2
  #driver=@browser.driver
  #driver.action.move_to(dropbox).perform
  #sleep 2
  #driver.find_element(css: )
  #dropbox.send_keys[:control].send_keys[:click].send_keys file, :return
  #dropbox.send_keys file
  dropbox.attribute(:dropzone).set file
  #driver.find_element(dropbox).send_keys file
  sleep 2
  upload_button = @browser.button(:xpath => "id('main-content')/app-root/section/div/div/div/table/tbody/tr/td[5]/button[1]")
  upload_button.click
  sleep 4
  success_button = @browser.link(:xpath => "id('main-content')/app-root/section/div/div/div/div[2]/div/div/div/a")
  success_button.wait_until(&:present?).click
  sleep 2

end

And(/User downloads error response$/) do
  @browser.a(xpath: '//*[@id="main-content"]/app-root/section/div/div/div/div[2]/div/div/div/a').click
  sleep 6
end

And(/The system checks for the error file at (.*)$/) do |download|
  dir = Pathname.new(__FILE__).join("../API")
  path = "#{dir}/" + "Data Files/" + download
  if File.basename(path, ".*").include? "error"
    errorFile = File.open(path)
    errorFile.read.include? "25"
    errorFile.read.include? "CT - The Clinical Document program name pqrs_mips_indiv is not recognized. Valid program names are MIPS_GROUP, CPCPLUS, or MIPS_INDIV."
    errorFile.close
  else
    return "The file generated is not an error file."
  end
end

And(/^The system opens the file at (.*) and checks its contents for (.*)$/) do |filepath, contents|

end

And(/^The system deletes the downloaded file at (.*)$/) do |download|
  dir = Pathname.new(__FILE__).join("../API")
  File.delete("#{dir}/" + "Data Files/" + download)
end