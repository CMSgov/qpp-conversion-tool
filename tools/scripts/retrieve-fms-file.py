#!/usr/bin/env python3

import argparse
import sys

import requests


def get_user_inputs():
    parser = argparse.ArgumentParser()
    parser.add_argument('-au', '--auth-url', required=True, type=str,
                        help='QPP Auth token retrieval url. Example: https://imp.qpp.cms.gov/api/auth/oauth/token')
    parser.add_argument('-fu', '--fms-url', required=True, type=str,
                        help='FMS Base url. Example: https://impl.ar.qpp.internal/dataservices')
    parser.add_argument('-t', '--fms-token', required=True, type=str,
                        help='QPP Auth client assertion token to retrieve the FMS S2S token')
    parser.add_argument('-p', '--fms-path', required=True, type=str,
                        help='FMS path with file name and extension. Example: /folder/file.xlsx')
    args = parser.parse_args()

    return args


def download_from_fms(auth_url, fms_url, fms_token, fms_path):
    try:
        d = {'client_assertion': fms_token,
             'client_assertion_type': 'urn:ietf:params:oauth:client-assertion-type:jwt-bearer',
             'grant_type': 'client_credentials',
             'scope': 'analyticsAndReporting'
             }
        # print('starting s2s token retrieval request from qpp auth')
        get_s2s_token = requests.post(
            url=auth_url,
            data=d,
            headers={
                'Content-Type': 'application/x-www-form-urlencoded',
                'Accept': 'application/vnd.qpp.cms.gov.v2+json'
            }
        )
        s2s_token = get_s2s_token.json()["data"]["token"]
        # print('starting download from fms for file - ' + fms_path)
        get_download_url = requests.post(
            url=fms_url + '/get-file',
            json={"path": fms_path},
            verify=False,
            headers={
                'Accept': 'application/vnd.qpp.cms.gov.v2+json',
                'Authorization': 'Bearer ' + s2s_token
            }
        )
        download_url = get_download_url.json()['presigned_url']

        # upload_status = s3_client.put_object(
        #     Bucket=S3_BUCKET,
        #     Key=s3_path.split(PII_BUCKET_PATH)[1],
        #     Body=download_result.content,
        #     ServerSideEncryption='aws:kms'
        # )
        print(download_url)
        return download_url

    except Exception as err:
        print(f"Unexpected Error. {err = }, {type(err) = }")
        sys.exit(1)


def main():
    args = get_user_inputs()
    s3_url = download_from_fms(args.auth_url, args.fms_url, args.fms_token, args.fms_path)
    return s3_url


if __name__ == '__main__':
    url = main()
