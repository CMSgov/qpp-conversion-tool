# This comment should describe what page or page section the class represents in the application
require_relative 'qpp_page'
class Manage_User_Access_Page < QPP_Page
  include PageObject
  link(:edit_registration,xpath: "//a[contains(@href, '/user/web-interface/#/registration/overview') and @aria-label='Registration overview.']")

  h1(:page_title_h1, xpath: "//h1[contains(text(),'Manage User Access')]")
  # Interactive methods used to hide implementation details
  # http://www.rubydoc.info/github/cheezy/page-object/PageObject
  #---------------------------------------------------------------------------------------
  #   Method implementations
  #---------------------------------------------------------------------------------------

  #
  # Clicks on the edit registration
  #
  def click_edit_registration
    #WebHelper.click(edit_registration_element)
    edit_registration_element.wait_until(&:present?).fire_event :onclick
  end

  def check_if_header_exists
    if page_title_h1_element.exists?
      true
    else
      false
    end
  end

end
