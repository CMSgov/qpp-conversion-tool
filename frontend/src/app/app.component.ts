import { Component, OnInit } from '@angular/core';
import { FileSelectDirective, FileDropDirective, FileUploader } from '../../node_modules/ng2-file-upload/ng2-file-upload';
import 'rxjs/Rx' ;

const URL  = 'https://qpp.cms.gov/api/submissions/converter';

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
	responseJson: string;
	fileName: string;
	status: number;
	error: boolean;

	constructor () {
		this.uploader.onCompleteItem = (item: any, response: any, status: any, headers: any) => {
			console.log('Endpoint Response:', response);
			this.status = status;
			this.responseJson = response;
			if (item.isCancel) {
				return;
			}
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

	saveBlob(response, contentType, filename) {
		const blob = new Blob([response], { type: contentType });
		if (typeof window.navigator.msSaveBlob !== 'undefined') {
			// IE workaround
			window.navigator.msSaveBlob(blob, filename);
		} else {
			const downloadUrl = window.URL.createObjectURL(blob);
			if (filename) {
				const a = document.createElement('a');
				if (typeof a.download === 'undefined') {
					window.location.href = downloadUrl;
				} else {
					a.href = downloadUrl;
					a.download = filename;
					document.body.appendChild(a);
					a.click();
				}
			} else {
				window.location.href = downloadUrl;
			}
			// cleanup
			setTimeout(function () { window.URL.revokeObjectURL(downloadUrl); }, 100);
		}
	}
}
