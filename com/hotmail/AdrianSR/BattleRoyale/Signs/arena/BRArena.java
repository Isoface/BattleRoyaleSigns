package com.hotmail.AdrianSR.BattleRoyale.Signs.arena;

import com.hotmail.AdrianSR.BattleRoyale.Signs.channel.PingingServer;

public class BRArena {
	
	private final String          name;
	private final int             port;
	private       PingingServer server;
	
//	private boolean running;

	public BRArena(String name, int port) {
		this.name = name;
		this.port = port;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPort() {
		return port;
	}
	
	public PingingServer getServer() {
		return server;
	}
	
	public void setServer(PingingServer server) {
		this.server = server;
	}
	
	public boolean hasServer() {
		return server != null;
	}
	
	public boolean isAvailable() {
		return isOnline() && !isFull() && !isRunning();
	}
	
	public boolean isOnline() {
		return hasServer() && server.isOnline();
	}
	
	public boolean isFull() {
		if (hasServer()) {
			return !(server.getPlayerCount() < server.getMaxPlayers());
		}
		return false;
	}
	
	public boolean isWaiting() {
		return isOnline() && !isRunning();
	}
	
	public boolean isRunning() {
		if (hasServer()) {
			try {
				Object[] data = PingingServer.decodeMotdData(server.getMotd());
				if (data.length == 4) {
					return (boolean) data[1];
				}
			} catch(Throwable t) {
				/* ignore */
			}
		}
		return false;
	}
	
	public int getPlayersLeft() {
		if (isRunning()) {
			try {
				Object[] data = PingingServer.decodeMotdData(server.getMotd());
				if (data.length == 4) {
					return (int) data[2];
				}
			} catch(Throwable t) {
				/* ignore */
			}
		}
		return -1;
	}
	
	public int getMaxPlayers() {
		if (isRunning()) {
			try {
				Object[] data = PingingServer.decodeMotdData(server.getMotd());
				if (data.length == 4) {
					return (int) data[3];
				}
			} catch(Throwable t) {
				/* ignore */
			}
		}
		return -1;
	}
}