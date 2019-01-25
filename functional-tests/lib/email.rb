require 'gmail'
require 'uri'

module EMAIL

  def self.get_resetLink(username, password)
     @gmail = Gmail.new("qppitreset1","HelloHivvs1!")
     #@gmail = Gmail.new(username, password)
     resetLink =""
    emails = @gmail.inbox.emails
    emails.each do |email|
      if email.message.multipart?
        email_body = email.message.html_part.body.decoded
      else
        email_body = email.message.body.decoded
      end

      resetLink = URI.extract(email_body, ["https"])[0]
      puts resetLink
      email.delete!
    end
    return resetLink
  end
end

# EMAIL.get_resetLink("a","b")