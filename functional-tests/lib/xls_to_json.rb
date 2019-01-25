require 'roo'
require 'json'

if $PROGRAM_NAME == __FILE__
# Open the spreadsheet to generate JSON from
  xlsx = Roo::Spreadsheet.open('./SpecialStatuses_v01.xlsx')
# foreach sheet in the file, loop the names of the sheets and the sheet data
  xlsx.each_with_pagename do |name, sheet|
    # Skip the Instructions tab
    next if name.eql? 'Instructions'
    # Create new JSON files from the sheet names -- replace spaces in names with underscores
    file_name = name.tr(' ', '_')
    new_file = File.new(File.dirname(__FILE__) + '/' + file_name + '.json', 'w+')
    # get the attribute names from the first row of the sheet
    header = sheet.first_row
    new_file.write('[')
    # loop through each row and hash the values to their respective headers
    2.upto(sheet.last_row) do |line|
      jsn = Hash[(sheet.row(header) - ['team']).zip sheet.row(line)]
      # delete the tin if the value is empty and it is in 'NPI level' or 'Clinician Status for NPIs' sheet
      jsn.delete('tin') if jsn['tin'].eql?(nil) && (name.eql?('NPI level') || name.eql?('Clinician Status for NPIs'))
      puts jsn
      new_file.write(JSON.pretty_generate(jsn))
      new_file.write(',') if line != sheet.last_row
    end
    new_file.write(']')
  end
end