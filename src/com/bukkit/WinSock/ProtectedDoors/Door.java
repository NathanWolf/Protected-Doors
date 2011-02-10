package com.bukkit.WinSock.ProtectedDoors;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

public class Door {

	private ArrayList<DoorCmd> pendingCommands = new ArrayList<DoorCmd>();
	private final ProtectedDoors plugin;

	Door(final ProtectedDoors plugin) {
		this.plugin = plugin;
	}

	public void addCommand(DoorCmd command) {
		if (getPendingPlayerCommand(command.GetPlayer()) == null) {
			pendingCommands.add(command);
		} else {
			removePendingPlayerCommand(command);
			pendingCommands.add(command);
		}
	}

	public DoorObject getDoorObject(String loc) {
		return plugin.persistence.get(loc, DoorObject.class);
	}

	public DoorCmd getPendingPlayerCommand(Player player) {
		for (DoorCmd data : pendingCommands) {
			if (data.GetPlayer().equals(player)) {
				return data;
			}
		}

		return null;
	}

	public Sign getSign(Block block) {
		World blockWorld = block.getWorld();
		
		Block signBlock = blockWorld.getBlockAt(block.getX() + 1, 
				block.getY(), block.getZ());
		if (signBlock.getType() != Material.WALL_SIGN)
		{
			signBlock = blockWorld.getBlockAt(block.getX() - 1, 
					block.getY(), block.getZ());
		}
		if (signBlock.getType() != Material.WALL_SIGN)
		{
			signBlock = blockWorld.getBlockAt(block.getX(), 
					block.getY(), block.getZ() + 1);
		}
		if (signBlock.getType() != Material.WALL_SIGN)
		{
			signBlock = blockWorld.getBlockAt(block.getX(), 
					block.getY(), block.getZ() - 1);
		}

		BlockState signState = signBlock.getState();
		if (signState instanceof Sign)
		{
			Sign sign = (Sign)signState;
			if (sign.getLine(0).contains("ProtectedDoor"))
			{
				return sign;
			}
		}
		return null;
	}

	public void remove(DoorObject obj) {
		plugin.persistence.remove(obj);
	}

	public void removePendingPlayerCommand(DoorCmd command) {
		pendingCommands.remove(command);
	}

	public void save() {
		plugin.persistence.save();
	}

	public void saveDoorObject(DoorObject obj) {
		plugin.persistence.put(obj);
	}
}
