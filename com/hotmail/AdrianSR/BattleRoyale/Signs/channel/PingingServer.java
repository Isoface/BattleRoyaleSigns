package com.hotmail.AdrianSR.BattleRoyale.Signs.channel;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

import org.apache.commons.lang3.StringUtils;

/**
 * Represents a server
 * that updates its status
 * data whenever its ping
 * method is invoked.
 * <p>
 * @author AdrianSR
 */
public class PingingServer {
	
	/**
	 * Default time out for
	 * ping server sockets.
	 */
	public static final int DEFAULT_TIME_OUT = 50;
	
	/**
	 * Minimum time out for
	 * ping server sockets.
	 */
	public static final int MINIMUM_TIME_OUT = 10;
	
	/**
	 * server connection
	 * data.
	 */
	private final String ip;
	private final int  port;

	/**
	 * server status data.
	 */
	private boolean   online;
	private int player_count;
	private int  max_players;
	private String      motd;

	/**
	 * Construct new Pinging
	 * server.
	 * <p>
	 * @param ip   server address.
	 * @param port server port.
	 */
	public PingingServer(String ip, int port) {
		this.ip   = ip;
		this.port = port;
	}

	/**
	 * Returns server
	 * address.
	 * <p>
	 * @return server address.
	 */
	public String getIp() {
		return this.ip;
	}

	/**
	 * Returns server
	 * port.
	 * <p>
	 * @return server port.
	 */
	public int getPort() {
		return this.port;
	}

	/**
	 * Returns false if a PingingServer.ping() 
	 * has never been performed.
	 * <p>
	 * @return true if the server is online.
	 */
	public boolean isOnline() {
		return this.online;
	}

	/**
	 * The count of online players.
	 * <p>
	 * @return count of online players.
	 */
	public int getPlayerCount() {
		return this.player_count;
	}

	/**
	 * The maximum players can be
	 * connected to the server.
	 * <p>
	 * @return server slots.
	 */
	public int getMaxPlayers() {
		return this.max_players;
	}

	/**
	 * The server description
	 * players can see in the
	 * server list.
	 * <p>
	 * @return
	 */
	public String getMotd() {
		return this.motd;
	}
	
	/**
	 * Ping to server using the 
	 * default time out.
	 * <p>
	 * The server status is updated if 
	 * the operation is successfully executed.
	 */
	public void ping() {
		ping(PingingServer.DEFAULT_TIME_OUT);
	}

	/**
	 * Ping to server.
	 * <p>
	 * The server status is updated if 
	 * the operation is successfully executed.
	 * <p>
	 * @param time_out the maximum time waiting for a reponse.
	 */
	public void ping(int time_out) {
		try {
			/* start connection */
			final Socket socket = new Socket();
			socket.setSoTimeout(time_out);
			socket.connect(new InetSocketAddress(getIp(), getPort()), time_out);
			
			/* write to socket */
			OutputStream            output_stream = socket.getOutputStream();
			DataOutputStream   data_output_stream = new DataOutputStream(output_stream);
			InputStream              input_stream = socket.getInputStream();
			InputStreamReader input_stream_reader = new InputStreamReader(input_stream, Charset.forName("UTF-16BE"));
			data_output_stream.write(new byte[] { (byte) 0xFE, (byte) 0x01 });
			
			/* check is failed */
			String   failed_msg = "Premature end of stream!";
			final int packet_id = input_stream.read();
			if (packet_id == -1 || packet_id != 0xFF) {
				if (packet_id != 0xFF) {
					failed_msg = "Invalid packet ID (" + packet_id + ").";
				}
				
				data_output_stream.close();
				output_stream.close();
				input_stream_reader.close();
				input_stream.close();
				socket.close();
				throw new IOException(failed_msg);
			}
			
			final int length = input_stream_reader.read();
			if (length == -1 || length == 0) {
				if (length == 0) {
					failed_msg = "Invalid string length!";
				}
				
				data_output_stream.close();
				output_stream.close();
				input_stream_reader.close();
				input_stream.close();
				socket.close();
				throw new IOException(failed_msg);
			}
			
			char[] chars = new char[length];
			if (input_stream_reader.read(chars, 0, length) != length) {
				data_output_stream.close();
				output_stream.close();
				input_stream_reader.close();
				input_stream.close();
				socket.close();
				throw new IOException(failed_msg);
			}
			
			/* read data */
			final String reponse_string = new String(chars);
			if (reponse_string.startsWith("§")) {
				String[] data     = reponse_string.split("\0");
				this.motd         = data[3];
				this.player_count = Integer.parseInt(data[4]);
				this.max_players  = Integer.parseInt(data[5]);
			} else {
				String[] data     = reponse_string.split("§");
				this.motd         = data[0];
				this.player_count = Integer.parseInt(data[1]);
				this.max_players  = Integer.parseInt(data[2]);
			}
			
			this.online = true; /* server online */
			
			/* close socket and streams */
			data_output_stream.close();
			output_stream.close();
			input_stream_reader.close();
			input_stream.close();
			socket.close();
		}
		/* server offline */
		catch (SocketException exception) {
			this.online = false;
		} catch (IOException exception) {
			this.online = false;
		}
	}
	
	public static final String MOTD_DATA_SPLITER     = ";";
	public static final String MOTD_DATA_START_ARG   = "{";
	public static final String MOTD_DATA_END_ARG     = "}";
	public static final String MOTD_DATA_RUNNING_KEY = "running";
	
	public static Object[] decodeMotdData(String motd) {
		if (StringUtils.isBlank(motd)) {
			return new Object[0];
		}
		
		try {
			if (motd.startsWith(MOTD_DATA_START_ARG) && motd.endsWith(MOTD_DATA_END_ARG)) {
				String motd_data = excludeFirstLast(cleanSpaces(motd)).toLowerCase();
				if (StringUtils.isBlank(motd_data) || !motd_data.contains(MOTD_DATA_SPLITER)) {
					return new Object[0];
				}
				
				String[] data = motd_data.split(MOTD_DATA_SPLITER);
				if (data.length == 4) {
					boolean available = Boolean.parseBoolean(data[0]);
					boolean   running = data[1] != null && data[1].equals(MOTD_DATA_RUNNING_KEY);
					int  players_left = Integer.parseInt(data[2]);
					int   max_players = Integer.parseInt(data[3]);
					return new Object[] { available, running, players_left, max_players };
				}
			}
		} catch(Throwable t) {
			/* ignore */
		}
		return new Object[0];
	}
	
	private static String excludeFirstLast(String string) {
		return ((string != null && string.length() > 2) ? string.substring(1, (string.length() - 1)) : string);
	}
	
	private static String cleanSpaces(String to_clean) {
		return (to_clean != null ? (to_clean.replace(" ", "")) : "");
	}
}
