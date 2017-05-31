import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { FileSelectDirective, FileDropDirective, FileUploader } from '../../node_modules/ng2-file-upload/ng2-file-upload';

import { AppComponent } from './app.component';

@NgModule({
	declarations: [
		AppComponent,
		FileSelectDirective,
		FileDropDirective
	],
	imports: [
		BrowserModule,
		FormsModule,
		HttpModule
	],
	providers: [],
	bootstrap: [AppComponent]
})
export class AppModule { }
