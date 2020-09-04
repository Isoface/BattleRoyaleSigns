package com.hotmail.AdrianSR.BattleRoyale.Signs.channel;

import java.util.Objects;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.hotmail.AdrianSR.BattleRoyale.Signs.arena.BRArena;
import com.hotmail.AdrianSR.BattleRoyale.Signs.arena.manager.BRArenaManager;
import com.hotmail.AdrianSR.BattleRoyale.Signs.main.BRSigns;
import com.hotmail.AdrianSR.BattleRoyale.Signs.util.MessagingUtils;
import com.hotmail.AdrianSR.BattleRoyale.Signs.util.ReadUtils;
import com.hotmail.AdrianSR.BattleRoyale.Signs.util.Written;
import com.hotmail.adriansr.core.util.scheduler.SchedulerUtil;

/**
 * A listener for Battle Royale Plugin Channel, 
 * which will receive notifications
 * of messages sent from a client.
 * <p>
 * @author AdrianSR
 */
public final class BRPluginChannel implements PluginMessageListener, Listener {

	/**
	 * Plugin Messaging channel name.
	 */
	public static final String MESSAGING_CHANNEL            = "BungeeCord";
	public static final String PLAYER_IP_ARGUMENT           = "IP";
	public static final String SERVER_PLAYER_COUNT_ARGUMENT = "PlayerCount";
	public static final String SERVER_PLAYER_LIST_ARGUMENT  = "PlayerList";
	public static final String SERVER_NAME_ARGUMENT         = "GetServer";
	public static final String SERVERS_NAMES_ARGUMENT       = "GetServers";
	public static final String SERVER_IP_ARGUMENT           = "ServerIP";
	public static final String CONNECT_OTHER_ARGUMENT       = "ConnectOther";
	
	/**
	 * Battle Royale Signs plugin instance.
	 */
	private final BRSigns plugin;

	/**
	 * Construct new BattleRoyal 
	 * plugin messaging channel.
	 * <p>
	 * @param plugin the BRSigns plugin instance.
	 */
	public BRPluginChannel(final BRSigns plugin) {
		this.plugin = plugin;
		
		/* register channel */
		Bukkit.getServer().getMessenger().registerIncomingPluginChannel(plugin, MESSAGING_CHANNEL, this);
		Bukkit.getServer().getMessenger().registerOutgoingPluginChannel(plugin, MESSAGING_CHANNEL);
		
		/* register events */
		Bukkit.getPluginManager().registerEvents(this, plugin);
		
		/* on initialize */
		onInitialize();
	}
	
	/**
	 * Check there are online
	 * players, and send get servers ip.
	 */
	private void onInitialize() {
		/* do nothing if there is not any player online */
		if (Bukkit.getOnlinePlayers().isEmpty()) {
			return;
		}
		
		/* get arenas servers ip */
		SchedulerUtil.runTaskLater ( ( ) -> {
			try {
				for (BRArena arena : BRArenaManager.getLoadedArenas()) {
					MessagingUtils.sendPluginMessage(new Written()
							.writeUTF(SERVER_IP_ARGUMENT)
							.writeUTF(arena.getName()));
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}, 60, plugin);
	}
	
	/**
	 * Perform update.
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent eve) {
		SchedulerUtil.runTaskLater ( ( ) -> {
			try {
				/* update the server of the arenas */
				for (BRArena arena : BRArenaManager.getLoadedArenas()) {
					MessagingUtils.sendPluginMessage(new Written()
							.writeUTF(SERVER_IP_ARGUMENT)
							.writeUTF(arena.getName()));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}, 60, plugin);
	}
	
    /**
     * A method that will be thrown when a PluginMessageSource sends a plugin
     * message on a registered channel.
     * <p>
     * @param channel Channel that the message was sent through.
     * @param player Source of the message.
     * @param message The raw message that was sent.
     */
	@Override
	public void onPluginMessageReceived(String channel, Player player, byte[] message) {
		/* read message */
		final Object[] readed  = ReadUtils.read(message);
		if (readed.length == 0) {
			return;
		}
		
		String        argument = (String) readed[0];
		final Object[] reponse = ReadUtils.readReponse(message);
		if (reponse.length == 0) {
			return;
		}
		
		/* do something depending on the argument */
		switch(argument) {
			/* load the Arenas PingServers */
			case SERVER_IP_ARGUMENT:
				if (reponse.length == 3 && reponse[2] instanceof Integer) {
					String server_name = (String) reponse[0];
					String server_ip   = (String) reponse[1];
					int         port   =    (int) reponse[2];
					if (server_name != null) {
						for (BRArena arena : BRArenaManager.getLoadedArenas()) {
							if (arena == null || port != arena.getPort()) {
								continue; 
							}
							
							if (!Objects.equals(server_name, arena.getName())) {
								continue;
							}
							
							arena.setServer(new PingingServer(server_ip, port));
						}
					}
				}
				break;
		}
		
//		/* TEST: Print reponse objects */
//		for (Object repo : reponse) {
//			System.out.println("repo " + (repo != null ? repo.toString() : null));
//		}
	}
}