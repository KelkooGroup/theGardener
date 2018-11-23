import { AppPage } from './app.po';

const chai = require('chai').use(require('chai-as-promised'));
const expect = chai.expect;

describe('workspace-project App', () => {
  let page: AppPage;

  beforeEach(() => {
    page = new AppPage();
  });

  it('should display title', () => {
    page.navigateTo();
    expect(page.getTitle()).to.be.eventually.equal('theGardener');
  });
});
