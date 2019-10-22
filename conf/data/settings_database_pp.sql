


INSERT INTO thegardener.project (id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ('shoppingAPI', 'Shopping API', 'http://gitlab.corp.kelkoo.net/syndication/shoppingApi.git', 'qa', 'qa|master|feature.*', 'test/features', 'documentation', null);
INSERT INTO thegardener.project (id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ('theGardener', 'theGardener', 'https://github.com/KelkooGroup/theGardener.git', 'master', 'master', 'test/features', 'documentation', null);
INSERT INTO thegardener.project (id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ('leadService', 'Lead service', 'http://gitlab.corp.kelkoo.net/syndication/kls.git', 'qa', 'qa|master|feature.*', 'test/features', 'documentation', null);
INSERT INTO thegardener.project (id, name, repositoryUrl, stableBranch, displayedBranches, featuresRootPath, documentationRootPath, variables) VALUES ('publisherSystems', 'publisher', 'http://gitlab.corp.kelkoo.net/syndication/thegardener-publisher-data.git', 'master', 'master', 'documentation', 'documentation', null);



INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.', 'root', 'root', 'Views', 'View', null);
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.01.', 'publisher', 'Publisher', 'System groups', 'System group', 'publisherSystems>master>/Public/');
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.02.', 'merchant', 'Merchant', 'Areas', 'Area', null);
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.03.', 'gsa', 'GSA', 'Areas', 'Area', null);
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.04.', 'kdp', 'KDP', 'Areas', 'Area', null);
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.05.', 'cop', 'COP', 'Areas', 'Area', null);
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.06.', 'tools', 'Tools', 'Projects', 'Project', null);
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.01.01.', 'mgt', 'Publisher management', 'Projects', 'Project', 'publisherSystems>master>/');
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.01.02.', 'api', 'Publishers service', 'Projects', 'Project', null);
INSERT INTO thegardener.hierarchyNode (id, slugName, name, childrenLabel, childLabel, directoryPath) VALUES ('.01.03.', 'api', 'Publishers extranet', 'Projects', 'Project', null);



INSERT INTO thegardener.project_hierarchyNode (projectId, hierarchyId) VALUES ('leadService', '.01.02.');
INSERT INTO thegardener.project_hierarchyNode (projectId, hierarchyId) VALUES ('shoppingAPI', '.01.02.');
INSERT INTO thegardener.project_hierarchyNode (projectId, hierarchyId) VALUES ('theGardener', '.06.');