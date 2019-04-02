@tcID_261992 @rubin

  Feature: Test that unprocessed files are ordered by date

    Scenario: Ticket QPPHT-1911 test.
      Given User starts QPPCT API test
      When User authenticates the QPPCT API with role=CPCPLUSJWT
      When User makes GET request to "/cpc/unprocessed-files"
      Then User receives 200 response code
      And  User can verify the unprocessed files' submission sequence is ordered by date
