Feature: Provide configuration

  @level_2_technical_details @nominal_case @valid @ongoing
  Scenario: provide configuration
    Given the configuration
      | path                    | value                          |
      | application.windowTitle | theGardener                    |
      | application.title       | In our documentation we trust. |
      | application.logoSrc     | assets/images/logo-white.png   |
      | application.faviconSrc  | assets/images/favicon.png      |
    When I perform a "GET" on following URL "/api/config"
    Then I get a response with status "200"
    And  I get the following json response body
"""
{
     "windowTitle":"theGardener",
     "title":"In our documentation we trust.",
     "logoSrc":"assets/images/logo-white.png",
     "faviconSrc":"assets/images/favicon.png"
}
"""