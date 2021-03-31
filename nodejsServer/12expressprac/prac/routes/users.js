var express = require('express');
var router = express.Router();

/* GET users listing. */
router.get('/', function(req, res, next) {
  console.log(req.baseUrl);
  console.log(req.query.q);
  res.send('respond with a resource');
});

module.exports = router;
