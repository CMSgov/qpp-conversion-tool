# Page object representing the mips overview page of the Quality Payment Program

class MIPS_Overview_Page
  include PageObject

  h1(:overview_hdr, :text => 'MIPS Overview')
  h3(:quality_hdr, :text => 'Quality')
  h3(:ia_hdr, :text => 'Improvement Activities')
  h3(:aci_hdr, :text => 'Advancing Care Information')
  h3(:cost_hdr, :text => 'Cost')
  a(:participation_status_btn, :class => 'btn-primary')

  def click_participation_status
    self.participation_status_btn_element.when_visible.click
  end

end