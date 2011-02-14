package com.bukkit.WinSock.Protected.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockDamageLevel;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockInteractEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.bukkit.WinSock.Protected.ProtectedCmd;
import com.bukkit.WinSock.Protected.ProtectedLocation;
import com.bukkit.WinSock.Protected.ProtectedObject;
import com.bukkit.WinSock.Protected.Messages;
import com.bukkit.WinSock.Protected.ProtectedPlugin;

/**
 * ICSigns block listener
 * 
 * @author WinSock
 */
public class ProtectedBlockListener extends BlockListener {
	private final ProtectedPlugin plugin;

	public ProtectedBlockListener(final ProtectedPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		if (event.getDamageLevel().equals(BlockDamageLevel.BROKEN)) {
			Boolean canKill = false;
			Block blockBlock = event.getBlock();
			if (blockBlock.getType().equals(Material.WOODEN_DOOR)) {
				World blockWorld = blockBlock.getWorld();
				Block DoorBlock = blockWorld.getBlockAt(blockBlock.getX(),
						blockBlock.getY() + 1, blockBlock.getZ());
				// For when someone clicks the bottom of the door
				if (DoorBlock.getType() != blockBlock.getType()) {
					DoorBlock = blockWorld.getBlockAt(blockBlock.getX(),
							blockBlock.getY(), blockBlock.getZ());
				}

				ProtectedLocation loc = new ProtectedLocation(DoorBlock.getLocation());
				ProtectedObject pObj = plugin.protectedHandler.getObject(loc);

				if (pObj != null) {
					if (pObj.canRemove(player, plugin)) {
						canKill = true;
						plugin.protectedHandler.remove(pObj);
						plugin.protectedHandler.save();
					}
				} else {
					// Not a protected door
					canKill = true;
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
		if (clickedBlock.getType().equals(Material.WOODEN_DOOR)) {
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

			Block aboveBlock = blockWorld.getBlockAt(DoorBlock.getX(),
					DoorBlock.getY() + 1, DoorBlock.getZ());

			ProtectedLocation loc = new ProtectedLocation(DoorBlock.getLocation());
			ProtectedObject pObj = plugin.protectedHandler.getObject(loc);

			if (pObj != null) {
				if (trigger instanceof Player) {
					player = (Player) trigger;
					boolean canOpen = false;
					ProtectedCmd pendingCmd = plugin.protectedHandler
							.getPendingPlayerCommand(player);
					if (pendingCmd != null) {
						switch (pendingCmd.GetCommand()) {
						case CREATE:
							player.sendMessage(Messages
									.getString("DoorBlockListener.7")); //$NON-NLS-1$
							break;
						case ADD:
							if (pObj.canModify(player, plugin)) //$NON-NLS-1$
							{
								List<String> temp = new ArrayList<String>(
										Arrays.asList(pendingCmd.GetArgs()));
								pObj.addUsers(temp);
								plugin.protectedHandler.saveObject(pObj);
								plugin.protectedHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.9")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.10")); //$NON-NLS-1$
							}
							break;
						case ADDG:
							if (pObj.canModify(player, plugin)) //$NON-NLS-1$
							{
								List<String> temp = new ArrayList<String>(
										Arrays.asList(pendingCmd.GetArgs()));
								pObj.addGroups(temp);
								plugin.protectedHandler.saveObject(pObj);
								plugin.protectedHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.12")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.13")); //$NON-NLS-1$
							}
							break;
						case REMOVE:
							if (pObj.canModify(player, plugin)) //$NON-NLS-1$
							{
								List<String> temp = new ArrayList<String>(
										Arrays.asList(pendingCmd.GetArgs()));
								pObj.removeUsers(temp);
								plugin.protectedHandler.saveObject(pObj);
								plugin.protectedHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.15")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.16")); //$NON-NLS-1$
							}
							break;
						case REMOVEG:
							if (pObj.canModify(player, plugin)) //$NON-NLS-1$
							{
								List<String> temp = new ArrayList<String>(
										Arrays.asList(pendingCmd.GetArgs()));
								pObj.removeGroups(temp);
								plugin.protectedHandler.saveObject(pObj);
								plugin.protectedHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.18")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.19")); //$NON-NLS-1$
							}
							break;
						case DELETE:
							if (pObj.canRemove(player, plugin)) //$NON-NLS-1$
							{
								plugin.protectedHandler.remove(pObj);
								plugin.protectedHandler.save();
								player.sendMessage(Messages
										.getString("DoorBlockListener.21")); //$NON-NLS-1$
							} else {
								player.sendMessage(Messages
										.getString("DoorBlockListener.22")); //$NON-NLS-1$
							}
							break;
						case INFO:
							player.sendMessage("Can open: "
									+ pObj.canOpen(player, plugin));
							player.sendMessage("Creator: " + pObj.getCreator());
							player.sendMessage("Users: "
									+ pObj.getUsersString());
							player.sendMessage("Groups: "
									+ pObj.getGroupsString());
							break;
						}
						plugin.protectedHandler.removePendingPlayerCommand();
						event.setCancelled(true);
					} else {
						if (pObj.canOpen(player, plugin)) //$NON-NLS-1$
						{
							canOpen = true;
							return;
						} else {
							if (plugin.useiConomy) {

								Sign sign = plugin.doorHandler
										.getSign(aboveBlock);

								if (sign != null) {
									if (plugin.doorHandler.checkCost(player,
											sign, pObj)) {
										return;
									} else {
										event.setCancelled(true);
										return;
									}
								}
							}
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
					boolean canCreate = false;
					if (plugin.useiPermissions) {
						if (ProtectedPlugin.Permissions.has(player,
								"pdoors.main")) {
							canCreate = true;
						}
					} else {
						canCreate = true;
					}

					if (canCreate) {
						ProtectedCmd pendingCmd = plugin.protectedHandler
								.getPendingPlayerCommand(player);
						if (pendingCmd != null) {
							switch (pendingCmd.GetCommand()) {
							case CREATE:
								ProtectedObject temp = new ProtectedObject();
								temp.setCreator(player.getDisplayName());
								temp.setLocation(loc);
								if (plugin.protectedHandler.getObject(loc) == null) {
									plugin.protectedHandler.saveObject(temp);
									plugin.protectedHandler.save();
									player.sendMessage(Messages
											.getString("DoorBlockListener.28")); //$NON-NLS-1$
								} else {
									player.sendMessage(Messages
											.getString("DoorBlockListener.29")); //$NON-NLS-1$
								}
								break;
							case INFO:
								player.sendMessage("Unprotected Door!");
								break;
							}
							plugin.protectedHandler
									.removePendingPlayerCommand();
							event.setCancelled(true);
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
		if (eventBlock.getType().equals(Material.REDSTONE_WIRE)
				|| eventBlock.getType().equals(Material.REDSTONE)
				|| eventBlock.getType().equals(Material.REDSTONE_ORE)
				|| eventBlock.getType().equals(Material.REDSTONE_TORCH_OFF)
				|| eventBlock.getType().equals(Material.REDSTONE_TORCH_ON)
				|| eventBlock.getType().equals(Material.WOOD_PLATE)
				|| eventBlock.getType().equals(Material.STONE_PLATE)
				|| eventBlock.getType().equals(Material.STONE_BUTTON)
				|| eventBlock.getType().equals(Material.LEVER)) {
			List<ProtectedObject> doorObjects = new ArrayList<ProtectedObject>();
			plugin.persistence.getAll(doorObjects, ProtectedObject.class);

			if (doorObjects.size() > 0) {
				BlockVector min = new BlockVector();
				min.setX(onBlock.getX() - 1);
				min.setZ(onBlock.getZ() - 1);
				min.setY(onBlock.getY() - 2);
				BlockVector max = new BlockVector();
				max.setX(onBlock.getX() + 1);
				max.setZ(onBlock.getZ() + 1);
				max.setY(onBlock.getY() + 2);

				for (ProtectedObject obj : doorObjects) {
					ProtectedLocation loc = obj.getLocation();
					if (loc.getWorldData().getWorld(plugin.getServer()).equals(eventBlock.getWorld())) {
						Vector pt = new Vector();
						pt = loc.toVector();

						int x = pt.getBlockX();
						int y = pt.getBlockY();
						int z = pt.getBlockZ();
						if (x >= min.getBlockX() && x <= max.getBlockX()
								&& y >= min.getBlockY() && y <= max.getBlockY()
								&& z >= min.getBlockZ() && z <= max.getBlockZ()) {
							ProtectedPlugin.log
									.info("Player: "
											+ event.getPlayer()
													.getDisplayName()
											+ ", Tried to place a redstone object by a protected door!");
							event.setCancelled(true);
						}
					}
				}
			} else {
				World blockWorld = onBlock.getWorld();

				Block block = blockWorld.getBlockAt(onBlock.getX() + 1,
						onBlock.getY(), onBlock.getZ());
				if (!block.getType().equals(Material.WOODEN_DOOR)) {
					block = blockWorld.getBlockAt(onBlock.getX() - 1,
							onBlock.getY(), onBlock.getZ());
				}
				if (!block.getType().equals(Material.WOODEN_DOOR)) {
					block = blockWorld.getBlockAt(onBlock.getX(),
							onBlock.getY(), onBlock.getZ() + 1);
				}
				if (!block.getType().equals(Material.WOODEN_DOOR)) {
					block = blockWorld.getBlockAt(onBlock.getX(),
							onBlock.getY(), onBlock.getZ() - 1);
				}

				BlockState blockState = block.getState();
				if (blockState.getType().equals(Material.WOODEN_DOOR)) {
					Block doorBlock = blockWorld.getBlockAt(blockState.getX(),
							blockState.getY() + 1, blockState.getZ());
					// For when someone clicks the bottom of the door
					if (doorBlock.getType() != blockState.getType()) {
						doorBlock = blockWorld.getBlockAt(blockState.getX(),
								blockState.getY(), blockState.getZ());
					}

					Sign sign = plugin.doorHandler.getSign(doorBlock);
					if (sign != null) {
						if (!sign.getLine(1).equalsIgnoreCase("Cost:")) {
							ProtectedPlugin.log
									.info("Player: "
											+ event.getPlayer()
													.getDisplayName()
											+ ", Tried to place a redstone object by a protected door!");
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@Override
	public void onBlockRedstoneChange(BlockRedstoneEvent event) {
		Block block = event.getBlock();
		List<ProtectedObject> doorObjects = new ArrayList<ProtectedObject>();
		plugin.persistence.getAll(doorObjects, ProtectedObject.class);

		if (doorObjects.size() > 0) {
			BlockVector min = new BlockVector();
			min.setX(block.getX() - 1);
			min.setZ(block.getZ() - 1);
			min.setY(block.getY() - 2);
			BlockVector max = new BlockVector();
			max.setX(block.getX() + 1);
			max.setZ(block.getZ() + 1);
			max.setY(block.getY() + 2);

			for (ProtectedObject obj : doorObjects) {
				ProtectedLocation loc = obj.getLocation();
				if (loc.getWorldData().getWorld(plugin.getServer()).equals(block.getWorld())) {
					Vector pt = new Vector();
					pt = loc.toVector();

					int x = pt.getBlockX();
					int y = pt.getBlockY();
					int z = pt.getBlockZ();
					if (x >= min.getBlockX() && x <= max.getBlockX()
							&& y >= min.getBlockY() && y <= max.getBlockY()
							&& z >= min.getBlockZ() && z <= max.getBlockZ()) {
						ProtectedPlugin.log
								.info("Redstone change by protected door, removed the redstone!");
						block.setType(Material.AIR);
					}
				}
			}
		} else {
			World blockWorld = block.getWorld();

			Block dblock = blockWorld.getBlockAt(block.getX() + 1,
					block.getY(), block.getZ());
			if (!dblock.getType().equals(Material.WOODEN_DOOR)) {
				dblock = blockWorld.getBlockAt(block.getX() - 1, block.getY(),
						block.getZ());
			}
			if (!dblock.getType().equals(Material.WOODEN_DOOR)) {
				dblock = blockWorld.getBlockAt(block.getX(), block.getY(),
						block.getZ() + 1);
			}
			if (!dblock.getType().equals(Material.WOODEN_DOOR)) {
				dblock = blockWorld.getBlockAt(block.getX(), block.getY(),
						block.getZ() - 1);
			}

			BlockState blockState = dblock.getState();
			if (blockState.getType().equals(Material.WOODEN_DOOR)) {
				Block doorBlock = blockWorld.getBlockAt(blockState.getX(),
						blockState.getY() + 1, blockState.getZ());
				// For when someone clicks the bottom of the door
				if (doorBlock.getType() != blockState.getType()) {
					doorBlock = blockWorld.getBlockAt(blockState.getX(),
							blockState.getY(), blockState.getZ());
				}

				Sign sign = plugin.doorHandler.getSign(doorBlock);
				if (sign != null) {
					if (!sign.getLine(1).equalsIgnoreCase("Cost:")) {
						ProtectedPlugin.log
								.info("Redstone change by protected door, removed the redstone!");
						block.setType(Material.AIR);
					}
				}
			}
		}
	}
}
