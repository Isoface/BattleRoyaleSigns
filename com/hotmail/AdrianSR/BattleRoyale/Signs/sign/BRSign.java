package com.hotmail.AdrianSR.BattleRoyale.Signs.sign;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.configuration.ConfigurationSection;

import com.hotmail.AdrianSR.BattleRoyale.Signs.arena.BRArena;
import com.hotmail.AdrianSR.BattleRoyale.Signs.util.ConfigurableLocation;
import com.hotmail.adriansr.core.util.reflection.general.EnumReflection;
import com.hotmail.adriansr.core.util.saveable.Saveable;

public class BRSign implements Saveable {
	
	private final Location   location;
	private final BlockFace direction;
	private final boolean   sign_post;
	private       BRArena     forward;

	public BRSign(Location location, BlockFace direction, boolean sign_post) {
		this.location  = location;
		this.direction = direction;
		this.sign_post = sign_post;
	}
	
	public BRSign(ConfigurationSection section) {
		if (section != null) {
			this.sign_post = section.getBoolean("SignPost");
			if (section.isConfigurationSection("Location")) {
				ConfigurableLocation        loc = ConfigurableLocation.of(section.getConfigurationSection("Location"));
				BlockFace face = EnumReflection.getEnumConstant(BlockFace.class, section.getString("Direction"));
				if (loc != null && loc.isValid ( ) && face != null) {
					this.location  = loc;
					this.direction = face;
					return;
				}
			}
		} else {
			this.sign_post = false;
		}
		
		this.location  = null;
		this.direction = null;
	}
	
	public Location getLocation() {
		return location;
	}
	
	public BlockFace getDirection() {
		return direction;
	}
	
	public Block getBlock() {
		return isValid() ? location.getBlock() : null;
	}
	
	public Sign getSign() {
		if (getBlock() == null) {
			return null;
		}
		
		if (getBlock().getType() != Material.WALL_SIGN || getBlock().getType() != Material.SIGN_POST) {
			getBlock().setType(sign_post ? Material.SIGN_POST : Material.WALL_SIGN);
			BlockState                  state = getBlock().getState();
			org.bukkit.material.Sign mat_sign = new org.bukkit.material.Sign(sign_post ? Material.SIGN_POST : Material.WALL_SIGN);
			if (getDirection() != null) {
				mat_sign.setFacingDirection(getDirection());
			}
			
			state.setData(mat_sign);
			state.update();
		}
		return (getBlock().getState() instanceof Sign) ? (Sign) getBlock().getState() : null;
	}
	
	public boolean isValid() {
		return getLocation() != null && getDirection() != null;
	}
	
	public BRArena getForward() {
		return forward;
	}
	
	public void setForward(BRArena forward) {
		this.forward = forward;
	}

	@Override
	public int save(ConfigurationSection section) {
		section.set("SignPost", sign_post);
		
		if (direction != null) {
			section.set("Direction", direction.name());
		}

		if (location != null) {
			new ConfigurableLocation(location).save(section.createSection("Location"));
		}
		return 1;
	}
}
