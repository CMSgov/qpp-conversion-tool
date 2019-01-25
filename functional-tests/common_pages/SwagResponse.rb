class SwagResponse
  def initialize(code, *body)
    @code = code
    @body = body if body
  end

  def code
    @code
  end

  def set_body(body)
    @body = body
  end

  def read_body
    @body
  end
end