
class UpdateUserInfo

  # This method will read the entire json file
  # @return Json file content
  # @param [String] json_file_name Name of the json file
  def read_json_file(json_file_name)
    # check the file type - read the content only if it's .json
    if json_file_name.chars.last(5).join == '.json'
      # get the file path
      temp_file_path = get_file_location(json_file_name)
      # read the file content
      file = File.read(temp_file_path)
    end
  end

  # This method will seach for the file in all the directories and return the path
  # @param [String] file_name Name of the file (Make sure to specify the file type like XXX.json)
  # @return [String] file path
  def get_file_location(file_name)
    Dir.glob(Dir.pwd + '/**/' + File.basename(file_name))[0]
  end

end