import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { AppService } from './app.service';
import { HttpModule } from '@angular/http';

describe('Service: AppService', () => {
	let service: AppService;

	beforeEach(() => {
		service = new AppService();
	});

	it('should parse xml', () => {
		const xml = '<?xml version="1.0" encoding="UTF-8"?><PostResponse><Bucket>qrda-conversion-files</Bucket></PostResponse>';
		const xmlString = service.parseXml(xml);
		console.log(xmlString);
		expect(xmlString).toBeTruthy();
	});
});
