# this will have all the methods related to the json
require_relative 'qpp_page'
class JSON_METHODS < QPP_Page
  #
  # This method will read the json file
  #
  #  @param [String] json_file_name
  #   json file name (make sure to provide the file name alone and make sure
  #     the file is located in lib/data/json_files
  #   Note: Do not provide the complete file path. This method requires only file name
  def read_json_file(json_file_name)
    if json_file_name.chars.last(5).join == '.json'
      if json_file_name.include? "/"
        temp_file_path = Dir.pwd + json_file_name
      else
        temp_file_path = Dir.pwd + '/data/json_files/' + json_file_name
      end
      #puts temp_file_path
      file = File.read(temp_file_path)
    end
  end

  #
  # This method will fetch the json data based on the
  #   requested json path
  #
  #  @param [String] json_file_name
  #   json file name (make sure to provide the file name alone and make sure
  #     the file is located in lib/data/json_files
  #   Note: Do not provide the complete file path. This method requires only file name
  #  @param [String] json_path
  #   json path to the required element in the json
  #   Example:
  #    '$..measurementSets[?(@.category==quality)]' - this will get all the elements
  #     in root>measurementSets>> where category = quality
  def get_matching_elements(json_file_name, json_path)
    json_data = read_json_file(json_file_name)
    data = JsonPath.new(json_path).on(json_data)
  end

  def get_matching_element_from_json_data(json_data, json_path)
    JsonPath.new(json_path).on(json_data)
  end

  #
  # This method will validate measures and
  # performance rate in IA group as per the uploaded json file
  #
  # @param [Object] file_name
  #   json file name (make sure to provide the file name alone and make sure
  #     the file is located in lib/data/json_files
  #   Note: Do not provide the complete file path. This method requires only file name
  def validate_ia_measures(file_name)
    @verifi = Verifications.new(@browser)
    @ia = Improvement_Activities.new(@browser)
    # Get IA group data
    json_path = '$..measurementSets[?(@.category==ia)]'
    json_elements = get_matching_elements(file_name, json_path)
    # iterate through the tabs
    json_elements.each do |json_element|
      # navigate to IA group page
      @ia.click_ia_link
      # click on the tab
      if json_element['submissionMethod'] == 'electronicHealthRecord'
        @ia.navigate_to_tab("EHR")
      elsif json_element['submissionMethod'] = "registry"
        @ia.navigate_to_tab("Registry")
      end
      # get performance start from json file
      temp_start_date = DateTime.parse(json_element['performanceStart'])
      start_date = temp_start_date.strftime('%m/%d/%Y').to_s
      # get performance end from json file
      temp_end_date = DateTime.parse(json_element['performanceEnd'])
      end_date = temp_end_date.strftime('%m/%d/%Y').to_s
      # Compare the start and end dates against json
      @verifi.verify_text(start_date, @ia.get_start_date)
      @verifi.verify_text(end_date, @ia.get_end_date)
      # get measures
      measures = json_element['measurements']
      measures.each do |measure|
        # validate the measure status
        measure_ele = @browser.button(xpath: "//span[text()='#{measure['measureId']}']//ancestor::div[starts-with(@class,'submission-measure')]//button")
        @verifi.verify_text('true', @ia.is_measure_selected(measure_ele).to_s)
      end
    end
  end

  #
  # This method will validate measures and
  # performance rate in Quality measures group
  # as per the uploaded json file
  #
  # @param [Object] file_name
  #   json file name (make sure to provide the file name alone and make sure
  #     the file is located in lib/data/json_files
  #   Note: Do not provide the complete file path. This method requires only file name
  def validate_quality_measures(file_name)
    @verifi = Verifications.new(@browser)
    @qs = Quality_Score.new(@browser)
    json_path = '$..measurementSets[?(@.category==quality)]'
    @verifi.click_qm_link
    #@browser.refresh
    #@browser.li(class: 'account').wait_until(&:present?)
    #sleep 2
    json_elements = get_matching_elements(file_name, json_path)
    json_elements.each do |json_element|
      submissionMethod = json_element['submissionMethod']
      if submissionMethod.eql?("electronicHealthRecord")
        #@browser.div(xpath: "//div[@class='submission-method-name' and text()='EHR']").click
        @browser.span(xpath: "//span[@class='quality-tabs-nav-list-item-label' and text()='EHR']").fire_event :click
      else
        #@browser.div(xpath: "//div[@class='submission-method-name' and text()='Registry']").click
        @browser.span(xpath: "//span[@class='quality-tabs-nav-list-item-label' and text()='Registry']").click
      end

      measures = json_element['measurements']
      measures.each do |measure|
        puts measure['measureId']
        # ???Validate measure is included under QM tab???
        #@verifi.verify_element_present(@browser.span(xpath: "//tab[@class='active tab-pane' or @class= 'tab-pane active']//div[@class='score-row']//span[text()='#{measure['measureId']}']").wait_until(&:present?))
        @verifi.verify_element_present(@browser.span(xpath: "//div[@class='submission-tab']//span[.='Measure ID: #{measure['measureId']}']").wait_until(&:present?))
        met = measure['value']['performanceMet']
        not_met = measure['value']['performanceNotMet']
        excl = measure['value']['eligiblePopulationExclusion']
        excl2 = measure['value']['eligiblePopulationException']

        total = measure['value']['eligiblePopulation']
        if met != 0 and not_met != 0 and total != 0
          excl_total = 0
          if excl != nil
            excl_total = excl
          end
          if excl2 != nil
            excl_total = excl_total + excl2
          end
          if not_met == nil
            not_met = 0
          end
          exp_perf_rate = (met.to_f / (met + not_met - (excl_total))) * 100
          if exp_perf_rate.to_s.split('.')[1][0..2].to_i != 0
            exp_perf_rate = (exp_perf_rate.to_s.split('.')[0] + '.' + exp_perf_rate.to_s.split('.')[1][0..2]).to_f.round(2)
          else
            exp_perf_rate = exp_perf_rate.to_i
          end
          @verifi.verify_text(exp_perf_rate.to_s + '%', @qs.get_QM_Measure_perf_rate(measure['measureId']), measure['measureId'])
        end
      end
    end
  end

  #
  # This method will validate measures and
  # performance rate in ACI group as per the uploaded json file
  #
  # @param [Object] file_name
  #   json file name (make sure to provide the file name alone and make sure
  #     the file is located in lib/data/json_files
  #   Note: Do not provide the complete file path. This method requires only file name
  def validate_aci_measures(file_name)
    @verifi = Verifications.new(@browser)
    @ia = Improvement_Activities.new(@browser)
    @aci = ACI_Page.new(@browser)

    json_path = '$..measurementSets[?(@.category==pi)]'
    json_elements = get_matching_elements(file_name, json_path)
    json_elements.each do |json_element|
      @aci.click_aci_info_link
      if @aci.is_reweight_modal_displayed
        @aci.accept_reweight
      end
      if json_element['submissionMethod'] == 'electronicHealthRecord'
        @aci.navigate_to_tab("EHR")
      elsif json_element['submissionMethod'] == "registry"
        @aci.navigate_to_tab("Registry")
      end
      temp_start_date = DateTime.parse(json_element['performanceStart'])
      start_date = temp_start_date.strftime('%m/%d/%Y').to_s

      temp_end_date = DateTime.parse(json_element['performanceEnd'])
      end_date = temp_end_date.strftime('%m/%d/%Y').to_s

      @verifi.verify_text(start_date, @ia.get_start_date)
      @verifi.verify_text(end_date, @ia.get_end_date)

      measures = json_element['measurements']
      measures.each do |measure|
        puts measure
        if !(measure['value'].to_s == 'true' || measure['value'].to_s == 'false')
          @verifi.verify_text(measure['value']['numerator'].to_s, @aci.get_numerator(measure['measureId']))
          @verifi.verify_text(measure['value']['denominator'].to_s, @aci.get_denominator(measure['measureId']))
        else
          button_status = (measure['value'].to_s == 'true') ? 'Yes' : 'No'
          if button_status == 'Yes'
            measure_ele = @browser.button(xpath: "//button[contains(@aria-label,'Deselect Yes for: #{measure['measureId']}')]")
          else
            measure_ele = @browser.button(xpath: "//button[contains(@aria-label,'Deselect No for: #{measure['measureId']}')]")
          end
          measure_ele.wait_until(&:present?)
          @verifi.verify_element_present(measure_ele)
        end
      end
    end
  end

  def validate_e2e_bonus_points_for_measures (file_name)
    json_path = '$..measurementSets[?(@.category==quality)]'
    json_elements = get_matching_elements(file_name, json_path)
    json_elements.each do |json_element|
      measures = json_element['measurements']
      measures.each do |measure|
        #puts measure['measureId']
        # ???Validate measure is included under QM tab???
        @verifi.verify_element_present(@browser.span(xpath: "//div[@class='score-row']//span[text()='#{measure['measureId']}']").wait_until(&:present?))
        num = measure['value']['performanceMet']
        den = measure['value']['eligiblePopulation']
        e2eReported = measure['value']['isEndToEndReported']
        eligPop = measure['value']['eligiblePopulation']

        if num == 0 and den == 0 and e2eReported.to_s == 'true'
          #actual = @browser.div(xpath: "//span[.='#{measure['measureId']}']/ancestor::div[@class='panel card panel-default']//h4[.='End-to-End Reporting']/parent::div/following-sibling::div").text
          actual = @browser.div(xpath: "//span[.='#{measure['measureId']}']/ancestor::div[@class='panel card panel-default']//div[.='End-to-End Reporting']/parent::div/following-sibling::div").text
          @verifi.verify_text("1", actual.to_s)
        end

      end
    end
  end
end
