import { AppPage } from './app.po';

import chai = require('chai');
import chaiAsPromised = require('chai-as-promised');
chai.use(chaiAsPromised);
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
