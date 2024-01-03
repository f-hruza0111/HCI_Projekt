const express = require("express")
const session = require("express-session")
const bodyParser = require('body-parser')
const { access } = require("fs")
const path = require("path")
const axios = require('axios').default
require('dotenv').config()


const restAPIURL = process.env.REST_API_URL
let access_token = null
let refresh_token = null



const externalUrl = process.env.RENDER_EXTERNAL_URL
const port = externalUrl && process.env.PORT ? parseInt(process.env.PORT) : 3000
const baseURL = externalUrl || `http://localhost:${port}`

const app = express();

app.use(session({secret: process.env.SESSION_SECRET}))



app.use(bodyParser.json())
app.use(bodyParser.urlencoded({
    extended: true
}))

app.use(express.json()); 
app.use(express.urlencoded({extended: true})); 

app.set('views', './views')
app.set('view engine', 'ejs');


app.use(express.static(path.join(__dirname, "views")));


//TODO: OVO JE SAM TU STAVLJENO OKVIRNO

app.get("/", function (req, res) {
    let posts = {}

    axios.get(restAPIURL + '/posts')
    .then(result => {
        console.log(result.data)
        posts = result.data
    })
    .catch(err => console.log(err))


    res.render('index', {posts: posts})
});

app.get("/registration",  function (req, res) {
    // console.log("Rendering Registraion Page")
    res.render('registration', {err:undefined})
})


//RADI
app.post("/registration",  async function (req, res) {
    var err = null
    if(req.body.password !== req.body.repeat_password){
        err = "Passwords dont match!"
    } else {
      await axios.post(restAPIURL + '/registration', {
            email: req.body.email,
            username: req.body.username,
            password: req.body.password
        })
        .then(response => {
            console.log("Location: " + response.headers.location)
            console.log("Data: " + response.data)
            if(response.status != 201){
                err = response.data
            } 
        })
        .catch(error => {
            console.log(error.response.data)
            err = error.response.data
        })
    }
    
    if(err){
        console.log(err)
        res.render('registration', {err: err} )
    } else {
        res.redirect('/')
    }
    
})

app.get("/login",  function (req, res) {
    res.render('login')
})

//posalji credentials
//spremi JWT tokene u session
app.post("/login",  function (req, res) {
    
})















if(externalUrl){

    const hostname = '0.0.0.0'
    app.listen(port, hostname, () => {
        console.log(`Server running locally on  http://${hostname}:${port} and externaly on ${externalUrl}`);
    })
} else {
    app.listen(port, () => {
        console.log(`Server running locally on  ${baseURL}`);
    })
}


