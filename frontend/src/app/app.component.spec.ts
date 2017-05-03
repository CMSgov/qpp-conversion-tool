import { TestBed, async, ComponentFixture } from '@angular/core/testing';
import { AppComponent } from './app.component';
import { AppService } from './app.service';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

describe('AppComponent', () => {
	beforeEach(async(() => {
		TestBed.configureTestingModule({
			declarations: [
				AppComponent
			],
			imports: [
				FormsModule,
				HttpModule
			],
			providers: [
				AppService
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

	it('should create random file name', async(() => {
		const fixture = TestBed.createComponent(AppComponent);
		fixture.componentInstance.generateFileName();
		const filename = fixture.componentInstance.file_name;

		expect(filename.length).toBeGreaterThan(23);
		expect(filename).toContain('.xml');
	}));

});
