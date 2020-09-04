package com.hotmail.AdrianSR.BattleRoyale.Signs.arena.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.hotmail.AdrianSR.BattleRoyale.Signs.arena.BRArena;
import com.hotmail.AdrianSR.BattleRoyale.Signs.channel.PingingServer;
import com.hotmail.AdrianSR.BattleRoyale.Signs.config.Config;
import com.hotmail.AdrianSR.BattleRoyale.Signs.main.BRSigns;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.function.FunctionUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

public class BRArenaManager {
	
	/**
	 * The arenas configuration section name.
	 */
	public static final String ARENAS_CONFIGURATION_SECTION = "arenas";

	private static final List<BRArena>          ARENAS = new ArrayList<BRArena>();
//	private static       BukkitTask SERVER_PINGER_TASK = null;
	private static       BRSigns                PLUGIN = null;
	
	public static List<BRArena> getLoadedArenas() {
		return Collections.unmodifiableList(ARENAS);
	}
	
	public static BRArena findAvailableArena() {
		return ARENAS.stream()
				.filter(BRArena :: isWaiting)
				.filter ( FunctionUtil.negate ( BRArena :: isFull ) )
				.findFirst()
				.orElse(null);
	}
	
	public BRArenaManager(BRSigns plugin) {
		/* cannot be initialized more than once */
		if (PLUGIN == null) {
			PLUGIN = plugin;
		} else {
			throw new UnsupportedOperationException("The Arenas loader is already initialized!");
		}
		
		/* load arenas from config */
		loadArenas();
		
		/* start ping task */
		/* this.SERVER_PINGER_TASK = */ SchedulerUtil.runTaskTimer(() -> {
			ARENAS.stream().filter(BRArena :: hasServer).forEach(arena -> {
				/* ping! */
				arena.getServer().ping(Math.max(Config.PING_TIME_OUT.toInt(), PingingServer.MINIMUM_TIME_OUT));
			});
		}, (20 * Math.max(Config.SERVERS_STATUS_REFRESH_DELAY.toInt(), 1)), 
				(20 * Math.max(Config.SERVERS_STATUS_REFRESH_DELAY.toInt(), 1)), PLUGIN);
	}
	
	/**
	 * Load arenas from config.
	 */
	private void loadArenas() {
		/* save default arenas config if it does not exist */
		File arenas_file = new File ( PLUGIN.getDataFolder ( ) , "BattleRoyaleArenas.yml" );
		if (!arenas_file.exists()) {
			PLUGIN.saveResource("BattleRoyaleArenas.yml", false);
		}
		
		/* load arenas configuration */
		YamlConfiguration arenas_yml = YamlConfiguration.loadConfiguration(arenas_file);
		if (arenas_yml == null) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED, "The Arenas configuration file could not be loaded!", PLUGIN);
			return;
		}
		
		ConfigurationSection section = arenas_yml.getConfigurationSection(ARENAS_CONFIGURATION_SECTION);
		if (section == null) {
			return;
		}
		
		for (String key : section.getKeys(false)) {
			ConfigurationSection arena_sc = section.getConfigurationSection(key);
			if (arena_sc == null) {
				continue;
			}
			
			if (arena_sc.isString("name") && arena_sc.isInt("port")) {
				String name = arena_sc.getString("name");
				int    port = arena_sc.getInt("port");
				if (!StringUtils.isBlank(name) || port < 0) { /* check arena name and port */
					BRArena arena = new BRArena(name, port);
					if (!ARENAS.contains(arena)) {
						ARENAS.add(arena);
					}
				}
			}
		}
	}
}
