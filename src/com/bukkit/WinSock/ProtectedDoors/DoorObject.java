package com.bukkit.WinSock.ProtectedDoors;

import java.util.List;
import com.elmakers.mine.bukkit.plugins.persistence.annotation.*;

@PersistClass(name = "DoorObject", schema = "ProtectedDoors")
public class DoorObject {
	
	public void setLocation(String loc)
	{
		this.loc = loc;
	}
	
	@Persist(id=true)
	public String getLocation()
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
	
	private String loc;
	private List<String> users;
	private List<String> groups;
}
