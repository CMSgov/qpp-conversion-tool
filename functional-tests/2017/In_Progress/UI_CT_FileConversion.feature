@smoke @O'Neill
Feature: This test is to verify a user can upload a valid QRDAIII file through the UI

  Scenario Outline: Positive
    Given User opens a browser
    And   User visit QPP home page
    And   User click sign in link on the top right of the page
    When  User logs in to QPPWI with role=<role>
    And   User selects <link> next to the desired TIN
    And   User selects the Start Reporting button for the group <group_name>
    When  User uploads file <file_name>

    Examples:
      | role  | link            | group_name       | file_name                                    |
      | Group | Report as group | Quality Measures | 2017/valid-QRDA-III-latest Automated000553743.xml |

