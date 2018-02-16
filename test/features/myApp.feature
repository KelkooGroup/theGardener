Feature: As Kelkoo, I have a basic web application

  @Level_1 @valid
  Scenario: Nominal case
    Given My data source returns the following data
      | field1               | field2 |
      | The universal answer | 42     |
      | Emergency number     | 911    |
      | Bad luck             | 13     |
    When I perform a GET on following URL "/dataJson"
    Then I get a response with status "200"
    And I get the following json response body
      | json                                                                                                                         |
      | [{"field1":"The universal answer","field2":42},{"field1":"Emergency number","field2":911},{"field1":"Bad luck","field2":13}] |
