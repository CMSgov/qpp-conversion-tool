And (/^User unlocks the 7\/1 experience features$/) do
  @browser.driver.execute_script('window.sessionStorage.setItem("forceFeedbackReady", "1");')
  sleep(5)
  @browser.refresh()
  sleep(5)
end

And (/^User clears all feature unlocks$/) do
  @browser.driver.execute_script("window.sessionStorage.clear();")
  @browser.refresh()
end


And(/^User run "(.*)" command in the browser console$/) do |command|
  @browser.driver.execute_script(command)
  puts "ran #{command} in the browser console."
end

And(/^User unlocks the 4\/1 experience features$/) do
  @browser.driver.execute_script('window.sessionStorage.setItem("forceFeedbackOpen", "1");')
  sleep(5)
  @browser.refresh()
  sleep(5)
end