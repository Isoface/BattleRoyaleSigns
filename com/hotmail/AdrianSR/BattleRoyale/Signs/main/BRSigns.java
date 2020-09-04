package com.hotmail.AdrianSR.BattleRoyale.Signs.main;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;

import com.hotmail.AdrianSR.BattleRoyale.Signs.arena.manager.BRArenaManager;
import com.hotmail.AdrianSR.BattleRoyale.Signs.channel.BRPluginChannel;
import com.hotmail.AdrianSR.BattleRoyale.Signs.config.Config;
import com.hotmail.AdrianSR.BattleRoyale.Signs.sign.BRSignManager;
import com.hotmail.adriansr.core.plugin.PluginAdapter;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.yaml.YamlConfigurationComments;
import com.hotmail.adriansr.core.version.CoreVersion;

/**
 * The Battle Royale Bungeecord Signs plugin.
 * <p>
 * @author AdrianSR
 */
public final class BRSigns extends PluginAdapter {
	
	private static BRSigns                  INSTANCE;
	private static BRPluginChannel MESSAGING_CHANNEL;
	private static BRArenaManager       ARENA_LOADER;
	
	public static BRSigns getInstance() {
		return INSTANCE;
	}
	
	public static BRPluginChannel getMessaginChannel() {
		return MESSAGING_CHANNEL;
	}
	
	public static BRArenaManager getArenaLoader() {
		return ARENA_LOADER;
	}
	
    @Override
    public boolean setUp() {
    	INSTANCE = this; /* load instance */
    	
    	 /* load messaging channel */
        MESSAGING_CHANNEL = new BRPluginChannel(this);
        
    	/* print plugin enabled message */
        ConsoleUtil.sendPluginMessage ( "Enabled!" , this );
        return true;
    }
    
	@Override
	public boolean setUpHandlers ( ) {
		/* check data folder */
		if (!getDataFolder().exists() || !getDataFolder().isDirectory()) {
			getDataFolder().mkdir();
		}
		
		/* initialize configuration */
		boolean configFine = true;
		final File configFile = new File ( getDataFolder ( ) , Config.CONFIG_YML_FILE_NAME );
		if (!configFile.exists()) {
			try {
				// create new file.
				configFile.createNewFile();
			} catch (IOException e) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED , "Could not load main config file: " , this );
				e.printStackTrace();
				configFine = false;
//				setEnabled(false);
				return false;
			}
		}

		// 2) load and check defaults.
		if (configFine) {
			// load UTF_8 Yaml Configuration and check.
			int saveConfig = 0;
			final YamlConfigurationComments configYml = YamlConfigurationComments.loadConfiguration(configFile);
			if (configYml != null) {
				// check defaults.
				for (Config item : Config.values()) {
					// check if is set.
					if (!configYml.isSet(item.getPath()) || configYml.get(item.getPath()) == null) {
						// set default.
						if (item.getDefault() instanceof String) {
							configYml.set(item.getPath(), Config.getCodeColor((String) item.getDefault()));
						} else {
							configYml.set(item.getPath(), item.getDefault());
						}
						saveConfig++;
					}
				}
			}
			
			// set file.
			Config.setFile(configYml);

			// save file.
			if (saveConfig > 0) {
				try {
					configYml.save(configFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		/* initialize managers */
		ARENA_LOADER = new BRArenaManager(this);
		new BRSignManager(this);
		return true;
	}

	@Override
	public CoreVersion getRequiredCoreVersion ( ) {
		return CoreVersion.v2_0_0;
	}
}
