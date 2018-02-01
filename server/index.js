var express = require('express');
var app = express();
app.get('/notifications', function(req, res) {
  console.log(req.body);
});
app.listen(3001);
console.log('Listening on port 3001...');
