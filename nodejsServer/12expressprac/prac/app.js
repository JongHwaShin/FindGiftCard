var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var db_config = require(__dirname + '/config/database.js');
var conn = db_config.init();
var schedule = require('node-schedule');
var phantom = require('phantom');

var indexRouter = require('./routes/index');
var usersRouter = require('./routes/users');
var locRouter = require('./routes/loc');
var latRouter = require('./routes/lat');
var lngRouter = require('./routes/lng');

var app = express();

// view engine setup
app.set('views', path.join(__dirname, 'views'));
app.set('view engine', 'jade');

app.use(logger('dev'));
app.use(express.json());
app.use(express.urlencoded({ extended: false }));
app.use(cookieParser());
app.use(express.static(path.join(__dirname, 'public')));

app.use('/', indexRouter);
app.use('/users', usersRouter);
app.use('/loc', locRouter);
app.use('/lat', latRouter);
app.use('/lng', lngRouter);

// catch 404 and forward to error handler
app.use(function(req, res, next) {
  next(createError(404));
});

// error handler
app.use(function(err, req, res, next) {
  // set locals, only providing error in development
  res.locals.message = err.message;
  res.locals.error = req.app.get('env') === 'development' ? err : {};

  // render the error page
  res.status(err.status || 500);
  res.render('error');
});

let maintain_connect = schedule.scheduleJob('00 00 * * * *', () => {
  let sql = `SELECT CURDATE() FROM DUAL;`;
  conn.query(sql, (err, rows) => {
    if(err){
      console.log('connection err ! ' + err);
    }else {
      console.log(rows);
    }

    setTimeout(()=> {
      console.log("mysql conn end.");
      conn.end();
    }, 10000);
  });
});

let pantom_parse = schedule.scheduleJob('00 16 15 * * *', () => {

  phantom
  .create()
  .then(ph => {
    _ph = ph;
    return _ph.createPage();
  })
  .then(page => {
    _page = page;
    return _page.open('http://bigdata.changwon.go.kr/nuvision/invntStts.do');
  })
  .then(status => {
    console.log(`status : ${status}`);
    return _page.evaluate(function(){
      var tbody = document.getElementById('info');
      var trs = tbody.getElementsByTagName('tr');
      var returnText = "UPDATE bankinfo SET \n";
      var p5000 = " p5000 = CASE idx \n";
      var p10000 = " p10000 = CASE idx \n"
      for(var i = 0; i < trs.length; i++){
        var tds = trs[i].getElementsByTagName('td');
        p5000 += " WHEN " + (i + 1) + " THEN '" + tds[4].textContent.replace('￦ ', '') + "'\n";
        p10000 += " WHEN " + (i + 1) + " THEN '" + tds[5].textContent.replace('￦ ', '') + "'\n";
        //returnText += "오천 : " + tds[4].textContent + " 만원 : " + tds[5].textContent + "\n";
      }
      p5000 += " ELSE p5000 END \n";
      p10000 += " ELSE p10000 END \n";
      returnText += p5000 + " , " + p10000 + " , updatetime = SYSDATE() \n WHERE idx > 0;";

      return returnText;
    })
  })
  .then(content => {
    console.log(content);
    datas = content;

    conn.query(content, (err, rows) => {
      if(err) {
        console.log("mysql err : " + err);
      }else {
        console.log('update');
      }
    });

    // _page.close(); 
    // _ph.exit();
  })
  .then(() => {
    console.log("fin");
  })
  .catch(e => console.log(`e : ${e}`));


});

module.exports = app;
