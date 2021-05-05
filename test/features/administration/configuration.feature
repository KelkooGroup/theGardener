Feature: Provide configuration

  @level_2_technical_details @nominal_case @valid @documentation
  Scenario: provide configuration
    Given the configuration
      | path                    | value                                                                            |
      | application.windowTitle | theGardener                                                                      |
      | application.title       | In our documentation we trust.                                                   |
      | application.logoSrc     | assets/images/logo-white.png                                                     |
      | application.faviconSrc  | assets/images/favicon.png                                                        |
      | application.baseUrl     | http://localhost:9000                                                            |
      | color.dark              | #3d853e                                                                          |
      | color.main              | #55b556                                                                          |
      | color.light             | #f2fff2                                                                          |
      | translate.to            | fr,de,es                                                                         |
      | translate.template      | https://translate.google.com/translate?hl=en&sl=auto&tl={{to}}&u={{encoded_url}} |
    When I perform a "GET" on following URL "/api/config"
    Then I get a response with status "200"
    And  I get the following json response body
"""
{
     "windowTitle":"theGardener",
     "title":"In our documentation we trust.",
     "logoSrc":"assets/images/logo-white.png",
     "faviconSrc":"assets/images/favicon.png",
     "baseUrl":"http://localhost:9000",
     "colorMain":"#55b556",
     "colorDark":"#3d853e",
     "colorLight":"#f2fff2",
     "translateTo": "fr,de,es",
     "translateTemplate": "https://translate.google.com/translate?hl=en&sl=auto&tl={{to}}&u={{encoded_url}}"
}
"""
