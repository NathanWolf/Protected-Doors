package com.bukkit.WinSock.Protected;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class Protected {

	private ArrayList<ProtectedCmd> pendingCommands = new ArrayList<ProtectedCmd>();
	private final ProtectedPlugin plugin;

	public Protected(final ProtectedPlugin plugin) {
		this.plugin = plugin;
	}

	public void remove(ProtectedObject obj) {
		plugin.persistence.remove(obj);
	}

	public void removePendingPlayerCommand() {
		pendingCommands.clear();
	}

	public void save() {
		plugin.persistence.save();
	}

	public void saveObject(ProtectedObject obj) {
		plugin.persistence.put(obj);
	}

	public void addCommand(ProtectedCmd command) {
		if (getPendingPlayerCommand(command.GetPlayer()) == null) {
			pendingCommands.add(command);
		} else {
			removePendingPlayerCommand();
			pendingCommands.add(command);
		}
	}

	public ProtectedObject getObject(ProtectedLocation loc) {
		return plugin.persistence.get(loc, ProtectedObject.class);
	}

	public ProtectedCmd getPendingPlayerCommand(Player player) {
		for (ProtectedCmd data : pendingCommands) {
			if (data.GetPlayer().equals(player)) {
				return data;
			}
		}

		return null;
	}

}
