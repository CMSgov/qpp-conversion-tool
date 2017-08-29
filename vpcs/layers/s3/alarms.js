

module.exports = function(bucketName) {
    var setup = {
        "Resources": {}
    }

    setup.Resources[bucketName + "4xxErrors"] = {
        "Type": "AWS::CloudWatch::Alarm",
        "Properties": {
            "EvaluationPeriods": "2",
            "Dimensions": [
                {
                    "Name": "BucketName",
                    "Value": {
                        "Ref": bucketName
                    }
                }
            ],
            "AlarmActions": [
                { "Ref": "AlarmNotificationTopic" }
            ],
            "AlarmDescription": bucketName + " Too many 4xx errors.",
            "Namespace": "AWS/S3",
            "Period": "300",
            "ComparisonOperator": "GreaterThanOrEqualToThreshold",
            "AlarmName": bucketName + "4xxAlarm",
            "Statistic": "Average",
            "Threshold": "10",
            "Unit": "Count",
            "MetricName": "4xxErrors"
        }
    };

    setup.Resources[bucketName + "5xxErrors"] = {
        "Type": "AWS::CloudWatch::Alarm",
        "Properties": {
            "EvaluationPeriods": "2",
            "Dimensions": [
                {
                    "Name": "BucketName",
                    "Value": {
                        "Ref": bucketName
                    }
                }
            ],
            "AlarmActions": [
                { "Ref": "AlarmNotificationTopic" }
            ],
            "AlarmDescription": bucketName + " Too many 5xx errors.",
            "Namespace": "AWS/S3",
            "Period": "300",
            "ComparisonOperator": "GreaterThanOrEqualToThreshold",
            "AlarmName": bucketName + "5xxAlarm",
            "Statistic": "Average",
            "Threshold": "5",
            "Unit": "Count",
            "MetricName": "5xxErrors"
        }
    };

    return setup;
};