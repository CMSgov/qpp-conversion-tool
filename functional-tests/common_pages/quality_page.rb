# This comment should describe what page or page section the class represents in the application
require_relative 'qpp_page'
class Quality_Page < QPP_Page
  include PageObject
# Interactive web elements associated with this page
# For more details see, https://github.com/cheezy/page-object/wiki/Elements

  link(:go_to_cms_web_interface_link, xpath: "//a[contains(text(), 'GO TO CMS WEB INTERFACE')]")



  # Interactive methods used to hide implementation details
  # http://www.rubydoc.info/github/cheezy/page-object/PageObject
  #---------------------------------------------------------------------------------------
  #   Method implementations
  #---------------------------------------------------------------------------------------

  def click_web_interface
    go_to_cms_web_interface_link_element.wait_until(&:present?).fire_event :onclick
  end


end