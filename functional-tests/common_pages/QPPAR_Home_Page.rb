# Page object representing the home page of the Quality Payment Program
require_relative 'qpp_page'

class QPPAR_Home_Page < QPP_Page

  page_url UserUtil.get_homepage("QPPAR")

end