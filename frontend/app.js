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
app.use("/images", express.static(path.join(__dirname, "images")));


//TODO: OVO JE SAM TU STAVLJENO OKVIRNO
//KAD JE USER ULOGIRAN definiran je req.session.userID, inace undefined
app.get("/", async function (req, res) {
    let posts = []
    await axios.get(restAPIURL + '/posts', { params: { userID: req.session.userID } })
		.then(result => {
			posts = result.data
	})
    .catch(err => console.log(err))
	
    res.render('index', 
        {
            err: undefined, 
            userID: req.session.userID ? req.session.userID : null, 
            posts: posts
    })
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
    res.render('login', {err: null, userID: req.session.userID})
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

        // console.log(response)
        if(response.status === 200){
            req.session.userID = response.data
        } else {
            error = "Invalid credentials,  please try again!"
        }

    } catch(err){
        console.log(err)
        error = "Invalid credentials, please try again"
    }


    console.log(error)
   if(error){
        res.render('login', {err:error, userID: undefined})
   } else {
        res.redirect('/')
   }
})

app.get('/logout', function(req, res) {
    req.session.destroy()
    res.redirect('/')
})

app.get("/post", authorize, function(req, res) {
    res.render('postForm', {err: undefined, userID: req.session.userID})
})


const storage = multer.diskStorage({
    destination: (req, file, cb) => {
        cb(null, 'images')
    },

    filename: async (req, file, cb) => {
        // console.log("Inside multer filename method")
		
        var err = null
        await axios.post(restAPIURL + "/post", {
            creatorID: req.session.userID,
            title: req.body.title,
            content: req.body.content,
            pictureFormat: file == undefined ? "" : path.extname(file.originalname)
        })
       .then( response => {
            if(response.status !== 201){
               err = "Error while creating post!"
                // res.render('postForm', {err: err, userID: req.session.userID})
               cb(err)
            } else {
                console.log("Ime slike " + decodeURIComponent(response.headers.location))

                
                cb(null, decodeURIComponent(response.headers.location) /*+ '_' + req.body.title + /*"_" + Date.now() + */ /*file == undefined ? "" : path.extname(file.originalname)*/)
            }
       })
       .catch(error => {
            // console.log("ERROR")
            err = error.response.data
       })


    }
})

const upload = multer({storage: storage})

app.post("/post",  authorize, upload.single('image'), async function(req, res) {
  
    console.log("Creating post...")
    // console.log(req.file)


    let err = undefined
    if(req.file == undefined){
        await axios.post(restAPIURL + "/post", {
            creatorID: req.session.userID,
            title: req.body.title,
            content: req.body.content,
        })
       .catch(error => {
            // console.log("ERROR")
            err = error.response.data
       })
    }

    // const formData = new FormData()
    // formData.append('creatorID', req.body.creatorID)
    // formData.append('title', req.body.title)
    // formData.append('content', req.body.content)
    //formData.append('image', req.image)

    // var err = null
    // await axios.post(restAPIURL + "/post", formData)
   // .then( response => {
        // if(response.status !== 200){
           // err = "Error while creating post!"
        // }
   // })
   // .catch(error => {
        // console.log("ERROR")
        // err = error.response.data
   // })

   // console.log(err)
   
    if(err){
        res.render('postForm', {err: err, userID: req.session.userID})
    } else {
        res.redirect('/')
    }
})

app.post("/comment",  authorize, async function(req, res) {
	
	await axios.post(restAPIURL + "/comment", {
            creatorID: req.session.userID,
            postID: req.body.postID,
			comment: req.body.commentText
        })
       .catch(error => {
            err = error.response.data
			console.log(err)
       })
	
	res.redirect(req.headers.referer + '#' + req.body.postID)
})

app.get('/search', async function(req, res) {
    const searchKey = req.query.searchKey
    console.log('Fetching users with name similar to ' + searchKey)
    console.log(searchKey)
    axios.get(restAPIURL + '/users?searchKey='+ searchKey, null)
    .then(response => {
        console.log(response.data)
        res.json(response.data)
    })
    .catch(error => console.log(error))
})

app.get('/profile/:id', async function(req, res) {
    let err = undefined
    const id = req.params.id
    let profile = {}
	let userIDParam = ''
	if (req.session.userID)
		userIDParam = '?userID=' + req.session.userID

    try{
        const response = await axios.get(restAPIURL + '/profile/' + id + userIDParam)
        profile = response.data
    } catch(error){
        err = "Something went wrong when getting profile"
        console.log(error)
    }

	console.log(profile)
    res.render('profile', {
		userID: req.session.userID,
		err: err,
		posts: profile.posts,
		username: profile.username,
		profileID: profile.id,
		followed: profile.followed
	})
})


app.get("/like/:postID", authorize, async function (req, res) {
	
    console.log('User ' + req.session.userID + ' liking post ' + req.params.postID)
	await axios.post(restAPIURL + "/like", {
            userID: req.session.userID,
            contentID: req.params.postID
        })
       .catch(error => {
            // console.log("ERROR")
            err = error.response.data
       })
	
	res.redirect(req.headers.referer + '#' + req.params.postID)
})

app.get("/comment/like/:commentID", authorize, async function (req, res) {
	
    console.log('User ' + req.session.userID + ' liking comment ' + req.params.commentID)
	await axios.post(restAPIURL + "/comment/like", {
            userID: req.session.userID,
            contentID: req.params.commentID
        })
       .catch(error => {
            // console.log("ERROR")
            err = error.response.data
       })
	
	res.redirect(req.headers.referer + '#' + req.params.commentID)
})

app.get("/follow/:profileID", authorize, async function (req, res) {
	
	await axios.post(restAPIURL + "/follow", {
            userID: req.session.userID,
            creatorID: req.params.profileID
        })
       .catch(error => {
            // console.log("ERROR")
            err = error.response.data
			console.log(err)
       })
	
	res.redirect('back')
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
        console.log('User not logged in')
        res.redirect('/login')
        // res.send("Please log in to take this action!")
    } else {
        next()
    }
}