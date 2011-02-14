package com.bukkit.WinSock.Protected.Objects;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import com.bukkit.WinSock.Protected.ProtectedObject;
import com.nijikokun.bukkit.iConomy.iConomy;

public class Door {

	public Sign getSign(Block block) {
		World blockWorld = block.getWorld();

		Block signBlock = blockWorld.getBlockAt(block.getX() + 1, block.getY(),
				block.getZ());
		if (signBlock.getType() != Material.WALL_SIGN) {
			signBlock = blockWorld.getBlockAt(block.getX() - 1, block.getY(),
					block.getZ());
		}
		if (signBlock.getType() != Material.WALL_SIGN) {
			signBlock = blockWorld.getBlockAt(block.getX(), block.getY(),
					block.getZ() + 1);
		}
		if (signBlock.getType() != Material.WALL_SIGN) {
			signBlock = blockWorld.getBlockAt(block.getX(), block.getY(),
					block.getZ() - 1);
		}

		BlockState signState = signBlock.getState();
		if (signState instanceof Sign) {
			Sign sign = (Sign) signState;
			if (sign.getLine(0).contains("ProtectedDoor")) {
				return sign;
			}
		}
		return null;
	}

	public boolean checkCost(Player player, Sign sign, ProtectedObject obj) {
		if (sign.getLine(1).equalsIgnoreCase("Cost:")) {
			int cost = 0;
			try {
				cost = Integer.decode(sign.getLine(2).trim());
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
		return false;
	}

	public boolean checkCost(Player player, ProtectedObject obj) {
		int playerMoney = iConomy.db.get_balance(player
				.getName());
		int ownerMoney = iConomy.db.get_balance(obj.getCreator());
		
		if (playerMoney >= obj.getCost()) {
			iConomy.db.set_balance(player.getDisplayName(), playerMoney
					- obj.getCost());
			iConomy.db.set_balance(obj.getCreator(), ownerMoney + obj.getCost());
			return true;
		}
		return false;
	}

	public boolean isInDoorway(Player player, ProtectedObject obj) {
		if (player.getLocation().equals(obj.getLocation()))
		{
			return true;
		}
		return false;
	}
}
