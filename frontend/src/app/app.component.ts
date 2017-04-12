import { Component } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import { AppService } from './app.service';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';


@Component({
	selector: 'app-root',
	templateUrl: './app.component.html',
	styleUrls: ['./app.component.scss']
})
export class AppComponent {
	title = 'app works!';
	message: string;
	errorMessage: string;


	constructor (private appService: AppService) {}

	pingGateway() {
		return this.appService.getEndpoint()
			.subscribe(
				message => this.message = <any>message,
				error => this.errorMessage = <any>error
			);
	}
}
