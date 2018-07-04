Feature: As a user, I want some book suggestions so that I can do some discovery

@level_0_high_level @nominal_case @ready
Scenario: providing several book suggestions
Given a user
When we ask for suggestions
Then the suggestions are popular and available books adapted to the age of the user