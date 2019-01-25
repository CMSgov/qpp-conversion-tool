module XmlPartition

  class ExitJustAfterEndIndexException < StandardError
    def initialize(message = "Exit SAX processing after the end index of the partition element was processed, i.e. all required partition elements were found")
      super(message)
    end
  end

  module SaxCallbacks

    def initialize(partition_element_name, start_index, end_index, output_file, exit_just_after_end_index = true)
      @partition_element_name = partition_element_name
      @start_index = start_index
      @end_index = end_index

      @total_count = 0
      @partition_count = 0

      @output_file = output_file

      @in_partition_element = false

      @exit_just_after_end_index = exit_just_after_end_index
    end

    # Handle start element event
    def start_element_callback(element_name)
      if element_name == @partition_element_name
        @in_partition_element = true
        @total_count = @total_count + 1
      end

      handle "<#{element_name}>" do
        if element_name == @partition_element_name
          @partition_count = @partition_count + 1
        end
      end
    end

    def text_callback(str)
      handle str
    end

    def end_element_callback(element_name)
      handle "</#{element_name}>"

      if element_name == @partition_element_name
        @in_partition_element = false

        if (@exit_just_after_end_index && @total_count == @end_index)
          @total_count = -1
          raise ExitJustAfterEndIndexException.new
        end
      end
    end

    attr_reader :total_count
    attr_reader :partition_count

    def handle (output)
      if (@in_partition_element && @total_count >= @start_index && (@total_count <= @end_index || @end_index == -1))
        @output_file.write output
        if block_given?
          yield
        end
      end
    end

    private :handle

  end

  require 'ox'

  # Only consider elements and texts
  class OxXmlPartitionerCallbacks < Ox::Sax
    include SaxCallbacks

    # Handle start element event
    def start_element(symbol)
      element_name = symbol.to_s

      start_element_callback(element_name)
    end

    def text(str)
      text_callback(str)
    end

    def end_element(symbol)
      element_name = symbol.to_s

      end_element_callback(element_name)
    end

  end

  class XmlPartitioner

    def initialize(xml_file_path, partition_element_name, start_index, end_index, output_file, exit_just_after_end_index=true)
      @xml_file_path = xml_file_path

      @partition_element_name = partition_element_name
      @start_index = start_index
      @end_index = end_index

      @output_file = output_file

      @exit_just_after_end_index = exit_just_after_end_index
    end

    def partition_with
      @start_time = Time.now

      callbacks = yield @xml_file_path, @partition_element_name, @start_index, @end_index, @output_file, @exit_just_after_end_index

      @total_count = callbacks.total_count
      @partition_count = callbacks.partition_count

      @finish_time = Time.now

    end

    attr_reader :start_time

    attr_reader :finish_time

    attr_reader :total_count

    attr_reader :partition_count

  end

  class OxXmlPartitioner < XmlPartitioner

    def partition
      partition_with do |xml_file_path, partition_element_name, start_index, end_index, output_file, exit_just_after_end_index|
        callbacks = OxXmlPartitionerCallbacks.new(partition_element_name, start_index, end_index, output_file, exit_just_after_end_index)

        xml_file = File.open(xml_file_path, "r")

        begin
          Ox.sax_parse(callbacks, xml_file, nil)
        rescue ExitJustAfterEndIndexException => e
          puts "*****" + e.message
        ensure
        end

        xml_file.close

        callbacks
      end
    end

  end

  module XmlPartitionRunner
    def run (xml_partitioner_type, xml_file_path, partition_element_name, start_index, end_index, output_directory, exit_just_after_end_index = true)

      time_now = Time.now

      puts "Start to partition <#{partition_element_name}> in the range [#{start_index}, #{end_index}] out from xml file '#{xml_file_path}' by using #{xml_partitioner_type.to_s} to the directory '#{output_directory}' at #{time_now}"
      puts "DON'T CLOSE ME. I am working hard to partition the file......"

      output_file_name = "#{File.basename(xml_file_path, ".*")}_#{partition_element_name}_#{start_index}-#{end_index}_#{time_now.to_i}.xml"

      output_file_path = "#{output_directory}/#{output_file_name}"

      if File.exists?(output_file_path)
        File.delete(output_file_path)
      end

      xml_partitioner = nil
      File.open(output_file_path, 'a') do |output_file|
        output_file.write '<?xml version="1.0" encoding="UTF-8" standalone="no"?>'

        root_element_name = "#{partition_element_name}_partition_root"

        # Write the start of the root element
        output_file.write "<#{root_element_name}>"

        xml_partitioner = xml_partitioner_type.new(xml_file_path, partition_element_name, start_index, end_index, output_file, exit_just_after_end_index)

        xml_partitioner.partition

        # Write the end of the root element
        output_file.write "</#{root_element_name}>"
      end

      if !xml_partitioner.nil?
        puts "\nXml partition results:"

        puts "*******************************************"

        puts "Start time: #{xml_partitioner.start_time}"
        puts "Finish time: #{xml_partitioner.finish_time}"
        puts "Total time spent: #{xml_partitioner.finish_time - xml_partitioner.start_time} Seconds, i.e. #{(xml_partitioner.finish_time - xml_partitioner.start_time) / 60} Minutes"

        new_output_file_path = "#{File.dirname(output_file_path)}\\#{File.basename(output_file_path, ".*")}_#{xml_partitioner.partition_count}_of_#{xml_partitioner.total_count}#{File.extname(output_file_path)}"
        File.rename(output_file_path, new_output_file_path)
        output_file_path = new_output_file_path

        puts "Total number of element '#{partition_element_name}' in the input '#{xml_file_path}': #{xml_partitioner.total_count}"
        puts "Total number of element '#{partition_element_name}' in the output '#{output_file_path}': #{xml_partitioner.partition_count}"

        puts "*******************************************"
      end
    end
  end
end

require 'readline'

puts "Partition XML file to get any range of specified elements"
puts ""

enter_xml_file_name_prompt = "Enter the full path of the xml file:"
xml_file_path = nil

while xml_file_path.nil? || ! File.exists?(xml_file_path) do
  if !(xml_file_path.nil?)
    puts "Error: The file '#{xml_file_path}' doesn't exist!"
  end

  puts enter_xml_file_name_prompt
  xml_file_path = Readline::readline
end

enter_element_name_prompt = "Enter the name of the element:"
element_name = nil

while (element_name.nil? || element_name.empty? || element_name.split.length > 1) do
  # e.g. the default top-level element names in pecos xml file: UGE HDR CNTNT MDCR_PRVDR PRVDR
  if !(element_name.nil?)
    puts "Error: Valid element name is required!"
  end

  puts enter_element_name_prompt
  element_name = Readline::readline.strip
end

enter_element_ranges_prompt = "Enter the index range of the elements separated with white spaces (1 is for the 1st element, 2 is for the 2nd element and so on; -1 is for the last element. Elements in this range will be partitioned out of the xml file.):"
indexes = []
first_time_enter_index_range = true

while (indexes.length == 0 || (indexes.length == 1 && indexes[0] == 0) || (indexes.length > 1 && (indexes[0] == 0 || indexes[1] == 0))) do
  if first_time_enter_index_range
    first_time_enter_index_range = false
  else
    puts "Error: Valid index range is required!"
  end

  puts enter_element_ranges_prompt
  indexes = Readline::readline.split
  indexes.each_with_index do |index_item, index|
    indexes[index] = index_item.to_i
  end
end

start_index = indexes[0]
end_index = start_index
if (indexes.length > 1)
  end_index = indexes[1]
end

enter_xml_file_output_directory_prompt = "Enter the directory of the output xml file:"
xml_file_output_directory = nil

while xml_file_output_directory.nil? || ! File.directory?(xml_file_output_directory) do
  if !(xml_file_output_directory.nil?)
    puts "Error: THE directory '#{xml_file_output_directory}' doesn't exist!"
  end

  puts enter_xml_file_output_directory_prompt
  xml_file_output_directory = Readline::readline
end

enter_exit_just_after_end_index_prompt = "Do you want to stop the xml processing after all partition elements are gotten (the defalut is yes)? Enter N if you don't want to:"
exit_just_after_end_index = true
puts enter_exit_just_after_end_index_prompt
exit_just_after_end_index_input = Readline::readline
if exit_just_after_end_index_input == "N"
  exit_just_after_end_index = false
end

include XmlPartition::XmlPartitionRunner
run(XmlPartition::OxXmlPartitioner, xml_file_path, element_name, start_index, end_index, xml_file_output_directory, exit_just_after_end_index)
