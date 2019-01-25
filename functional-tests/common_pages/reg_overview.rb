# This comment should describe what page or page section the class represents in the application
require_relative 'qpp_page'
class Registration_Overview_Page < QPP_Page
  include PageObject

  # Interactive web elements associated with this page
  # For more details see, https://github.com/cheezy/page-object/wiki/Elements
  h1(:page_title_h1, xpath: "//h1[@class='title']")

  #overview page
  link(:manage_user_access_link, xpath:"//a[contains(text(),'Manage User Access')]")
  h2(:cancel_modal_header, xpath: "//h2[contains(text(), 'Cancel Registration?')]")
  element(:modal_text,"p",xpath: "//h2[contains(text(), 'Cancel Registration?')]/../../div[2]")
  button(:modal_continue_button, xpath:"//button[contains(text(), 'Continue')]")
  link(:modal_continue_link, xpath: "//a[contains(text(),'Continue')]")
  button(:modal_cancel_button, xpath: "//button[contains(text(), 'CANCEL')]")
  element(:two_twentyfour_input, "input",xpath: "//label[contains(text(),'2-24')]/../input")
  element(:onehundred_greater_input, "input", xpath: "//label[contains(text(),'100 or greater')]/../input")

  button(:show_detail_button, xpath: "//button[contains(text(), 'Show Details')]")
  button(:show_less_button, xpath: "//button[contains(text(), 'Show Less')]")
  link(:show_detail_edit_addr, xpath: "//div[@class='details__item details__item--address']//a")
  link(:show_detail_add_contact, xpath: "//a[contains(text(),'+ Add New Contact')]")


  element(:show_detail_org_address, xpath: "//div[@class='details__item details__item--address']/p")

  button(:CMS_Web_Interface_Register_button, xpath: "//b[contains(text(), 'CMS Web Interface')]/../../../button")
  button(:cahps_Register_button, xpath: "//b[contains(text(), 'CAHPS for MIPS Survey')]/../../../button")

  h1(:page_title_h1, xpath: "//h1[@class='title']")
  li(:org_list_tin_li, xpath: "//div[@class='list__column list__column--1']/ul/li")
  li(:org_size_li, xpath: "//div[@class='list__column list__column--1']/ul/li[2]")
  button(:cms_web_interface_reg_link, xpath:"//*[@type='groupReporting']/*//button")
  button(:cancel_reg_continue, xpath:"//div[@class='modal__content__actions']/button[@class='btn btn-primary']")
  button(:cancel_reg_cancel, xpath:"//div[@class='modal__content__actions']/button[@class='btn btn-link btn-cancel']")


  h2(:re_register_modal_title_h2, xpath: "//h2[@id='modal-title']")
  link(:reg_again_continue, xpath:"//a[contains(text(), 'Continue')]")
  button(:reg_again_cancel, xpath:"//div[@class='modal__content__actions']/button[@class='btn btn-link btn-cancel']")
  button(:cahps_survey_link ,xpath:"//div[@class='list__column list__column--2']/*[2]//button[1]")


  h2(:org_edit_title_h2, xpath:"//h2[@id='modal-title']")
  element(:org_edit_description_p, xpath:"//p[@id='modal-description']")

  link(:edit_org_modal_ok, xpath:"//a[contains(text(), 'OK')]")
  link(:edit_modal_ok_link, xpath: "//a[text()='OK']")
  button(:change_size_yes_button, xpath: "//button[contains(text(),'Yes')]")


  text_field(:org_edit_org_name, :id => 'organizationName')
  text_field(:org_edit_addr1, :id => 'mailingAddress.address1')
  text_field(:org_edit_addr2, :id => 'mailingAddress.address2')
  text_field(:org_edit_city, :id => 'mailingAddress.city')
  select_list(:org_edit_state, :id => 'mailingAddress.state')
  text_field(:org_edit_zip, :id => 'mailingAddress.zipCode')
  element(:org_edit_last_name_error_msg_p, xpath: "//p[contains(text(), 'A last name may only contain')]")
  element(:org_edit_first_name_error_msg_p, xpath: "//p[contains(text(), 'A first name may only contain')]")

  text_field(:poc_first_name, :id => 'firstName')
  text_field(:poc_last_name, :id => 'lastName')
  text_field(:poc_email, :id => 'email')
  text_field(:poc_phone, :id => 'phone')
  text_field(:poc_phone_ext, :id => 'phoneExtension')
  link(:poc_create_edit_cancel, xpath:"//a[@class='btn btn-link registration-cancel-action--contact']")
  element(:org_edit_error_message,xpath: "//*[@id='id']")

  button(:org_edit_update_button , xpath:"//button[contains(text(),'Update')]")
  link(:org_edit_cancel, xpath:"//a[contains(text(),'Cancel')]")

  #registration page
  button(:edit_button, xpath: "//button[contains(text(),'EDIT')]")
  button(:edit_org_size_button, xpath: "//button[contains(text(),'Edit')]")
  link(:show_detail_edit_contact_link, xpath: "//app-contact-edit-actions[1]/div[@class='edit-actions']/a")
  button(:delete_yes_button, xpath: "//button[contains(text(),'YES')]")
  button(:add_new_contact_button, xpath: "//button[contains(text(),'+ ADD NEW')]")

  element(:org_id,"strong",xpath: "//h3[contains(text(), 'Organization Name')]/../p/strong")
  element(:org_address, xpath: "//h3[contains(text(), 'Address')]/../p")
  element(:org_group_size, "strong", xpath: "//h3[contains(text(), 'Group Size')]/../p/strong")
  element(:org_tin, "strong", xpath: "//h3[contains(text(), 'TIN')]/../p/strong")
  unordered_list(:contacts_list, xpath:"//ul[@class='contacts']")
  button(:register_button, xpath: "//button[contains(text(), 'Register')]")
  link(:cancel_link, xpath:"//a[contains(text(), 'cancel')]")
  link(:back_to_list_link, xpath: "//a[contains(text(),'BACK TO LIST')]")

  span(:organization_name_asterisk, xpath:"//span[text()='Organization Name*']")
  span(:organization_address_asterisk, xpath:"//span[text()='Address*']")
  span(:organization_city_asterisk, xpath:"//span[text()='City*']")
  span(:organization_state_asterisk, xpath:"//span[text()='State*']")
  span(:organization_zipcode_asterisk, xpath:"//span[text()='Zip Code*']")
  element(:org_edit_required_label_p, xpath:"//p[contains(text(),'* Required')]")


  h1(:poc_modal_title_p, xpath: "//h1[@id='registration-card-heading']")
  element(:poc_modal_description_p, xpath: "//div[@class='heading']/p")



  b(:org_list_label_li, xpath: "//div[@class='list__column list__column--1']/ul/li/b")
  h3(:org_list_review_label, xpath: "//div[@class='info-items__item info-items__item--half'][2]/h3")
  strong(:org_list_review_tin, xpath: "//div[@class='info-items__item info-items__item--half'][2]/p/strong")
  h2(:org_list_info_label, xpath: "//div[@class='info-items__item'][2]/h2")
  strong(:org_list_info_tin, xpath: "//div[@class='info-items__item'][2]/p/strong")

  button(:group_size_help_button, xpath: "//button[@aria-label='Click for help with selecting a group size']")
  element(:group_size_modal_content_p, xpath: "//p[@class='modal__content__text']")
  button(:group_size_help_close_modal_button, xpath: "//button[@class='btn btn-link']")
  h3(:org_address_label, xpath: "//div[@class='info-items__item'][2]/h3")
  strong(:group_size_text, xpath: "//div[@class='info-items__item info-items__item--half']/p/strong")

  element(:grey_line_exist, xpath: "//div[@class='registration__card-content'][2]/div")
  element(:org_cont_info_message, xpath: "//div[@class='description']/p")
  element(:org_info_qpp_logo, xpath: "//img[@alt='QPP Logo']")
  element(:qpp_page_title, xpath: "//a[@class='header-brand']//img[@class='qpp-logo']")
  label(:select_group_size_25_99, xpath: "//label[@for='group-size-102884-1']")
  label(:org_edit_addr1_label, xpath: "//label[@class='sr-only'][contains(text(),'Address*')]")
  label(:org_edit_addr2_label, xpath: "//label[@class='sr-only'][contains(text(),'Address 2')]")
  strong(:org_info_edit_size, xpath: "//div[@class='info-items__item']/p/strong")
  h2(:org_info_edit_label, xpath: "//div[@class='info-items__item']/h2")


  div(:landing_page_message_div, xpath: "//div[@class='page-message']")

  button(:contact_info_edit_button, xpath: "//li[1]//div[@class='edit-actions']/button[contains(text(),'Edit')]")
  link(:edit_org_contact_ok_button, xpath: "//a[@class='btn btn-primary'][contains(text(), 'OK')]")
  element(:landing_page_subtitle, xpath: "//p[@class='subtitle subtitle--small']")
  h1(:review_page_title, :id => 'registration-card-heading')
  element(:review_page_subtitle, xpath: "//div[@class='heading']//p")
  element(:edit_org_info_text, xpath: "//p[@class='subtitle subtitle--small']")
  element(:edit_org_info_text, :id => 'modal-description')
  h1(:back_to_list_modal_title, :id => 'modal-title')
  element(:cancel_reg_modal_text, :id => 'modal-description')
  element(:reg_again_modal_text, :id => 'modal-description')

  b(:org_cms_web_label, xpath: "//b[contains(text(), 'CMS Web Interface')]")
  b(:org_cahps_mips_label, xpath: "//b[contains(text(), 'CAHPS for MIPS Survey')]")
  element(:org_cms_disc_text, xpath: "//section[@class='registration-disclaimer']/p")
  element(:org_cahps_disc_text, xpath: "//section[@class='registration-disclaimer']/p")

  text_field(:org_edit_name, :id => 'organizationName')


  # Interactive methods used to hide implementation details
  # http://www.rubydoc.info/github/cheezy/page-object/PageObject
  #--------------------------------------------------//*[@class='title']-------------------------------------
  #   Method implementations
  #---------------------------------------------------------------------------------------
  def get_landing_page_message_div
    landing_page_message_div_element.wait_until(&:present?)
  end

  def get_page_title
    page_title_h1_element.wait_until(&:present?)
  end

  def get_org_edit_dialog_title
    org_edit_title_h2_element.wait_until(&:present?).flash
    org_edit_title_h2_element.wait_until(&:present?)
  end

  def get_org_edit_dialog_description
    org_edit_description_p_element.wait_until(&:present?)
  end

  def get_poc_modal_title
    poc_modal_title_p_element.wait_until(&:present?)
  end

  def get_poc_modal_description
    poc_modal_description_p_element.wait_until(&:present?)
  end

  def click_group_size_help_button
    group_size_help_button_element.wait_until(&:present?).fire_event :onclick
  end

  def click_group_size_help_close_modal_button
    group_size_help_close_modal_button_element.wait_until(&:present?).fire_event :onclick
  end

  def get_group_size_help_content
    group_size_modal_content_p_element.wait_until(&:present?)
  end

  def click_manage_access_link
    manage_user_access_link_element.wait_until(&:present?).fire_event :onclick
  end

  def get_web_interface_reg_button
    cms_web_interface_reg_link_element.wait_until(&:present?)
  end

  def check_web_interface_button_exists
    if cms_web_interface_reg_link_element.exists?
      true
    else
      false
    end
  end

  def get_re_register_modal_title
    re_register_modal_title_h2_element.wait_until(&:present?)
  end


  def get_required_label_p
    org_edit_required_label_p_element.wait_until(&:present?)
  end

  def set_web_interface_reg_button(val)
    CMS_Web_Interface_Register_button.set(val)
  end

  def click_change_size_yes_button
    change_size_yes_button_element.wait_until(&:present?).fire_event :onclick
  end

  def click_back_to_list_link
    back_to_list_link_element.wait_until(&:present?).fire_event :onclick
  end

  def click_register_button
    register_button_element.wait_until(&:present?).fire_event :onclick
  end

  def click_poc_create_edit_cancel
    poc_create_edit_cancel_element.wait_until(&:present?).fire_event :onclick
  end

  def click_twotwentyfour_input
    two_twentyfour_input_element.wait_until(&:present?).fire_event :onclick
  end

  def click_modal_continue_link
    modal_continue_link_element.wait_until(&:present?).fire_event :onclick
  end

  def click_modal_continue_button
    modal_continue_button_element.wait_until(&:present?).fire_event :onclick
  end


  def click_modal_cancel_button
    modal_cancel_button_element.wait_until(&:present?).fire_event :onclick
  end

  def get_modal_cancel_button
    modal_cancel_button_element.wait_until(&:present?)
  end

  def click_onehundredgreater_input
    onehundred_greater_input_element.wait_until(&:present?).fire_event :onclick
  end

  def get_cahps_edit_button
    cahps_edit_button.wait_while_present
  end

  def get_org_error_message_text
    org_edit_error_message_element.wait_until(&:present?).text
  end

  def get_org_first_name_error_message_text
    org_edit_first_name_error_msg_p_element.wait_until(&:present?).text
  end

  def get_org_last_name_error_message_text
    org_edit_last_name_error_msg_p_element.wait_until(&:present?).text
  end

  def get_poc_first_name
    poc_first_name_element.wait_until(&:present?)
  end

  def set_poc_first_name(val)
    poc_first_name_element.set(val)
  end

  def get_poc_last_name
    poc_last_name_element.wait_until(&:present?)
  end

  def set_poc_last_name(val)
    poc_last_name_element.set(val)
  end

  def get_poc_email
    poc_email_element.wait_until(&:present?)
  end

  def set_poc_email(val)
    poc_email_element.set(val)
  end


  def get_poc_phone
    poc_phone_element.wait_until(&:present?)
  end

  def set_poc_phone(val)
    poc_phone_element.set(val)
  end

  def get_poc_phone_ext
    poc_phone_ext_element.wait_until(&:present?)
  end

  def set_poc_phone_ext(val)
    poc_phone_ext_element.set(val)
  end


  def click_show_detail_edit_contact_link
    show_detail_edit_contact_link_element.wait_until(&:present?).fire_event :onclick
  end


  def click_show_detail_button
    show_detail_button_element.wait_until(&:present?).fire_event :onclick
  end

  def click_show_less_button
    show_less_button_element.wait_until(&:present?).fire_event :onclick
  end

  def get_show_less_button
    show_less_button_element.wait_until(&:present?)
  end

  def click_show_detail_edit_addr
    show_detail_edit_addr_element.wait_until(&:present?).fire_event :onclick
  end


  def click_show_detail_add_contact
    show_detail_add_contact_element.wait_until(&:present?).fire_event :onclick
  end

  def click_org_edit_state_button
    org_edit_state_element.wait_until(&:present?).fire_event :onclick
  end

  def click_org_edit_update_button
    #org_edit_update_button_element.wait_until(&:present?).flash
    WebHelper.click(org_edit_update_button_element)
  end

  def click_org_edit_button
    edit_button_element.wait_until(&:present?).fire_event :onclick
  end

  def click_edit_org_size_button
    edit_org_size_button_element.wait_until(&:present?).fire_event :onclick
  end


  def click_group_size_edit_button
    edit_button_element_wait_until(&:present?).fire_event :onclick
  end



  def click_edit_org_modal_ok_button
    edit_org_modal_ok_element.wait_until(&:present?).fire_event :onclick
  end

  def get_edit_org_modal_ok_button
    edit_org_modal_ok_element.wait_until(&:present?)
  end

  def click_modal_ok_button
    edit_modal_ok_link_element.wait_until(&:present?).fire_event :onclick
  end

  def click_modal_ok_link
    edit_org_modal_ok_element.wait_until(&:present?).flash
    edit_org_modal_ok_element.wait_until(&:present?).fire_event :onclick
  end


  def get_show_detail_org_address
    show_detail_org_address_element.wait_until(&:present?)
  end



  def get_org_edit_org_name
    org_edit_org_name_element.wait_until(&:present?)
  end

  def get_org_edit_city
    org_edit_city_element.wait_until(&:present?)
  end

  def get_org_edit_state
    org_edit_state_element.wait_until(&:present?)
  end

  def get_org_edit_zip
    org_edit_zip_element.wait_until(&:present?)
  end

  def get_org_edit_address1
    org_edit_addr1_element.wait_until(&:present?)
  end

  def get_org_edit_address2
    org_edit_addr2_element.wait_until(&:present?)
  end

  def set_org_edit_org_name(val)
    org_edit_org_name_element.set(val)
  end

  def set_org_edit_city(val)
    org_edit_city_element.set(val)
  end

  def set_org_edit_state(val)
    org_edit_state_element.select val
  end

  def set_org_edit_zip(val)
    org_edit_addr1_element.set(val)
  end

  def set_org_edit_address1(val)
    org_edit_addr1_element.set(val)
  end


  def set_org_edit_address2(val)
    org_edit_addr2_element.set(val)
  end

  def get_cahps_new_button
    cahps_add_new_contact_button.wait_while_present
  end

  def click_add_new_contact_button
    add_new_contact_button_element.wait_until(&:present?).fire_event :onclick
  end


  def get_caphs_org_name
    org_id_element.wait_until(&:present?)
  end

  def get_caphs_org_address
    org_address_element.wait_until(&:present?)
  end

  def get_caphs_org_tin
    cahps_org_tin_element.wait_until(&:present?)
  end

  def get_contacts_lists
    contacts_list_element.wait_until(&:present?)
  end


  def get_org_list_tin
    tin_with_text = org_list_tin_li_element.wait_until_present.text
    tin = tin_with_text.sub("TIN:","").delete(' ')
    tin
  end

 def get_org_size_text
   org_size = org_size_li_element.wait_until_present.text
   org_size_text = org_size.sub("Size:","")
   org_size_text
end


  def cms_web_reg_button_is_cancel
    cms_web_link_button_text = cms_web_interface_reg_link_element.wait_until_present.text
    if cms_web_link_button_text.delete(' ').downcase == "cancelregistration"
      true
    else
      false
    end
  end

  def cms_web_reg_button_is_register
    cms_web_link_button_text = cms_web_interface_reg_link_element.wait_until_present.text
    if cms_web_link_button_text.delete(' ').downcase == "cancelregistration"
      false
    else
      true
    end
  end

def click_delete_yes_button
  delete_yes_button_element.wait_until(&:present?).fire_event :onclick
end

def click_org_edit_cancel
  org_edit_cancel_element.flash
  org_edit_cancel_element.wait_until(&:present?).fire_event :onclick
end

  #This method will click on both the register and cancel register links
  def click_cms_web_link
    cms_web_interface_reg_link_element.wait_until(&:present?).fire_event :onclick
  end


  def click_cancel_reg_continue_button
    #cancel_reg_continue_element.wait_until(&:present?).fire_event :onclick
    WebHelper.click(cancel_reg_continue_element)
  end

  def click_reg_again_continue_button
    reg_again_continue_element.wait_until(&:present?).fire_event :onclick
  end


  #This method will click on both the register and cancel register links
  def click_cahps_survey_link
    #cahps_survey_link_element.wait_until(&:present?).fire_event :onclick

    WebHelper.click(cahps_survey_link_element)
  end

  def cahps_survey_button_is_register
    cahps_survey_button_text = cahps_survey_link_element.wait_until_present.text
    if cahps_survey_button_text.delete(' ').downcase == "cancelregistration"
      false
    else
      true
    end
  end


  def cahps_survey_button_is_cancel
    cahps_survey_button_text = cahps_survey_link_element.wait_until_present.text
    if cahps_survey_button_text.delete(' ').downcase == "cancelregistration"
      true
    else
      false
    end
  end


  #This method will evaluate any link to see the text is cancel or register.
  #It will be used when we have a specific line item to evaluate
  def is_link_cancel(link_element)
    cahps_survey_button_text = link_element.wait_until_present.text
    if cahps_survey_button_text.delete(' ').downcase == ("cancelregistration")
      true
    else
      false
    end
  end

def delete_poc(name)
  if @browser.element(xpath:"//strong[contains(text(),'"+name+"')]/../*[4]/div/button[2]").exists?
    @browser.element(xpath:"//strong[contains(text(),'"+name+"')]/../*[4]/div/button[2]").fire_event :onclick
    sleep 3
    click_delete_yes_button
    sleep 3
  end
end

def click_edit_poc(name)
  if @browser.element(xpath: "//strong[contains(text(),'"+name+"')]/../*[4]/div/button[1]").exists?
    @browser.element(xpath: "//strong[contains(text(),'"+name+"')]/../*[4]/div/button[1]").flash
    @browser.element(xpath:"//strong[contains(text(),'"+name+"')]/../*[4]/div/button[1]").fire_event :onclick
  end
end



  def poc_exists(name)
    if @browser.element(xpath:"//strong[contains(text(),'"+name+"')]").exists?
      @browser.element(xpath:"//strong[contains(text(),'"+name+"')]").wait_until_present.flash
      true
    else
      false
    end
  end

  def edit_contact_info
    set_poc_last_name("pocdeletelastedited")

    $name = get_poc_last_name
  end

  def add_contact_info
    set_poc_first_name("Andrew")
    set_poc_last_name("Lunsford")
    set_poc_email("andrew.lunsford@fei.hcqis.org")
    set_poc_phone("1234567899")

  end

  def verify_edited_contact_info
    if @browser.element(xpath:"//strong[contains(text(),'pocdeletelastedited')]").wait_until(&:present?).exists?
      puts "The name was edited"
    else
      puts "The name was not edited"
    end
  end

  def show_detail_poc_exists(name)
    @browser.button(xpath:"//li[contains(text(),'"+name+"')]//button").wait_until_present.flash
    if @browser.button(xpath:"//li[contains(text(),'"+name+"')]//button").exists?
      true
    else
      false
    end
  end

  def delete_show_detail_poc(name)
    if @browser.button(xpath:"//li[contains(text(),'"+name+"')]//button").exist?
      @browser.button(xpath:"//li[contains(text(),'"+name+"')]//button").fire_event :onclick
      sleep 3
      click_delete_yes_button
      sleep 3

    end
  end

  def get_organization_name_asterisk_text
    organization_name_asterisk_element.wait_until(&:present?).text
  end

  def get_organization_address_asterisk_text
    organization_address_asterisk_element.wait_until(&:present?).text
  end

  def get_organization_city_asterisk_text
    organization_city_asterisk_element.wait_until(&:present?).text
  end

  def get_organization_state_asterisk_text
    organization_state_asterisk_element.wait_until(&:present?).text
  end

  def get_organization_zipcode_asterisk_text
    organization_zipcode_asterisk_element.wait_until(&:present?).text
  end


  def click_CAHPS_Register_button
    cahps_Register_button_element.wait_until(&:present?).fire_event :onclick
  end

  def get_org_list_label
    org_list_label_li_element.wait_until(&:present?).flash
    org_list_label_li_element.wait_until(&:present?).text
  end

  def get_org_list_review_label
    org_list_review_label_element.wait_until(&:present?).flash
    org_list_review_label_element.wait_until(&:present?).text
  end

  def get_org_list_review_tin
    org_list_review_tin_element.wait_until_present.flash
    tin_with_text = org_list_review_tin_element.wait_until_present.text
    tin = tin_with_text.sub("TIN:","").delete(' ')
    tin
  end

  def get_org_list_info_label
    org_list_info_label_element.wait_until(&:present?).flash
    org_list_info_label_element.wait_until(&:present?).text
  end

  def get_org_list_info_tin
    org_list_info_tin_element.wait_until_present.flash
    tin_with_text = org_list_info_tin_element.wait_until_present.text
    tin = tin_with_text.sub("TIN:","").delete(' ')
    tin
  end
  def get_org_address_label
    org_address_label_element.wait_until(&:present?).text
  end

  def get_group_size_text
    group_size_text_element.wait_until(&:present?)
  end

  def get_grey_line_exist
    grey_line_exist_element.wait_until(&:present?)
  end

  def get_org_contacts_list
    contact_list=@browser.elements(xpath:"//ul[@class='contacts']/li")
    contact_list
  end

  def get_org_cont_info_message
    org_cont_info_message_element.wait_until(&:present?)
  end

  def click_org_info_qpp_logo
    org_info_qpp_logo_element.wait_until(&:present?).fire_event :onclick
  end

  def get_qpp_page_title
    qpp_page_title_element.wait_until(&:present?)
  end

  def click_select_group_size_25_99
    select_group_size_25_99_element.wait_until(&:present?).fire_event :onclick
  end

  def get_org_edit_addr1_label
    org_edit_addr1_label_element.wait_until(&:present?).text
  end

  def get_org_edit_addr2_label
    org_edit_addr2_label_element.wait_until(&:present?).text
  end

  def get_org_info_edit_size
    org_info_edit_size_element.wait_until(&:present?).text
  end

  def get_org_info_edit_label
    org_info_edit_label_element.wait_until(&:present?).text
  end

  def click_contact_info_edit_button
    contact_info_edit_button_element.wait_until(&:present?).fire_event :onclick
  end

  def click_edit_org_contact_ok_button
    edit_org_contact_ok_button_element.wait_until(&:present?).fire_event :onclick
  end

  def get_org_reg_contacts_list
    contact_list=@browser.elements(xpath:"//div[@class='details__contacts']//ul/li")
    contact_list
  end

  def org_edit_update_button
    org_edit_update_button_element.wait_until(&:present?)
  end

  def get_landing_page_subtitle
    landing_page_subtitle_element.wait_until(&:present?).text
  end

  def get_org_title_list
    org_titles=@browser.elements(xpath:"//div[@class='list']//app-overview-list-item//app-registration-status[2]//b")
    org_titles
  end

  def get_review_page_title
    review_page_title_element.wait_until(&:present?).text
  end

  def get_review_page_subtitle
    review_page_subtitle_element.wait_until(&:present?).text
  end

  def get_edit_org_info_text
    edit_org_info_text_element.wait_until(&:present?).text
  end

  def get_back_to_list_modal_title
    back_to_list_modal_title_element.wait_until(&:present?).text
  end

  def get_cancel_reg_modal_text
    cancel_reg_modal_text_element.wait_until(&:present?).text
  end

  def get_reg_again_modal_text
    reg_again_modal_text_element.wait_until(&:present?).text
  end

  def get_org_cms_web_label
    org_cms_web_label_element.wait_until(&:present?).text
  end

  def get_org_cahps_mips_label
    org_cahps_mips_label_element.wait_until(&:present?).text
  end

  def get_org_cms_disc_text
  org_cms_disc_text_element.wait_until(&:present?).text
  end

  def get_org_cahps_disc_text
    org_cahps_disc_text_element.wait_until(&:present?).text
  end

  def get_org_edit_name
    val = org_edit_name_element.wait_until(&:present?).value.to_s
    if  val
      org_edit_name_element.set(val+",")
    elsif val == (val+","+","+",")
    org_edit_name_element.set(val)
    end
  end

end
