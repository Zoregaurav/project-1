const express = require('express');
const path = require('path');
const bodyParser = require('body-parser');
const mongoose = require('mongoose');

const app = express();
const PORT = 3000;

mongoose.connect(
  ''
)
  .then(() => console.log("MongoDB Connected"))
  .catch(err => console.log(err));


const userSchema = new mongoose.Schema({
  username: String,
  password: String
});

const User = mongoose.model('User', userSchema);


app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, 'public')));


app.set('view engine', 'ejs');
app.set('views', path.join(__dirname, 'views'));

app.get('/', (req, res) => {
  res.render('login');
});

app.get('/login', (req, res) => {
  res.render('login');
});

// Signup page
app.get('/signup', (req, res) => {
  res.render('signup');
});

// Index page (AFTER LOGIN SUCCESS)
app.get('/index', (req, res) => {
  res.render('index');
});

// Form page (optional)
app.get('/form', (req, res) => {
  res.render('form');
});


app.post('/signup', async (req, res) => {
  const { username, password } = req.body;

  try {
    const newUser = new User({ username, password });
    await newUser.save();

    res.redirect('/login');
  } catch (err) {
   
    alert("Error saving user");
  }
});


app.post('/login', async (req, res) => {
  const { username, password } = req.body;

  try {
    const user = await User.findOne({ username, password });

    if (user) {
      // ✅ FIXED: correct redirect
      res.redirect('/index');
    } else {
      res.send("Invalid credentials");
    }
  } catch (err) {
    console.log(err);
    res.send("Error during login");
  }
});

/* =========================
   API
========================= */
app.post('/api/check-health', (req, res) => {
  const { calories, fat, diabeties, bgp, obesity } = req.body;

  const isHealthy = calories < 100 && fat < 5 && diabeties === 'no';

  res.json({ result: isHealthy ? 'Healthy' : 'Not Healthy' });
});


app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});