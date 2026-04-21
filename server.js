require("dotenv").config();
const express = require("express");
const path = require("path");
const bodyParser = require("body-parser");
const mongoose = require("mongoose");

const app = express();
const PORT = 3000;

const multer = require("multer");
const { GoogleGenerativeAI } = require("@google/generative-ai");

// use memory storage (no file saving)
const upload = multer({ storage: multer.memoryStorage() });

const genAI = new GoogleGenerativeAI(process.env.GEMINI_API_KEY);

// MongoDB connection
mongoose.connect(process.env.MONGO_URI)
  .then(() => console.log("MongoDB Connected"))
  .catch(err => console.log(err));

// User schema
const userSchema = new mongoose.Schema({
  username: String,
  password: String
});

const User = mongoose.model("User", userSchema);

// Middleware
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({ extended: true }));
app.use(express.static(path.join(__dirname, "public")));

app.set("view engine", "ejs");
app.set("views", path.join(__dirname, "views"));

// Routes
app.get("/", (req, res) => res.render("login"));
app.get("/login", (req, res) => res.render("login"));
app.get("/signup", (req, res) => res.render("signup"));
app.get("/index", (req, res) => res.render("index"));

app.get("/form", (req, res) => {
  res.render("form", { query: req.query });
});

app.get("/scan", (req, res) => res.render("scan"));
app.get("/subscription", (req, res) => {
  res.render("subscription");
});
app.get("/label-scan", (req, res) => res.render("form"));
app.get("/manual", (req, res) => res.render("form"));

// Auth
app.post("/signup", async (req, res) => {
  const { username, password } = req.body;
  try {
    const newUser = new User({ username, password });
    await newUser.save();
    res.redirect("/login");
  } catch (err) {
    res.send("Error saving user");
  }
});

/* =========================
   🔥 MERGED LOGIN (TEAM + YOUR FEATURE)
   - Team redirect kept
   - Your free scan logic added
========================= */
app.post("/login", async (req, res) => {
  const { username, password } = req.body;
  try {
    const user = await User.findOne({ username, password });

    if (user) {
      // ✅ YOUR LOGIC ADDED (localStorage injection)
      res.send(`
        <script>
          const user = {
            username: "${user.username}",
            freeScansLeft: 5
          };

          localStorage.setItem("user", JSON.stringify(user));

          // also support your scansLeft logic
          if (!localStorage.getItem("scansLeft")) {
            localStorage.setItem("scansLeft", 5);
          }

          window.location.href = "/index";
        </script>
      `);
    } else {
      res.send("Invalid credentials");
    }
  } catch (err) {
    res.send("Error during login");
  }
});

/* =========================
   UNLABELED SCAN
========================= */
app.post("/unlabeled-scan", upload.single("image"), async (req, res) => {
  try {
    const file = req.file;

    const model = genAI.getGenerativeModel({
      model: "gemini-2.5-flash"
    });

    const imageBuffer = file.buffer;

    const prompt = `
Analyze this food image and estimate its nutrition.

Return ONLY valid JSON in this exact format:
{
  "food_name": "",
  "Calories": number,
  "Protein": number,
  "Carbohydrates": number,
  "Fat": number,
  "Fiber": number,
  "Sugars": number,
  "Sodium": number,
  "Cholesterol": number
}
`;

    const result = await model.generateContent([
      {
        inlineData: {
          data: imageBuffer.toString("base64"),
          mimeType: file.mimetype,
        },
      },
      prompt
    ]);

    let responseText = result.response.text().trim();
    console.log("Gemini raw:", responseText);

    let data;

    try {
      const clean = responseText.replace(/```json|```/g, "").trim();
      data = JSON.parse(clean);
    } catch (err) {
      console.error("JSON parse error:", err);

      data = {
        food_name: "Unknown",
        Calories: 200,
        Protein: 10,
        Carbohydrates: 20,
        Fat: 10,
        Fiber: 3,
        Sugars: 5,
        Sodium: 200,
        Cholesterol: 20
      };
    }

    // Safety check
    if (
      data.Calories < 0 || data.Calories > 2000 ||
      data.Protein < 0 || data.Fat < 0 ||
      data.Carbohydrates < 0 || data.Fiber < 0
    ) {
      data = {
        food_name: data.food_name || "Unknown",
        Calories: 200,
        Protein: 10,
        Carbohydrates: 20,
        Fat: 10,
        Fiber: 3,
        Sugars: 5,
        Sodium: 200,
        Cholesterol: 20
      };
    }

    console.log("Response:", data);

    res.json({
      foodName: data.food_name,
      macros: data
    });

  } catch (err) {
    console.error(err);
    res.status(500).send("Unlabeled scan failed");
  }
});

/* =========================
   YOUR EXTRA API (KEPT)
========================= */
app.post('/api/check-health', (req, res) => {
  const { calories, fat, diabeties, bgp, obesity } = req.body;

  const isHealthy = calories < 100 && fat < 5 && diabeties === 'no';

  res.json({ result: isHealthy ? 'Healthy' : 'Not Healthy' });
});

// Start server
app.listen(PORT, () => {
  console.log(`Server running at http://localhost:${PORT}`);
});