const express = require("express")
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


app.use(express.json()); 
app.use(express.urlencoded({extended: true})); 

app.set('views', './views')
app.set('view engine', 'ejs');

app.use((req, res, next) => {
    console.log(new Date().toLocaleString() + " " + req.url);
    next();
});



app.use(express.static(path.join(__dirname, "views")));

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


