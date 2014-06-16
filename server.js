var fs = require('fs');
var _  = require('underscore');
//var express = require('express');
//var app = express();
//var bodyParser = require('body-parser');
//var methodOverride = require('method-override');
//
//app.use(bodyParser());
//app.use(methodOverride());
//app.use(express.static(__dirname + '/public'));
//
//
//var server = app.listen(666, function() {
//    console.log('Listening on port %d', server.address().port);
//});

fs.readFile('./daworld.wld', function(err, data) {
  if (err) throw err;
  var binary = require('binary');
  var vars = binary.parse(data)
    .word32ls('version')
    .word16ls('sections')
    .tap(function(vars) {
      this.into('sectionPointers', function() {
        for (var i = 0; i < vars.sections; i++) {
          this.word32ls(i.toString())
        }
      })
      vars.sectionPointers = _.toArray(vars.sectionPointers);
    })
    .word16ls('tileTypeCount')
    .tap(function(vars){
      var flags = 0;
      var mask = 0x80;
      var importantTiles = new Array(vars.tileTypeCount);
      for(var k = 0; k < vars.tileTypeCount; k++){
        if(mask == 0x80){
          this.word8ls("flagAndShit").tap(function(vars){
            flags = vars.flagAndShit;
          });
          mask = 0x01;
        } else {
          mask <<= 1;
        }
        if((flags & mask) == mask){
          importantTiles[k] = true;
        } else{
          importantTiles[k] = false;
        }
      }
      vars.importantTiles = importantTiles;
    })
    .tap(function(vars){
      var x = 0,
          y = 0,
          w = 0,
          h = 0
      
      this.buffer('name', 8);
      this.word32ls('id');
      
      this.word32ls('x');
      this.word32ls('w');
      this.word32ls('y');
      this.word32ls('h');

      this.word32ls('maxY');
      this.word32ls('maxX');
    })
    .vars;

    console.log(vars);
});
