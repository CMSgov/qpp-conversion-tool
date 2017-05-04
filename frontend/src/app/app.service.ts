import { Injectable } from '@angular/core';
import { Http, Response } from '@angular/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/observable/throw';
import 'rxjs/add/operator/catch';
import 'rxjs/add/operator/map';
import {Parser} from 'xml2js';

@Injectable()
export class AppService {

	public endpoint = 'https://gpeermmuj9.execute-api.us-east-1.amazonaws.com/dev/ping';

	constructor (private http: Http) {}

	getPolicy() {
		return this.http.get(this.endpoint)
			.map(this.extractData)
			.catch(this.handleError);
	}

	postPolicy(url, form) {
		return this.http.post(url, form);
	}

	private extractData(res: Response) {
		const body = res.json();
		console.log(body);
		return body || { };
	}

	private parseXml(res: Response) {
		const parser = new Parser();
		const parseString = parser.parseString(res);
		console.log(parseString);
		return parseString;
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
