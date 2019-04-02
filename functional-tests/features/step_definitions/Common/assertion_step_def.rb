And(/^User is viewing the page title=(.*)|User is taken to the {string} page.$/) do |title|
  @verifi = Verifications.new(@browser)
  @verifi.verify_page_title(title)
end

And(/^User verifies the page title "(.*)" is displayed$/) do |title|
  @verifi = Verifications.new(@browser)
  @verifi.verify_page_title(title)
end

And(/^User verifies "(.*)" is displayed as title$/) do |title|
  @verifi = Verifications.new(@browser)
  @verifi.verify_page_title(title)
end

Then('User is taken to {string} page for the TIN') do |title|
  @verifi = Verifications.new(@browser)
  @verifi.verify_page_title(title)
  @verifi.verify_text('TIN# ' + $userObj[:tin], @browser.element(xpath: "//p[@class='practice-tin']").text)
end

And(/^User verifies the hover text displayed when hovers mouse over on text$/) do |table|
  # table is a table.hashes.keys # => [:hover_on, :hover_text]
  @verifi = Verifications.new(@browser)
  table.hashes.each do |hover|
    @browser.element(xpath: "//*[contains(.,' Learn more about']").when_present(30).hover
    actual_text = @browser.element(xpath: "//*[.='#{hover['hover_on']}']/ancestor::app-instruction-modal").attribute_value('title')
    @verifi.verify_text(hover['hover_text'], actual_text)
  end
end

Then(/^User verifies pop up window displayed with header "([^"]*)"$/) do |modal_dialog_title|
  @verifi = Verifications.new(@browser)
  @verifi.verify_modal_dialog_displayed(modal_dialog_title)
end

And(/^User verifies (.*) in pop up window$/) do |popup_text|
  @validations = Validation_Messages_Page.new(@browser)
  @validations.verify_modal_dialog_body_content(popup_text)
end

And(/^User closes the pop up window$/) do
  @browser.button(xpath: "//div[contains(@class, 'modal-dialog')]//button[contains(@class, 'close pull-right')]", visible: true).wait_until(&:present?).fire_event :onclick
end

And(/^User verifies text (.*) displayed$/) do |text|
  @verifi = Verifications.new(@browser)
  @verifi.verify_element_present(@browser.element(xpath: "//*[normalize-space(.)='#{text}']"))
  @browser.element(xpath: "//*[normalize-space(.)='#{text}']").scroll.to :bottom
end

Then(/^User verifies popup with message is displayed$/) do |table|
  # table is a table.hashes.keys # => [:message]
  @validations = Validation_Messages_Page.new(@browser)
  table.hashes.each do |popup|
    @validations.verify_modal_dialog_body_content(popup['message'])
  end
end

Then(/^User verifies "([^"]*)" link is displayed on the page$/) do |link_name|
  Verifications.new(@browser).verify_element_present(@browser.link(xpath: "//a[normalize-space(.)='#{link_name}']"))
end