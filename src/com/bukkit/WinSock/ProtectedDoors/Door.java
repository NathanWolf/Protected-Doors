package com.bukkit.WinSock.ProtectedDoors;

import com.bukkit.WinSock.ProtectedDoors.DoorCmd;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;

import com.bukkit.WinSock.ProtectedDoors.DoorObject;

public class Door {
	
	private ArrayList<DoorCmd> pendingCommands = new ArrayList<DoorCmd>();
	private final ProtectedDoors plugin;
	
	Door(final ProtectedDoors plugin)
	{
		this.plugin = plugin;
	}
	
	public void addCommand(DoorCmd command)
	{
		if (getPendingPlayerCommand(command.GetPlayer()) == null)
		{
			pendingCommands.add(command);
		}
		else
		{
			removePendingPlayerCommand(command);
			pendingCommands.add(command);
		}
	}
	
	public DoorCmd getPendingPlayerCommand(Player player)
	{
		for (DoorCmd data : pendingCommands)
		{
			if (data.GetPlayer().equals(player))
			{
				return data;
			}
		}
		
		return null;
	}
	
	public void removePendingPlayerCommand(DoorCmd command)
	{
		pendingCommands.remove(command);
	}
	
	public void saveDoorObject(DoorObject obj)
	{
		plugin.persistence.put(obj);
		plugin.persistence.save();
	}
	
	public void save()
	{
		plugin.persistence.save();
	}
	
	public DoorObject getDoorObject(String loc)
	{
		return plugin.persistence.get(loc, DoorObject.class);
	}
	
	public Sign getSign(Block block)
	{
		World blockWorld = block.getWorld();
		// Cannot not find a better way of doing this
		Block signBlock = blockWorld.getBlockAt(block.getX() + 1, 
				block.getY(), block.getZ());
		BlockState signState = signBlock.getState();
		MaterialData signData = signState.getData();
		if (signData instanceof org.bukkit.material.Sign)
		{
			Sign sign = (Sign)signState;
			if (sign.getLines()[0].equalsIgnoreCase("ProtectedDoors"))
			{
				return sign;
			}
		}
		if (signBlock.getType() != Material.WALL_SIGN)
		{
			signBlock = blockWorld.getBlockAt(block.getX() - 1, 
					block.getY(), block.getZ());
			signState = signBlock.getState();
			signData = signState.getData();
			if (signData instanceof org.bukkit.material.Sign)
			{
				Sign sign = (Sign)signState;
				if (sign.getLines()[0].equalsIgnoreCase("ProtectedDoors"))
				{
					return sign;
				}
			}
		}
		if (signBlock.getType() != Material.WALL_SIGN)
		{
			signBlock = blockWorld.getBlockAt(block.getX(), 
					block.getY(), block.getZ() + 1);
			signState = signBlock.getState();
			signData = signState.getData();
			if (signData instanceof org.bukkit.material.Sign)
			{
				Sign sign = (Sign)signState;
				if (sign.getLines()[0].equalsIgnoreCase("ProtectedDoors"))
				{
					return sign;
				}
			}
		}
		if (signBlock.getType() != Material.WALL_SIGN)
		{
			signBlock = blockWorld.getBlockAt(block.getX(), 
					block.getY(), block.getZ() - 1);
			signState = signBlock.getState();
			signData = signState.getData();
			if (signData instanceof org.bukkit.material.Sign)
			{
				Sign sign = (Sign)signState;
				if (sign.getLines()[0].equalsIgnoreCase("ProtectedDoors"))
				{
					return sign;
				}
			}
		}
		
		return null;
	}
}
