import {HierarchyNodeApi} from '../_models/hierarchy';

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
              projects: []        ,
              children: []
            }
          ]
        }
      ]
    },
    {
      id: '.02.',
      hierarchy: '_eng_library_biz',
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

