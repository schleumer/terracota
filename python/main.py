import struct
import json
import array
import binascii
from ctypes import *

read_int = lambda f: struct.unpack('i', f.read(4))[0]
read_short = lambda f: struct.unpack('h', f.read(2))[0]
read_double = lambda f: struct.unpack('d', f.read(8))[0]
read_float = lambda f: struct.unpack('f', f.read(4))[0]
read_bool = lambda f: struct.unpack('?', f.read(1))[0]
# read_byte = lambda f: c_byte(f.read(1)[0])

def read_byte(f):
	b = f.read(1)
	return struct.unpack('=b', bytearray(b))[0]

def read_prefixed_string(f):
	prefix_len = struct.unpack('b', f.read(1))[0]
	return struct.unpack(str(prefix_len) + 's', f.read(prefix_len))[0].decode('utf-8')

f = open("../daworld4.wld", "rb")
version = read_int(f)
print("your version is: %s" % version)

sectionCount = read_short(f)
sectionPointers = []
for i in range(sectionCount):
	sectionPointers.append(read_int(f))

tiletypeCount = read_short(f)
tileImportant = [False] * tiletypeCount

flags = 0
mask = 0x80

for i in range(tiletypeCount):
	if(mask == 0x80):
		flags = read_byte(f)
		mask = 0x01
	else:
		mask <<= 1

	if((flags & mask) == mask):
		tileImportant[i] = True


name = read_prefixed_string(f)
id = read_int(f)
print("World id is %s and name %s" % (id, name))

bounds = {}
bounds['x'] = read_int(f)
bounds['w'] = read_int(f)
bounds['y'] = read_int(f)
bounds['h'] = read_int(f)
bounds['maxY'] = read_int(f)
bounds['maxX'] = read_int(f)

treeX = [0] * 3
treeStyle = [0] * 4
caveBackX = [0] * 3
caveBackStyle = [0] * 4

moonType = read_byte(f)
for i in range(3):
	treeX[i] = read_int(f)

for i in range(4):
	treeStyle[i] = read_int(f)

for i in range(3):
	caveBackX[i] = read_int(f)


for i in range(4):
	caveBackStyle[i] = read_int(f)

iceBackStyle = read_int(f)
jungleBackStyle = read_int(f)
hellBackStyle = read_int(f)

spawn = {}
spawn['x'] = read_int(f)
spawn['y'] = read_int(f)

surfaceLevel = read_double(f)
rockLayer = read_double(f)
temporaryTime = read_double(f)
isDayTime = read_bool(f)
moonPhase = read_int(f)
isBloodMoon = read_bool(f)
isEclipse = read_bool(f)

dungeonPoint = {}
dungeonPoint["x"] = read_int(f)
dungeonPoint["y"] = read_int(f)
crimson = read_bool(f)

defeated = {}
saved = {}

defeated["Boss1"] = read_bool(f)
defeated["Boss2"] = read_bool(f)
defeated["Boss3"] = read_bool(f)
defeated["QueenBee"] = read_bool(f)
defeated["MechBoss1"] = read_bool(f)
defeated["MechBoss2"] = read_bool(f)
defeated["MechBoss3"] = read_bool(f)
defeated["MechBossAny"] = read_bool(f)
defeated["PlantBoss"] = read_bool(f)
defeated["GolemBoss"] = read_bool(f)
saved["Goblin"] = read_bool(f)
saved["Wizard"] = read_bool(f)
saved["Mechanic"] = read_bool(f)
defeated["GoblinArmy"] = read_bool(f)
defeated["Clown"] = read_bool(f)
defeated["Frost"] = read_bool(f)
defeated["Pirates"] = read_bool(f)

shadowOrbs = {}
altars = {}

shadowOrbs["smashed"] = read_bool(f)

meteorSpawned = read_bool(f)

shadowOrbs["smashedCount"] = read_byte(f)

altars["destroyed"] = read_int(f)

hardmode = read_bool(f)

invasion_delay = read_int(f)
invasion_size = read_int(f)
invation_type = read_int(f)
invasion_point_x = read_double(f)

ore_tiers = [0] * 3
styles = [0] * 8

is_raining = read_bool(f)
rain_time = read_int(f)
max_rain = read_float(f)

for i in range(3):
	ore_tiers[i] = read_int(f)

for i in range(8):
	styles[i] = read_byte(f)

clouds_active = read_int(f)
num_clouds = read_short(f)
wind_speed = read_float(f)

print(clouds_active, num_clouds, wind_speed)

exit()

for i in reversed(range(read_int(f))):
	print(i)

saved["angler"] = read_bool(f)
angler_quest = read_int(f)

tiles = [[0 for x in range(bounds["maxY"])] for x in range(bounds["maxX"])] 

class TileProperties:
    Amethyst = 67
    BackgroundOffset = 333
    BlueWire = 331
    Chest = 21
    CopperCache = 330
    Cropped = 326
    Diamond = 68
    Emerald = 65
    EnchantedSword = 333
    ExposedGems = 178
    GoldCache = 332
    GreenWire = 332
    Honey = 329
    LargeDetritus = 186
    LargeDetritus2 = 187
    Lava = 328
    Processed = 325
    RedWire = 330
    Ruby = 64
    Sapphire = 63
    Sign = 55
    SilverCache = 331
    SmallDetritus = 185
    Topaz = 66
    Unknown = 273
    WallOffset = 339
    Water = 327

class ChestType:
	Unknown = -1
	Chest = 0
	GoldChest = 0
	LockedGoldChest = 0
	ShadowChest = 0
	LockedShadowChest = 0
	Barrel = 5
	TrashCan = 5
	EbonwoodChest = 5
	RichMahoganyChest = 5
	PearlwoodChest = 5
	IvyChest = 10
	IceChest = 10
	LivingWoodChest = 10
	SkywareChest = 10
	ShadewoodChest = 10
	WebCoveredChest = 15
	LihzahrdChest = 15
	WaterChest = 15
	JungleChest = 15
	CorruptionChest = 15
	CrimsonChest = 20
	HallowedChest = 20
	FrozenChest = 20
	LockedJungleChest = 20
	LockedCorruptionChest = 20
	LockedCrimsonChest = 25
	LockedHallowedChest = 25
	LockedFrozenChest = 27


ntile_type = TileProperties.BackgroundOffset
run = 0
for ncolumn in range(bounds["maxX"]):
	for nrow in range(bounds["maxY"]):
		if(run > 0):
			try:
				tiles[ncolumn][nrow] = ntile_type
			except:
				print(ncolumn, nrow)
				raise
			run = run - 1
			continue

		if(nrow < surfaceLevel):
			ntile_type = TileProperties.BackgroundOffset

		elif (nrow == surfaceLevel):
			ntile_type = TileProperties.BackgroundOffset + 1

		elif (nrow < (rockLayer + 38)):
			ntile_type = TileProperties.BackgroundOffset + 2

		elif (nrow == (rockLayer + 38)):
			ntile_type = TileProperties.BackgroundOffset + 4

		elif (nrow < (bounds["maxY"] - 202)):
			ntile_type = TileProperties.BackgroundOffset + 3

		elif (nrow == (bounds["maxY"] - 202)):
			ntile_type = TileProperties.BackgroundOffset + 6

		else:
			ntile_type = TileProperties.BackgroundOffset + 5

		second_header = 0
		third_header = 0

		first_header = read_byte(f)

		if((first_header & 1) == 1):
			second_header = read_byte(f)
			if((second_header & 1) == 1):
				third_header = read_byte(f)

		if((first_header & 2) == 2):
			if((first_header & 32) == 32):
				ntile_type = read_short(f)
			else:
				ntile_type = read_byte(f)

			if(tileImportant[ntile_type]):
				typex = read_short(f)
				typey = read_short(f)
				if(ntile_type == TileProperties.ExposedGems):
					if typex == 0:
						ntile_type = TileProperties.Amethyst
					elif typex == 18:
						ntile_type = TileProperties.Topaz
					elif typex == 36:
						ntile_type = TileProperties.Sapphire
					elif typex == 54:
						ntile_type = TileProperties.Emerald
					elif typex == 72:
						ntile_type = TileProperties.Ruby
					elif typex == 90:
						ntile_type = TileProperties.Diamond
					elif typex == 108:
						ntile_type = TileProperties.ExposedGems

				elif ntile_type ==  TileProperties.SmallDetritus:
					if (typex % 36 == 0) and (typey == 18):
						vtype = typex / 36

						if vtype == 16:
							ntile_type = TileProperties.CopperCache
						elif vtype == 17:
							ntile_type = TileProperties.SilverCache
						elif vtype == 18:
							ntile_type = TileProperties.GoldCache
						elif vtype == 19:
							ntile_type = TileProperties.Amethyst
						elif vtype == 20:
							ntile_type = TileProperties.Topaz
						elif vtype == 21:
							ntile_type = TileProperties.Sapphire
						elif vtype == 22:
							ntile_type = TileProperties.Emerald
						elif vtype == 23:
							ntile_type = TileProperties.Ruby
						elif vtype == 24:
							ntile_type = TileProperties.Diamond

				elif ntile_type == TileProperties.LargeDetritus:
					if (typex % 54 == 0) and (typey == 0):
						vtype = typex / 54
						if vtype == 16 or vtype == 17:
							ntile_type = TileProperties.CopperCache
						elif vtype == 18 or vtype == 19:
							ntile_type = TileProperties.SilverCache
						elif vtype == 20 or vtype == 21:
							ntile_type = TileProperties.GoldCache
				elif ntile_type == TileProperties.LargeDetritus2:
					if (typex % 54 == 0) and (typey == 0):
						vtype = typex / 54

						if vtype == 17:
							ntile_type = TileProperties.EnchantedSword

				#if ntile_type == TileProperties.Chest && (typex % 36 == 0) && (typey == 0):
				#	if (typex / 36) <= 

			if((third_header & 8) == 8):
				read_byte(f)

		if (first_header & 4) == 4:
			wallType = read_byte(f)
			if ntile_type >= TileProperties.Unknown:
				ntile_type = wallType + TileProperties.WallOffset
			if(third_header & 16) == 16:
				read_byte(f)

		if (first_header & 8) == 8:
			ntile_type = TileProperties.Water
			read_byte(f)
		elif (first_header & 16) == 16:
			ntile_type = TileProperties.Lava
			read_byte(f)

		if (first_header & 64) == 64:
			run = read_byte(f)

		if (first_header & 128) == 128:
			run = read_short(f)


		try:
			tiles[ncolumn][nrow] = ntile_type
		except:
			print(ncolumn, nrow)
			raise

f2 = open('D:\\cu2.json', 'w')
f2.write(json.dumps(tiles))
f2.close()
f.close()

exit()