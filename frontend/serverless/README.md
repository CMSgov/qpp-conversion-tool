Create a file in the serverless directory called 'lambda_config.js'

Paste the following JavaScript into lamba_config.js and replace placeholder values with your own.

```
module.exports.fetchAccessKey = () => {
   return <YOUR AWS ACCESS KEY>;
}
module.exports.fetchSecretKey = () => {
   return <YOUR AWS SECRET KEY>;
}
module.exports.fetchS3Bucket= () => {
   return <YOUR S3 BUCKET>;
}
```
