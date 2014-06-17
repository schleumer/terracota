var fs = require('fs');
var _ = require('underscore');
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
  var backgroundOffset = 333;
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
    .tap(function(vars) {
      var flags = 0;
      var mask = 0x80;
      var importantTiles = new Array(vars.tileTypeCount);
      for (var k = 0; k < vars.tileTypeCount; k++) {
        if (mask == 0x80) {
          this.word8ls("flagsAndShit").tap(function(vars) {
            flags = vars.flagsAndShit;
          });
          mask = 0x01;
        } else {
          mask <<= 1;
        }
        if ((flags & mask) == mask) {
          importantTiles[k] = true;
        } else {
          importantTiles[k] = false;
        }
      }
      vars.importantTiles = importantTiles;
    })
    .tap(function(vars) {
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

      var treeX = new Array(3);
      var treeStyle = new Array(4);
      var caveBackX = new Array(3);
      var caveBackStyle = new Array(4);
      var moonType = null;

      this.word8ls('moonType');
      for (i = 0; i < 3; i++) {
        this.word32ls('_treeX').tap(function(vars) {
          treeX[i] = vars._treeX;
        });
      }

      for (i = 0; i < 4; i++) {
        this.word32ls('_treeStyle').tap(function(vars) {
          treeStyle[i] = vars._treeStyle;
        });
      }

      for (i = 0; i < 3; i++) {
        this.word32ls('_caveBackX').tap(function(vars) {
          caveBackX[i] = vars._caveBackX;
        });
      }

      for (i = 0; i < 4; i++) {
        this.word32ls('_caveBackStyle').tap(function(vars) {
          caveBackStyle[i] = vars._caveBackStyle;
        });
      }

      this.word32ls('iceBackStyle');
      this.word32ls('jungleBackStyle');
      this.word32ls('hellBackStyle');


      this.word32ls('spawn.x');
      this.word32ls('spawn.y');

      this.buffer('surfaceLevel', 8).tap(function(vars) {
        vars.surfaceLevel = vars.surfaceLevel.readDoubleLE(0)
      });

      this.buffer('rockLayer', 8).tap(function(vars) {
        vars.rockLayer = vars.rockLayer.readDoubleLE(0)
      });

      this.buffer('temporaryTime', 8).tap(function(vars) {
        vars.temporaryTime = vars.temporaryTime.readDoubleLE(0)
      });

      this.buffer('isDayTime', 1).tap(function(vars) {
        vars.isDayTime = Boolean(vars.isDayTime.readInt8(0));
      });

      this.word32ls('moonPhase');

      this.buffer('isBloodMoon', 1).tap(function(vars) {
        vars.isBloodMoon = Boolean(vars.isBloodMoon.readInt8(0));
      });

      this.buffer('isEclipse', 1).tap(function(vars) {
        vars.isEclipse = Boolean(vars.isEclipse.readInt8(0));
      });

      this.word32ls('dungeonPoint.x');
      this.word32ls('dungeonPoint.y');

      this.buffer('hasCrimson', 1).tap(function(vars) {
        vars.hasCrimson = Boolean(vars.hasCrimson.readInt8(0));
      });

      this.tap(function(vars) {
        this.into('deadBosses', function() {
          this.buffer('boss1', 1).tap(function(vars) {
            vars.boss1 = Boolean(vars.boss1.readInt8(0));
          });

          this.buffer('boss2', 1).tap(function(vars) {
            vars.boss2 = Boolean(vars.boss2.readInt8(0));
          });

          this.buffer('boss3', 1).tap(function(vars) {
            vars.boss3 = Boolean(vars.boss3.readInt8(0));
          });

          this.buffer('QueenBee', 1).tap(function(vars) {
            vars.QueenBee = Boolean(vars.QueenBee.readInt8(0));
          });

          this.buffer('MechBoss1', 1).tap(function(vars) {
            vars.MechBoss1 = Boolean(vars.MechBoss1.readInt8(0));
          });

          this.buffer('MechBoss2', 1).tap(function(vars) {
            vars.MechBoss2 = Boolean(vars.MechBoss2.readInt8(0));
          });

          this.buffer('MechBoss3', 1).tap(function(vars) {
            vars.MechBoss3 = Boolean(vars.MechBoss3.readInt8(0));
          });

          this.buffer('MechBossAny', 1).tap(function(vars) {
            vars.MechBossAny = Boolean(vars.MechBossAny.readInt8(0));
          });

          this.buffer('PlantBoss', 1).tap(function(vars) {
            vars.PlantBoss = Boolean(vars.PlantBoss.readInt8(0));
          });

          this.buffer('GolemBoss', 1).tap(function(vars) {
            vars.GolemBoss = Boolean(vars.GolemBoss.readInt8(0));
          });
        })

        this.into('npcsSaved', function() {
          this.buffer('goblin', 1).tap(function(vars) {
            vars.goblin = Boolean(vars.goblin.readInt8(0));
          }).buffer('wizard', 1).tap(function(vars) {
            vars.wizard = Boolean(vars.wizard.readInt8(0));
          }).buffer('mechanic', 1).tap(function(vars) {
            vars.mechanic = Boolean(vars.mechanic.readInt8(0));
          })
        });

        this.into('deadBosses', function() {
          this.buffer('goblinArmy', 1).tap(function(vars) {
            vars.goblinArmy = Boolean(vars.goblinArmy.readInt8(0));
          }).buffer('clown', 1).tap(function(vars) {
            vars.clown = Boolean(vars.clown.readInt8(0));
          }).buffer('frost', 1).tap(function(vars) {
            vars.frost = Boolean(vars.frost.readInt8(0));
          }).buffer('pirates', 1).tap(function(vars) {
            vars.pirates = Boolean(vars.pirates.readInt8(0));
          });
        });

        this.into('shadowOrbs', function() {
          this.buffer('isSmashed', 1).tap(function(vars) {
            vars.isSmashed = Boolean(vars.isSmashed.readInt8(0));
          });
        });

        this.into('spawned', function() {
          this.buffer('meteors', 1).tap(function(vars) {
            vars.meteors = Boolean(vars.meteors.readInt8(0));
          });
        });

        this.into('shadowOrbs', function() {
          this.word8ls('smashedCount');
        });

        this.into('altars', function() {
          this.word32ls('destroyed');
        });

        this.buffer('hardMode', 1).tap(function(vars) {
          vars.hardMode = Boolean(vars.hardMode.readInt8(0));
        });

        this.into('invasion', function() {
          this.word32ls('delay');
          this.word32ls('size');
          this.word32ls('type');
          this.buffer('pointX', 8).tap(function(vars) {
            vars.pointX = vars.pointX.readDoubleLE(0)
          });
        });

        var oreTiers = Array(3);
        var styles = Array(8);

        this.buffer('isRaining', 1).tap(function(vars) {
          vars.isRaining = Boolean(vars.isRaining.readInt8(0));
        });

        this.word32ls('rainTime');

        this.buffer('rainTime', 4).tap(function(vars) {
          vars.rainTime = vars.rainTime.readFloatLE(0);
        });

        for (i = 0; i < 3; i++) {
          this.word32ls('_oreTiers').tap(function(vars) {
            oreTiers[i] = vars._oreTiers;
          });
        }

        for (i = 0; i < 8; i++) {
          this.word8ls('_styles').tap(function(vars) {
            styles[i] = vars._styles;
          });
        }

        this.vars.oreTiers = oreTiers;
        this.vars.styles = styles;

        this.word32ls('cloudsActive');
        this.word16ls('numClouds');

        this.buffer('windSpeed', 4).tap(function(vars) {
          vars.windSpeed = vars.windSpeed.readFloatLE(0);
        });

        var anglerWhoFinishedToday = [];
        this.word32ls('anglerCount').tap(function(vars) {
          for (var index = vars.anglerCount; index > 0; --index) {
            this.buffer('_anglerWhoFinishedTodayName', 8).tap(function(vars) {
              anglerWhoFinishedToday.push(vars._anglerWhoFinishedTodayName);
            });
          }
        });

        this.buffer('savedAngler', 1).tap(function(vars) {
          vars.savedAngler = Boolean(vars.savedAngler.readInt8(0));
        });

        this.word32ls('anglerQuest');

        this.vars.anglerWhoFinishedToday = anglerWhoFinishedToday;
      });

      var tiles = [];
      var run = 0;
      var ntileType = 333;
      var firstHeader = 0,
          secondHeader = 0,
          thirdHeader = 0;
      for (var ncolumn = 0; ncolumn < this.vars.maxX; ncolumn++) {
        for (var nrow = 0; nrow < this.vars.maxX; nrow++) {
          if (run > 0) {
            tiles[ncolumn, nrow] = ntileType;
            run--;
            continue;
          }

          if(nrow < this.vars.surfaceLevel){
            ntileType = backgroundOffset;
          } else if (nrow == this.vars.surfaceLevel) {
            ntileType = backgroundOffset + 1;
          } else if (nrow < (this.vars.rockLayer + 38)){
            ntileType = backgroundOffset + 2;
          } else if (nrow == (this.vars.rockLayer + 38)){
            ntileType = backgroundOffset + 4;
          } else if (nrow < (this.vars.maxY - 202)){
            ntileType = backgroundOffset + 3;
          } else if (nrow == (this.vars.maxY - 202)){
            ntileType = backgroundOffset + 6;
          } else {
            ntileType = backgroundOffset + 5;
          }

          secondHeader = 0;
          thirdHeader = 0;

          this.word8ls("_firstHeader").tap(function(vars) {
            firstHeader = vars._firstHeader;
            if((firstHeader & 1) == 1){
              this.word8ls("_secondHeader").tap(function(vars) {
                secondHeader = vars._secondHeader;
                if((secondHeader & 1) == 1){
                  this.word8ls("_thirdHeader").tap(function(vars) {
                    thirdHeader =  vars._thirdHeader;
                  })
                }
              });
            }
          });


        }
      }

    })
    .vars;


  console.log(vars);
});