  @tcID_261989
@rubin @testRail  @ConverterEndpoint @qppct_imp_api
Feature: C261989 This is a test to verify every that eMeasureUuid within the measures-data.json is valid

  Scenario: Verify all eMeasureUuids within measures-data.json are valid.
    Given User starts QPPCT API test
    When  User accesses the 2017 measures data JSON in Github
    Then  User can verify all eMeasureUuids for the 2017 performance year are valid
