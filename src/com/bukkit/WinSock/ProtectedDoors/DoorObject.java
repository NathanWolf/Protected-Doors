package com.bukkit.WinSock.ProtectedDoors;

import java.util.List;

import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistClass;
import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistField;

@PersistClass(name = "DoorObject", schema = "ProtectedDoors")
public class DoorObject {

	private List<String> groups = null;

	private String loc = "";

	private List<String> users = null;

	private String creator = "";

	@PersistField(contained = true)
	public List<String> getGroups() {
		return groups;
	}

	@PersistField
	public String getCreator() {
		return creator;
	}

	public void setCreator(String player) {
		this.creator = player;
	}

	@PersistField(id = true)
	public String getLocation() {
		return loc;
	}

	@PersistField(contained = true)
	public List<String> getUsers() {
		return users;
	}

	private void setGroups(List<String> groups) {
		this.groups = groups;
	}

	public void addGroups(List<String> groups) {
		if (this.groups != null) {
			this.groups.addAll(groups);
		} else {
			setGroups(groups);
		}
	}

	public void removeGroups(List<String> groups) {
		if (this.groups != null) {
			this.groups.removeAll(groups);
		}
	}

	public void setLocation(String loc) {
		this.loc = loc;
	}

	private void setUsers(List<String> users) {
		this.users = users;
	}

	public void addUsers(List<String> users) {
		if (this.users != null) {
			System.out.println(this.users.size());
			this.users.addAll(users);
			System.out.println(users.size());
			System.out.println(this.users.size());
		} else {
			setUsers(users);
		}
	}

	public void removeUsers(List<String> users) {
		if (this.users != null) {
			this.users.removeAll(users);
		}
	}
	
	public boolean canOpen(Player player, ProtectedDoors plugin)
	{
		if (this.creator.equalsIgnoreCase(player.getDisplayName()) || this.users.contains(player.getDisplayName())){
			return true;
		}
		if (plugin.useiPermissions)
		{
			if (ProtectedDoors.Permissions.has(player, "pdoors.mod")){
				return true;
			}
		}
		else
		{
			if (plugin.readOP().contains(player.getDisplayName()))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean canRemove(Player player, ProtectedDoors plugin)
	{
		if (this.creator.equalsIgnoreCase(player.getDisplayName()) || this.users.contains(player.getDisplayName())){
			return true;
		}
		if (plugin.useiPermissions)
		{
			if (ProtectedDoors.Permissions.has(player, "pdoors.admin")){
				return true;
			}
		}
		else
		{
			if (plugin.readOP().contains(player.getDisplayName()))
			{
				return true;
			}
		}
		return false;
	}
	
	public boolean canModify(Player player, ProtectedDoors plugin)
	{
		if (this.creator.equalsIgnoreCase(player.getDisplayName())){
			return true;
		}
		if (plugin.useiPermissions)
		{
			if (ProtectedDoors.Permissions.has(player, "pdoors.mod")){
				return true;
			}
		}
		else
		{
			if (plugin.readOP().contains(player.getDisplayName()))
			{
				return true;
			}
		}
		return false;
	}
}
