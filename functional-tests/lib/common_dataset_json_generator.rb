# ======================================================
# This script will pull in an Excel spreadsheet with users intended
#   to import into the practice_status.json & practice_clinician_status.json
#   files.  It can optionally add/update users in the User Manager at the
#   same time.
#
#   Usage:
#   - Open command prompt and navigate to folder with common_dataset_json_generator.rb
#   - Run the command:
#     ruby common_dataset_json_generator.rb --path="{file_path}" --providerVer={provider_ver} --practiceStatusVer={practice_status_ver} --clinicianStatusVer={clinician_status_ver} --virtualGroupVer={virtual_group_ver}
#       where {file_path} is the full path to the Excel file you importing
#       and {provider_ver} is the new version of the provider.json
#       and {practice_status_ver} is the new version of the practice_status.json
#       and {clinician_status_ver} is the new version of the practice_clinician_status.json
#       and {virtual_group_ver} is the new version of the virtual_group_status.json
#
#   Options:
#   - Include the -h option at the end of the command for a usage summary
#   - Include the -um option to add/update User Manager users
#   - Include the -eu option to generate data for row with empty user names
#
#   Assumptions:
#   - It is assumed there is a file in the same directory as the spreadsheet named practice_status_old.json with the previous version, and it will write to a file named practice_status.json
#   - It is assumed there is a file in the same directory as the spreadsheet named practice_clinician_status_old.json with the previous version, and it will write to a file named practice_clinician_status.json
#   - It is assumed there is a file in the same directory as the spreadsheet named virtual_group_status_old.json with the previous version, and it will write to a file named virtual_group_status.json
#
# ======================================================

require 'date'
require 'bundler/setup'
require 'user_manager_client'
require 'roo'
require 'json'
require 'optparse'
require 'ostruct'

class CommonDatasetGeneratorOptions
  attr_accessor :file_path,
                :output_path,
                :old_practice_status_file,
                :new_practice_status_file,
                :practice_status_version,
                :old_clinician_status_file,
                :new_clinician_status_file,
                :clinician_status_version,
                :old_virtual_group_status_file,
                :new_virtual_group_status_file,
                :virtual_group_status_version,
                :old_ptan_file,
                :new_ptan_file,
                :generate_um_users,
                :include_empty_users

  def initialize
    self.file_path                     = ""
    self.output_path                   = ""
    self.old_practice_status_file      = ""
    self.new_practice_status_file      = ""
    self.old_clinician_status_file     = ""
    self.new_clinician_status_file     = ""
    self.old_virtual_group_status_file = ""
    self.new_virtual_group_status_file = ""
    self.old_ptan_file                 = ""
    self.new_ptan_file                 = ""
    self.generate_um_users             = false
    self.include_empty_users           = false
  end

  def define_options(parser)
    parser.banner = "Usage: common_dataset_json_generator.rb [options]"

    parser.on("-pFILEPATH", "--path=FILEPATH", "(Required) The full path to the Excel file to import") do |path|
      self.file_path   = path
      puts "Excel file path: #{path}"

      self.output_path = File.dirname(path)
      puts "Output file directory: #{self.output_path}"

      self.old_practice_status_file      = "#{self.output_path}\\practice_status_old.json"
      self.new_practice_status_file      = "#{self.output_path}\\practice_status.json"
      self.old_clinician_status_file     = "#{self.output_path}\\practice_clinician_status_old.json"
      self.new_clinician_status_file     = "#{self.output_path}\\practice_clinician_status.json"
      self.old_virtual_group_status_file = "#{self.output_path}\\virtual_group_status_old.json"
      self.new_virtual_group_status_file = "#{self.output_path}\\virtual_group_status.json"
      self.old_ptan_file                 = "#{self.output_path}\\ptan_old.json"
      self.new_ptan_file                 = "#{self.output_path}\\ptan.json"
    end

    parser.on("--practiceStatusVer=VERSION", "(Required) The new version number of the practice_status.json file") do |version|
      self.practice_status_version = self.parseVersionNum(version)
      puts "New practice_status.json file version: #{version}"
    end

    parser.on("--clinicianStatusVer=VERSION", "(Required) The new version number of the practice_clinician_status.json file") do |version|
      self.clinician_status_version = self.parseVersionNum(version)
      puts "New practice_clincian_status.json file version: #{version}"
    end

    parser.on("--virtualGroupVer=VERSION", "(Required) The new version number of the virtual_group_status.json file") do |version|
      self.virtual_group_status_version = self.parseVersionNum(version)
      puts "New virtual_group_status.json file version: #{version}"
    end

    parser.on("--ptanVer=VERSION", "(Required) The new version number of the ptan.json file") do |version|
      self.virtual_group_status_version = self.parseVersionNum(version)
      puts "New ptan.json file version: #{version}"
    end

    parser.on("-um", "Add/update user manager users") do |um|
      self.generate_um_users = um
      puts "Generating user manager records"
    end

    parser.on("-eu", "Generate data for rows with empty user names") do |um|
      self.include_empty_users = um
      puts "Include user rows with empty user names"
    end

    puts ""

    parser.on_tail("-h", "--help", "Show this message") do
      puts parser
      puts "Showing help and exiting"
      exit
    end
  end

  def parse(args)
    @options = CommonDatasetGeneratorOptions.new
    @args = OptionParser.new do |parser|
      @options.define_options(parser)
      parser.parse!(args)
    end
    @options
  end

  def parseVersionNum(version)
    if (true if Integer(version) rescue false)
      number = version.to_i
    elsif (true if Float(version) rescue false)
      number = version.to_f
    else
      number = version
    end

    number
  end
end

class CommonDatasetGenerator
  def initialize(args)
    @options = CommonDatasetGeneratorOptions.new
    @options = @options.parse(args)
  end

  def generate_dataset
    options = @options

    if options.file_path == "" || !File.exists?(options.file_path)
      puts "A valid file path is required"
      exit
    end

    if options.old_practice_status_file == "" || !File.exists?(options.old_practice_status_file)
      puts "Missing practice_status_old.json"
      exit
    end

    if options.old_clinician_status_file == "" || !File.exists?(options.old_clinician_status_file)
      puts "Missing provider_clinician_status_old.json"
      exit
    end

    if options.old_virtual_group_status_file == "" || !File.exists?(options.old_virtual_group_status_file)
      puts "Missing virtual_group_status_old.json"
      exit
    end

    if options.old_ptan_file == "" || !File.exists?(options.old_ptan_file)
      puts "Missing ptan_old.json"
      exit
    end

    config = UserManagerClient::Configuration.new
    config.host = 'cmsdocker.titania.solutions:8080'
    api = UserManagerClient::ApiClient.new config
    $um_client = UserManagerClient::UsersApi.new api

    spreadsheet = Roo::Spreadsheet.open(options.file_path)

    ds_provider_array = []
    ds_clinician_array = []
    ds_vg_array = []
    ds_ptan_array = []
    @um_new_array = []
    @um_updates_array = []

    @um_new_index = 0
    @um_updates_index = 0

    tin_npi_max = {}
    tin_npi_cur = {}
    tin_org_names = {}

    puts ""

    # Initially populate the practice status file with records from the last run
    ds_provider_string = IO.read (options.old_practice_status_file)
    ds_provider_hash = JSON.parse ds_provider_string

    ds_provider_hash.each_index {|index|
      ds_provider_array.push(ds_provider_hash[index])
    }

    ds_provider_index = ds_provider_array.length
    puts "Pulled in #{ds_provider_array.length} practices from the previous practice status file"

    # Initially populate the clinician status file with records from the last run
    ds_clinician_string = IO.read (options.old_clinician_status_file)
    ds_clinician_hash = JSON.parse ds_clinician_string

    ds_clinician_hash.each_index {|index|
      ds_clinician_array.push(ds_clinician_hash[index])
    }

    ds_clinician_index = ds_clinician_array.length
    puts "Pulled in #{ds_clinician_array.length} clinicians from the previous practice status file"

    ds_clinician_array.each do |c|
      tin = c["tin"]
      if tin_npi_cur[tin] == nil
        tin_npi_cur[tin] = 1
      else
        tin_npi_cur[tin] += 1
      end
    end

    # Initially populate the virtual group status file with records from the last run
    ds_vg_string = IO.read (options.old_virtual_group_status_file)
    ds_vg_hash = JSON.parse ds_vg_string

    ds_vg_hash.each_index {|index|
      ds_vg_array.push(ds_vg_hash[index])
    }

    ds_vg_index = ds_vg_array.length
    puts "Pulled in #{ds_vg_array.length} virtual groups from the previous virtual group status file"

    # Initially populate the ptan file with records from the last run
    ds_ptan_string = IO.read (options.old_ptan_file)
    ds_ptan_hash = JSON.parse ds_ptan_string

    ds_ptan_hash.each_index {|index|
      ds_ptan_array.push(ds_ptan_hash[index])
    }

    ds_ptan_index = ds_ptan_array.length
    puts "Pulled in #{ds_ptan_array.length} PTANs from the previous ptan file"

    # Iterate through the worksheets
    spreadsheet.each_with_pagename do |name, ws|
      if name.downcase == "practices"
        headers = Hash.new
        ws.row(1).each_with_index do |header,i|
          headers[header.strip] = i
        end

        fields = %w[mips_eligible is_non_patient_facing is_hospital_based is_small_group is_rural is_hpsa is_asc is_low_volume has_aci_hardship has_ia_study]

        ws.each_with_index do |row,i|
          # Skip the header row and any row where the first column is empty
          if row[0].to_s.strip == "" || i == 0
            next
          end

          user_name = row[headers["um_user_name"]].to_s.strip
          if !options.include_empty_users && (user_name == nil || user_name == "")
            next
          end

          tin = row[headers["tin"]].to_s.strip
          org_name = row[headers["org_name"]].to_s.strip
          ptan = row[headers["ptan"]].to_s.strip

          tin_npi_max[tin] = row[headers["num_npis"]].to_i
          tin_npi_cur[tin] = 0
          tin_org_names[tin] = org_name

          existing_practice_index = self.get_practice_index_from_array_by_tin(ds_provider_array, tin)
          if existing_practice_index < 0
            ds_provider_array[ds_provider_index] = {}
            ds_provider_array[ds_provider_index]["tin"]                 = tin
            ds_provider_array[ds_provider_index]["org_name"]            = org_name
            ds_provider_array[ds_provider_index]["first_approval_date"] = row[headers["first_approval_date"]].to_s.strip

            # Flags
            ds_provider_array[ds_provider_index] = self.get_flag_values_from_excel(row, ds_provider_array[ds_provider_index], headers, fields)

            ds_provider_index += 1
          else
            # Flags
            ds_provider_array[existing_practice_index] = self.get_flag_values_from_excel(row, ds_provider_array[existing_practice_index], headers, fields)
            ds_provider_array[existing_practice_index]["first_approval_date"] = row[headers["first_approval_date"]].to_s.strip
          end

          existing_ptan_index = self.get_practice_index_from_array_by_tin(ds_ptan_array, tin)
          if existing_ptan_index < 0 && ptan != ""
            ds_ptan_array[ds_ptan_index] = {}
            ds_ptan_array[ds_ptan_index]["tin"]  = tin
            ds_ptan_array[ds_ptan_index]["ptan"] = ptan

            ds_ptan_index += 1
          end

          # User manager
          self.generate_um_user(row, headers, tin, nil, false, "entityId", row[headers["entity_id"]].to_s.strip)
        end
      elsif name.downcase == "virtual groups"
        headers = Hash.new
        ws.row(1).each_with_index do |header,i|
          headers[header.strip] = i
        end

        g_fields = %w[mips_eligible is_non_patient_facing is_hospital_based is_rural is_hpsa is_asc is_low_volume has_aci_hardship has_ia_study]
        vg_fields = %w[is_non_patient_facing is_hospital_based is_rural is_hpsa is_asc is_low_volume has_aci_hardship has_ia_study]

        ws.each_with_index do |row,i|
          # Skip the header row and any row where the first column is empty
          if row[0].to_s.strip == "" || i == 0
            next
          end

          user_name = row[headers["um_user_name"]].to_s.strip
          if !options.include_empty_users && (user_name == nil || user_name == "")
            next
          end

          tin = row[headers["tin"]].to_s.strip
          org_name = row[headers["org_name"]].to_s.strip
          ptan = row[headers["ptan"]].to_s.strip
          entity_id = row[headers["entity_id"]].to_s.strip

          tin_npi_max[tin] = row[headers["num_npis"]].to_i
          tin_npi_cur[tin] = 0
          tin_org_names[tin] = org_name

          # Group status entry
          existing_practice_index = self.get_practice_index_from_array_by_tin(ds_provider_array, tin)
          if existing_practice_index < 0
            ds_provider_array[ds_provider_index] = {}
            ds_provider_array[ds_provider_index]["tin"]                 = tin
            ds_provider_array[ds_provider_index]["org_name"]            = org_name
            ds_provider_array[ds_provider_index]["first_approval_date"] = row[headers["first_approval_date"]].to_s.strip

            # Flags
            ds_provider_array[ds_provider_index] = self.get_flag_values_from_excel(row, ds_provider_array[ds_provider_index], headers, g_fields, "g_")
            ds_provider_array[ds_provider_index]["is_small_group"] = row[headers["g_is_small_group"]].to_s.strip.downcase == "true"

            ds_provider_index += 1
          else
            ds_provider_array[existing_practice_index]["first_approval_date"] = row[headers["first_approval_date"]].to_s.strip

            # Flags
            ds_provider_array[existing_practice_index] = self.get_flag_values_from_excel(row, ds_provider_array[existing_practice_index], headers, g_fields, "g_")
            ds_provider_array[existing_practice_index]["is_small_group"] = row[headers["g_is_small_group"]].to_s.strip.downcase == "true"
          end

          # Virtual group status entry
          existing_vg_index = self.get_practice_index_from_array_by_tin(ds_vg_array, tin)
          if existing_vg_index < 0
            ds_vg_array[ds_vg_index] = {}
            ds_vg_array[ds_vg_index]["tin"]              = tin
            ds_vg_array[ds_vg_index]["virtual_group_id"] = entity_id

            # Flags
            ds_vg_array[ds_vg_index] = self.get_flag_values_from_excel(row, ds_vg_array[ds_vg_index], headers, vg_fields, "vg_")
            ds_vg_array[ds_vg_index]["clinician_count"] = self.get_clinician_count(row, headers, "vg_clinician_count")

            ds_vg_index += 1
          else
            ds_vg_array[existing_vg_index]["virtual_group_id"] = entity_id

            # Flags
            ds_vg_array[existing_vg_index] = self.get_flag_values_from_excel(row, ds_vg_array[existing_vg_index], headers, vg_fields, "vg_")
            ds_vg_array[existing_vg_index]["clinician_count"] = self.get_clinician_count(row, headers, "vg_clinician_count")
          end

          existing_ptan_index = self.get_practice_index_from_array_by_tin(ds_ptan_array, tin)
          if existing_ptan_index < 0 && ptan != ""
            ds_ptan_array[ds_ptan_index] = {}
            ds_ptan_array[ds_ptan_index]["tin"]  = tin
            ds_ptan_array[ds_ptan_index]["ptan"] = ptan

            ds_ptan_index += 1
          end

          # User manager
          self.generate_um_user(row, headers, tin, nil, false, "entityId", entity_id)
        end
      elsif name.downcase == "clinicians"
        headers = Hash.new
        ws.row(1).each_with_index do |header,i|
          headers[header.strip] = i
        end

        fields = %w[mips_eligible is_non_patient_facing is_hospital_based is_small_group is_rural is_hpsa is_asc is_low_volume has_aci_hardship has_ia_study has_extreme_hardship is_maqi]

        ws.each do |row|
          if row[0].to_s.strip == "" || headers[row[0]] != nil
            next
          end

          user_name = row[headers["um_user_name"]].to_s.strip
          if !options.include_empty_users && (user_name == nil || user_name == "")
            next
          end

          tin = row[headers["tin"]].to_s.strip
          npi = row[headers["npi"]].to_s.strip
          org_name = row[headers["org_name"]].to_s.strip

          first_name = row[headers["first_name"]].to_s.strip
          if first_name.length > 25 # truncate first_name to 25 characters
            first_name = first_name[0...25]
          end

          last_name = row[headers["last_name"]].to_s.strip
          if last_name.length > 25 # truncate last_name to 25 characters
            last_name = last_name[0...25]
          end

          if tin_org_names[tin] == nil
            tin_org_names[tin] = org_name
          end

          if npi != nil && npi != ""
            existing_clinician_index = self.get_provider_index_from_array_by_tin_npi(ds_clinician_array, tin, npi)
          end

          if existing_clinician_index < 0
            existing_clinician_index = self.get_provider_index_from_array_by_name(ds_clinician_array, tin, first_name, last_name)
          end

          if existing_clinician_index < 0
            ds_clinician_array[ds_clinician_index] = {}
            ds_clinician_array[ds_clinician_index]["tin"]                 = tin
            ds_clinician_array[ds_clinician_index]["npi"]                 = npi
            ds_clinician_array[ds_clinician_index]["org_name"]            = org_name
            ds_clinician_array[ds_clinician_index]["first_name"]          = first_name
            ds_clinician_array[ds_clinician_index]["last_name"]           = last_name
            ds_clinician_array[ds_clinician_index]["first_approval_date"] = row[headers["first_approval_date"]].to_s.strip

            # Flags
            ds_clinician_array[ds_clinician_index] = self.get_flag_values_from_excel(row, ds_clinician_array[ds_clinician_index], headers, fields)
            ds_clinician_array[ds_clinician_index]["complex_patient_score"] = row[headers["complex_patient_score"]].to_s.strip.to_f

            if tin_npi_cur[tin] != nil
              tin_npi_cur[tin] += 1
            end

            ds_clinician_index += 1
          else
            if (ds_clinician_array[existing_clinician_index]["npi"] == nil || ds_clinician_array[existing_clinician_index]["npi"] == "") && npi != nil && npi != ""
              puts "Updating #{row[headers["um_role"]]} NPI to #{npi}"
              ds_clinician_array[existing_clinician_index]["npi"] = npi
            end

            ds_clinician_array[existing_clinician_index]["first_approval_date"] = row[headers["first_approval_date"]].to_s.strip

            ds_clinician_array[existing_clinician_index] = self.get_flag_values_from_excel(row, ds_clinician_array[existing_clinician_index], headers, fields)
            ds_clinician_array[existing_clinician_index]["complex_patient_score"] = row[headers["complex_patient_score"]].to_s.strip.to_f
          end

          # User manager
          self.generate_um_user(row, headers, tin, npi, true)
        end
      else
        next
      end
    end

    # Generate remaining clinicians that will fill out the # of NPIs specified for each practice
    tin_npi_max.each_key do |tin|
      while tin_npi_cur[tin] < tin_npi_max[tin] do
        tin_npi_cur[tin] += 1
        first_name = tin_org_names[tin]
        last_name = "Clinician #{tin_npi_cur[tin]}"
        npi = nil

        existing_index = self.get_practice_index_from_array_by_tin(ds_provider_array, tin)

        # -----------------------------------
        # Add to clinician_status file
        # -----------------------------------
        ds_clinician_array[ds_clinician_index] = {}
        ds_clinician_array[ds_clinician_index]["tin"]                 = tin
        ds_clinician_array[ds_clinician_index]["npi"]                 = npi
        ds_clinician_array[ds_clinician_index]["org_name"]            = tin_org_names[tin]
        ds_clinician_array[ds_clinician_index]["first_name"]          = first_name
        ds_clinician_array[ds_clinician_index]["last_name"]           = last_name
        ds_clinician_array[ds_clinician_index]["first_approval_date"] = "2015/01/01"

        # Flags
        ds_clinician_array[ds_clinician_index]["is_mips_eligible"]      = true
        ds_clinician_array[ds_clinician_index]["is_non_patient_facing"] = false
        ds_clinician_array[ds_clinician_index]["is_hospital_based"]     = existing_index >= 0 ? ds_provider_array[existing_index]["is_hospital_based"] : false
        ds_clinician_array[ds_clinician_index]["is_small_group"]        = existing_index >= 0 ? ds_provider_array[existing_index]["is_small_group"] : false
        ds_clinician_array[ds_clinician_index]["is_rural"]              = existing_index >= 0 ? ds_provider_array[existing_index]["is_rural"] : false
        ds_clinician_array[ds_clinician_index]["is_hpsa"]               = existing_index >= 0 ? ds_provider_array[existing_index]["is_hpsa"] : false
        ds_clinician_array[ds_clinician_index]["is_asc"]                = existing_index >= 0 ? ds_provider_array[existing_index]["is_asc"] : false
        ds_clinician_array[ds_clinician_index]["is_low_volume"]         = false
        ds_clinician_array[ds_clinician_index]["has_aci_hardship"]      = false
        ds_clinician_array[ds_clinician_index]["has_ia_study"]          = false
        ds_clinician_array[ds_clinician_index]["has_extreme_hardship"]  = false

        ds_clinician_index += 1
      end
    end

    # -----------------------------------
    # Change file version numbers
    # -----------------------------------
    file_date = Date.today.to_s

    if ds_provider_array.length > 0 && ds_provider_array[0]["meta"] != nil
      ds_provider_array[0]["meta"]["version"] = options.practice_status_version
      ds_provider_array[0]["meta"]["date"] = file_date
    end

    if ds_clinician_array.length > 0 && ds_clinician_array[0]["meta"] != nil
      ds_clinician_array[0]["meta"]["version"] = options.clinician_status_version
      ds_clinician_array[0]["meta"]["date"] = file_date
    end

    if ds_vg_array.length > 0 && ds_vg_array[0]["meta"] != nil
      ds_vg_array[0]["meta"]["version"] = options.virtual_group_status_version
      ds_vg_array[0]["meta"]["date"] = file_date
    end

    # -----------------------------------
    # Write data to files
    # -----------------------------------
    puts ""

    ds_provider_string = JSON.pretty_generate(ds_provider_array)
    IO.write(options.new_practice_status_file, ds_provider_string)
    puts "Generated #{ds_provider_array.length} providers for practice_status.json"

    ds_clinician_string = JSON.pretty_generate(ds_clinician_array)
    IO.write(options.new_clinician_status_file, ds_clinician_string)
    puts "Generated #{ds_clinician_array.length} clinicians for practice_clinician_status.json"

    ds_vg_string = JSON.pretty_generate(ds_vg_array)
    IO.write(options.new_virtual_group_status_file, ds_vg_string)
    puts "Generated #{ds_vg_array.length} virtual groups for virtual_group_status.json"

    ds_ptan_string = JSON.pretty_generate(ds_ptan_array)
    IO.write(options.new_ptan_file, ds_ptan_string)
    puts "Generated #{ds_ptan_array.length} PTANs for ptan.json"

    um_new_string = JSON.pretty_generate(@um_new_array)
    IO.write(options.output_path + "\\user_manager_new.json", um_new_string)

    um_updates_string = JSON.pretty_generate(@um_updates_array)
    IO.write(options.output_path + "\\user_manager_updates.json", um_updates_string)

    # -----------------------------------
    # Perform User Manager changes
    # -----------------------------------
    if options.generate_um_users
      @um_new_array.each do |u|
        $um_client.create_user(u)
      end
      puts "Created #{@um_new_array.length} new users in User Manager."

      @um_updates_array.each do |u|
        $um_client.update_user(u[:id], u)
      end
      puts "Updated #{@um_updates_array.length} users in User Manager."
    end
  end

  def generate_um_user(row, headers, tin, npi, include_npi = false, entity_id_key = nil, entity_id = nil)
    exists_in_um = false
    um_id = row[headers["um_id"]].to_s.strip
    user_name = row[headers["um_user_name"]].to_s.strip

    if um_id != ""
      user = $um_client.get_user(um_id.to_i)
    end

    # Try to retrieve the user by role and user_name
    if user == nil
      users = $um_client.users_get()
      if users != nil
        users.each do |u|
          if u[:role] == row[headers["um_role"]] && u[:uid] == user_name
            user = u
            break
          end
        end
      end
    end

    # If the user exists, update it in User Manager
    if user != nil
      exists_in_um = true

      user[:uid]  = user_name
      user[:aut]  = row[headers["um_app"]]
      user[:role] = row[headers["um_role"]]

      if row[headers["pwd_e"]].to_s.strip != ""
        user[:pwdE] = row[headers["pwd_e"]].to_s.strip
      end

      if tin != ""
        has_tin = false
        user[:properties].each do |prop|
          if prop[:key] == "tin"
            prop[:value] = tin
            has_tin = true
            break
          end
        end

        if !has_tin
          user[:properties][user[:properties].length] = {
              :key => "tin",
              :value => tin
          }
        end
      end

      if include_npi
        has_npi = false
        user[:properties].each do |prop|
          if prop[:key] == "npi"
            prop[:value] = npi
            has_npi = true
            break
          end
        end

        if !has_npi
          user[:properties][user[:properties].length] = {
              :key => "npi",
              :value => npi
          }
        end
      end

      if entity_id != nil && entity_id != "" && entity_id_key != nil && entity_id_key != ""
        has_entity_id = false
        user[:properties].each do |prop|
          if prop[:key] == entity_id_key
            prop[:value] = entity_id
            has_entity_id = true
            break
          end
        end

        if !has_entity_id
          user[:properties][user[:properties].length] = {
              :key => entity_id_key,
              :value => entity_id
          }
        end
      end

    # If the user really doesn't exist, create a new one
    else
      user = {}

      user[:uid]        = user_name
      user[:pwdE]       = row[headers["pwd_e"]]
      user[:aut]        = row[headers["um_app"]]
      user[:env]        = "IMP"
      user[:role]       = row[headers["um_role"]]
      user[:checkedOut] = false
      user[:properties] = []

      prop_index = 0
      user[:properties][prop_index] = {
          :key => "tin",
          :value => tin
      }

      if include_npi
        prop_index += 1
        user[:properties][prop_index] = {
            :key => "npi",
            :value => npi
        }
      end

      if entity_id != nil && entity_id != "" && entity_id_key != nil && entity_id_key != ""
        prop_index += 1
        user[:properties][prop_index] = {
            :key => entity_id_key,
            :value => entity_id
        }
      end
    end

    if exists_in_um
      @um_updates_array[@um_updates_index] = user
      @um_updates_index += 1
    else
      @um_new_array[@um_new_index] = user
      @um_new_index += 1
    end
  end

  def get_practice_index_from_array_by_tin(array, tin)
    idx = -1

    if array != nil && array.length > 0
      array.each_with_index {|row,index|
        if row != nil && row.has_key?("tin") && row["tin"] == tin
          idx = index
          break
        end
      }
    end

    idx
  end

  def get_provider_index_from_array_by_tin_npi(array, tin, npi)
    idx = -1

    if array != nil && array.length > 0
      array.each_with_index {|row,index|
        if row != nil && row.has_key?("tin") && row["tin"] == tin && row["npi"] != "" && row["npi"] == npi
          idx = index
          break
        end
      }
    end

    idx
  end

  def get_provider_index_from_array_by_name(array, tin, first_name, last_name)
    idx = -1

    if array != nil && array.length > 0
      array.each_with_index {|row,index|
        if row != nil && row.has_key?("tin") && row["tin"] == tin && row["first_name"] == first_name && row["last_name"] == last_name
          idx = index
          break
        end
      }
    end

    idx
  end

  def get_flag_values_from_excel(excel_row, array_row, headers, fields, excel_prefix = "")
    fields.each {|field|
      array_row[field] = excel_row[headers["#{excel_prefix}#{field}"]].to_s.strip.downcase == "true"
    }

    array_row
  end

  def get_clinician_count(excel_row, headers, column, default_count = 100)
    value = excel_row[headers[column]].to_s.strip.downcase

    if value != "" && (true if Integer(value) rescue false)
      value = value.to_i
    else
      value = default_count
    end

    return value
  end
end

generator = CommonDatasetGenerator.new(ARGV)
generator.generate_dataset