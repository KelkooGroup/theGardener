

INSERT INTO project (id, name, repositoryUrl, stableBranch,  displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ( 'publisherSystems',      'publisher', 'http://gitlab.corp.kelkoo.net/syndication/thegardener-publisher-data.git', 'master', 'master', 'documentation', 'documentation', null);


INSERT INTO project (id, name, repositoryUrl, stableBranch,  displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ( 'shoppingAPI',              'Shopping API', 'http://gitlab.corp.kelkoo.net/syndication/shoppingApi.git', 'qa', 'qa', 'test/features', 'documentation/Public', null);
INSERT INTO project (id, name, repositoryUrl, stableBranch,  displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ( 'ecs',              'eCommerce Services', 'http://gitlab.corp.kelkoo.net/syndication/ecsFrontApp.git', 'qa', 'qa', 'test/features', 'doc/public', null);
INSERT INTO project (id, name, repositoryUrl, stableBranch,  displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ( 'leadService',              'Lead service', 'http://gitlab.corp.kelkoo.net/syndication/kls.git', 'qa', 'qa', 'test/features', 'documentation/Public', null);
INSERT INTO project (id, name, repositoryUrl, stableBranch , displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ( 'feedService',    'Feed service', 'http://gitlab.corp.kelkoo.net/syndication/publisherFeedSparkApplication.git', 'qa', 'qa', 'features', 'documentation/Public', null);





INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.', 'root', 'root', 'Views', 'View', null);
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.01.', 'publisher', 'Publisher', 'System groups', 'System group', 'publisherSystems>master>/Public/');



INSERT INTO thegardener.project_hierarchyNode (projectId, hierarchyId) VALUES (  'shoppingAPI',             '.01.');
INSERT INTO thegardener.project_hierarchyNode (projectId, hierarchyId) VALUES (  'ecs',             '.01.');
INSERT INTO thegardener.project_hierarchyNode (projectId, hierarchyId) VALUES (  'leadService',               '.01.');
INSERT INTO thegardener.project_hierarchyNode (projectId, hierarchyId) VALUES (  'feedService',   '.01.');
