package com.bukkit.WinSock.ProtectedDoors;

import java.util.Arrays;

import org.bukkit.entity.*;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.Material;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockDamageEvent;

import com.nijikokun.bukkit.iConomy.iConomy;

import com.bukkit.WinSock.ProtectedDoors.DoorObject;
import com.bukkit.WinSock.ProtectedDoors.DoorLocation;

/**
 * ICSigns block listener
 * @author WinSock
 */
public class DoorBlockListener extends BlockListener {
    private final ProtectedDoors plugin;

    public DoorBlockListener(final ProtectedDoors plugin) {
        this.plugin = plugin;
    }
    
    private Boolean checkCost(Player player, Sign sign)
    {
    	if (sign.getLine(0).contains("ProtectedDoor") && sign.getLine(1).contains("Cost:"))
		{
			int cost = 0;
			try
			{
				cost = Integer.parseInt(sign.getLine(2).trim());
				int playerMoney = iConomy.db.get_balance(player.getDisplayName());
				if (playerMoney >= cost)
				{
					iConomy.db.set_balance(player.getDisplayName(), playerMoney - cost);
					return true;
				}
				else
				{
					return false;
				}
			}
			catch(Exception e)
			{
				// invaid cost
				return true;
			}
		}
    	// No cost
    	return true;
    }

    //put all Block related code here
    @Override
    public void onBlockRedstoneChange(BlockFromToEvent event) {
    	Block changedBlock = event.getBlock();
    	
    	if (changedBlock.getType() == Material.WOODEN_DOOR)
    	{
    		World blockWorld = changedBlock.getWorld();
			Block DoorBlock = blockWorld.getBlockAt(changedBlock.getX(), 
					changedBlock.getY() + 1, changedBlock.getZ());
			// For when someone clicks the bottom of the door
			if (DoorBlock.getType() != changedBlock.getType())
			{
				DoorBlock = blockWorld.getBlockAt(changedBlock.getX(), 
						changedBlock.getY(), changedBlock.getZ());
			}
			
			DoorLocation loc = new DoorLocation();
			loc.setX(DoorBlock.getX());
			loc.setY(DoorBlock.getY());
			loc.setZ(DoorBlock.getZ());
			DoorObject DoorObj = plugin.doorHandler.getDoorObject(loc);
			
			if (DoorObj != null)
			{
				event.setCancelled(true);
			}
    	}
    }
    
    @Override
    public void onBlockDamage(BlockDamageEvent event)
    {
    	if (event.getDamageLevel() == BlockDamageLevel.BROKEN && 
    			!plugin.readOP().contains(event.getPlayer().getDisplayName()))
    	{
    		Boolean canKill = false;
    		Block blockBlock = event.getBlock();
			if (blockBlock.getType() == Material.WOODEN_DOOR)
			{
				// TODO Add protection
			}
			else
			{
				// Other Block
				canKill = true;
			}
			if (!canKill)
			{
				event.setCancelled(true);
			}
    	}
    }
    
    @Override
    public void onBlockInteract(BlockInteractEvent event)
    {
    	Block clickedBlock = event.getBlock();
		if(clickedBlock.getType() == Material.WOODEN_DOOR)
		{
    		LivingEntity trigger = event.getEntity();
    		Player player;
    	
    		World blockWorld = clickedBlock.getWorld();
    		Block DoorBlock = blockWorld.getBlockAt(clickedBlock.getX(), 
					clickedBlock.getY() + 1, clickedBlock.getZ());
			// For when someone clicks the bottom of the door
			if (DoorBlock.getType() != clickedBlock.getType())
			{
				DoorBlock = blockWorld.getBlockAt(clickedBlock.getX(), 
						clickedBlock.getY(), clickedBlock.getZ());
			}
    	
			DoorLocation loc = new DoorLocation();
			loc.setX(DoorBlock.getX());
			loc.setY(DoorBlock.getY());
			loc.setZ(DoorBlock.getZ());
			DoorObject DoorObj = plugin.doorHandler.getDoorObject(loc);
		
			if(DoorObj != null)
			{
				if (trigger instanceof Player)
    			{
    				Boolean canOpen = false;
    				player = (Player)trigger;
    				if (plugin.useiPermissions)
    				{
    					if (!ProtectedDoors.Permissions.has(player, "pdoors.admin"))
    					{

    						if (plugin.useiConomy)
    						{
    							Block aboveBlock = blockWorld.getBlockAt(clickedBlock.getX(), 
    									clickedBlock.getY() + 1, clickedBlock.getZ());
    							// For when someone clicks the bottom of the door
    							if (aboveBlock.getType() == clickedBlock.getType())
    							{
    								aboveBlock = blockWorld.getBlockAt(clickedBlock.getX(), 
    										clickedBlock.getY() + 2, clickedBlock.getZ());
    							}
    				
    							Sign sign = plugin.doorHandler.getSign(aboveBlock);
    				
    							if (sign != null)
    							{
    								canOpen = checkCost(player, sign);
    							}
    							else
    							{
    								// No sign protecting it so you can open it
    								canOpen = true;
    							}
    						}
    					}
    				}
    				else
    				{
    					if (!plugin.readOP().contains(player.getDisplayName()))
    					{
    						if (clickedBlock.getType() == Material.WOODEN_DOOR)
    						{
    							if (plugin.useiConomy)
    							{
    								Block aboveBlock = blockWorld.getBlockAt(clickedBlock.getX(), 
    										clickedBlock.getY() + 1, clickedBlock.getZ());
    								// For when someone clicks the bottom of the door
    								if (aboveBlock.getType() == clickedBlock.getType())
    								{
    									aboveBlock = blockWorld.getBlockAt(clickedBlock.getX(), 
    											clickedBlock.getY() + 2, clickedBlock.getZ());
    								}
    				
    								Sign sign = plugin.doorHandler.getSign(aboveBlock);
    				
    								if (sign != null)
    								{
    									canOpen = checkCost(player, sign);
    								}
    								else
    								{
    									// No cost so open
    									canOpen = true;
    								}
    							}
    						}
    					}		
    				}
    				if (!canOpen)
    				{
    					// Person cannot open the door and cancel the event
    					event.setCancelled(true);
    				}
    			}
			}
			else
			{
				System.out.println("Not a protected Door!!");
				if (trigger instanceof Player)
    			{
    				player = (Player)trigger;
    				DoorCmd pendingCmd = plugin.doorHandler.getPendingPlayerCommand(player);
    				if (pendingCmd != null)
    				{
    					System.out.println("Player has pending Command!");
    					switch (pendingCmd.GetCommand())
    					{
    						case CREATE:
    							DoorObject temp = new DoorObject();
    							temp.setUsers(Arrays.asList(player.getDisplayName()));
    							temp.setLocation(loc);
    							plugin.doorHandler.saveDoorObject(temp);
    							player.sendMessage("Protected Door Created!");
    							plugin.doorHandler.removePendingPlayerCommand(pendingCmd);
    							break;
    					}
    					event.setCancelled(true);
    				}
    			}
			}
		}
    }
}
