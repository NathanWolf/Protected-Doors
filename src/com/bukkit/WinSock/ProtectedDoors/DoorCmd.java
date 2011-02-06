package com.bukkit.WinSock.ProtectedDoors;

import org.bukkit.entity.Player;

public class DoorCmd {
	DoorCmd(Player player, DoorCommand command, String[] args)
	{
		this.player = player;
		this.command = command;
		this.args = args;
	}
	
	private Player player;
	private DoorCommand command;
	private String[] args;
	
	public Player GetPlayer()
	{
		return player;
	}
	
	public DoorCommand GetCommand()
	{
		return command;
	}
	
	public String[] GetArgs()
	{
		return args;
	}
	
	public enum DoorCommand
	{
		CREATE, ADD, ADDG, REMOVE, REMOVEG, DELETE
	}
}
