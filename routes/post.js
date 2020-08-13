const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');

// const { Post, Hashtag, User} = require('../models');
// const { isLoggedIn} = require('./middlewares');

const router = express.Router();
fs.readdir('uploads', (error) => {
    if (error){
        console.error('uploads 폴더가 없어 uploads 폴더 생성');
        fs.mkdirSync('uploads');
    }
});

const upload = multer({
    // dest: 'uploads/',
    storage: multer.diskStorage({
        destination(req, file, cb){
            cb(null, 'uploads/');
        },
        filename(req, file, cb){
            const ext = path.extname(file.originalname);
            cb(null, path.basename(file.originalname, ext) + Date.now() + ext);
        },
    }),
    limits: {fileSize: 5 * 1024 * 1024},
});

router.post('/', upload.single('img'), (req, res) => {
    console.log("Request Get img upload");
    var a = req.headers;
    console.log(a)
    var image = req.file;
    console.log(image)
    var img = req.body;
    console.log(img)

    res.json({url: '/${req.file.filename'});
    console.log(req.file.filename)
});

/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('respond');
});

module.exports = router;