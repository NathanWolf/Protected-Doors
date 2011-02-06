package com.bukkit.WinSock.ProtectedDoors;

import com.elmakers.mine.bukkit.plugins.persistence.annotation.*;

@PersistClass(name = "DoorLocation", schema = "ProtectedDoors")
public class DoorLocation {
	@Persist(id=true, auto=true)
	public int id;
	
	public void setX(int x)
	{
		this.x = x;
	}
	
	@Persist
	public int getX()
	{
		return x;
	}
	
	public void setY(int y)
	{
		this.y = y;
	}
	
	@Persist
	public int getY()
	{
		return y;
	}
	
	public void setZ(int z)
	{
		this.z = z;
	}
	
	@Persist
	public int getZ()
	{
		return z;
	}
	
	private int x;
	private int y;
	private int z;
}
