package src.train.common.library;

public enum EnumSoundsFiles {

	chme3_horn("chme3_horn"),
	chme3_run("chme3_run"),
	chme3_idle("chme3_idle"),
	vl10_horn("vl10_horn"),
	vl10_run("vl10_run"),
	vl10_idle("vl10_idle"),
	eu07_horn("eu07_horn"),
	shunter_horn("shunter_horn"),
	tram_horn("tram_horn"),
	gp_horn("gp_horn"),
	steam_horn("steam_horn"),
	steam_run("steam_run"),
	steam_idle("steam_idle"),
	v60_horn("v60_horn"),
	subway_horn("subway_horn"),
	high_speed_horn("high_speed_horn"),
	gp40_2_horn("gp40-2_horn"),
	german_steam_horn("german_steam_horn"),
	american_steam_horn("american_steam_horn"),
	sd70_horn("sd70_horn"),
	british_two_tone("british_two_tone"),
	shay_horn("shay_horn"),
	class62_horn("class62_horn"),
	cd742_horn("742_horn"),
	cd742_engine("742_motor"),
	cd742_engine_slow("742_motor_slow"),
	cd742_engine_fast("742_motor_fast"),
	adler_horn("adler_whistle"),
	adler_run("adler_run");

	private String soundName;

	private EnumSoundsFiles(String soundName) {
		this.soundName = soundName;
	}

	public String getSoundName() {
		return Info.modID + ":" + soundName;
	}
}
