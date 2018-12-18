require('dotenv').config();

const 
  bodyParser = require('body-parser'),
  express = require('express'),
  bcrypt = require('./lib/bCrypt.js'),
  db = require('./db/db.js');
  

var app = express();

/** bodyParser.urlencoded(options)
 * Parses the text as URL encoded data (which is how browsers tend to send form data from regular forms set to POST)
 * and exposes the resulting object (containing the keys and values) on req.body
 */
app.use(bodyParser.urlencoded({
    extended: true
}));

/**bodyParser.json(options)
 * Parses the text as JSON and exposes the resulting object on req.body.
 */
app.use(bodyParser.json());



app.get('/', function (req, res) {
  res.send('Hello World!');
});


app.listen(process.env.PORT || 5000);
console.log('Server running in port ' + (process.env.PORT || 5000));


///////////////////////////////////
/////////    CUSTOMERS    /////////
///////////////////////////////////
//customer registration
// receives: 

app.post('/register', function(req, res) {
	//TODO
	if(!req.body.user){
		res.status(404).send('No user info received!');
		return;
	}

	var user = req.body.user;
	console.log(req.body);
	if(!user.name || !user.roll || !user.contact || !user.password || !user.gender || !user.branch || !user.year)
		res.status(404).send("Missing parameters!");
	else{
		db.insertUser(user, function(result){
			if(result == null){
				res.send({"error" : "Invalid parameters, or already existing email address!"});
			}
			else{
				console.log(result);
				res.send(result);
			}
		});
		}
	});

//customer login
// receives
// { "user" : { email: "xxx", pin : xxxx}}
app.post('/login', function(req, res) {
	if(!req.body.user){
		res.status(404).send('No user info received!');
		return;
	}

	var user = req.body.user;
	console.log(user);
	if(!user.roll || !user.pass)
		res.status(404).send("Missing parameters!");
	else{
		db.checkLoginByRoll(user, function(result){
			if(result == null){
				res.send({"error" : "Invalid Roll or password!"});
			}
			else{
				console.log(result);
				res.send(result);
			}
		});
	}
});


app.post('/feedback', function(req, res) {
	if(!req.body.user){
		res.status(404).send('No user info received!');
		return;
	}

	var user = req.body.user;
	console.log(user);
	if(!user.type || !user.fb)
		res.status(404).send("Missing parameters!");
	else{
		res.send({"response" : "Success"});
	}
});

app.post('/record', function(req, res) {
	if(!req.body.order){
		res.status(404).send('No user info received');
		return;
	}

	var user = req.body.order;
	console.log(user);
	if(!user.roll || !user.purp)
		res.status(404).send("Missing parameters!");
	else{
		db.insertRecord(user,function(result){
			if(result == null){
				res.send({"error" : "Error recording"});
			}
			else{
				console.log(result);
				res.send({"response" : result});
			}
			
		});
		
	}
});


app.post('/event', function(req, res){
	if(!req.body.user){
		res.status(404).send('No user info received!');
		return;
	}

	var user = req.body.user;
	console.log(user);
	if(!user.roll)
		res.status(404).send("Missing parameters!");
	else
	{
	db.getEvent(function(result){
		if(result == null){
				res.send({"error" : "Invalid Roll or password!"});
			}
			else{
				console.log(result);
				res.send({"event" : result});
			}
	});
	}
	
});


