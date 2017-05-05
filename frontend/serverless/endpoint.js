'use strict';

function dateString() {
    var date = new Date().toISOString();
    return date.substring(0, 4) + date.substring(5, 7) + date.substring(8, 10);
}

function amzCredential(accessKey, region) {
    return [accessKey, dateString(), region, 's3/aws4_request'].join('/')
}

function hmac(key, string) {
    var hmac = require('crypto').createHmac('sha256', key);
    hmac.end(string);
    return hmac.read();
}

// Signs the policy with the credential
function s3UploadSignature(secret, region, policyBase64, credential) {
    var dateKey = hmac('AWS4' + secret, dateString());
    var dateRegionKey = hmac(dateKey, region);
    var dateRegionServiceKey = hmac(dateRegionKey, 's3');
    var signingKey = hmac(dateRegionServiceKey, 'aws4_request');
    return hmac(signingKey, policyBase64).toString('hex');
}

module.exports.endpoint = (event, context, callback) => {
  const crypto = require('crypto');
  const fs = require('fs');

  const awsAccessKeyId = process.env.ACCESS;
  const awsSecretAccessKey = process.env.SECRET;
  const bucketName = process.env.BUCKET;

  const msPerDay = 24 * 60 * 60 * 1000;
  const expiration = new Date(Date.now() + msPerDay).toISOString();
  const bucketUrl = `https://${bucketName}.s3.amazonaws.com`;
  const credential = amzCredential(awsAccessKeyId, 'us-east-1')

  const policy = {
    expiration,
    conditions: [
      // ['starts-with', '$key', 'uploads/'],
      ['starts-with', '$key', ''],
      { bucket: bucketName },
      { acl: 'public-read' },
      ['starts-with', '$Content-Type', ''],
      { success_action_status: '201' },
      { "x-amz-algorithm": "AWS4-HMAC-SHA256" },
      { "x-amz-credential": credential },
      { "x-amz-date": dateString() + "T000000Z" }
    ],
  };

  const policyB64 = new Buffer(JSON.stringify(policy)).toString('base64');

  const response = {
    statusCode: 200,
    headers: {
        "Access-Control-Allow-Origin": "*"
    },
    body: JSON.stringify({
      bucket_url: bucketUrl,
      acl: 'public-read',
      success_action_status: '201',
      policy: policyB64,
      x_amz_algorithm: 'AWS4-HMAC-SHA256',
      x_amz_credential: credential,
      x_amz_date: dateString() + 'T000000Z',
      x_amz_signature: s3UploadSignature(awsSecretAccessKey, 'us-east-1', policyB64, credential)
    })
  };

  callback(null, response);
};
