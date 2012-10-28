package com.bukkit.WinSock.Protected;

import java.util.List;

import org.bukkit.entity.Player;

import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistClass;
import com.elmakers.mine.bukkit.plugins.persistence.annotation.PersistField;

@PersistClass(name = "Object", schema = "Protected")
public class ProtectedObject {

	private List<String> groups = null;

	private ProtectedLocation loc = null;

	private List<String> users = null;

	private String creator = "";
	
	private int cost = 0;

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
	public ProtectedLocation getLocation() {
		return loc;
	}

	@PersistField(contained = true)
	public List<String> getUsers() {
		return users;
	}
	
	@PersistField
	public int getCost()
	{
		return this.cost;
	}
	
	public void setCost(int cost)
	{
		this.cost = cost;
	}

	public void setGroups(List<String> groups) {
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

	public void setLocation(ProtectedLocation loc) {
		this.loc = loc;
	}

	public void setUsers(List<String> users) {
		this.users = users;
	}

	public void addUsers(List<String> users) {
		if (this.users != null) {
			this.users.addAll(users);
		} else {
			setUsers(users);
		}
	}

	public void removeUsers(List<String> users) {
		if (this.users != null) {
			this.users.removeAll(users);
		}
	}

	public boolean canOpen(Player player, ProtectedPlugin plugin) {
		if (this.creator.equalsIgnoreCase(player.getDisplayName())) {
			return true;
		}
		if (this.users != null) {
			if (this.users.contains(player.getDisplayName())) {
				return true;
			}
		}
		if (plugin.useiPermissions) {
			if (ProtectedPlugin.Permissions.has(player, "pdoors.mod")) {
				return true;
			}
			if (this.groups != null) {
				if (this.groups.contains(ProtectedPlugin.Permissions
						.getGroup(player.getDisplayName()))) {
					return true;
				}
			}
		} else {
			if (plugin.readOP().contains(player.getDisplayName())) {
				return true;
			}
		}
		return false;
	}

	public boolean canRemove(Player player, ProtectedPlugin plugin) {
		if (this.creator.equalsIgnoreCase(player.getDisplayName())) {
			return true;
		}
		if (plugin.useiPermissions) {
			if (ProtectedPlugin.Permissions.has(player, "pdoors.admin")) {
				return true;
			}
		} else {
			if (plugin.readOP().contains(player.getDisplayName())) {
				return true;
			}
		}
		return false;
	}

	public boolean canModify(Player player, ProtectedPlugin plugin) {
		if (this.creator.equalsIgnoreCase(player.getDisplayName())) {
			return true;
		}
		if (plugin.useiPermissions) {
			if (ProtectedPlugin.Permissions.has(player, "pdoors.mod")) {
				return true;
			}
		} else {
			if (plugin.readOP().contains(player.getDisplayName())) {
				return true;
			}
		}
		return false;
	}

	public String getUsersString() {
		String users = "";
		if (this.users != null) {
			for (String s : this.users) {
				users += s + ", ";
			}
			if (users.length() > 1) {
				return users.substring(0, users.length() - 2);
			}
		}
		return "None";
	}

	public String getGroupsString() {
		String groups = "";
		if (this.groups != null) {
			for (String s : this.groups) {
				groups += s + ", ";
			}
			if (groups.length() > 1) {
				return groups.substring(0, groups.length() - 2);
			}
		}
		return "None";
	}
}
