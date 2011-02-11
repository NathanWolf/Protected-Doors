package com.bukkit.WinSock.ProtectedDoors;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.event.player.PlayerItemEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

public class DoorPlayerListener extends PlayerListener {
	private final ProtectedDoors plugin;

	public DoorPlayerListener(final ProtectedDoors plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerItem(PlayerItemEvent event) {
		if (event.getItem().getType().equals(Material.REDSTONE)) {
			Block block = event.getBlockClicked();
			List<DoorObject> doorObjects = new ArrayList<DoorObject>();
			plugin.persistence.getAll(doorObjects, DoorObject.class);

			if (doorObjects.size() > 0) {
				BlockVector min = new BlockVector();
				min.setX(block.getX() - 1);
				min.setZ(block.getZ() - 1);
				min.setY(block.getY() - 2);
				BlockVector max = new BlockVector();
				max.setX(block.getX() + 1);
				max.setZ(block.getZ() + 1);
				max.setY(block.getY() + 2);

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
						ProtectedDoors.log.info("Player: "
								+ event.getPlayer().getDisplayName()
								+ ", tried to place redstone!");
						event.setCancelled(true);
					}
				}
			} else {
				World blockWorld = block.getWorld();

				Block dblock = blockWorld.getBlockAt(block.getX() + 1,
						block.getY(), block.getZ());
				if (!dblock.getType().equals(Material.WOODEN_DOOR)) {
					dblock = blockWorld.getBlockAt(block.getX() - 1,
							block.getY(), block.getZ());
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
							ProtectedDoors.log.info("Player: "
									+ event.getPlayer().getDisplayName()
									+ ", tried to place redstone!");
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}
}
