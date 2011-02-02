package com.bukkit.WinSock.ProtectedDoors;

import org.bukkit.entity.*;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.block.Sign;
import org.bukkit.Material;
import org.bukkit.material.MaterialData;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockDamageEvent;

/**
 * ICSigns block listener
 * @author WinSock
 */
public class DoorBlockListener extends BlockListener {
    private final ProtectedDoors plugin;

    public DoorBlockListener(final ProtectedDoors plugin) {
        this.plugin = plugin;
    }

    //put all Block related code here
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
				World blockWorld = blockBlock.getWorld();
				Block aboveBlock = blockWorld.getBlockAt(blockBlock.getX(), 
						blockBlock.getY() + 1, blockBlock.getZ());
				// For when someone clicks the bottom of the door
				if (aboveBlock.getType() == blockBlock.getType())
				{
					aboveBlock = blockWorld.getBlockAt(blockBlock.getX(), 
							blockBlock.getY() + 2, blockBlock.getZ());
				}
			
				// Cannot not find a better way of doing this
				Block signBlock = blockWorld.getBlockAt(aboveBlock.getX() + 1, 
						aboveBlock.getY(), aboveBlock.getZ());
				if (signBlock.getType() != Material.WALL_SIGN)
    			{
    				signBlock = blockWorld.getBlockAt(aboveBlock.getX() - 1, 
    						aboveBlock.getY(), aboveBlock.getZ());
				}
				if (signBlock.getType() != Material.WALL_SIGN)
				{
					signBlock = blockWorld.getBlockAt(aboveBlock.getX(), 
							aboveBlock.getY(), aboveBlock.getZ() + 1);
				}
				if (signBlock.getType() != Material.WALL_SIGN)
				{
					signBlock = blockWorld.getBlockAt(aboveBlock.getX(), 
							aboveBlock.getY(), aboveBlock.getZ() - 1);
				}
			
				BlockState signState = signBlock.getState();
				MaterialData signData = signState.getData();
				if (signData instanceof org.bukkit.material.Sign)
				{
					Sign sign = (Sign)signState;
					if (sign.getLine(0).contains("ProtectedDoor"))
					{
						for (int i = 0; i < sign.getLines().length; i++)
						{
							// Loop though the lines
							if (sign.getLine(i).contains(event.getPlayer().getDisplayName()))
							{
								// Player on one of the lines allow access
								canKill = true;
							}
							else
							{
								canKill = false;
							}
							// TODO group control when bukkit suports groups
						}
					}
				}
				else
				{
					// Not a Protected Door
					canKill = true;
				}
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
    	LivingEntity trigger = event.getEntity();
    	Player player;
    	if (trigger instanceof Player)
    	{
    		player = (Player)trigger;
    		if (!plugin.readOP().contains(player.getDisplayName()))
    		{
    			if (clickedBlock.getType() == Material.WOODEN_DOOR)
    			{
    				Boolean canOpen = false;
    				World blockWorld = clickedBlock.getWorld();
    		
    				Block aboveBlock = blockWorld.getBlockAt(clickedBlock.getX(), 
    						clickedBlock.getY() + 1, clickedBlock.getZ());
    				// For when someone clicks the bottom of the door
    				if (aboveBlock.getType() == clickedBlock.getType())
    				{
    					aboveBlock = blockWorld.getBlockAt(clickedBlock.getX(), 
    							clickedBlock.getY() + 2, clickedBlock.getZ());
    				}
    		
    				// Cannot not find a better way of doing this
    				Block signBlock = blockWorld.getBlockAt(aboveBlock.getX() + 1, 
    						aboveBlock.getY(), aboveBlock.getZ());
    				if (signBlock.getType() != Material.WALL_SIGN)
    				{
    					signBlock = blockWorld.getBlockAt(aboveBlock.getX() - 1, 
    							aboveBlock.getY(), aboveBlock.getZ());
    				}
    				if (signBlock.getType() != Material.WALL_SIGN)
    				{
    					signBlock = blockWorld.getBlockAt(aboveBlock.getX(), 
    							aboveBlock.getY(), aboveBlock.getZ() + 1);
    				}
    				if (signBlock.getType() != Material.WALL_SIGN)
    				{
    					signBlock = blockWorld.getBlockAt(aboveBlock.getX(), 
    							aboveBlock.getY(), aboveBlock.getZ() - 1);
    				}
    		
    				BlockState signState = signBlock.getState();
    				MaterialData signData = signState.getData();
    				System.out.println(signData.getItemType().name());
    				if (signData instanceof org.bukkit.material.Sign)
    				{
    					Sign sign = (Sign)signState;
    					if (sign.getLine(0).contains("ProtectedDoor"))
    					{
    						for (int i = 0; i < sign.getLines().length; i++)
    						{
    							// Loop though the lines
    							if (sign.getLine(i).contains(player.getDisplayName()))
    							{
    								// Player on one of the lines allow access
    								canOpen = true;
    							}
    							// TODO group control when bukkit suports groups
    						}
    					}
    					else
    					{
    						// Not a Protected Door Sign
    						canOpen = true;
    					}
    				}
    				else
    				{
    					// No sign protecting it so you can open it
    					canOpen = true;
    				}
    				if (!canOpen)
    				{
    					// Person cannot open the door and cancel the event
    					event.setCancelled(true);
    				}
    			}
    		}
    	}
    }
}
