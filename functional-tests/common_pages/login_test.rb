Given(/^Test all (.*) UserManager logins$/) do |aut|
  users = UserUtil.get_all_users(aut)
  users.each do |user|
    if !user[:uid].nil? and !user[:pwdE].nil?
      @browser = SeleniumUtil.open()
      step "User visit QPP home page"
      step "User click sign in link on the top right of the page"
      step "User logs in to QPPWI with uid=#{user[:uid]} and pwdE=#{user[:pwdE]}"
      step "User logs out"
      step "User closes browser"
    end
  end
end