package com.bukkit.WinSock.ProtectedDoors;

import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistClass;
import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistField;

@PersistClass(name = "DoorLocation", schema = "ProtectedDoors")
public class DoorLocation {
	@PersistField(id = true, auto = true)
	public int id;

	private int x;

	private int y;

	private int z;

	@PersistField
	public int getX() {
		return x;
	}

	@PersistField
	public int getY() {
		return y;
	}

	@PersistField
	public int getZ() {
		return z;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}
}
