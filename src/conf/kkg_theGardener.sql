INSERT INTO hierarchyNode VALUES (".", "root", "root", "Views", "View");

INSERT INTO hierarchyNode VALUES (".01.", "eng", "Engineering", "Areas", "Area");

INSERT INTO hierarchyNode VALUES (".01.01.", "publisher", "Publisher", "System groups", "System group");

INSERT INTO hierarchyNode VALUES (".01.01.01.", "extranet", "Publisher extranet", "Projects", "Project");
INSERT INTO hierarchyNode VALUES (".01.01.02.", "internal", "Publisher internal management", "Projects", "Project");
INSERT INTO hierarchyNode VALUES (".01.01.03.", "feeds", "Feeds to publishers", "Projects", "Project");
INSERT INTO hierarchyNode VALUES (".01.01.04.", "api", "API to publishers", "Projects", "Project");

INSERT INTO hierarchyNode VALUES (".01.02.", "search", "Search", "System groups", "System group");

INSERT INTO hierarchyNode VALUES (".01.02.01.", "meta", "Search meta info", "Projects", "Project");

INSERT INTO hierarchyNode VALUES (".01.03.", "gsa", "Google Shopping Ads", "Systems", "System");

INSERT INTO hierarchyNode VALUES (".01.03.01.", "plabidder", "PLA Bidder", "Projects", "Project");

INSERT INTO hierarchyNode VALUES (".02.", "biz", "Business", "Areas", "Area");

INSERT INTO project VALUES ("publisherExtranet", "Publisher extranet", "git@gitlab.corp.kelkoo.net:syndication/publisherExtranet.git", "qa", "test/features");
INSERT INTO project VALUES ("publisherExtranetDataAggregatorSparkApplication", "Publisher extranet data aggregator", "git@gitlab.corp.kelkoo.net:syndication/publisherExtranetDataAggregatorSparkApplication.git", "qa", "src/test/features");
INSERT INTO project VALUES ("publisherManagementBO", "Publisher management BO", "git@gitlab.corp.kelkoo.net:syndication/publisherManagementBO.git", "qa", "test/features");
INSERT INTO project VALUES ("publisherFeedSparkApplication", "Offer feed service", "git@gitlab.corp.kelkoo.net:syndication/publisherFeedSparkApplication.git", "qa", "features");
INSERT INTO project VALUES ("kelkooUrlForLeadService", "Lead service urls", "git@gitlab.corp.kelkoo.net:common/kelkooUrl.git", "qa", "features/LeadService");
INSERT INTO project VALUES ("leadService", "Lead service", "git@gitlab.corp.kelkoo.net:syndication/kls.git", "qa", "test/features");
INSERT INTO project VALUES ("googleShoppingAdsBidderServices", "PLA Bidder services", "git@gitlab.corp.kelkoo.net:google-eu/googleShoppingAdsBidderServices.git", "qa", "src/test/test/features");
INSERT INTO project VALUES ("offerStatsWS", "Offer stats WS", "git@gitlab.corp.kelkoo.net:syndication/offerStatsWS.git", "qa", "test/features");

INSERT INTO project_hierarchyNode VALUES ("publisherExtranet", ".01.01.01.");
INSERT INTO project_hierarchyNode VALUES ("publisherExtranetDataAggregatorSparkApplication", ".01.01.01.");
INSERT INTO project_hierarchyNode VALUES ("publisherManagementBO", ".01.01.02.");
INSERT INTO project_hierarchyNode VALUES ("publisherFeedSparkApplication", ".01.01.03.");
INSERT INTO project_hierarchyNode VALUES ("kelkooUrlForLeadService", ".01.01.04.");
INSERT INTO project_hierarchyNode VALUES ("leadService", ".01.01.04.");
INSERT INTO project_hierarchyNode VALUES ("offerStatsWS", ".01.02.01.");
INSERT INTO project_hierarchyNode VALUES ("googleShoppingAdsBidderServices", ".01.03.01.");
