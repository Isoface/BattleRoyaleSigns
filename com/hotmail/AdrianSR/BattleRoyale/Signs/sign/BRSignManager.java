package com.hotmail.AdrianSR.BattleRoyale.Signs.sign;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import com.hotmail.AdrianSR.BattleRoyale.Signs.arena.BRArena;
import com.hotmail.AdrianSR.BattleRoyale.Signs.arena.manager.BRArenaManager;
import com.hotmail.AdrianSR.BattleRoyale.Signs.channel.BRPluginChannel;
import com.hotmail.AdrianSR.BattleRoyale.Signs.config.Config;
import com.hotmail.AdrianSR.BattleRoyale.Signs.main.BRSigns;
import com.hotmail.AdrianSR.BattleRoyale.Signs.util.MessagingUtils;
import com.hotmail.AdrianSR.BattleRoyale.Signs.util.Written;
import com.hotmail.adriansr.core.util.EventUtil;
import com.hotmail.adriansr.core.util.StringUtil;
import com.hotmail.adriansr.core.util.console.ConsoleUtil;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

public class BRSignManager implements Listener {
	
	/**
	 * The key that allow players
	 * to create Battle Royale signs.
	 */
	public static final String BATTLE_ROYALE_SIGN_KEY = "{BR}";
	
	/**
	 * The name of the configuration
	 * section for the signs.
	 */
	public static final String BATTLE_ROYALE_SIGN_SECTION = "BattleRoyaleSigns";
	
	/**
	 * The battle royale saved 
	 * signs file.
	 */
	public static final String BATTLE_ROYALE_SIGNS_FILE = "BattleRoyaleSigns.yml";
	
	/**
	 * Static variables.
	 */
	private static final Map<Location, BRSign>           SIGNS = new HashMap<Location, BRSign>();
//	private static       BukkitTask SING_UPDATER_TASK = null;
	private static       BRSigns               PLUGIN = null;
	
	public static List<BRSign> getSigns() {
		return Collections.unmodifiableList(new ArrayList<BRSign>(SIGNS.values()));
	}

	public BRSignManager(BRSigns plugin) {
		/* cannot be initialized more than once */
		if (PLUGIN == null) {
			PLUGIN = plugin;
		} else {
			throw new UnsupportedOperationException("The Signs manager is already initialized!");
		}
		
		/* register listeners */
		Bukkit.getPluginManager().registerEvents(this, PLUGIN);
		
		/* load signs */
		loadSigns();
		
		/* start signs updater task */
		/* SING_UPDATER_TASK = */ SchedulerUtil.runTaskTimer ( ( ) -> {
			getSigns().stream().forEach(br_sign -> {
				/* check is need change its forward */
				BRArena forward = br_sign.getForward();
				if (forward == null || !forward.isAvailable()) {
					/* update forward */
					BRArena arena = BRArenaManager.findAvailableArena();
					Sign     sign = br_sign.getSign();
					if (sign != null) {
						/* clear old lines */
						for (int x = 0; x < 4; x++) {
							sign.setLine(x, "");
						}
						
						/* when a new forward has been found */
						if (arena != null) { 
							/* update forward */
							br_sign.setForward(arena);
							
							/* display available lines */
							String[] lines = Config.SIGN_AVAILABLE_TEXT.toString().split(Config.SPLITER_KEY);
							for (int x = 0; (x < 4 && x < lines.length); x++) {
								sign.setLine(x, StringUtil.translateAlternateColorCodes(StringUtil.defaultString(lines[x], "")));
							}
						/* when could not find any available forward */
						} else {  
							/* clean forward */
							br_sign.setForward(null);
							
							/* display seaching match lines */
							String[] lines = Config.SIGN_SEARCHING_MATCH_TEXT.toString().split(Config.SPLITER_KEY);
							for (int x = 0; (x < 4 && x < lines.length); x++) {
								sign.setLine(x, StringUtil.translateAlternateColorCodes(StringUtil.defaultString(lines[x], "")));
							}
						}
						sign.update();
					}
				}
			});
		}, 0 ,  (20 * Math.max(Config.SIGNS_REFRESH_DELAY.toInt(), 1)), PLUGIN);
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onClickPlay(PlayerInteractEvent event) {
		/* check is clicking a br sign */
		Block block = event.getClickedBlock();
		if (block == null || !(block.getState() instanceof Sign)) {
			return;
		}
		
		BRSign sign = SIGNS.get(block.getLocation());
		if (sign == null) {
			return;
		}
		
		/* check forward is available */
		BRArena forward = sign.getForward();
		if (forward == null || !forward.isAvailable()) {
			return;
		}
		
		/* check gamemode */
		if (EventUtil.isLeftClick(event.getAction()) && event.getPlayer().getGameMode() == GameMode.CREATIVE) {
			return;
		}
		
		/* send to forward */
		try {
			MessagingUtils.sendPluginMessage(event.getPlayer(), new Written()
					.writeUTF(BRPluginChannel.CONNECT_OTHER_ARGUMENT)
					.writeUTF(event.getPlayer().getName())
					.writeUTF(forward.getName()));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onCreate(SignChangeEvent event) {
		/* register sign if have the br signs key */
		for (String line : event.getLines()) {
			if (!StringUtils.containsIgnoreCase(line, BATTLE_ROYALE_SIGN_KEY)) {
				continue;
			}
			
			/* make and register sign */
			Sign                         sign = (Sign) event.getBlock().getState();
			org.bukkit.material.Sign sign_mat = (org.bukkit.material.Sign) event.getBlock().getState().getData();
			BlockFace                  facing = sign_mat.getFacing();
			SIGNS.put(sign.getLocation(), new BRSign(event.getBlock().getLocation(), facing, (event.getBlock().getType() == Material.SIGN_POST)));
			
			/* save signs */
			saveSigns();
			break;
		}
	}
	
	@EventHandler (priority = EventPriority.LOWEST)
	public void onRemove(BlockBreakEvent event) {
		/* check the broken block is a sign */
		Block block = event.getBlock();
		if (block.getType() != Material.SIGN_POST && block.getType() != Material.WALL_SIGN) {
			return;
		}
		
		/* remove */
		BRSign sign = SIGNS.get(block.getLocation());
		if (sign == null) {
			return;
		}
		
		/* check gamemode */
		if (event.getPlayer().getGameMode() != GameMode.CREATIVE) {
			event.setCancelled(true);
			return;
		}
		
		SIGNS.remove(block.getLocation());
		
		/* update signs file */
		saveSigns();
	}
	
	/**
	 * Load signs from its configuration
	 * file.
	 */
	private void loadSigns() {
		/* load yml configuration */
//		File signs_file = PLUGIN.getFileInDataFolder(BATTLE_ROYALE_SIGNS_FILE);
		File signs_file = new File ( PLUGIN.getDataFolder ( ) , BATTLE_ROYALE_SIGNS_FILE );
		if (!signs_file.exists()) {
			return;
		}
		
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(signs_file);
		if (yml == null) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED, "Could not load the signs!", PLUGIN);
			return;
		}
		
		ConfigurationSection signs_section = yml.getConfigurationSection(BATTLE_ROYALE_SIGN_SECTION);
		if (signs_section != null) {
			for (String key : signs_section.getKeys(false)) {
				ConfigurationSection sign_sc = signs_section.getConfigurationSection(key);
				if (sign_sc == null) {
					continue;
				}
				
				BRSign sign = new BRSign(sign_sc);
				if (sign.isValid()) {
					SIGNS.put(sign.getLocation(), sign);
				}
			}
		}
	}
	
	/**
	 * Save signs to its configuration file.
	 */
	public void saveSigns() {
		/* load yml configuration */
//		File signs_file = PLUGIN.getFileInDataFolder(BATTLE_ROYALE_SIGNS_FILE);
		File signs_file = new File ( PLUGIN.getDataFolder ( ) , BATTLE_ROYALE_SIGNS_FILE );
		if (!signs_file.exists()) {
			try {
				signs_file.createNewFile();
			} catch (IOException e) {
				ConsoleUtil.sendPluginMessage ( ChatColor.RED, "Could not save the signs: ", PLUGIN);
				e.printStackTrace();
				return;
			}
		}
		
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(signs_file);
		if (yml == null) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED, "Could not save the signs!", PLUGIN);
			return;
		}
		
		ConfigurationSection signs_section = yml.createSection(BATTLE_ROYALE_SIGN_SECTION);
		
		/* save */
		int count = 0;
		for (Location key : SIGNS.keySet()) {
			BRSign sign = SIGNS.get(key);
			if (sign != null) {
				sign.save(signs_section.createSection("sign-" + count));
				count ++;
			}
		}
		
		try {
			yml.save(signs_file);
		} catch (IOException e) {
			ConsoleUtil.sendPluginMessage ( ChatColor.RED, "Could not save the signs: ", PLUGIN);
			e.printStackTrace();
		}
 	}
}