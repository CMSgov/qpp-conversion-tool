require 'open-uri'
class FileDownload < QPP_Page
  include Test::Unit::Assertions
  include PageObject

  #
  # This method will delete files out of the browser downloads directory
  #
  def delete_downloaded_files
    if ENV['SAUCEY'] == 'false'
      download_path = ENV["DOWNLOAD_FOLDER"]
      if !Dir.empty?(download_path)
        Dir.foreach(download_path) { |entry|
          fn = File.join(download_path, entry);
          File.delete(fn) if entry != '.' && entry != '..'
        }
      end
    end
  end

  #
  # This method will delete files out of the browser downloads directory
  #
  def delete_downloaded_files_by_name(file_name)
    if ENV['SAUCEY'] == 'false'
        dot_position = file_name.index(".")
        file_name_no_extension = file_name.slice!(0,dot_position)
        delete_matching_regexp(ENV["DOWNLOAD_FOLDER"],file_name_no_extension)
    end
  end


  def delete_matching_regexp(dir, file_name)
    Dir.entries(dir).each do |name|
      path = File.join(dir, name)
      #puts "comparing " + file_name + " and " + name
      if name.downcase.include? file_name.downcase
        puts "found match for " + file_name + " and " + name
        ftype = File.directory?(path) ? Dir : File
        begin
          puts "deleting file "+ name
          ftype.delete(path)
        rescue SystemCallError => e
          $stderr.puts e.message
        end
      end
    end
  end



  #
  # This method will verify that a file downloaded from the browser
  #
  def verify_file_download(file_name, sleep_seconds = 5)
    puts "Verifying download of #{file_name}"

    if ENV['SAUCEY'] == 'false'
      download_path = ENV["DOWNLOAD_FOLDER"]

      # sleep long enough for the download to complete
      sleep(sleep_seconds)
      puts("Checking for '#{file_name}' in downloads directory: #{download_path}")

      if !Dir.empty?(download_path)
        Dir.foreach(download_path) {|entry|
          if entry != '.' && entry != '..' && entry.to_s.downcase == file_name.to_s.downcase
            assert(true)
            return
          end
        }
      end
    else
      file = transferDowloadedFile(file_name,ENV['TRANSFER_FOLDER'])
    end
  end



  #
  # This method will verify that a file downloaded from the browser
  #
  def verify_and_return_download_file_path(file_name, sleep_seconds = 5)
    puts "Verifying download of #{file_name}"

    if ENV['SAUCEY'] == 'false'
      download_path = ENV["DOWNLOAD_FOLDER"]+"/"

      # sleep long enough for the download to complete
      sleep(sleep_seconds)
      puts("Checking for '#{file_name}' in downloads directory: #{download_path}")

      if !Dir.empty?(download_path)
        Dir.foreach(download_path) {|entry|
          if entry != '.' && entry != '..' && entry.to_s.downcase == file_name.to_s.downcase
            assert(true)
          end
        }

        downloadPath = ENV['DOWNLOAD_FOLDER'] + "/"+file_name
        downloaded_file = Dir.glob(downloadPath).max_by(1) {|f| File.mtime(f)}
        puts "file is " + downloaded_file[0]

        puts "single Bene Download is  " + $SINGLE_BENE_FOR_DOWNLOAD
        file = downloaded_file[0]
        return file
      end
    else
      transferDowloadedFile(file_name,ENV['TRANSFER_FOLDER'])
    end
  end




  def transferDowloadedFile(fileName, destLocation)
    file = nil;
    # hard coded the path for time being (this will work only on windows)
    upload_file = "C:\\users\\administrator\\downloads\\#{fileName}"
    puts("Uploading #{upload_file} to Pastebin: " + ENV['PASTEBIN_SERVICE'])
    @browser.execute_script("window.open('','_blank');");
    #@browser.a(:class => 'header-brand').click(:shift)
    download_url = nil
    @browser.window(:index => 1).use do
      $UPLOAD_VM = true
      @browser.goto ENV['PASTEBIN_SERVICE']
      sleep(5)
      @browser.checkbox(:name => 'burn').set true
      @browser.input(:id => 'inputfile').send_keys upload_file
      @browser.button(:type => 'submit').click
      download_url = @browser.body.text
      $UPLOAD_VM = false
      puts download_url
    end
    if destLocation.nil?
      destLocation = ENV['DOWNLOAD_FOLDER']
    end
    file = "#{destLocation}//#{fileName}"
    #file = "C:/myprojects/open_submission/qpp-hivvs-test/data/excel_files/Torphy little and Bogisich-sample-list-with-data.xlsx"

    #local_file = new FileInputStream(new File(download_url));
    open(file,'wb') do |file|
      file << open(download_url).read
    end


    file
  end

end