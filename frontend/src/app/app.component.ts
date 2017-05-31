import { Component, OnInit } from '@angular/core';
import { FileSelectDirective, FileDropDirective, FileUploader } from '../../node_modules/ng2-file-upload/ng2-file-upload';

const URL  = 'http://184.73.24.93:2680/v1/qrda3';
@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent {
	title = 'Convert QRDA-III to QPP';
	success_data: any;
	error_data: any;
	uploader: FileUploader = new FileUploader({url: URL});
	hasBaseDropZoneOver: boolean;
	hasAnotherDropZoneOver: boolean;
	response: string;

	constructor () {
		this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
			console.log('Endpoint Response:', response);
			this.response = response;
		};
	}

	fileOverBase(e: any): void {
		this.hasBaseDropZoneOver = e;
	}

	fileOverAnother(e: any): void {
		this.hasAnotherDropZoneOver = e;
	}

}
