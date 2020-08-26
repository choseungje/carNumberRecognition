const express = require('express');
const multer = require('multer');
const path = require('path');
const fs = require('fs');
var execSync = require('child_process').execSync;
var cmd = require( "node-cmd" );

const router = express.Router();
fs.readdir('uploads', (error) => {
    if (error){
        console.error('uploads 폴더가 없어 uploads 폴더 생성');
        fs.mkdirSync('uploads');
    }
});

const upload = multer({
    dest: 'uploads/',
    storage: multer.diskStorage({
        destination(req, file, cb){
            // cb(null, 'uploads/');
            cb(null, 'routes/darknet/')
        },
        filename(req, file, cb){
            // const ext = path.extname(file.originalname);
            // cb(null, path.basename(file.originalname, ext) + Date.now() + ext);
            const ext = path.extname(file.originalname);
            cb(null, path.basename('car' + ext));
        },
    }),
    limits: {fileSize: 5 * 1024 * 1024},
});

router.post('/post', upload.single('img'), (req, res) => {
    console.log("Request Get img upload");
    // var a = req.headers;
    // console.log(a)
    // var image = req.file;
    // console.log(image)
    var img = req.body;
    console.log(img)
    
    console.log("fun() start");

    //res.send("image delivered.")
    execSync('darknet.bat')
    // console.log(execSync('z.bat'))

    // cmd.get(
    //     // 실행할 노드 실행 파일
    //     "node c.js"
    //     , function( error, success, stderr ) {
    //         if( error ) {
    //                 console.log( "ERROR 발생 :\n\n", error );
    //         } else {
    //                 console.log( "SUCCESS :\n\n", success );
    //         }
    //     }
    // );
    
    res.send(req.file.filename);
    
    console.log(req.file.path);
    console.log(__filename);
    console.log(__dirname);
    console.log(req.file.filename)
});

/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('responds');
});

module.exports = router;