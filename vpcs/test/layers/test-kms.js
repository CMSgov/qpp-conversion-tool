var fs = require('fs');
var assert = require('assert');

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

        it('should match expected template', function() {
            var options = {enabled: true, description: 'meep'};
            var result = kmsTemplate('Bob', roles, options);

            assert.deepEqual(result, kmsTemplateFixture);
        });

        it('should be disabled by default', function () {
            var options = {description: 'meep'};
            var result = kmsTemplate('Bob', roles, options);

            assert.equal(result.Resources.Bob.Properties.Enabled, false);
        });

        it('should support creation of enabled key', function () {
            var options = {enabled: true};
            var result = kmsTemplate('Bob', roles, options);

            assert(result.Resources.Bob.Properties.Enabled);
        });

        it('should default an empty description', function () {
            var options = {enabled: true};
            var result = kmsTemplate('Bob', roles, options);

            assert.equal(result.Resources.Bob.Properties.Description, '');
        });

    });

});