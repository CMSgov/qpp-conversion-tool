#!/usr/bin/env ruby

module XmlAnalysis
  module XmlAnalysisRunner
    def run (xml_analyser_type, xml_file_path, element_names)

      puts "Start to analyze xml file '#{xml_file_path}' by calling #{xml_analyser_type.name} at #{Time.now}"
      puts "DON'T CLOSE ME. I am working hard to process the file......"

      xml_analyzer = xml_analyser_type.new(element_names, xml_file_path)

      xml_analyzer.analyze

      puts "\nAnalysis results:"

      puts "*******************************************"

      puts "Start time: #{xml_analyzer.start_time}"
      puts "Finish time: #{xml_analyzer.finish_time}"
      puts "Total time spent: #{xml_analyzer.finish_time - xml_analyzer.start_time} Seconds, i.e. #{(xml_analyzer.finish_time - xml_analyzer.start_time) / 60} Minutes"

      xml_analyzer.element_totals.each do |key, value|
        puts "Total #{key} elements: #{value}"
      end

      puts "*******************************************"
    end
  end

  class XmlAnalyzer

    def initialize(element_names, xml_file_path)
      @element_names = element_names
      @xml_file_path = xml_file_path
    end

    def analyze_with
      @start_time = Time.now

      @element_totals = yield @element_names, @xml_file_path

      @finish_time = Time.now

    end

    attr_reader :start_time

    attr_reader :finish_time

    attr_reader :element_totals

  end

  module SaxCallbacks
    def initialize(element_names)
      parameters = SaxCallbacks.instance_method(:initialize).parameters.map(&:last).map(&:to_s)

      expected_type = Array
      unless element_names.is_a?(expected_type)
        raise ArgumentError, "Invalid argument type #{element_names.class} is provided. Only #{expected_type} type is allowed for parameter '#{parameters[0]}'"
      end

      @element_names = element_names

      # Initialize each element's total with 0
      @element_totals = {}
      @element_names.each do |element_name|
        @element_totals[element_name] = 0
      end

    end

    def start_element_callback(element_name)
      @element_totals[element_name] = @element_totals[element_name] + 1 if @element_names.include?(element_name)
    end

    attr_reader :element_totals
  end

  require 'ox'
  #require_relative 'sax_callbacks'

  class OxXmlAnalyzerCallbacks < Ox::Sax
    include SaxCallbacks

    # Handle start element event
    def start_element(symbol)
      element_name = symbol.to_s
      start_element_callback(element_name)
    end

    attr_reader :element_totals
  end

  #require_relative 'xml_analyzer'
  #require_relative 'ox_xml_analyzer_callbacks'

  class OxXmlAnalyzer < XmlAnalyzer

    def analyze
      analyze_with do |element_names, xml_file_path|
        callback = OxXmlAnalyzerCallbacks.new(element_names)

        xml_file = File.open(xml_file_path, "r")
        Ox.sax_parse(callback, xml_file, nil)
        xml_file.close

        callback.element_totals
      end
    end
  end

end

require 'readline'

puts "Analyze XML file to get the total numbers of elments"
puts ""

enter_xml_file_name_prompt = "Enter the full path of the xml file:"
puts enter_xml_file_name_prompt
xml_file_path = Readline::readline

while ! File.exists?(xml_file_path) do
  puts "Error: File '#{xml_file_path}' doesn't exist!"
  puts enter_xml_file_name_prompt
  xml_file_path = Readline::readline
end

enter_element_names_prompt = "Enter names of the elements separated with space:"
puts enter_element_names_prompt
element_names = Readline::readline.split

while element_names.length == 0
  # e.g. the default top-level element names in pecos xml file: UGE HDR CNTNT MDCR_PRVDR PRVDR
  puts "Error: Names of the elements are required!"
  puts enter_element_names_prompt
  element_names = Readline::readline.split
end

include XmlAnalysis::XmlAnalysisRunner

run(XmlAnalysis::OxXmlAnalyzer, xml_file_path, element_names)



