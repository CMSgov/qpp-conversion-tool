import { NgTestPage } from './app.po';

describe('ng-test App', () => {
	let page: NgTestPage;

	beforeEach(() => {
		page = new NgTestPage();
	});

	it('should display message saying app works', () => {
		page.navigateTo();
		expect(page.getParagraphText()).toEqual('app works!');
	});
});
