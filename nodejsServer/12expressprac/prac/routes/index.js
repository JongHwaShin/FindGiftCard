var express = require('express');
var router = express.Router();
var db_config = require(__dirname + '/../config/database.js');
var conn = db_config.init();



/* GET home page. */
router.get('/', function(req, res, next) {
  console.log(req.baseUrl);
  console.log(req.query.q);

  let SQL = req.query.q;
  let values = "";

  if(SQL === null || SQL === ""){
    res.render('index', { title: "" });
  }else {
    conn.query(SQL, (err, rows) => {
      if(err) {
        console.log(err);
      }else {
        
        let column = Object.keys(rows[0]);
  
        let ValueList = new Array();
        for(let i = 0; i < column.length; i++){
          ValueList[i] = [column[i]];
        }
  
        for(let i = 0; i < rows.length; i++) {
           for(let j = 0; j < column.length; j++){
            ValueList[j].push(rows[i][column[j]]);
           }
        }
        for(let i = 0; i < ValueList.length; i++) {
          for(let j = 0; j < ValueList[i].length; j++){
            values = values + ValueList[i][j] + "\t";
          }
          values = values + "\n";
       }
      }
      res.render('index', { title: values });
    });
  }
});

module.exports = router;
