require 'pdf-reader'
require 'open-uri'
require 'csv'

module FileUtil

  # This method will create an absolute folder path starting from the parent_folder specified.
  #   Helpful when working with files so it will work on multiple machines regardless of where Repo is saved.
  #
  # @param parent_folder [string] parent folder/starting point; defaults to project folder, EQRS
  # @param new_folder_path [string] the file path to be added to the parent folder

  def self.set_filepath(new_folder_path, parent_folder = 'qpp-hivvs-test')
    path = Pathname(File.dirname(__FILE__)) # retrieves folder
    dir = nil
    path.ascend {|f| dir = f and break if f.basename.to_s == parent_folder}
    File.expand_path(File.join(dir, new_folder_path))
  end

  # sets the download path based on the environment download folder location
  def self.set_download_filepath(filepath_and_file_ext)
    ENV['DOWNLOAD_FOLDER'] + filepath_and_file_ext
  end


  def find_most_recent_file_in_folder(download_path)
    Dir.glob(download_path).max_by {|f| File.mtime(f)} # finds most recent file
  end

  #---------------
  # CSV
  # --------------

  # Gets a value from a .csv file with multiple records based on a unique field/header and value (field1_name, field1_value)
  # to select the correct record, then returns the value of a field in that record (field2_name)
  # @param [String] csv_filepath - complete filepath to .csv file
  # @param [String] field1_name - field name/header of the field with the unique identifier for the record
  # @param [String] field1_value - field value searching for to uniquely identify the record (i.e., an id or facility number)
  # @param [String] field2_name - name of the field to retrieve the value

  def self.get_csv_file_field_value(csv_filepath, field1_name, field1_value, field2_name)
    field1_name = field1_name.downcase.tr(" ", "_") # convert field1_name to snake case if needed since .csv headers convert to snake case keys in method
    field2_name = field2_name.downcase.tr(" ", "_") # convert field1_name to snake case if needed since .csv headers convert to snake case keys in method
    csv_hash = csv_to_hash(csv_filepath) # converts file to an array of hashes
    csv_hash.each do |hash|
      if hash[field1_name.to_sym].to_s == field1_value
        hash.each do |key2, value2|
          if key2 == field2_name.to_sym
            return value2
          end
        end
      end
    end
  end


  # creates an array of hashes with the headers as keys for in each hash
  # @param [String] csv_filepath - complete filepath to .csv file
  # @param [String] col_separator - separator for the .csv file, defaults to comma ,

  def self.csv_to_hash(csv_filepath, col_separator = ',')
    csv_data = CSV.read(csv_filepath, {col_sep: col_separator, encoding: "UTF-8", headers: true, header_converters: :symbol, converters: :all})
    csv_data.map {|d| d.to_hash} # return csv file as a hash
  end


  # creates an array with each record in the .csv as an item in the array (headers are the first item array[0])
  # @param [String] csv_filepath - complete filepath to .csv file
  # @param [String] col_separator - separator for the .csv file, defaults to comma ,

  def self.csv_to_array(csv_filepath, col_separator = ',')
    CSV.read(csv_filepath, {:col_sep => col_separator})
  end

  # gets all values from all records in a field (header name) and returns into an array in a .csv file
  # @param [String] csv_filepath - complete filepath to .csv file
  # @param [String] field1_name - header name of field to get values for in all records

  def self.get_all_values_one_field(csv_filepath, field1_name)
    field1_name = field1_name.downcase.tr(" ", "_") # convert field1_name to snake case if needed since .csv headers convert to snake case keys in method
    csv_hash = csv_to_hash(csv_filepath) # converts file to an array of hashes
    values_array = []
    csv_hash.each do |hash|
      values_array << hash[field1_name.to_sym].to_s
    end
    values_array
  end


  #---------------
  # PDF
  # --------------

  # Gets pdf text from pdf file based on page number and returns all the text in a string
  # @param [String] filename - complete filepath and filename to .pdf file
  # @param [Integer] page_num - page number in the pdf file
  def self.get_pdf_text_from_page(filename, page_num)
    File.open(filename, "rb") do |io|
      reader = PDF::Reader.new(io)
      reader.page(page_num).text
    end
  end

  # Gets pdf text from entire pdf and returns all the text in a string
  # @param [String] filename - complete filepath and filename to .pdf file
  def self.get_pdf_text_from_file(filename)
    File.open(filename, "rb") do |io|
      reader = PDF::Reader.new(io)
      reader.pages.map(&:text)
    end
  end

  def self.write_content_in_file(filePath,content)
    @filePath = set_filepath(filePath)
    File.write(@filePath,content+ "\n", mode: "a")
  end

end

