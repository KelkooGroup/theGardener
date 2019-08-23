import {DirectoryApi, HierarchyNodeApi, PageApi} from '../_models/hierarchy';

export const MENU_HEADER_SERVICE_RESPONSE: HierarchyNodeApi = {
  id: '.',
  hierarchy: '_',
  slugName: 'root',
  name: 'Hierarchy root',
  childrenLabel: 'Views',
  childLabel: 'View',
  children: [
    {
      id: '.01.',
      hierarchy: '_eng',
      slugName: 'eng',
      name: 'Engineering view',
      childrenLabel: 'System groups',
      childLabel: 'System group'
    },
    {
      id: '.02.',
      hierarchy: '_biz',
      slugName: 'biz',
      name: 'Business view',
      childrenLabel: 'Units',
      childLabel: 'Unit'
    }
  ]
};

export const MENU_SUBMENU_SERVICE_RESPONSE: HierarchyNodeApi = {
  id: '.01.',
  hierarchy: '_eng',
  slugName: 'eng',
  name: 'Engineering view',
  childrenLabel: 'System groups',
  childLabel: 'System group',
  projects: [],
  children: [
    {
      id: '.01.01.',
      hierarchy: '_eng_library',
      slugName: 'library',
      name: 'Library system group',
      childrenLabel: 'Systems',
      childLabel: 'System',
      projects: [],
      children: [
        {
          id: '.01.01.01.',
          hierarchy: '_eng_library_suggestion',
          slugName: 'suggestion',
          name: 'Suggestion system',
          childrenLabel: 'Projects',
          childLabel: 'Project',
          projects: [
            {
              id: 'suggestionsReports',
              path: 'suggestionsReports',
              label: 'Suggestions Reports',
              stableBranch: 'master',
              branches: [
                {
                  name: 'master',
                  path: 'suggestionsReports>master'
                }
              ]
            },
            {
              id: 'suggestionsWS',
              path: 'suggestionsWS',
              label: 'Suggestions WebServices',
              stableBranch: 'master',
              branches: [
                {
                  name: 'master',
                  path: 'suggestionsWS>master',
                  rootDirectory: {
                    id: '1',
                    path: 'suggestionsWS>master>/',
                    name: 'root',
                    label: 'SuggestionsWS',
                    description: 'Suggestions WebServices',
                    order: 0,
                    children: [
                      {
                        id: '2',
                        path: 'suggestionsWS>master>/suggestions/',
                        name: 'suggestions',
                        label: 'Suggestions',
                        description: 'Suggestions...',
                        order: 0,
                        children: []
                      },
                      {
                        id: '3',
                        path: 'suggestionsWS>master>/admin/',
                        name: 'admin',
                        label: 'Admin',
                        description: 'Administration...',
                        order: 1,
                        children: []
                      }
                    ]
                  }
                },
                {
                  name: 'bugfix/351',
                  path: 'suggestionsWS>bugfix/351'
                }
              ]
            }
          ],
          children: []
        },
        {
          id: '.01.01.02.',
          hierarchy: '_eng_library_user',
          slugName: 'user',
          name: 'User system',
          childrenLabel: 'Projects',
          childLabel: 'Project',
          projects: [
            {
              id: 'usersWS',
              path: 'usersWS',
              label: 'Users WebServices',
              stableBranch: 'master',
              branches: [
                {
                  name: 'master',
                  path: 'usersWS>master'
                }
              ]
            }
          ],
          children: []
        },
        {
          id: '.01.01.03.',
          hierarchy: '_eng_library_search',
          slugName: 'search',
          name: 'Search system',
          childrenLabel: 'Projects',
          childLabel: 'Project',
          projects: [],
          children: []
        }
      ]
    }
  ]
};

export const DIRECTORIES_SERVICE_RESPONSE: Array<DirectoryApi> = [
  {
    id: '11',
    path: 'publisherManagementWS>qa>/constraints/',
    name: 'constraints',
    label: 'Constraints',
    description: 'Filter offers provided to the publisher',
    order: 0,
    pages: [
      {
        path: 'publisherManagementWS>qa>/constraints/overview',
        relativePath: 'overview',
        name: 'overview',
        label: 'Overview',
        description: 'overview',
        order: 0,
      },
      {
        path: 'publisherManagementWS>qa>/constraints/for_a_publisher',
        relativePath: 'for_a_publisher',
        name: 'for_a_publisher',
        label: 'For a publisher',
        description: 'for_a_publisher',
        order: 1,
      },
      {
        path: 'publisherManagementWS>qa>/constraints/for_a_merchant',
        relativePath: 'for_a_merchant',
        name: 'for_a_merchant',
        label: 'For a merchant',
        description: 'for_a_merchant',
        order: 3,
      },
      {
        path: 'publisherManagementWS>qa>/constraints/for_an_offer',
        relativePath: 'for_an_offer',
        name: 'for_an_offer',
        label: 'For an offer',
        description: 'for_an_offer',
        order: 2,
      }
    ]
  }
];

export const PAGE_SERVICE_RESPONSE: Array<PageApi> = [{
  path: 'publisherManagementWS>qa>/constraints/overview',
  relativePath: '/constraints/overview',
  name: 'overview',
  label: 'overview',
  description: 'overview',
  order: 0,
  markdown: 'For various reasons, the offers provided to the publishers can be filtered. We don\'t necessary want to provide all the offers to the publishers : ![Overview](../assets/images/constraints_overview.png) This is the constraints objective. The constraints can be defined manually by the **BizDevs** or automatically by the **TrafficOptimizer**. ### Reference and application The constraints are defined and stored in PMWS component. The impact of those constraints is implemented at the client level: - **eCS** and **ShoppingAPI** are filtering the call to Search6 - **FeedService** is filtering the offers provided (offers coming from OfferProcessing) - others systems are also using the constraints to filter offers provided to external clients : example GSA Exporter or COP. The constraints are stored against a profile (which is linked to a contract, which is linked to the publisher itself ([See details](thegardener://${current.project}/${current.branch}/overview))). All trackings of a profile share the same constraints. Clients are most of the time using a tracking as input data to find out what are the constraints to be applied. ### Different sources of constraints There are several ways to define the constraints : - for a publisher, [filter the merchants that can provide offers](thegardener://${current.project}/${current.branch}/constraints/for_a_publisher). - for a merchant, [filter the publishers that can receive offers](thegardener://${current.project}/${current.branch}/constraints/for_a_merchant). - moreover, [offers can be filtered for various reasons](thegardener://${current.project}/${current.branch}/constraints/for_an_offer). ![Sources](../assets/images/constraints_sources_overview.png) All those filters are cumulative : **each offer need to pass through all the filters**. In other words it\'s a AND between each constraint. We can see the impact of [those constraints on the PMBO](thegardener://${current.project}/${current.branch}/constraints/from_pmbo). '
}];

export const MENU_SERVICE_RESPONSE: HierarchyNodeApi = {
  id: '.',
  hierarchy: '_',
  slugName: 'root',
  name: 'Hierarchy root',
  childrenLabel: 'Views',
  childLabel: 'View',
  projects: [],
  children: [
    {
      id: '.01.',
      hierarchy: '_eng',
      slugName: 'eng',
      name: 'Engineering view',
      childrenLabel: 'System groups',
      childLabel: 'System group',
      projects: [],
      children: [
        {
          id: '.01.01.',
          hierarchy: '_eng_library',
          slugName: 'library',
          name: 'Library system group',
          childrenLabel: 'Systems',
          childLabel: 'System',
          projects: [],
          children: [
            {
              id: '.01.01.01.',
              hierarchy: '_eng_library_suggestion',
              slugName: 'suggestion',
              name: 'Suggestion system',
              childrenLabel: 'Projects',
              childLabel: 'Project',
              projects: [
                {
                  id: 'suggestionsReports',
                  path: 'suggestionsReports',
                  label: 'Suggestions Reports',
                  stableBranch: 'master',
                  branches: [
                    {
                      name: 'master',
                      path: 'suggestionsReports>master'
                    }
                  ]
                },
                {
                  id: 'suggestionsWS',
                  path: 'suggestionsWS',
                  label: 'Suggestions WebServices',
                  stableBranch: 'master',
                  branches: [
                    {
                      name: 'master',
                      path: 'suggestionsWS>master',
                      rootDirectory: {
                        id: '1',
                        path: 'suggestionsWS>master>/',
                        name: 'root',
                        label: 'SuggestionsWS',
                        description: 'Suggestions WebServices',
                        order: 0,
                        pages: [
                          'context'
                        ],
                        children: [
                          {
                            id: '2',
                            path: 'suggestionsWS>master>/suggestions/',
                            name: 'suggestions',
                            label: 'Suggestions',
                            description: 'Suggestions...',
                            order: 0,
                            pages: [
                              'suggestionsWS>master>/suggestions/suggestion',
                              'suggestionsWS>master>/suggestions/examples'
                            ],
                            children: []
                          },
                          {
                            id: '3',
                            path: 'suggestionsWS>master>/admin/',
                            name: 'admin',
                            label: 'Admin',
                            description: 'Administration...',
                            order: 1,
                            pages: [
                              'suggestionsWS>master>/admin/admin'
                            ],
                            children: []
                          }
                        ]
                      }
                    },
                    {
                      name: 'bugfix/351',
                      path: 'suggestionsWS>bugfix/351'
                    }
                  ]
                }
              ],
              children: []
            },
            {
              id: '.01.01.02.',
              hierarchy: '_eng_library_user',
              slugName: 'user',
              name: 'User system',
              childrenLabel: 'Projects',
              childLabel: 'Project',
              projects: [
                {
                  id: 'usersWS',
                  path: 'usersWS',
                  label: 'Users WebServices',
                  stableBranch: 'master',
                  branches: [
                    {
                      name: 'master',
                      path: 'usersWS>master'
                    }
                  ]
                }
              ],
              children: []
            },
            {
              id: '.01.01.03.',
              hierarchy: '_eng_library_search',
              slugName: 'search',
              name: 'Search system',
              childrenLabel: 'Projects',
              childLabel: 'Project',
              projects: [],
              children: []
            }
          ]
        }
      ]
    },
    {
      id: '.02.',
      hierarchy: '_biz',
      slugName: 'biz',
      name: 'Business view',
      childrenLabel: 'Units',
      childLabel: 'Unit',
      projects: [
        {
          id: 'suggestionsWS',
          path: 'suggestionsWS',
          label: 'Suggestions WebServices',
          stableBranch: 'master',
          branches: [
            {
              name: 'master',
              path: 'suggestionsWS>master'
            },
            {
              name: 'bugfix/351',
              path: 'suggestionsWS>bugfix/351'
            }
          ]
        }
      ],
      children: []
    }
  ]
};

