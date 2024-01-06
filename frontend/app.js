const express = require("express")
const session = require("express-session")
const bodyParser = require('body-parser')
const { access } = require("fs")
const path = require("path")
const multer = require("multer");
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
//KAD JE USER ULOGIRAN definiran je req.session.userID, inace undefined
app.get("/", async function (req, res) {
    // let posts = {}
    // await axios.get(restAPIURL + '/posts')
    // .then(result => {
    //     console.log(result.data)
    //    posts = result.data
       

    // })
    // .catch(err => console.log(err))
    console.log(req.session.userID)
    res.render('index', {userID: req.session.userID ? req.session.userID : null})
});

app.get("/registration",  function (req, res) {
    // console.log("Rendering Registraion Page")
    res.render('registration', {err:undefined, userID: undefined})
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
           req.session.userID = response.headers.location
            if(response.status != 201){
                err = response.data
            } 
        })
        .catch(error => {
            console.log(error.response)
            err = error.response
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
    res.render('login', {err: null, userID: undefined})
})

//posalji credentials
// ak vrati ok napravi sesiju
app.post("/login",  async function (req, res) {
    var response
    var error = null
    try{
        response = await axios.post(restAPIURL + "/login",{
            username: req.body.username,
            password: req.body.password
        })

        console.log(response)
        if(response.status === 200){
            req.session.userID = response.data
        } else {
            error = "Invalid credentials!"
        }

    } catch(err){
        console.log(error)
        error = err
    }


   if(error){
        res.render('login', {err:error})
   } else {
        res.redirect('/')
   }
})

app.post('/logout', function(req, res) {
    req.session.destroy()
    res.redirect('/')
})

app.get("/post", authorize, function(req, res) {
    res.render('postForm', {err: undefined})
})


const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'images')
    },

    filename: (req, file, cb) => {
        cb(null, Date.now() + path.extname(file.originalname))
    }
})

const upload = multer({storage: storage})


app.post("/post",  authorize, upload.single('image'), async function(req, res) {
  
    console.log(req.file)
   
    const formData = new FormData()
    formData.append('creatorID', req.body.creatorID)
    formData.append('title', req.body.title)
    formData.append('content', req.body.content)
    formData.append('image', req.file)

    var err = null
    await axios.post(restAPIURL + "/post", formData)
   .then( response => {
        if(response.status !== 200){
           err = "Error while creating post!"
        }
   })
   .catch(error => {
        // console.log("ERROR")
        err = error.response.data
   })

   console.log(err)
   
   if(err){
        res.render('postForm', {err: err})
   } else {
        res.redirect('/')
   }
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

//middleware
function authorize(req, res, next){
    if(req.session.userID === undefined){
        res.status(403)
        res.send("Please log in to take this action!")
    } else {
        next()
    }
}
