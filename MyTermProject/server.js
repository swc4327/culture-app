const express = require('express');
const mariadb = require('mariadb/callback');
const app = express();

app.listen('8080', () => {
    console.log('Server Started');
});

var dbc = mariadb.createConnection({
    host: "localhost",
    database: "mydb",
    user: "root",
    password: "zz3302"
});

dbc.connect((err) => {
    if (err) throw err;
    console.log('Database Connected');
});

app.get('/select', (req, res) => {
    var query = `SELECT * FROM music WHERE id=${req.query.id} UNION SELECT * FROM movie WHERE id=${req.query.id} UNION SELECT * FROM book WHERE id=${req.query.id}`;
    dbc.query(query, (err, result, fields) => {
        if (err) return;
        res.send(result);
    });
});

app.get('/', (req, res) => {
    var query = `SELECT * FROM music UNION SELECT * FROM movie UNION SELECT * FROM book`;
    dbc.query(query, (err, result, fields) => {
        if (err) return console.log(err);
        res.send(result);
    });
});

app.get('/image_query', (req, res) => {
    var query = `SELECT image FROM music WHERE id=${req.query.id}`;
    dbc.query(query, (err, result, fields) => {
        if (err) throw err;

        if (result[0]) {
            //console.log(__dirname + "images/" + result[0].image);
            res.sendFile(__dirname + "/images/" + result[0].image);
        }
        else
            res.send(404, "Not Found");
    });
});

app.use('/image', express.static('images'));

//---------------------------------------
app.use(express.urlencoded());
//app.use(express.json());

app.use('/add', express.static('insert.html'));

app.post('/insert', (req, res) => {
    //console.log(req.body.name);
    var query = `INSERT INTO gundam VALUES (NULL, "${req.body.name}", "${req.body.model}", "${req.body.image}")`;
    //console.log(query);
    dbc.query(query, (err, result, fields) => {
        if (err) return console.log(err);

        //res.send(result);
        var str = req.body.name;
        str += hasJongsung(req.body.name) ? "을" : "를";
        str += " 추가했습니다.";
        res.send(str)
    });
})

function hasJongsung(str) {
    //var jong = String.fromCharCode(str.charCodeAt(str.length - 1));
    //console.log(jong);
    var jong = str.charCodeAt(str.length - 1);
    return ((jong - 44032) % 28 == 0) ? false : true;
}