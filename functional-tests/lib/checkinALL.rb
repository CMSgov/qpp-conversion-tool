require 'bundler/setup'
require 'user_manager_client'

module CheckInUsers
  config = UserManagerClient::Configuration.new
  config.host = 'cmsdocker.titania.solutions:8080'
  api = UserManagerClient::ApiClient.new config
  $umClient = UserManagerClient::UsersApi.new api

  def self.allUsers
    users = $umClient.users_get
    users.each do |user|
      checkInUser(user)
    end
  end

  def self.withRole(userRole)
    users = $umClient.users_get
    users.each do |user|
      checkInUser(user) if user[:role] == userRole
    end
  end

  private_class_method def self.checkInUser(user)
                         if user[:checkedOut]
                           user[:checkedOut] = false
                           begin
                             resp = $umClient.update_user user[:id], user
                             puts "updated #{user[:id]}"
                           rescue StandardError => e
                             puts e
                           end
                         end
                       end
end

#-----------------------------------------------------------------------
# Use this to check-in all the users
# Note: Use the CheckInUsers.withRole to checkin any specific role users
#       Make sure no other jobs/tasks running before you checkin all users
#------------------------------------------------------------------------
#CheckInUsers.allUsers

#------------------------------------------------------------------------
# Use the below line of code to check-in any specific role related users
# (make sure to comment line to checkin all users.)
# Usage: CheckInUsers.withRole('GroupSubUi')
#------------------------------------------------------------------------
# CheckInUsers.withRole('XXX')
