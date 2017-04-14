'use strict';

// dependencies
var async = require('async');
var AWS = require('aws-sdk');
var util = require('util');

// get reference to S3 client
var s3 = new AWS.S3();

module.exports.move_file = function(event, context, callback) {
    // Read options from the event.
    console.log("Reading options from event:\n", util.inspect(event, { depth: 5 }));
    var srcBucket = event.Records[0].s3.bucket.name;
    // Object key may have spaces or unicode non-ASCII characters.
    var srcKey = decodeURIComponent(event.Records[0].s3.object.key.replace(/\+/g, " "));
    var dstBucket = srcBucket + "-destination";
    var dstKey = "transferred-" + srcKey;

    // Sanity check: validate that source and destination are different buckets.
    if (srcBucket == dstBucket) {
        callback("Source and destination buckets are the same.");
        return;
    }

    // Download the image from S3, transform, and upload to a different S3 bucket.
    async.waterfall([
        function download(next) {
            // Download the image from S3 into a buffer.
            s3.getObject({
                    Bucket: srcBucket,
                    Key: srcKey
                },
                next);
        },
        function upload(response, next) {
            // Stream the transformed image to a different S3 bucket.
            s3.putObject({
                    Bucket: dstBucket,
                    Key: dstKey,
                    Body: response.Body,
                    ContentType: response.ContentType
                },
                next);
        }
    ], function(err) {
        if (err) {
            console.error(
                ' Unable to upload to ' + dstBucket + '/' + dstKey +
                ' due to an error: ' + err
            );
        } else {
            console.log(
                'Successfully moved' + srcBucket + '/' + srcKey +
                ' to ' + dstBucket + '/' + dstKey
            );
        }

        callback(null, "message");
    });
};