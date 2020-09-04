package com.hotmail.AdrianSR.BattleRoyale.Signs.util;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.hotmail.AdrianSR.BattleRoyale.Signs.main.BRSigns;
import com.hotmail.adriansr.core.plugin.Plugin;

public class MessagingUtils {

	/**
	 * Messaging Utils variable.
	 */
	public  static final String    MESSAGING_CHANNEL = "BungeeCord";
	private static       Plugin CHANNEL_READER = null;

	/**
	 * Check channel reader instance.
	 */
	static {
		CHANNEL_READER = BRSigns.getInstance();
	}
	
	/**
	 * Send plugin message using the channel 'BungeeCord'.
	 * <p>
	 * 
	 * @param arguments
	 *            the arguments to send.
	 * @throws IOException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void sendPluginMessage(Writable... arguments) throws IOException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		/* donnot send empty arguments */
		if (arguments == null || arguments.length == 0) {
			return;
		}

		/* make streams and write arguments */
		final ByteArrayOutputStream array_stream = new ByteArrayOutputStream();
		final DataOutputStream        out_stream = new DataOutputStream(array_stream);
		for (Writable argument : arguments) {
			if (argument != null && argument.getObjectToWrite() != null && argument.getWriteType() != null) {
				argument.writeTo(out_stream);
			}
		}

		/* send */
		Bukkit.getServer().sendPluginMessage(CHANNEL_READER, MESSAGING_CHANNEL, array_stream.toByteArray());
	}
	
	/**
	 * Send plugin message using the channel 'BungeeCord'.
	 * <p>
	 * @param written arguments from {@link Written}
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static void sendPluginMessage(Written written) throws IOException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		/* donnot send empty arguments */
		if (written == null || written.getWritables().isEmpty()) {
			return;
		}

		/* make streams and write arguments */
		final ByteArrayOutputStream array_stream = new ByteArrayOutputStream();
		final DataOutputStream        out_stream = new DataOutputStream(array_stream);
		for (Writable argument : written.getWritables()) {
			if (argument != null && argument.getObjectToWrite() != null && argument.getWriteType() != null) {
				argument.writeTo(out_stream);
			}
		}

		/* send */
		Bukkit.getServer().sendPluginMessage(CHANNEL_READER, MESSAGING_CHANNEL, array_stream.toByteArray());
	}
	
	/**
	 * Send plugin message using the channel 'BungeeCord' to a specific
	 * {@link Player}.
	 * <p>
	 * 
	 * @param the
	 *            player messenger.
	 * @param arguments
	 *            the arguments to send.
	 * @throws IOException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	public static void sendPluginMessage(Player player, Writable... arguments)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		/* donnot send empty arguments */
		if (player == null || arguments == null || arguments.length == 0) {
			return;
		}

		/* make streams and write arguments */
		final ByteArrayOutputStream array_stream = new ByteArrayOutputStream();
		final DataOutputStream        out_stream = new DataOutputStream(array_stream);
		for (Writable argument : arguments) {
			if (argument != null && argument.getObjectToWrite() != null && argument.getWriteType() != null) {
				argument.writeTo(out_stream);
			}
		}

		/* send */
		player.sendPluginMessage(CHANNEL_READER, MESSAGING_CHANNEL, array_stream.toByteArray());
	}
	
	/**
	 * Send plugin message using the channel 'BungeeCord' to a specific {@link Player}.
	 * <p>
	 * @param the
	 *            player messenger.
	 * @param arguments
	 *            the arguments to send.
	 * @throws IOException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 * @throws InvocationTargetException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 */
	public static void sendPluginMessage(Player player, Written written)
			throws IOException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
			NoSuchMethodException, SecurityException {
		/* donnot send empty arguments */
		if (player == null || written == null || written.getWritables().isEmpty()) {
			return;
		}

		/* make streams and write arguments */
		final ByteArrayOutputStream array_stream = new ByteArrayOutputStream();
		final DataOutputStream out_stream = new DataOutputStream(array_stream);
		for (Writable argument : written.getWritables()) {
			if (argument != null && argument.getObjectToWrite() != null && argument.getWriteType() != null) {
				argument.writeTo(out_stream);
			}
		}

		/* send */
		player.sendPluginMessage(CHANNEL_READER, MESSAGING_CHANNEL, array_stream.toByteArray());
	}
}