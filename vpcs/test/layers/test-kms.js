var fs = require('fs');
var assert = require('assert');

console.log('file name', __filename);
console.log('directory name', __dirname);

var directory = __dirname;
var kmsTemplate = require(directory + '/../../layers/kms/kms');

function getFixture() {
    return JSON.parse(fs.readFileSync(directory + '/../fixtures/kms-template.json'));
}

describe('KMS Layer', function() {
    var kmsTemplateFixture = getFixture();
    var roles = {
        supers: [
            'arn:aws:iam::123456789012:root'
        ],
        administrators: [
            'arn:aws:iam::123456789012:user/some-admin'
        ],
        users: [
            'arn:aws:iam::123456789012:role/role1',
            'arn:aws:iam::123456789012:user/auser'
        ]
    };

    describe('#template', function() {
        var result = kmsTemplate('Bob', 'meep', true, roles);

        it('matches expected template', function() {
            assert.deepEqual(result, kmsTemplateFixture);
        })
    });
});