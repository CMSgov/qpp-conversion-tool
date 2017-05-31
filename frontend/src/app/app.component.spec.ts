import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { FileSelectDirective, FileDropDirective, FileUploader } from '../../node_modules/ng2-file-upload/ng2-file-upload';

describe('AppComponent', () => {
	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [
				AppComponent,
				FileSelectDirective,
				FileDropDirective
			],
			imports: [
				FormsModule,
				HttpModule
			]
		}).compileComponents();
	}));

	it('should create the app', async(() => {
		const fixture = TestBed.createComponent(AppComponent);
		const app = fixture.debugElement.componentInstance;
		expect(app).toBeTruthy();
	}));

	it(`should have as title 'Convert QRDA-III to QPP'`, async(() => {
		const fixture = TestBed.createComponent(AppComponent);
		const app = fixture.debugElement.componentInstance;
		expect(app.title).toEqual('Convert QRDA-III to QPP');
	}));

	it('should render title in a h1 tag', async(() => {
		const fixture = TestBed.createComponent(AppComponent);
		fixture.detectChanges();
		const compiled = fixture.debugElement.nativeElement;
		expect(compiled.querySelector('h1').textContent).toContain('Convert QRDA-III to QPP');
	}));
});
