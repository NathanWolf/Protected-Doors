package com.bukkit.WinSock.Protected;

import org.bukkit.entity.Player;

public class ProtectedCmd {
	public enum ProtectedCommand {
		ADD, ADDG, CREATE, DELETE, REMOVE, REMOVEG, INFO
	}

	public enum ProtectedType {
		Door, Chest, Sign, Cuboid
	}

	private String[] args;
	private ProtectedCommand command;
	private Player player;

	ProtectedCmd(Player player, ProtectedCommand command, String[] args) {
		this.player = player;
		this.command = command;
		this.args = args;
	}

	public String[] GetArgs() {
		return args;
	}

	public ProtectedCommand GetCommand() {
		return command;
	}

	public Player GetPlayer() {
		return player;
	}
}
