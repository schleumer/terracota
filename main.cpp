#include <string>
#include <ostream>
#include <iostream>
#include <fstream>
#include <map>
#include <vector>
//#include "prettyprint.hpp"
#include "tiles.hpp"
#include "chests.hpp"

using namespace std;

typedef std::map< std::string, int > IntMap;
typedef std::map< std::string, bool > BoolMap;
typedef std::vector< std::vector<int> > TileMap;

class BinaryReader {
public:
	ifstream infile;
	BinaryReader(const char* filename) {
		infile.open(filename, ios::binary | ios::in);
	}
	int read_int32() {
		char buffer[4];
		infile.read (buffer, 4);
		return *(int *) buffer;
	}

	short read_short() {
		char buffer[2];
		infile.read (buffer, 2);
		return *(short *) buffer;
	}

	char read_byte() {
		char buffer[1];
		infile.read (buffer, 1);
		return *(char *) buffer;
	}

	bool read_bool() {
		char buffer[1];
		infile.read (buffer, 1);
		return *(bool *) buffer;
	}

	double read_double() {
		char buffer[8];
		infile.read (buffer, 8);
		return *(double *) buffer;	
	}

	float read_float() {
		char buffer[4];
		infile.read (buffer, 4);
		return *(float *) buffer;	
	}

	std::string read_prefixed_string() {
		char buffer1[1];
		infile.read (buffer1, 1);
		char header = *(char *) buffer1;

		char buffer[header];
		infile.read (buffer, header);
		return buffer;
	}

};


int main() { 
	ios::sync_with_stdio(false);

	BinaryReader* reader = new BinaryReader("daworld4.wld");
	int version = reader->read_int32();

	//cout << "Version is: " << version << endl;

	short section_count = reader->read_short();
	int section_pointers [section_count];
	
	for ( int i = 0; i < section_count; i++ ) {
		section_pointers[i] = reader->read_int32();
	}

	short tile_type_count = reader->read_short();

	bool important_tiles [tile_type_count];

	short flags = 0;
	short mask = 0x80;

	for ( int i = 0; i < tile_type_count; i++ ) {
		if (mask == 0x80) {
			flags = reader->read_byte();
			mask = 0x01;
		} else {
			mask = mask << 1;
		}

		if ((flags & mask) == mask) {
			important_tiles[i] = true;
		} else {
			important_tiles[i] = false;
		}
	}



	std::string name = reader->read_prefixed_string();
	int id = reader->read_int32();

	//cout << "World name is: " << name << " and id is " << id << endl;

	IntMap bounds;
	bounds["x"] = reader->read_int32();
	bounds["w"] = reader->read_int32();
	bounds["y"] = reader->read_int32();
	bounds["h"] = reader->read_int32();
	bounds["maxY"] = reader->read_int32();
	bounds["maxX"] = reader->read_int32();

	//cout << "Bounds: " << bounds << endl;

	int tree_x [3];
	int tree_style [4];
	int cave_back_x [3];
	int cave_back_style [4];

	char moon_type = reader->read_byte();

	for ( int i = 0; i < 3; i++ ) {
		tree_x[i] = reader->read_int32();
	}

	for ( int i = 0; i < 4; i++ ) {
		tree_style[i] = reader->read_int32();
	}

	for ( int i = 0; i < 3; i++ ) {
		cave_back_x[i] = reader->read_int32();
	}

	for ( int i = 0; i < 4; i++ ) {
		cave_back_style[i] = reader->read_int32();
	}

	int ice_back_style = reader->read_int32();
	int jungle_back_style = reader->read_int32();
	int hell_back_style = reader->read_int32();

	//cout << "Tree X: " << tree_x << endl;
	//cout << "Tree Style: " << tree_style << endl;
	//cout << "Cave Back X: " << cave_back_x << endl;
	//cout << "Cave Back Style: " << cave_back_style << endl;
	//cout << "Ice Back Style: " << ice_back_style << endl;
	//cout << "Jungle Back Style: " << jungle_back_style << endl;
	//cout << "Hell Back Style: " << hell_back_style << endl;

	IntMap spawn;
	spawn["x"] = reader->read_int32();
	spawn["y"] = reader->read_int32();

	//cout << "Spawn: " << spawn << endl;

	double surface_level = reader->read_double();
	double rock_layer = reader->read_double();
	double temporary_time = reader->read_double();
	bool is_day_time = reader->read_bool();
	int moon_phase = reader->read_int32();
	bool is_blood_moon = reader->read_bool();
	bool is_eclipse = reader->read_bool();
	IntMap dungeon_point;
	dungeon_point["x"] = reader->read_int32();
	dungeon_point["y"] = reader->read_int32();
	bool crimson = reader->read_bool();

	//cout << "Surface Level: " << surface_level << endl
	//	 << "Rock Layer: " << rock_layer << endl
	//	 << "Temporary Time: " << temporary_time << endl
	//	 << "Is day time: " << is_day_time << endl
	//	 << "Moon phase: " << moon_phase << endl
	//	 << "Is blood moon: " << is_blood_moon << endl
	//	 << "Is eclipse: " << is_eclipse << endl
	//	 << "Dungeon point: " << dungeon_point << endl
	//	 << "Crimson: " << crimson << endl;
	
	BoolMap saved;
	BoolMap defeated;

	defeated["Boss1"] = reader->read_bool();
	defeated["Boss2"] = reader->read_bool();
	defeated["Boss3"] = reader->read_bool();
	defeated["QueenBee"] = reader->read_bool();
	defeated["MechBoss1"] = reader->read_bool();
	defeated["MechBoss2"] = reader->read_bool();
	defeated["MechBoss3"] = reader->read_bool();
	defeated["MechBossAny"] = reader->read_bool();
	defeated["PlantBoss"] = reader->read_bool();
	defeated["GolemBoss"] = reader->read_bool();
	saved["Goblin"] = reader->read_bool();
	saved["Wizard"] = reader->read_bool();
	saved["Mechanic"] = reader->read_bool();
	defeated["GoblinArmy"] = reader->read_bool();
	defeated["Clown"] = reader->read_bool();
	defeated["Frost"] = reader->read_bool();
	defeated["Pirates"] = reader->read_bool();

	bool shadow_orbs_smashed = reader->read_bool();
	bool meteorSpawned = reader->read_bool();
	int shadow_orbs_smashed_count = reader->read_byte();
	int altars_destroyed = reader->read_int32();
	bool hard_mode = reader->read_bool();

	//cout << "Is any shadow orbs smashed: " << shadow_orbs_smashed << endl
	//	 << "Is any meteor spawned: " << meteorSpawned << endl
	//	 << "Shadow orbs smashed: " << shadow_orbs_smashed_count << endl
	//	 << "Altars destroyed: " << altars_destroyed << endl
	//	 << "Is hard mode: " << hard_mode << endl;

    int invasion_delay = reader->read_int32();
    int invasion_size = reader->read_int32();
    int invasion_type = reader->read_int32();
    double invasion_point_x = reader->read_double();

    //cout << "Invasion delay: " << invasion_delay << endl;
    //cout << "Invasion size: " << invasion_size << endl;
    //cout << "Invasion type: " << invasion_type << endl;
    //cout << "Invasion point x: " << invasion_point_x << endl;

    int ore_tiers [3];
    int styles [8];

    bool is_raining = reader->read_bool();
    int rain_time = reader->read_int32();
    float max_rain = reader->read_float();

    //cout << "Is raining: " << is_raining << endl;
    //cout << "Rain time: " << rain_time << endl;
    //cout << "Max rain: " << max_rain << endl;

    for ( int i = 0; i < 3; i++ ) {
    	ore_tiers[i] = reader->read_int32();
    }

	for ( int i = 0; i < 8; i++ ) {
    	styles[i] = reader->read_byte();
    }    

    //cout << "Ore tiers: " << ore_tiers << endl;
    //cout << "Styles: " << styles << endl;

    int clouds_active = reader->read_int32();
    short num_clouds = reader->read_short();
    float wind_speed = reader->read_float();

    //cout << "Clouds active: " << clouds_active << endl;
    //cout << "Num clouds: " << num_clouds << endl;
    //cout << "Wind speed: " << wind_speed << endl;

    for ( int i = reader->read_int32(); i > 0; i--){
    	//cout << i << endl;
    }

    saved["Angler"] = reader->read_bool();
    int angler_quest = reader->read_int32();

    //cout << "Saved: " << saved << endl
	//	 << "Defeated: " << defeated << endl;
    //
    //cout << "Angler quest: " << angler_quest << endl;

    TileMap tiles;

    tiles.resize( bounds["maxX"] , vector<int>( bounds["maxY"] , 0 ) );

    int total_tiles = bounds["maxX"] * bounds["maxY"];
    int current_tile = 0;

    for ( int ncolumn = 0; ncolumn < bounds["maxX"]; ncolumn++ ) {
    	for ( int nrow = 0; nrow < bounds["maxY"]; nrow++ ) {
    		current_tile++;

    	}
    	//cout << "\r " << current_tile << " / " << total_tiles << " bla bla";
	}

	return 0;
} 