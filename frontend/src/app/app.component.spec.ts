import {
	TestBed,
	getTestBed,
	async,
	inject
} from '@angular/core/testing';
import {
	Headers, BaseRequestOptions,
	Response, HttpModule, Http, XHRBackend, RequestMethod
} from '@angular/http';

import { AppComponent } from './app.component';
import {MockBackend, MockConnection} from '@angular/http/testing';
import {ResponseOptions} from '@angular/http';

import { Observable } from 'rxjs/Observable';
import { AppService } from './app.service';


describe('AppComponent', () => {
	let mockBackend: MockBackend;

	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [ AppComponent],
			providers: [
				AppService,
				MockBackend,
				BaseRequestOptions,
				{
					provide: Http,
					deps: [MockBackend, BaseRequestOptions],
					useFactory:
						(backend: XHRBackend, defaultOptions: BaseRequestOptions) => {
							return new Http(backend, defaultOptions);
						}
				}
			],
			imports: [
				HttpModule
			]
		}).compileComponents();
		mockBackend = getTestBed().get(MockBackend);
	}));


	it('should create the app', async(() => {
		const fixture = TestBed.createComponent(AppComponent);
		const app = fixture.debugElement.componentInstance;
		expect(app).toBeTruthy();
	}));

	it(`should have as title 'app works!'`, async(() => {
		const fixture = TestBed.createComponent(AppComponent);
		const app = fixture.debugElement.componentInstance;
		expect(app.title).toEqual('app works!');
	}));

	it('should render title in a h1 tag', async(() => {
		const fixture = TestBed.createComponent(AppComponent);
		fixture.detectChanges();
		const compiled = fixture.debugElement.nativeElement;
		expect(compiled.querySelector('h1').textContent).toContain('app works!');
	}));

	it('Pinging gateway should return a response', async(() => {
		let appService: AppService;
		mockBackend.connections.subscribe(
			(connection: MockConnection) => {
			connection.mockRespond(new Response(
			new ResponseOptions({
				body: [
					{
					id: 26,
					contentRendered: '<p><b>Hi there</b></p>',
					contentMarkdown: '*Hi there*'
					}]
				}
			)));
		});

		appService = getTestBed().get(AppService);
		expect(appService).toBeDefined();

		appService.getEndpoint().subscribe((message) => {
			expect(message).toBeDefined();
		});

	}));

});
