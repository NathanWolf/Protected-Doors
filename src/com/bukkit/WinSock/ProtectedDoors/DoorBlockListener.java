package com.bukkit.WinSock.ProtectedDoors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.nijikokun.bukkit.iConomy.iConomy;

/**
 * ICSigns block listener
 * 
 * @author WinSock
 */
public class DoorBlockListener extends BlockListener {
	private final ProtectedDoors plugin;

	public DoorBlockListener(final ProtectedDoors plugin) {
		this.plugin = plugin;
	}

	private Boolean checkCost(Player player, Sign sign, DoorObject obj) {
		if (sign.getLine(1).contains("Cost:")) {
			int cost = 0;
			try {
				cost = Integer.parseInt(sign.getLine(2).trim());
				int playerMoney = iConomy.db.get_balance(player
						.getDisplayName());
				int ownerMoney = iConomy.db.get_balance(obj.getCreator());
				if (playerMoney >= cost) {
					iConomy.db.set_balance(player.getDisplayName(), playerMoney
							- cost);
					iConomy.db.set_balance(obj.getCreator(), ownerMoney + cost);
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				// invaid cost
				return false;
			}
		}
		// No cost
		return true;
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		if (event.getDamageLevel() == BlockDamageLevel.BROKEN) {
			Boolean canKill = false;
			Block blockBlock = event.getBlock();
			if (blockBlock.getType() == Material.WOODEN_DOOR) {
				World blockWorld = blockBlock.getWorld();
				Block DoorBlock = blockWorld.getBlockAt(blockBlock.getX(),
						blockBlock.getY() + 1, blockBlock.getZ());
				// For when someone clicks the bottom of the door
				if (DoorBlock.getType() != blockBlock.getType()) {
					DoorBlock = blockWorld.getBlockAt(blockBlock.getX(),
							blockBlock.getY(), blockBlock.getZ());
				}
				
				String loc = Messages.getString("DoorBlockListener.3");
				loc += String.valueOf(DoorBlock.getX())
						+ Messages.getString("DoorBlockListener.2");
				loc += String.valueOf(DoorBlock.getY())
						+ Messages.getString("DoorBlockListener.2");
				loc += String.valueOf(DoorBlock.getZ());
				DoorObject DoorObj = plugin.doorHandler.getDoorObject(loc);

				if (DoorObj != null) {
					if (DoorObj.canRemove(player, plugin)) {
						canKill = true;
						plugin.doorHandler.remove(DoorObj);
						plugin.doorHandler.save();
					}
				}
			} else {
				// Other Block
				canKill = true;
			}
			if (!canKill) {
				event.setCancelled(true);
			}
		}
	}

	@Override
	public void onBlockInteract(BlockInteractEvent event) {
		Block clickedBlock = event.getBlock();
		if (clickedBlock.getType() == Material.WOODEN_DOOR) {
			LivingEntity trigger = event.getEntity();
			Player player;

			World blockWorld = clickedBlock.getWorld();
			Block DoorBlock = blockWorld.getBlockAt(clickedBlock.getX(),
					clickedBlock.getY() + 1, clickedBlock.getZ());
			// For when someone clicks the bottom of the door
			if (DoorBlock.getType() != clickedBlock.getType()) {
				DoorBlock = blockWorld.getBlockAt(clickedBlock.getX(),
						clickedBlock.getY(), clickedBlock.getZ());
			}
			
			Block aboveBlock = blockWorld.getBlockAt(DoorBlock.getX(), DoorBlock.getY() + 1, DoorBlock.getZ());

			String loc = Messages.getString("DoorBlockListener.3");
			loc += String.valueOf(DoorBlock.getX())
					+ Messages.getString("DoorBlockListener.2");
			loc += String.valueOf(DoorBlock.getY())
					+ Messages.getString("DoorBlockListener.2");
			loc += String.valueOf(DoorBlock.getZ());
			DoorObject DoorObj = plugin.doorHandler.getDoorObject(loc);

			if (DoorObj != null) {
				if (trigger instanceof Player) {
					player = (Player) trigger;
					Boolean canOpen = false;
					DoorCmd pendingCmd = plugin.doorHandler
							.getPendingPlayerCommand(player);
					if (pendingCmd != null) {
						switch (pendingCmd.GetCommand()) {
						case CREATE:
							player.sendMessage(Messages
									.getString("DoorBlockListener.7")); //$NON-NLS-1$
							plugin.doorHandler
									.removePendingPlayerCommand(pendingCmd);
							break;
						case ADD:
							if (DoorObj.canModify(player, plugin)) //$NON-NLS-1$
							{
								List<String> temp = new ArrayList<String>(
										Arrays.asList(pendingCmd.GetArgs()));
								DoorObj.addUsers(temp);
								plugin.doorHandler.saveDoorObject(DoorObj);
								plugin.doorHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.9")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.10")); //$NON-NLS-1$
							}
							break;
						case ADDG:
							if (DoorObj.canModify(player, plugin)) //$NON-NLS-1$
							{
								List<String> temp = new ArrayList<String>(
										Arrays.asList(pendingCmd.GetArgs()));
								DoorObj.addGroups(temp);
								plugin.doorHandler.saveDoorObject(DoorObj);
								plugin.doorHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.12")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.13")); //$NON-NLS-1$
							}
							break;
						case REMOVE:
							if (DoorObj.canModify(player, plugin)) //$NON-NLS-1$
							{
								List<String> temp = new ArrayList<String>(
										Arrays.asList(pendingCmd.GetArgs()));
								DoorObj.removeUsers(temp);
								plugin.doorHandler.saveDoorObject(DoorObj);
								plugin.doorHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.15")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.16")); //$NON-NLS-1$
							}
							break;
						case REMOVEG:
							if (DoorObj.canModify(player, plugin)) //$NON-NLS-1$
							{
								List<String> temp = new ArrayList<String>(
										Arrays.asList(pendingCmd.GetArgs()));
								DoorObj.removeGroups(temp);
								plugin.doorHandler.saveDoorObject(DoorObj);
								plugin.doorHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.18")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.19")); //$NON-NLS-1$
							}
							break;
						case DELETE:
							if (DoorObj.canRemove(player, plugin)) //$NON-NLS-1$
							{
								plugin.doorHandler.remove(DoorObj);
								plugin.doorHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.21")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.22")); //$NON-NLS-1$
							}
							break;
						}
						plugin.doorHandler
								.removePendingPlayerCommand(pendingCmd);
					} else {
						if (!DoorObj.canOpen(player, plugin)) //$NON-NLS-1$
						{
							if (plugin.useiConomy) {
								
								Sign sign = plugin.doorHandler
										.getSign(aboveBlock);

								if (sign != null) {
									canOpen = checkCost(player, sign, DoorObj);
								} else {
									// No sign protecting it so you can open
									// it
									canOpen = true;
								}
							}
						} else {
							canOpen = true;
						}
						if (!canOpen) {
							// Person cannot open the door and cancel the event
							event.setCancelled(true);
						}
					}
				}
			} else {
				if (trigger instanceof Player) {
					player = (Player) trigger;
					Sign sign = plugin.doorHandler.getSign(aboveBlock);
					if (sign != null)
					{
						// Old style sign
						for (int i = 0; i < sign.getLines().length; i++)
    					{
    						if (plugin.useiPermissions)
    						{
    							// Loop though the lines
    							if (!sign.getLine(i).contains(player.getDisplayName()) || 
	    								!ProtectedDoors.Permissions.has(player, "pdoors.mod"))
	    						{
    								event.setCancelled(true);
	    						}
    						}
    						else
    						{
    							// Loop though the lines
    							if (!sign.getLine(i).contains(player.getDisplayName()) || 
	    								!plugin.readOP().contains(player))
	    						{
    								event.setCancelled(true);
	    						}
    						}
    					}
					}
					else
					{
					boolean canCreate = false;
					if (plugin.useiPermissions) {
						if (ProtectedDoors.Permissions.has(player,
								"pdoors.main")) {
							canCreate = true;
						}
					} else {
						canCreate = true;
					}

					if (canCreate) {
						DoorCmd pendingCmd = plugin.doorHandler
								.getPendingPlayerCommand(player);
						if (pendingCmd != null) {
							switch (pendingCmd.GetCommand()) {
							case CREATE:
								DoorObject temp = new DoorObject();
								temp.setCreator(player.getDisplayName());
								temp.setLocation(loc);
								if (plugin.doorHandler.getDoorObject(loc) == null) {
									plugin.doorHandler.saveDoorObject(temp);
									plugin.doorHandler.save();
									player.sendMessage(Messages
											.getString("DoorBlockListener.28")); //$NON-NLS-1$
								} else {
									player.sendMessage(Messages
											.getString("DoorBlockListener.29")); //$NON-NLS-1$
								}
								break;
							}
							plugin.doorHandler
									.removePendingPlayerCommand(pendingCmd);
							event.setCancelled(true);
						}
					}
					}
				}
			}
		}
	}

	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		Block eventBlock = event.getBlockPlaced();
		Block onBlock = event.getBlock();
		if (eventBlock.getType() == Material.REDSTONE_WIRE
				|| eventBlock.getType() == Material.REDSTONE_TORCH_OFF
				|| eventBlock.getType() == Material.REDSTONE_TORCH_ON
				|| eventBlock.getType() == Material.WOOD_PLATE
				|| eventBlock.getType() == Material.STONE_PLATE
				|| eventBlock.getType() == Material.STONE_BUTTON
				|| eventBlock.getType() == Material.LEVER) {
			List<DoorObject> doorObjects = new ArrayList<DoorObject>();
			plugin.persistence.getAll(doorObjects, DoorObject.class);

			if (doorObjects.size() > 0) {
				BlockVector min = new BlockVector();
				min.setX(onBlock.getX() - 1);
				min.setZ(onBlock.getZ() - 1);
				min.setY(onBlock.getY() - 2);
				BlockVector max = new BlockVector();
				max.setX(onBlock.getX() + 1);
				max.setZ(onBlock.getZ() + 1);
				max.setY(onBlock.getY() + 2);

				for (DoorObject obj : doorObjects) {
					String loc = obj.getLocation();
					String[] locd = loc.split(Messages
							.getString("DoorBlockListener.2")); //$NON-NLS-1$
					Vector pt = new Vector();
					pt.setX(Integer.parseInt(locd[0]));
					pt.setY(Integer.parseInt(locd[1]));
					pt.setZ(Integer.parseInt(locd[2]));

					int x = pt.getBlockX();
					int y = pt.getBlockY();
					int z = pt.getBlockZ();
					if (x >= min.getBlockX() && x <= max.getBlockX()
							&& y >= min.getBlockY() && y <= max.getBlockY()
							&& z >= min.getBlockZ() && z <= max.getBlockZ()) {
						event.setCancelled(true);
					}
				}
			}
		}
	}
}
