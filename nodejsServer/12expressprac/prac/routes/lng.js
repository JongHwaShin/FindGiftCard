var express = require('express');
var router = express.Router();

let lng_datas = " lng = CASE idx \n";

/* GET users listing. */
router.get('/', function(req, res, next) {
  
  if(req.query.lng){
    lng_datas += req.query.lng;
  }
  if(req.query.clear){
    lng_datas = "";
  }
  
  res.render('lng', {lng_values : lng_datas});
});

module.exports = router;
