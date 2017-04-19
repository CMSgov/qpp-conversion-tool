'use strict';

module.exports.endpoint = (event, context, callback) => {
    const response = {
        statusCode: 200,
        headers: {
            "Access-Control-Allow-Origin": "*"
        },
        body: JSON.stringify({
            message: 'Communication Received',
        }),
    };

    callback(null, response);
};
