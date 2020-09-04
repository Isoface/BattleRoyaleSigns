package com.hotmail.AdrianSR.BattleRoyale.Signs.config;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.yaml.YamlConfigurationComments;

/**
 * Represents the plugin configuration class.
 * <p>
 * @author AdrianSR
 */
public enum Config {
	
	SIGNS_REFRESH_DELAY(1),
	SERVERS_STATUS_REFRESH_DELAY(2),
	PING_TIME_OUT(50),
	
	SIGN_AVAILABLE_TEXT(ChatColor.GREEN + "Click to Play" + Config.SPLITER_KEY + ChatColor.GOLD + "!Battle Royale!" + Config.SPLITER_KEY + "/-/-/-/-/-/-/"),
	SIGN_SEARCHING_MATCH_TEXT("" + Config.SPLITER_KEY + ChatColor.DARK_GRAY + "Searching Match"),
	
	; // END!
	
	/**
	 * Global Variables.
	 */
	public static final String          SPLITER_KEY = "%N%";
	public static final String CONFIG_YML_FILE_NAME = "BattleRoyaleSignsConfig.yml";
	private static      YamlConfigurationComments    FILE;
	
	/**
	 * Class values.
	 */
	private final String path;
	private final Object  def;

	/**
	 * Config enum constructor.
	 * 
	 * @param path The string path.
	 * @param start The default value.
	 */
	private Config(String path, Object start) {
		this.path = path;
		def = start;
	}
	
	/**
	 * Config enum constructor, path no needed.
	 * 
	 * @param start The default value.
	 */
	private Config(Object start) {
		this.path = name().toLowerCase().replace("_", "-");
		def = start;
	}

	/**
	 * Set the {@link YamlConfiguration} to use.
	 * 
	 * @param config The config to set.
	 */
	public static void setFile(YamlConfigurationComments config) {
		// set.
		FILE = config;
	}
	
	@Override
	public String toString() {
		// check instanceof String.
		if (!(def instanceof String)) {
			return null;
		}
		
		// get string.
		final String value = FILE.getString(path, (String) def);
		
		// return loaded string.
		return StringUtil.translateAlternateColorCodes ( StringUtil.defaultString ( value , (String) def ) );
	}
	
	/**
	 * Get config item as {@link GameMode}.
	 * 
	 * @return the config item value as GameMode.
	 */
	public GameMode toGameMode() {
		// check instanceof Game Mode
		if (!(def instanceof GameMode)) {
			return null;
		}
		
		// load.
		final GameMode loaded = Enum.valueOf(GameMode.class, FILE.getString(path).toUpperCase());
		
		// return def or loaded.
		return loaded != null ? loaded : ((GameMode) def);
	}
	
	/**
	 * Get config item as {@link File}.
	 * 
	 * @return the config item value as File.
	 */
	public File toFile() {
		return new File(FILE.getString(path));
	}

	/**
	 * Get config item as {@link Integer}.
	 * 
	 * @return the config item value as Integer.
	 */
	public int toInt() {
		// check instance of Integer.
		if (!(def instanceof Integer)) {
			return 0;
		}
		
		// return def or loaded.
		return FILE.getInt(path, (Integer) def);
	}
	
	/**
	 * Get config item as {@link Double}.
	 * 
	 * @return the config item value as Double.
	 */
	public double toDouble() {
		// check instance of Double.
		if (!(def instanceof Double)) {
			return 0;
		}
		
		// return def or loaded.
		return FILE.getDouble(path, (Double) def);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T of(Class<T> instance_of) {
		if (!instance_of.isAssignableFrom(def.getClass())) {
			return null;
		}
		return (T) FILE.get(path, def);
	}

//	/**
//	 * Config item to class.
//	 * <p>
//	 * @param class_assignable the Class to.
//	 * @return a instanceof of the given class or null if is not assigable.
//	 */
//	public <T> T toAssignable(Class<?> class_assignable) {
//		if (!class_assignable.isAssignableFrom(def.getClass())) {
//			return (T) null;
//		}
//		
//		return (T) FILE.get(path, def);
//	}
	
	/**
	 * Get config item as {@link Boolean}.
	 * 
	 * @return the config item value as Boolean.
	 */
	public boolean toBoolean() {
		// check instanceof Boolean.
		if (!(def instanceof Boolean)) {
			return false;
		}
		
		// return def or loaded.
		return FILE.getBoolean(path, (Boolean) def);
	}

	/**
	 * Get config item as {@link List<String>}.
	 * 
	 * @return the config item value as List<String>.
	 */
	public List<String> toStringList() {
		// check instanceof List.
		if (!(def instanceof List)) {
			return null;
		}
		
		// return loaded.
		return FILE.getStringList(path);
	}
	
	/**
	 * @return to string unstranslated colors.
	 */
	public String untranslatedColors() {
		return StringUtil.untranslateAlternateColorCodes(ChatColor.COLOR_CHAR, toString());
	}
	
	/**
	 * Get the unstranslated colors String.
	 * 
	 * @param str the string to unstranslate.
	 * @return return a unstranslated colors of a String.
	 */
	public static String getCodeColor(String str) {
		return str != null ? str.replaceAll("\\xa7", "&") : null;
	}

	/**
	 * Get the String path.
	 * 
	 * @return The String patch.
	 */
	public String getPath() {
		return path;
	}
	
	/**
	 * Get the default value.
	 * 
	 * @return the default value.
	 */
	public Object getDefault() {
		return def;
	}
}