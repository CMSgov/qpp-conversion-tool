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

  if(configuration.tableName === null) {
    throw new Error('tableName must be a non-null String');
  }
  if(configuration.attributes === null) {
    throw new Error('attributes must be a non-null array of AttributeDefinitions');
  } else if (!Array.isArray(configuration.attributes)) {
    throw new Error('attributes must be a non-null array of AttributeDefinitions');
  }
  if(configuration.partitionKey === null) {
    throw new Error('partitionKey must be a non-null String');
  }
  if(configuration.readThroughput === null) {
    throw new Error('readThroughput must be a non-null integer');
  } else if(configuration.readThroughput % 1 !== 0 || typeof configuration.readThroughput !== 'number') {
    throw new Error('readThroughput must be a non-null integer');
  }
  if(configuration.writeThroughput === null) {
    throw new Error('writeThroughput must be a non-null integer');
  } else if(configuration.writeThroughput % 1 !== 0 || typeof configuration.writeThroughput !== 'number') {
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
