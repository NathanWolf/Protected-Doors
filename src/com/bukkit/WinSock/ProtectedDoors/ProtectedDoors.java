package com.bukkit.WinSock.ProtectedDoors;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.WinSock.ProtectedDoors.DoorCmd.DoorCommand;
import com.elmakers.mine.bukkit.plugins.persistence.Persistence;
import com.elmakers.mine.bukkit.plugins.persistence.PersistencePlugin;
import com.elmakers.mine.bukkit.plugins.persistence.PluginUtilities;
import com.elmakers.mine.bukkit.plugins.persistence.dao.PermissionType;
import com.elmakers.mine.bukkit.plugins.persistence.dao.PluginCommand;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * ICSings for Bukkit
 * 
 * @author WinSock
 */
public class ProtectedDoors extends JavaPlugin {
	public static Logger log;
	public static PermissionHandler Permissions = null;

	private final DoorBlockListener blockListener = new DoorBlockListener(this);
	private final DoorPlayerListener playerListener = new DoorPlayerListener(
			this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	public Door doorHandler = null;
	private PluginUtilities putils = null;
	private PluginDescriptionFile pdfFile = this.getDescription();
	private PluginCommand pDoorsCommand = null;

	public Persistence persistence = null;

	public boolean useiConomy = false;

	public boolean useiPermissions = false;

	public ProtectedDoors(PluginLoader pluginLoader, Server instance,
			PluginDescriptionFile desc, File folder, File plugin,
			ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);

		// NOTE: Event registration should be done in onEnable not here as all
		// events are unregistered when a plugin is disabled
	}

	private boolean checkiConomy() {
		Plugin test = this.getServer().getPluginManager()
				.getPlugin(Messages.getString("ProtectedDoors.1"));

		if (test != null) {
			this.useiConomy = true;
		} else {
			this.useiConomy = false;
		}

		return useiConomy;
	}

	private boolean checkPermissions() {
		Plugin test = this.getServer().getPluginManager()
				.getPlugin(Messages.getString("ProtectedDoors.2"));

		if (test != null) {
			this.useiPermissions = true;
		} else {
			this.useiPermissions = false;
		}

		return useiPermissions;
	}

	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		} else {
			return false;
		}
	}

	public boolean onAddGroup(CommandSender sender, String[] args) {
		Player p;
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length > 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.ADDG, args);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.3"));
		} else {
			pDoorsCommand.sendHelp(sender, null, true, true);
		}
		return true;
	}

	public boolean onAddUser(CommandSender sender, String[] args) {
		Player p;
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length > 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.ADD, args);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.4"));
		} else {
			pDoorsCommand.sendHelp(sender, null, true, true);
		}
		return true;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd,
			String commandLabel, String[] args) {
		return putils.dispatch(this, sender, cmd.getName(), args);
	}

	public boolean onCreate(CommandSender sender, String[] args) {
		Player p;
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}

		if (args.length == 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.CREATE, args);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.5"));
		} else {
			pDoorsCommand.sendHelp(sender, null, true, true);
		}
		return true;

	}

	public boolean onDelete(CommandSender sender, String[] args) {
		Player p;
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length == 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.DELETE, args);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.6"));
		} else {
			pDoorsCommand.sendHelp(sender, null, true, true);
		}
		return true;
	}

	@Override
	public void onDisable() {
		// TODO: Place any custom disable code here

		// NOTE: All registered events are automatically unregistered when a
		// plugin is disabled

		// EXAMPLE: Custom code, here we just output some info so we can check
		// all is well
		log.info(pdfFile.getName()
				+ Messages.getString("ProtectedDoors.7") + pdfFile.getVersion() + Messages.getString("ProtectedDoors.8")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void onEnable() {
		log = Logger.getLogger(Messages.getString("ProtectedDoors.9"));

		Plugin checkForPlugin = this.getServer().getPluginManager()
				.getPlugin(Messages.getString("ProtectedDoors.10"));
		if (checkForPlugin != null) {
			PersistencePlugin plugin = (PersistencePlugin) checkForPlugin;
			persistence = plugin.getPersistence();
		} else {
			log.warning(Messages.getString("ProtectedDoors.11"));
			this.getServer().getPluginManager().disablePlugin(this);
		}

		doorHandler = new Door(this);

		if (checkiConomy()) {
			log.info(pdfFile.getName()
					+ Messages.getString("ProtectedDoors.12"));
		}

		if (checkPermissions()) {
			log.info(pdfFile.getName()
					+ Messages.getString("ProtectedDoors.13"));
			ProtectedDoors.Permissions = ((Permissions) this.getServer()
					.getPluginManager()
					.getPlugin(Messages.getString("ProtectedDoors.2")))
					.getHandler();
		}

		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.BLOCK_INTERACT, blockListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.BLOCK_PLACED, blockListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener,
				Priority.Highest, this);
		pm.registerEvent(Event.Type.PLAYER_ITEM, playerListener,
				Priority.Highest, this);

		// Register Commands
		putils = persistence.getUtilities(this);

		pDoorsCommand = putils.getGeneralCommand("pdoor",
				"Protected Doors Main Command", "pdoor <parameters>",
				PermissionType.ALLOW_ALL);
		pDoorsCommand.bind("onpDoors");

		PluginCommand pDoorsCreate = pDoorsCommand.getSubCommand("create",
				"Create a protected door", "create", PermissionType.ALLOW_ALL);
		pDoorsCreate.bind("onCreate");

		PluginCommand pDoorsAddUser = pDoorsCommand.getSubCommand("addu",
				"Add users to a protected door",
				"addu <List of users seperated by space>",
				PermissionType.ALLOW_ALL);
		pDoorsAddUser.bind("onAddUser");

		PluginCommand pDoorsAddGroup = pDoorsCommand.getSubCommand("addg",
				"Add groups to a protected door",
				"addg <List of groups seperated by space>",
				PermissionType.ALLOW_ALL);
		pDoorsAddGroup.bind("onAddGroup");

		PluginCommand pDoorsDeleteProtection = pDoorsCommand.getSubCommand(
				"delete", "Remove door protection", "delete",
				PermissionType.ALLOW_ALL);
		pDoorsDeleteProtection.bind("onDelete");

		PluginCommand pDoorsRemoveUser = pDoorsCommand.getSubCommand("removeu",
				"Remove users from allowed list",
				"removeu <List of users seperated by space>",
				PermissionType.ALLOW_ALL);
		pDoorsRemoveUser.bind("onRemoveUser");

		PluginCommand pDoorsRemoveGroup = pDoorsCommand.getSubCommand(
				"removeg", "Remove groups from allowed list",
				"removeg <List of groups seperated by space>",
				PermissionType.ALLOW_ALL);
		pDoorsRemoveGroup.bind("onRemoveGroup");

		PluginCommand pDoorsInfo = pDoorsCommand.getSubCommand("info",
				"Get info on a door", "info", PermissionType.ALLOW_ALL);
		pDoorsInfo.bind("onInfo");

		// EXAMPLE: Custom code, here we just output some info so we can check
		// all is well
		log.info(pdfFile.getName() + Messages.getString("ProtectedDoors.7")
				+ pdfFile.getVersion()
				+ Messages.getString("ProtectedDoors.44"));
	}

	public boolean onInfo(CommandSender sender, String[] args) {
		Player p;
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		DoorCmd data = new DoorCmd(p, DoorCommand.INFO, args);
		doorHandler.addCommand(data);
		p.sendMessage("Right click a door to get its info!");
		return true;
	}

	public boolean onpDoors(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		pDoorsCommand.sendHelp(sender, null, true, true);

		return true;
	}

	public boolean onRemoveGroup(CommandSender sender, String[] args) {
		Player p;
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length > 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.REMOVEG, args);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.45"));
		} else {
			pDoorsCommand.sendHelp(sender, null, true, true);
		}
		return true;
	}

	public boolean onRemoveUser(CommandSender sender, String[] args) {
		Player p;
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length > 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.REMOVE, args);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.46")); //$NON-NLS-1$
		} else {
			pDoorsCommand.sendHelp(sender, null, true, true); //$NON-NLS-1$
		}
		return true;
	}

	public ArrayList<String> readOP() {
		ArrayList<String> ops = new ArrayList<String>();
		File opTxt = new File(Messages.getString("ProtectedDoors.47")); //$NON-NLS-1$
		try {
			String line = null;
			Scanner freader = new Scanner(opTxt);
			while (freader.hasNextLine()) {
				line = freader.nextLine();
				ops.add(line);
			}
			freader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return ops;
	}

	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}
}