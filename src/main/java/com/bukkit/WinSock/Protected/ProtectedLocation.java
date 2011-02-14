package com.bukkit.WinSock.Protected;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.util.BlockVector;
import org.bukkit.util.Vector;

import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistField;
import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistClass;
import com.elmakers.mine.bukkit.plugins.persistence.dao.WorldData;

@PersistClass(schema="Protected", name="location", contained=true)
public class ProtectedLocation {
	
	private WorldData worldData;

	private double x,y,z = 0;
	
	public ProtectedLocation()
	{
		
	}
	
	public ProtectedLocation(Location loc)
	{
		this.setX(loc.getX());
		this.setY(loc.getY());
		this.setZ(loc.getZ());
		
		this.worldData.update(loc.getWorld());
	}

	public void setX(double x) {
		this.x = x;
	}

	@PersistField
	public double getX() {
		return x;
	}

	public void setY(double y) {
		this.y = y;
	}

	@PersistField
	public double getY() {
		return y;
	}
	
	public void setZ(double z) {
		this.z = z;
	}
	
	@PersistField
	public double getZ() {
		return z;
	}
	
	@PersistField(id = true)
	public WorldData getWorldData() {
		return worldData;
	}

	public void setWorldData(WorldData worldData) {
		this.worldData = worldData;
	}
	
	public Vector toVector()
	{
		return new Vector(x,y,z);
	}
	
	public BlockVector toBlockVector()
	{
		return new BlockVector(x,y,z);
	}
	
	public Location toLocation(Server server)
	{
		return new Location(worldData.getWorld(server), x, y, z, (float)0, (float)0);
	}
}
