#This comment should describe what page or page section the class represents in the application
require_relative 'qpp_page'
class Performance_Feedback < QPP_Page
  include PageObject
# Interactive web elements associated with this page
# For more details see, https://github.com/cheezy/page-object/wiki/Elements

  link(:apm_view_practice_link, xpath: "//h3[contains(text(), 'APM Organizations')]/../../div[@class='buttons-container']/a")

  link(:view_performance_details, xpath: "//a[text()='VIEW PERFORMANCE DETAILS']")

  link(:view_practice_feedback_link, xpath: "//a[contains(text(),'VIEW PRACTICE FEEDBACK')]")


  # Interactive methods used to hide implementation details
  # http://www.rubydoc.info/github/cheezy/page-object/PageObject
  #---------------------------------------------------------------------------------------
  #   Method implementations
  #---------------------------------------------------------------------------------------

  def click_apm_view_practice_link
    apm_view_practice_link_element.wait_until(&:present?).fire_event :onclick
  end


  def click_view_performance_details_link
    view_performance_details_element.wait_until(&:present?).fire_event :onclick
  end

  def click_view_practice_feedback_link
    view_practice_feedback_link_element.wait_until(&:present?).fire_event :onclick
  end

end