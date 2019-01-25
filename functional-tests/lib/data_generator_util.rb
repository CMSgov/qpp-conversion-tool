module DataGeneratorUtilities
  def self.randm_number(numberLength)
    charset = Array('1'..'9')
    Array.new(numberLength){charset.sample}.join
  end

end