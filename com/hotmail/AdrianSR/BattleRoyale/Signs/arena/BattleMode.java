package com.hotmail.AdrianSR.BattleRoyale.Signs.arena;

import java.util.Map;

import com.google.common.collect.Maps;

/**
 * Represents the various type of battle royale game modes.
 * 
 * @author AdrianSR
 */
public enum BattleMode {

	/**
	 * Solo mode, represents a Everybody VS Everybody mode.
	 */
	SOLO(0),

	/**
	 * Squad mode, the players can select his squad mate.
	 */
	SQUAD(1),

	/**
	 * Team mode, the players can join a team.
	 */
	TEAM(2);

	/**
	 * Global class values.
	 */
	private final static Map<Integer, BattleMode> BY_ID = Maps.newHashMap();

	/**
	 * Class value.
	 */
	private final int value;

	/**
	 * Construct a new Battle Mode.
	 * <p>
	 * @param value the value to get by ID.
	 */
	BattleMode(final int value) {
		this.value = value;
	}

	/**
	 * Gets the mode value associated with this BattleMode
	 * <p>
	 * @return An integer value of this battlemode
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Gets the BattleMode represented by the specified value.
	 *<p>
	 * @param value Value to check
	 * @return Associative {@link BattleMode} with the 
	 * given value, or null if it
	 * doesn't exist
	 */
	public static BattleMode getByValue(final int value) {
		return BY_ID.get(value);
	}

	/**
	 * Build "BY_ID" map.
	 */
	static {
		for (BattleMode mode : values()) {
			BY_ID.put(mode.getValue(), mode);
		}
	}
}
