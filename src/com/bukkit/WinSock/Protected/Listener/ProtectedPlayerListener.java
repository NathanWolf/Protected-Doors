package com.bukkit.WinSock.Protected.Listener;

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

import com.bukkit.WinSock.Protected.ProtectedLocation;
import com.bukkit.WinSock.Protected.ProtectedObject;
import com.bukkit.WinSock.Protected.ProtectedPlugin;

public class ProtectedPlayerListener extends PlayerListener {
	private final ProtectedPlugin plugin;

	public ProtectedPlayerListener(final ProtectedPlugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onPlayerItem(PlayerItemEvent event) {
		if (event.getItem().getType().equals(Material.REDSTONE)) {
			Block block = event.getBlockClicked();
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
							ProtectedPlugin.log.info("Player: "
									+ event.getPlayer().getDisplayName()
									+ ", tried to place redstone!");
							event.setCancelled(true);
						}
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
							ProtectedPlugin.log.info("Player: "
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
