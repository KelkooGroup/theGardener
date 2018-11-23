const { SpecReporter } = require('jasmine-spec-reporter');

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

  framework: 'jasmine',

  specs: [
    './e2e/**/*.e2e-spec.ts'
  ],

  jasmineNodeOpts: {
    showColors: true,
    defaultTimeoutInterval: 30000,
    print: function() {}
  },

  onPrepare() {
    require('ts-node').register({
      project: 'e2e/tsconfig.e2e.json'
    });
    jasmine.getEnv().addReporter(new SpecReporter({ spec: { displayStacktrace: true } }));
  }
};
