var express = require('express');
var router = express.Router();

let lat_datas = " lat = CASE idx \n";

/* GET users listing. */
router.get('/', function(req, res, next) {
  
  if(req.query.lat){
    lat_datas += req.query.lat;
  }
  if(req.query.clear){
    lat_datas = "";
  }
  
  res.render('lat', {lat_values : lat_datas});
});

module.exports = router;
