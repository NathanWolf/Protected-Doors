package com.bukkit.WinSock.ProtectedDoors;

import java.util.List;
import com.elmakers.mine.bukkit.plugins.persistence.annotation.*;
import com.bukkit.WinSock.ProtectedDoors.DoorLocation;

@PersistClass(name = "DoorObject", schema = "ProtectedDoors")
public class DoorObject {
	
	public void setLocation(DoorLocation loc)
	{
		this.loc = loc;
	}
	
	@Persist(id=true)
	public DoorLocation getLocation()
	{
		return loc;
	}
	
	public void setUsers(List<String> users)
	{
		this.users = users;
	}
	
	@Persist(contained=true)
	public List<String> getUsers()
	{
		return users;
	}
	
	public void setGroups(List<String> groups)
	{
		this.groups = groups;
	}
	
	@Persist(contained=true)
	public List<String> getGroups()
	{
		return groups;
	}
	
	private DoorLocation loc;
	private List<String> users;
	private List<String> groups;
}
