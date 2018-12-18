require('dotenv').config();

const pg = require('pg');
const connectionString = process.env.DATABASE_URL || 'postgres://postgres:root@localhost:5432/postgres';
const
	bcrypt = require('../lib/bCrypt.js');


function openClient(){
	const client = new pg.Client({
	    user: process.env.DATABASE_USER,
	    database: process.env.DATABASE_SERVER,
	    port: process.env.DATABASE_PORT,
	    host: process.env.DATABASE_HOST,
	    ssl: false
	});
	return client;
}

function rollback(err, client) {
	console.log(err);
	//terminating a client connection will
	//automatically rollback any uncommitted transactions
	//so while it's not technically mandatory to call
	//ROLLBACK it is cleaner and more correct
	client.query('ROLLBACK', function() {
		client.end();
	});
};

/**
* Register
*/
function insertUser(user, callback){
	console.log("----------------------debug-------------------------")

	var client = new pg.Client(connectionString);
	client.connect();

	var query_1 = 'INSERT INTO student (name, roll_no, gender, branch, year, contact_number) VALUES ($1, $2, $3, $4, $5, $6) RETURNING *';
	var query_1_params = [user.name,user.roll,user.gender,user.branch,user.year,user.contact];
  	var query_2 = 'INSERT INTO login(roll_no, hashed_password) VALUES($1, $2) RETURNING *';
  	var query_2_params = [user.roll, user.password];
 

	client.query('BEGIN', function(err, result) {
		console.log(result.rows[0]);
		if(err){
			callback(null);
			return rollback(err, client);
		}
		client.query(query_1, query_1_params, function(err, result_user) {
			console.log(result_user);

			if(err){
				callback(null);
				return rollback(err, client);
			}

			var user_result = result_user.rows[0];
			console.log(user_result);
			console.log(query_2_params);

			client.query(query_2, query_2_params, function(err, result_login) {
				console.log(result_login);
				if(err){
					callback(null);
					return rollback(err, client);
				}
				user_result.pass = result_login.rows[0].hashed_password;

				console.log("----------------------final---------------")
				

	
					//disconnect after successful commit
					client.query('COMMIT', client.end.bind(client));
					callback(user_result);
				

			});
		});
	});
}

function checkLoginByRoll(user, callback){

	var client = new pg.Client(connectionString);
	client.connect();
	
	const query = client.query('SELECT * FROM login WHERE roll_no = $1', [user.roll], function(error, result){
		if(error){
			callback(null);
			return;
		}
		console.log(result);
		if(result.rowCount > 0) {
			client.query('SELECT * FROM student WHERE roll_no = $1',
				[result.rows[0].roll_no],
				function (error1, result1) {
					if(error1) {
						callback(null);
						return;
					}
					var user_result = result1.rows[0];
					user_result.pass = result.rows[0].hashed_password;
					console.log(user_result);
					callback(user_result);
				});
		} else { // wrong password/id
			callback(null);
		}
	});
}

function getEvent(callback){
	var client = new pg.Client(connectionString);
	client.connect();
	const results = [];
	const query = client.query('select * from event where ondate >= now();',function(error, result){
		if(error){
			callback(null);
			return;
		}
		console.log(result.rows);
		var user_result = result.rows;
		callback(user_result);
	});
}

function insertRecord(user,callback)
{
	var client = new pg.Client(connectionString);
	client.connect();
		console.log(user);
		const query = client.query("SELECT * FROM record WHERE roll_no = $1 and entry_time is null", [user.roll], function(error, result){
		if(error){
			callback(null);
			return rollback(error,client);
		}
		console.log(result);
		if(result.rowCount == 0) {
			client.query('INSERT INTO record(record_date, roll_no,exit_time, purpose) VALUES (CURRENT_DATE, $1, now(), $2) returning *',[user.roll,user.purp],function(err,resexit){
				console.log(resexit);
				if(err){
				return rollback(err,client);
			}
				var user_result = 'Exit';
				callback(user_result);
			});

		} else { 
		
		client.query("UPDATE record SET entry_time =  now() WHERE roll_no = $1 and entry_time is null",[user.roll],function(err,resenter){
				console.log(resenter+" no updation");
				if(err){
				return rollback(err,client);
			}
				var user_result = 'Entry';
				callback(user_result);
			});
			
		}
	});
}

////////////////////////////////

exports.insertUser = insertUser;
exports.checkLoginByRoll = checkLoginByRoll;
exports.getEvent = getEvent;
exports.insertRecord = insertRecord;

function toHexString(byteArray) {
  return byteArray.map(function(byte) {
    return ('0' + (byte & 0xFF).toString(16)).slice(-2);
  }).join('')
}