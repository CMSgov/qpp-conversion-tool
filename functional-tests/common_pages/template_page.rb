
#class PatientAdmitTreatmentDialysisPage
#include PageObject

# Interactive web elements associated with this page
# For more details see, https://github.com/cheezy/page-object/wiki/Elements


#in_iframe(:id => 'T:e1338526097::f') do |iframe|

#---------------------------
# inputs
#---------------------------
#text_field(:crown_upi_textbox, id: 'patientSearchForm:crownUpi', :frame => iframe)  #**if not in iframe then remove frame: iframe


#---------------------------
# links
#---------------------------
#link(:crown_home_link, id: 'homeLink', frame: iframe)


#---------------------------
# checkboxes
#---------------------------
#checkbox(:mbi_na_checkbox, id: 'admitPatientForm:mbiNotApplicable', frame: iframe)


#---------------------------
# buttons
#---------------------------
#button(:go_button, id: 'patientSearchForm:facGo', frame: iframe)


#---------------------------
# selects
#---------------------------
#select_list(:display_results_per_page_dropdown, id: 'patientSearchForm:rowsPerPage', frame: iframe)

#---------------------------
# file_field
#---------------------------
#file_field(:xml_file_field, id: 'ediUploadForm:ediFile', frame: iframe)


#---------------------------
# headers and labels
#---------------------------
#h1(:search_for_patients_header, xpath: "//div[@id='main']/div/h1", frame: iframe)
#span(:transplant_date_read_only, id: 'admitDialysisPatientForm:txTreatmentTransplantDateTxt', frame: iframe)


#end


#---------------------------------------------------------------------------------
#  METHODS
#---------------------------------------------------------------------------------

# Interactive methods used to hide implementation details
# http://www.rubydoc.info/github/cheezy/page-object/PageObject

# whenever you want to work with the page object element then append object name with "_element"
#     Example: variable_name1_element


#---------------------------
# INPUT METHODS
#---------------------------

# This method will input the value in XXXX feild
# @param [String] value - value to enter into XXXX field

#def input_crown_upi_textbox(value)
#  WebHelper.input(crown_upi_textbox_element, value)
#end


#---------------------------
# LINK METHODS
#---------------------------

#def click_edi_link
#  WebHelper.click(edi_link_element)
#  return FileUploadPage.new(@browser)
#end


#---------------------------
# CHECKBOX METHODS
#---------------------------

#def enable_mbi_na_checkbox
#  WebHelper.enable_checkbox(mbi_na_checkbox_element)
#end


#def disable_mbi_na_checkbox
#  WebHelper.disable_checkbox(mbi_na_checkbox_element)
#end


#---------------------------
# BUTTON METHODS
#---------------------------
#def click_search_button
#  WebHelper.click(search_button_element)
#  return PatientSearchResultsPage.new(@browser)
#end


#---------------------------
# SELECT METHODS
#---------------------------
#Valid Values: (this is a comment to list valid values for the field)
#def select_mfa_device_type_dropdown(value)
#  WebHelper.select(element, value)
#end

#Valid Values: this method selects by index number
#def select_by_index_attending_practitioner_dropdown(index)
#  WebHelper.select_by_index(element, value)
#end


#---------------------------
# FILE FIELD METHODS
#---------------------------
#def set_xml_file_field(filepath)
#  WebHelper.set_filefield(self.xml_file_field_element, filepath)
#end






#end