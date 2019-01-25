
class XmlMethods
  def update_user_details(xmlFilePath)
    puts CommonXmlUtil.instance_methods
    temp_file_path = Dir.pwd + '/data/xml_files/' + xmlFilePath
    # update the TIN Number
    CommonXmlUtil::XMLUtilAPI.xml_edit_attribute_value(temp_file_path,'id','extension',$userObj['tin'.to_sym],'performer//representedOrganization',0,0)
    # get the NPI number (If it's individual user)
    if $userObj['npi'.to_sym]=='' or !$userObj['npi'.to_sym].nil?
      CommonXmlUtil::XMLUtilAPI.xml_edit_attribute_value(temp_file_path,'id','extension',$userObj['npi'.to_sym],'performer//assignedEntity',0,0)
    end
  end
end