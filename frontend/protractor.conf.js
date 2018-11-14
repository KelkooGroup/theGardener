exports.config = {
  allScriptsTimeout: 11000,

  capabilities: {
    browserName: 'chrome',
    chromeOptions: {
      args: ['disable-infobars', "--disable-gpu", "--window-size=1920,2400", "--headless"],
      prefs: {
        'credentials_enable_service': false,
        'profile': {
          'password_manager_enabled': false
        }
      }
    }

  },
  directConnect: true,
  baseUrl: 'http://localhost:4200/',

  // Use Cucumber
  framework: 'custom',
  frameworkPath: require.resolve('protractor-cucumber-framework'),

  specs: [
    './e2e/features/**/*.feature'
  ],

  cucumberOpts: {
    require: ['./e2e/steps/**/*.ts'],
    tags: ["@valid"],
    strict: true,
    format: [
      'node_modules/cucumber-pretty/index.js',
      'json:reports/summary.json'
    ],
    dryRun: false,
    compiler: []
  },

  onPrepare() {
    require('ts-node').register({
      project: 'e2e/tsconfig.e2e.json'
    });
  },

  useAllAngular2AppRoots: true
};
