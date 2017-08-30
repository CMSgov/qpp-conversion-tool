var _ = require('underscore');

module.exports = function(configuration) {
  /*
  Example configuration object:
  var tableArguments = {
	'tableName': 'aTableName',
	'attributes': [
	  {'AttributeName': 'firstKey', 'AttributeType': 'S'},
	  {'AttributeName': 'anotherKey', 'AttributeType': 'S'},
	],
	'partitionKey': 'firstKey',
	'sortKey': 'anotherKey',
	'readThroughput': 4,
	'writeThroughput': 2,
	'keepOnStackDelete': true
  };
  */

  if(!_.isString(configuration.tableName)) {
    throw new Error('tableName must be a non-null String');
  }
  if(!Array.isArray(configuration.attributes)) {
    throw new Error('attributes must be a non-null array of AttributeDefinitions');
  }
  if(!_.isString(configuration.partitionKey)) {
    throw new Error('partitionKey must be a non-null String');
  }
  if(!Number.isInteger(configuration.readThroughput)) {
    throw new Error('readThroughput must be a non-null integer');
  }
  if(!Number.isInteger(configuration.writeThroughput)) {
    throw new Error('writeThroughput must be a non-null integer');
  }

  var cloudformation = { 'Resources': {} };

  cloudformation.Resources[configuration.tableName] = {};
  var table = cloudformation.Resources[configuration.tableName];

  table.Type = 'AWS::DynamoDB::Table';
  if(configuration.keepOnStackDelete) {
    table.DeletionPolicy = 'Retain';
  }
  table.Properties = {};
  var tableProperties = table.Properties;

  tableProperties.TableName = configuration.tableName;
  tableProperties.AttributeDefinitions = configuration.attributes;

  tableProperties.ProvisionedThroughput = {
    'ReadCapacityUnits': configuration.readThroughput,
    'WriteCapacityUnits': configuration.writeThroughput
  };

  tableProperties.KeySchema = [ {'AttributeName': configuration.partitionKey, 'KeyType': 'HASH'} ];
  if(configuration.sortKey !== null) {
    tableProperties.KeySchema[1] = {'AttributeName': configuration.sortKey, 'KeyType': 'RANGE'}
  }

  return cloudformation;
}
