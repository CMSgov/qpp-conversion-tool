require "json"
require "httparty"

@measures2017 = JSON.parse(HTTParty.get("https://raw.githubusercontent.com/CMSgov/qpp-measures-data/master/measures/2017/measures-data.json", headers: {:Accept => "application/json"}).read_body)
@measures2018 = JSON.parse(HTTParty.get("https://raw.githubusercontent.com/CMSgov/qpp-measures-data/master/measures/2018/measures-data.json", headers: {:Accept => "application/json"}).read_body)

errors = JSON.parse('')


def replaceStuff(measure2017, measure2018)
  #read file
  path = ""
  text = File.read(path)

  #replace eMeasureUuid
  text.gsub!(measure2017["eMeasureUuid"], measure2018["eMeasureUuid"])

  #replace ipop
  measure2017["strata"].each_with_index do |stratum, i|
    ipop2017 = stratum["eMeasureUuids"]["initialPopulationUuid"]
    ipop2018 = measure2018["strata"][i]["eMeasureUuids"]["initialPopulationUuid"]
    text.gsub!(ipop2017, ipop2018) unless ipop2017.nil? || ipop2017.empty? or ipop2018.nil? || ipop2018.empty?
  end

  #replace denom
  measure2017["strata"].each_with_index do |stratum, i|
    denom2017 = stratum["eMeasureUuids"]["denominatorUuid"]
    denom2018 = measure2018["strata"][i]["eMeasureUuids"]["denominatorUuid"]
    text.gsub!(denom2017, denom2018) unless denom2017.nil? || denom2017.empty? or denom2018.nil? || denom2018.empty?
  end

  #replace num
  measure2017["strata"].each_with_index do |stratum, i|
    num2017 = stratum["eMeasureUuids"]["numeratorUuid"]
    num2018 = measure2018["strata"][i]["eMeasureUuids"]["numeratorUuid"]
    text.gsub!(num2017, num2018) unless num2017.nil? || num2017.empty? or num2018.nil? || num2018.empty?
  end

  #replace denex
  measure2017["strata"].each_with_index do |stratum, i|
    denex2017 = stratum["eMeasureUuids"]["denominatorExclusionUuid"]
    denex2018 = measure2018["strata"][i]["eMeasureUuids"]["denominatorExclusionUuid"]
    text.gsub!(denex2017, denex2018) unless denex2017.nil? || denex2017.empty?  or denex2018.nil? || denex2018.empty?
  end

  #replace reporting stratum
  #IMPLEMENT ME

  #write replacements to file
  File.open(path, "w") {|file| file.puts text }
end

errors["errors"][0]["details"].each do |error|
  if error["errorCode"] == 6
    measureId = @measures2017.map {|uh| uh["measureId"] if uh["eMeasureUuid"] == "#{error["message"].slice(31..66)}"}.compact.first
    uuid2018  = @measures2018.map {|um| um["eMeasureUuid"] if um["measureId"] == "#{measureId}"}.compact.first
    puts "#{measureId}   2017: #{error["message"].slice(31..66)} and the 2018: #{uuid2018}"
    measure2017 = @measures2017.map {|measure| measure if measure["eMeasureUuid"] == error["message"].slice(31..66)}.compact.first
    measure2018 = @measures2018.map {|measure| measure if measure["eMeasureUuid"] == uuid2018}.compact.first
    replaceStuff(measure2017, measure2018) unless measure2017.nil? || measure2017.empty? or uuid2018.nil? || uuid2018.empty?
  end
end

