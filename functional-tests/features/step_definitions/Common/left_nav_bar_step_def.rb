Then(/^User verifies that the text, Quality measures have changed for this year, is displayed$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  qua_measure_text = @left_nav.get_qua_measure_text
  expect(qua_measure_text.downcase). to eq ("Quality measures have changed for this year".downcase)
end

And(/^User verifies that an icon is displayed$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  icon_img_elem = @left_nav.get_icon_img_elem
  expect(icon_img_elem). to exist
end

And(/^User verifies that the text, Learn about measures and what you need to report, is displayed$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  learn_about_text = @left_nav.get_learn_about_report_text
  expect(learn_about_text.downcase). to eq ("Learn about measures and what you need to report".downcase)
end

And(/^User verifies that the Explore Measures Link is displayed$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  exp_measure_text = @left_nav.get_exp_measure_link
  expect(exp_measure_text.downcase). to eq ("EXPLORE MEASURES".downcase)
end

And(/^User clicks on Explore Measure Link$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  @left_nav.click_exp_measure_link
  sleep 30
end

And(/^User verifies that the landing page has a Download measure rate section$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  download_text = @left_nav.get_download_msr_rate_button.text
  expect(download_text.downcase). to eq ("DOWNLOAD MEASURE RATE".downcase)
end

And(/^User verifies that the landing page has a Download data confirmation section$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  download_text = @left_nav.get_download_data_conf_button.text
  expect(download_text.downcase). to eq ("DOWNLOAD DATA CONFIRMATION".downcase)
end



And(/^User verifies that the blue banner displays the Organization Name and TIN Label$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  org_name_tin_label = @left_nav.verify_july_org_name_tin
  expect(org_name_tin_label). to exist
end

And(/^User verifies that the blue banner displays the text CMS Web Interface is closed$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  cms_web_text = @left_nav.verify_cms_web_int_closed
  expect(cms_web_text.downcase). to eq ("cms web interface is closed.\nhere’s what you can do until the 2019 submission begins.".downcase)
end

And(/^User verifies that the link Return to Performance Feedback is displayed$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  return_to_text = @left_nav.verify_return_to_perform
  expect(return_to_text.downcase). to eq ("RETURN TO PERFORMANCE FEEDBACK".downcase)
end

And(/^User verifies that the text Select and download your reports from previous years in Excel format is displayed$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  select_text = @left_nav.verify_select_download_report
  expect(select_text.downcase). to eq ("Select and download your reports from previous years in Excel format".downcase)
end

And(/^User verifies that the text January (\d+) \- March (\d+) has been displayed under submission period label$/) do |arg1, arg2|
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  jan_march_text = @left_nav.verify_jan_march_text
  expect(jan_march_text.downcase). to eq ("January 2018 - March 2018".downcase)
end

And(/^User verifies that the text Download the Measure Rates and the Data Confirmation reports is displayed$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  reports_text = @left_nav.verify_measure_rates_report
  expect(reports_text.downcase). to eq ("Download the Measure Rates and the Data Confirmation reports.".downcase)
end

And(/^User verifies that the Download button is displayed to the right of the data confirmation reports text$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  download_button = @left_nav.get_download_msr_rate_button.text
  expect(download_button.downcase). to eq ("DOWNLOAD MEASURE RATE".downcase)
end

And(/^User clicks the Download button$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  @left_nav.click_download_button_text
  sleep 5
end

And(/^User clicks the Download measure rates button$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  @left_nav.click_download_msr_rate_button
  sleep 5
end

And(/^User clicks the Download data confirmation button$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  @left_nav.click_download_data_conf_button
  sleep 5
end


And(/^User clicks on return to performance feedback link and verifies that he is taken to performance feedback page$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  @left_nav.click_return_to_perform
  sleep 1
  perform_feed = @left_nav.verify_perform_feed_title
  expect(perform_feed). to exist
end

And(/^User verifies that the download pills display when the download occurs$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  pills_disp = @left_nav.verify_download_pills_display
  expect(pills_disp). to exist
end


And(/^User verifies that the left nav displays the Account Dashboard Label$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  acc_dash_label = @left_nav.verify_account_dashboard
  expect(acc_dash_label.downcase). to eq ("Account Dashboard".downcase)
end

And(/^User verifies that the left nav displays the Organization Name$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  org_name_label = @left_nav.verify_july_org_name
  expect(org_name_label). to exist
end

And(/^User verifies that the left nav displays the TIN Label$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  actual = @left_nav.verify_left_nav_tin_text.split(" ")[0]
  expect(actual). to eq ("TIN#")
end

And(/^User verifies that the left nav displays the Quality Data Reporting Label$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  quality_label = @left_nav.verify_quality_data_report
  expect(quality_label.downcase). to eq ("Quality Data Reporting".downcase)
end

And(/^User verifies that the left nav displays the CMS Web Interface Label$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  cms_web_label = @left_nav.verify_cms_web_interface
  expect(cms_web_label.downcase). to eq ("CMS Web Interface".downcase)
end


And(/^User verifies that left navigation bar collapsed$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  @left_nav.collapse_left_nav_bar
  sleep 2
  collapse = @left_nav.left_nav_bar_collapsed
  expect(collapse). to exist
end




And(/^User verifies that left navigation bar expanded$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  @left_nav.expand_left_nav_bar
  sleep 2
  expanded = @left_nav.left_nav_bar_expanded
  expect(expanded). to exist
end



And(/^User verifies that the landing page has a blue banner section$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)
  page_header_div = @left_nav.get_page_header_div
  css_attribute = page_header_div.style('background-color').to_s
  expect(css_attribute). to eq("rgba(0, 51, 102, 1)")
end


And(/^User verifies that left nav is able to expand and collapse$/) do
  @left_nav = Left_Nav_Bar_Page_July.new(@browser)

  if (@left_nav.get_collapse_left_nav_bar.exists?)
    @left_nav.click_collapse_left_nav_bar
    expect(@left_nav.get_expand_left_nav_bar.exists?)

    @left_nav.click_expand_left_nav_bar
    expect(@left_nav.get_collapse_left_nav_bar.exists?)
  elsif (@left_nav.get_expand_left_nav_bar.exists?)
      @left_nav.click_expand_left_nav_bar
      expect(@left_nav.get_collapse_left_nav_bar.exists?)

      @left_nav.click_expand_left_nav_bar
      expect(@left_nav.get_collapse_left_nav_bar.exists?)
  else
    expect(false).to be(true), "Expand and collapse logic for left nav failed"
  end
end

And(/^User checks for Explore Measure File$/) do
  check_explore_measure_download
end


And(/^User checks for download button files$/) do
  check_download_file_results
end


And(/^User deletes old downloaded files from directory$/) do
  delete_download_file_results
end


#This method will evaluate a row in a spreadsheet
#to validate that the field values match what is on the UI or that
#those same fields are blank for when we download without data
def check_explore_measure_download

  puts "checking daily activity file"

  file = nil;

  downloadPath = ENV['DOWNLOAD_FOLDER'] + "/2018-Web-Interface-Measures-and-supporting-documents*.zip"
  downloaded_file = Dir.glob(downloadPath).max_by(1) {|f| File.mtime(f)}
  puts "Found file " + downloaded_file[0]

  #puts "single Bene Download is  " + $SINGLE_BENE_FOR_DOWNLOAD
  file = downloaded_file[0]


  if ENV['SAUCEY'] != 'false'
    upload_file = "#{ENV['DOWNLOAD_FOLDER']}\\#{file}"
    @browser.a(:class=> 'header-brand').click(:shift)
    download_url = nil
    @browser.window(:index => 1).use do
      $UPLOAD_VM = true
      @browser.goto ENV['PASTEBIN_SERVICE']
      @browser.checkbox(:name => 'burn').set true
      @browser.input(:id=> 'inputfile').send_keys upload_file
      @browser.button(:type => 'submit').click
      download_url = @browser.body.text
      $UPLOAD_VM = false
      puts download_url
    end
  end
end


def delete_download_file_results
  Dir.glob(ENV['DOWNLOAD_FOLDER'] + "/*").select{ |file| /data-confirmation/.match file }.each { |file| File.delete(file)}
  Dir.glob(ENV['DOWNLOAD_FOLDER'] + "/*").select{ |file| /measure-rates-report/.match file }.each { |file| File.delete(file)}
end


#This method will evaluate a row in a spreadsheet
#to validate that the field values match what is on the UI or that
#those same fields are blank for when we download without data
def check_download_file_results

  puts "checking daily activity file"

  file = nil;

  downloadPath = ENV['DOWNLOAD_FOLDER'] + "/*data-confirmation*.xlsx"
  downloaded_file = Dir.glob(downloadPath).max_by(1) {|f| File.mtime(f)}
  puts "Found file " + downloaded_file[0]

  #puts "single Bene Download is  " + $SINGLE_BENE_FOR_DOWNLOAD
  file = downloaded_file[0]


  file = nil;

  downloadPath = ENV['DOWNLOAD_FOLDER'] + "/*measure-rates-report*.xlsx"
  downloaded_file = Dir.glob(downloadPath).max_by(1) {|f| File.mtime(f)}
  puts "Found file " + downloaded_file[0]

  #puts "single Bene Download is  " + $SINGLE_BENE_FOR_DOWNLOAD
  file = downloaded_file[0]


  if ENV['SAUCEY'] != 'false'
    upload_file = "#{ENV['DOWNLOAD_FOLDER']}\\#{file}"
    @browser.a(:class=> 'header-brand').click(:shift)
    download_url = nil
    @browser.window(:index => 1).use do
      $UPLOAD_VM = true
      @browser.goto ENV['PASTEBIN_SERVICE']
      @browser.checkbox(:name => 'burn').set true
      @browser.input(:id=> 'inputfile').send_keys upload_file
      @browser.button(:type => 'submit').click
      download_url = @browser.body.text
      $UPLOAD_VM = false
      puts download_url
    end
  end



end

