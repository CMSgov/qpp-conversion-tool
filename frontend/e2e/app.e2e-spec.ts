import { NgTestPage } from './app.po';

describe('ng-test App', () => {
	let page: NgTestPage;

	beforeEach(() => {
		page = new NgTestPage();
	});

	it('should display message saying Convert QRDA-III to QPP', () => {
		page.navigateTo();
		expect(page.getParagraphText()).toEqual('Convert QRDA-III to QPP');
	});
});
