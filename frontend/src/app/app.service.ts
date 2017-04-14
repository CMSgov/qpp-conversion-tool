import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';

@Injectable()
export class AppService {

	private endpoint = 'https://3u24bs28df.execute-api.us-east-1.amazonaws.com/dev/ping';

	constructor (private http: Http) {}

	getEndpoint() {
		return this.http.get(this.endpoint)
			.map(this.extractData)
			.catch(this.handleError);
	}

	private extractData(res: Response) {
		const body = res.json();
		return body.message || { };
	}

	private handleError (error: Response | any) {
		let errMsg: string;
		if (error instanceof Response) {
			const body = error.json() || '';
			const err = body.error || JSON.stringify(body);
			errMsg = `${error.status} - ${error.statusText || ''} ${err}`;
		} else {
			errMsg = error.message ? error.message : error.toString();
		}
		console.error(errMsg);
		return Observable.throw(errMsg);
	}
}
