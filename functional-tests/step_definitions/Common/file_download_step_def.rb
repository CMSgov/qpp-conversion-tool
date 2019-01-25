And(/^User deletes previous downloads$/) do
  @fDownload = FileDownload.new(@browser)
  @fDownload.delete_downloaded_files
end

Then(/^User receives the file "(.*)"$/) do |file_name|
  @fDownload = FileDownload.new(@browser)
  @fDownload.verify_file_download(file_name)
end

Then(/^User receives the file "(.*)" after (\d+) seconds$/) do |file_name,sleep_seconds|
  @fDownload = FileDownload.new(@browser)
  @fDownload.verify_file_download(file_name, sleep_seconds)
end

Then(/^User verifies that the "([^"]*)" file successfully downloaded$/) do |file_name|
  @fDownload = FileDownload.new(@browser)
  @fDownload.verify_file_download(file_name)
end