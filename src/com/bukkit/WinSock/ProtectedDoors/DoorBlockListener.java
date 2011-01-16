package com.bukkit.WinSock.ProtectedDoors;

import java.util.ArrayList;
import org.bukkit.entity.*;
import org.bukkit.World;
import org.bukkit.block.*;
import org.bukkit.Material;
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
    	Boolean canKill = false;
    	Block blockBlock = event.getBlock();
    	BlockState blockState = blockBlock.getState();
		if (blockState instanceof Sign)
		{
			Sign sign = (Sign)blockState;
			if (sign.getLine(0).contains("ProtectedDoor"))
			{
				for (int i = 0; i < sign.getLines().length; i++)
				{
					// Loop though the lines
					if (sign.getLine(i).contains(event.getPlayer().getDisplayName()) || 
							plugin.readOP().contains(event.getPlayer().getDisplayName()))
					{
						// Player on one of the lines allow kill
						canKill = true;
					}
					else
					{
						canKill = false;
					}
					// TODO group control when bukkit suports groups
				}
			}
			else
			{
				// Not a Protected Sign
				canKill = true;
			}
		}
		else if (blockState.getType() == Material.WOODEN_DOOR)
		{
			World blockWorld = blockState.getWorld();
			Block aboveBlock = blockWorld.getBlockAt(blockState.getX(), 
					blockState.getY() + 1, blockState.getZ());
			// For when someone clicks the bottom of the door
			if (aboveBlock.getType() == blockState.getType())
			{
				aboveBlock = blockWorld.getBlockAt(blockState.getX(), 
						blockState.getY() + 2, blockState.getZ());
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
			if (signState instanceof Sign)
			{
				Sign sign = (Sign)signState;
				if (sign.getLine(0).contains("ProtectedDoor"))
				{
					for (int i = 0; i < sign.getLines().length; i++)
					{
						// Loop though the lines
						if (sign.getLine(i).contains(event.getPlayer().getDisplayName()) || 
								plugin.readOP().contains(event.getPlayer().getDisplayName()))
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
		else
		{
			World blockWorld = blockState.getWorld();
			Block aboveBlock = blockWorld.getBlockAt(blockState.getX(), 
					blockState.getY() + 1, blockState.getZ());
			// For when someone clicks the bottom of the door
			if (aboveBlock.getType() == blockState.getType())
			{
				aboveBlock = blockWorld.getBlockAt(blockState.getX(), 
						blockState.getY() + 2, blockState.getZ());
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
			if (signState instanceof Sign)
			{
				Sign sign = (Sign)signState;
				if (sign.getLine(0).contains("ProtectedDoor"))
				{
					for (int i = 0; i < sign.getLines().length; i++)
					{
						// Loop though the lines
						if (sign.getLine(i).contains(event.getPlayer().getDisplayName()) || 
								plugin.readOP().contains(event.getPlayer().getDisplayName()))
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
				// Not a sign on the block
				canKill = true;
			}
		}
		if (!canKill)
		{
			event.setCancelled(true);
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
    	
    	
    		if (clickedBlock.getType() == Material.IRON_DOOR || clickedBlock.getType() == Material.WOODEN_DOOR)
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
    			if (signState instanceof Sign)
    			{
    				Sign sign = (Sign)signState;
    				if (sign.getLine(0).contains("ProtectedDoor"))
    				{
    					for (int i = 0; i < sign.getLines().length; i++)
    					{
    						// Loop though the lines
    						if (sign.getLine(i).contains(player.getDisplayName()) || 
    								plugin.readOP().contains(player))
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
