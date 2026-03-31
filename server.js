const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');

const app = express();
const PORT = 3000;

// Set EJS as the view engine
app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

// Middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public'))); // Serves static files like index.html

// Serve form.ejs on /form
app.get('/form', (req, res) => {
  res.render('form'); // no need to write .ejs
});

app.get('/scan', (req, res) => {
  res.render('scan');
});

app.get('/label-scan', (req, res) => {
  res.render('form');
});

app.get('/manual', (req, res) => {
  res.render('form');
});


// Handle form POST request (from form.ejs)
app.post('/api/check-health', (req, res) => {
  const { calories, fat, diabeties, bgp, obesity } = req.body;

  const isHealthy = calories < 100 && fat < 5 && diabeties === 'no';
  res.json({ result: isHealthy ? 'Healthy' : 'Not Healthy' });
});
app.get('/', (req, res) => {
  res.render('index'); // renders views/index.ejs
});


app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});
