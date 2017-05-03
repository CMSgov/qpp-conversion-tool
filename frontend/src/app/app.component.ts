import { Component, OnInit } from '@angular/core';
import { AppService } from './app.service';

@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
	title = 'Convert QRDA-III to QPP';
	policy = { 'bucket_url': ''};
	file_name: string;
	success_data = {};

	constructor (private appService: AppService) {}

	onSubmit(policyForm: any) {
		this.appService.postPolicy(this.policy.bucket_url, policyForm)
			.subscribe(
				data => this.success_data = data,
				error => console.log('Error: ', error)
			);
	};

	getPolicy() {
		this.appService.getPolicy()
			.subscribe(
				data => this.policy = data,
				error => console.log('Error: ', error)
			);
		this.generateFileName();
	};

	generateFileName() {
		let text = '';
		const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

		for ( let i = 0; i < 20; i++ ) {
			text += possible.charAt(Math.floor(Math.random() * possible.length));
		}
		this.file_name = text + '.xml';
	};

	ngOnInit(): void {
		this.getPolicy();
	};
}
