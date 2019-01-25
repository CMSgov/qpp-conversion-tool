
class Verifications < QPP_Page
  include Test::Unit::Assertions
  include PageObject

  @@max_score_reached = "Max score for this category has been achieved for this performance period!"

  #span(:toast_msg, xpath: "//span[@class='toast-title']//span[@role='status']")
  span(:toast_msg, xpath: "//span[starts-with(@class,'toast-title')]")

  def verify_page_title(title)
    # puts "verify page title :- " + title
    #assert(@browser.title == title, "Page title is not matched. \n Expected:" + title + "\n Actual:" + @browser.title)
    actual = @browser.element(xpath: "//div[contains(@class,'title-container')]//h1|//div[contains(@class,'title-container')]|//div[contains(@class,'page-title')]//span|//h1[@class='page-title']").wait_until(&:present?).text
    expected = title.sub(' - QPP','')
    assert(actual == expected, "Page title is not matched. \n Expected:" + expected + "\n Actual:" + actual)
  end

  def verify_QPP_page_title(expected_title)
    actual = @browser.h1.wait_until(&:present?).text
    assert_equal(expected_title, actual, "Verifying page title. \\n Expected: #{expected_title}\\n Actual: #{actual}")
  end

  def verify_text(expected_text, actual_text,reporting_name='')
    #puts expected_text
    #puts actual_text
    if reporting_name != ''
      puts reporting_name
      assert_equal(expected_text, actual_text, "Text did not matched. \n Element Name:" + reporting_name +"\n Expected:" + expected_text + "\n Actual:" + actual_text)
    else
      assert_equal(expected_text, actual_text, "Text did not matched. \n Expected:" + expected_text + "\n Actual:" + actual_text)
    end
  end

  def verify_element_present(element, expected_value=true, rpt_element_name='')
    # puts "verify element present:-" + rpt_element_name
    if rpt_element_name !=''
      puts rpt_element_name
    end
    assert_equal(expected_value, element.exists?)
  end

  def verify_element_not_present(element, element_name='')
    assert_false(element.exist?, 'element' +element_name+ 'is present')
  end

  def verify_toast_message(expected_message)
    actual = toast_msg_element.wait_until(&:present?).text
    assert_equal(expected_message, actual, "Verifying toast message. \\n Expected: #{expected_message}\\n Actual: #{actual}")
  end
  
  def verify_element_disabled(element, rpt_element_name='')
    assert_true(element.disabled?, "element #{rpt_element_name} is disabled.")
  end

  def verify_element_enabled(element, rpt_element_name='')
    assert_false(element.disabled?, "element #{rpt_element_name} is enabled.")
  end

  def verify_modal_dialog_displayed(modal_dialog_title)
    actual_title = @browser.element(xpath: "(//div[contains(@class, 'modal-dialog')]//*[contains(@class,'modal-title')])[1]", visible: true).wait_until(&:present?).text
    assert_equal(modal_dialog_title,actual_title)
  end
end