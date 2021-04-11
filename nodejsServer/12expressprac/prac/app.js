var createError = require('http-errors');
var express = require('express');
var path = require('path');
var cookieParser = require('cookie-parser');
var logger = require('morgan');
var db_config = require(__dirname + '/config/database.js');
var conn = db_config.init();
var schedule = require('node-schedule');

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

module.exports = app;
