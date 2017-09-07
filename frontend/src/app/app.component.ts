import { Component, OnInit } from '@angular/core';
import { FileSelectDirective, FileDropDirective, FileUploader } from '../../node_modules/ng2-file-upload/ng2-file-upload';
import 'rxjs/Rx' ;
import { DomSanitizer } from '@angular/platform-browser';


const URL  = 'http://184.73.24.93:2680/submissions/converter';

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent {
	title = 'Convert QRDA-III to QPP';
	uploader: FileUploader = new FileUploader({url: URL});
	hasBaseDropZoneOver: boolean;
	hasAnotherDropZoneOver: boolean;
	response: string;
	downloadJsonHref: any;
	fileName: string;
	status: number;
	error: boolean;

	constructor (private sanitizer: DomSanitizer) {
		this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
			console.log('Endpoint Response:', response);
			this.status = status;
			this.generateDownloadJsonUri(response);
			this.response = JSON.parse(response);
			if (item.isError) {
				this.error = true;
			} else {
				this.error = false;
				this.fileName = item.file.name.replace('.xml', '') + '.json';
			}
		};
	}

	fileOverBase(e: any): void {
		this.hasBaseDropZoneOver = e;
	}

	fileOverAnother(e: any): void {
		this.hasAnotherDropZoneOver = e;
	}

	generateDownloadJsonUri(response) {
		const theJSON = response;
		const uri = this.sanitizer.bypassSecurityTrustUrl('data:text/json;charset=UTF-8,' + encodeURIComponent(theJSON));
		this.downloadJsonHref = uri;
	}
}
