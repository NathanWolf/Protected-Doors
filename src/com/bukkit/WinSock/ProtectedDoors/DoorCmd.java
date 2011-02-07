package com.bukkit.WinSock.ProtectedDoors;

import org.bukkit.entity.Player;

public class DoorCmd {
	public enum DoorCommand {
		ADD, ADDG, CREATE, DELETE, REMOVE, REMOVEG
	}

	private String[] args;
	private DoorCommand command;
	private Player player;

	DoorCmd(Player player, DoorCommand command, String[] args) {
		this.player = player;
		this.command = command;
		this.args = args;
	}

	public String[] GetArgs() {
		return args;
	}

	public DoorCommand GetCommand() {
		return command;
	}

	public Player GetPlayer() {
		return player;
	}
}
