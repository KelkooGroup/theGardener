



INSERT INTO thegardener.project (id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ('shoppingApi', 'Shopping API', 'http://gitlab.corp.kelkoo.net/syndication/shoppingApi.git', 'qa', 'qa', 'test/features', 'documentation/Public', null);
INSERT INTO thegardener.project (id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ('leadService', 'Lead service', 'http://gitlab.corp.kelkoo.net/syndication/kls.git', 'qa', 'qa', 'test/features', 'documentation/Public', null);
INSERT INTO thegardener.project (id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ('publisherSystems', 'publisher', 'http://gitlab.corp.kelkoo.net/syndication/thegardener-publisher-data.git', 'master', 'master', 'documentation', 'documentation/Public', null);

INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.', 'root', 'root', 'Views', 'View', null);
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.01.', 'publisher', 'Publisher', 'System groups', 'System group', 'publisherSystems>master>/');

INSERT INTO thegardener.project_hierarchyNode (projectId, hierarchyId) VALUES ('leadService', '.01.');
INSERT INTO thegardener.project_hierarchyNode (projectId, hierarchyId) VALUES ('shoppingApi', '.01.');