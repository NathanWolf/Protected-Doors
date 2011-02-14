package com.bukkit.WinSock.Protected;

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

import com.bukkit.WinSock.Protected.ProtectedCmd.ProtectedCommand;
import com.bukkit.WinSock.Protected.Listener.ProtectedBlockListener;
import com.bukkit.WinSock.Protected.Listener.ProtectedPlayerListener;
import com.bukkit.WinSock.Protected.Objects.Door;
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
public class ProtectedPlugin extends JavaPlugin {
	public static Logger log;
	public static PermissionHandler Permissions = null;

	private final ProtectedBlockListener blockListener = new ProtectedBlockListener(
			this);
	private final ProtectedPlayerListener playerListener = new ProtectedPlayerListener(
			this);
	private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
	public Door doorHandler = null;
	public final Protected protectedHandler = new Protected(this);
	private PluginUtilities putils = null;
	private PluginDescriptionFile pdfFile = this.getDescription();

	private PluginCommand pDoorsCommand = null;
	private PluginCommand pDoorsCreate = null;
	private PluginCommand pDoorsAddUser = null;
	private PluginCommand pDoorsAddGroup = null;
	private PluginCommand pDoorsDeleteProtection = null;
	private PluginCommand pDoorsRemoveUser = null;
	private PluginCommand pDoorsRemoveGroup = null;
	private PluginCommand pDoorsInfo = null;

	public Persistence persistence = null;

	public boolean useiConomy = false;

	public boolean useiPermissions = false;

	public ProtectedPlugin(PluginLoader pluginLoader, Server instance,
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
			ProtectedCmd data = new ProtectedCmd(p, ProtectedCommand.ADDG, args);
			protectedHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.3"));
		} else {
			pDoorsAddGroup.sendHelp(sender, "", true, false);
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
			ProtectedCmd data = new ProtectedCmd(p, ProtectedCommand.ADD, args);
			protectedHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.4"));
		} else {
			pDoorsAddUser.sendHelp(sender, "", true, false);
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

		ProtectedCmd data = new ProtectedCmd(p, ProtectedCommand.CREATE, args);
		protectedHandler.addCommand(data);
		p.sendMessage(Messages.getString("ProtectedDoors.5"));

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
			ProtectedCmd data = new ProtectedCmd(p, ProtectedCommand.DELETE,
					args);
			protectedHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.6"));
		} else {
			pDoorsDeleteProtection.sendHelp(sender, "", true, false);
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

		doorHandler = new Door();

		if (checkiConomy()) {
			log.info(pdfFile.getName()
					+ Messages.getString("ProtectedDoors.12"));
		}

		if (checkPermissions()) {
			log.info(pdfFile.getName()
					+ Messages.getString("ProtectedDoors.13"));
			ProtectedPlugin.Permissions = ((Permissions) this.getServer()
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

		pDoorsCreate = pDoorsCommand.getSubCommand("create",
				"Create a protected door", "create", PermissionType.ALLOW_ALL);
		pDoorsCreate.bind("onCreate");

		pDoorsAddUser = pDoorsCommand.getSubCommand("addu",
				"Add users to a protected door",
				"addu <List of users seperated by space>",
				PermissionType.ALLOW_ALL);
		pDoorsAddUser.bind("onAddUser");

		pDoorsAddGroup = pDoorsCommand.getSubCommand("addg",
				"Add groups to a protected door",
				"addg <List of groups seperated by space>",
				PermissionType.ALLOW_ALL);
		pDoorsAddGroup.bind("onAddGroup");

		pDoorsDeleteProtection = pDoorsCommand.getSubCommand("delete",
				"Remove door protection", "delete", PermissionType.ALLOW_ALL);
		pDoorsDeleteProtection.bind("onDelete");

		pDoorsRemoveUser = pDoorsCommand.getSubCommand("removeu",
				"Remove users from allowed list",
				"removeu <List of users seperated by space>",
				PermissionType.ALLOW_ALL);
		pDoorsRemoveUser.bind("onRemoveUser");

		pDoorsRemoveGroup = pDoorsCommand.getSubCommand("removeg",
				"Remove groups from allowed list",
				"removeg <List of groups seperated by space>",
				PermissionType.ALLOW_ALL);
		pDoorsRemoveGroup.bind("onRemoveGroup");

		pDoorsInfo = pDoorsCommand.getSubCommand("info", "Get info on a door",
				"info", PermissionType.ALLOW_ALL);
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
		ProtectedCmd data = new ProtectedCmd(p, ProtectedCommand.INFO, args);
		protectedHandler.addCommand(data);
		p.sendMessage("Right click a door to get its info!");
		return true;
	}

	public boolean onpDoors(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		pDoorsCommand.sendHelp(sender, "", true, true);

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
			ProtectedCmd data = new ProtectedCmd(p, ProtectedCommand.REMOVEG,
					args);
			protectedHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.45"));
		} else {
			pDoorsRemoveGroup.sendHelp(sender, "", true, false);
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
			ProtectedCmd data = new ProtectedCmd(p, ProtectedCommand.REMOVE,
					args);
			protectedHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.46")); //$NON-NLS-1$
		} else {
			pDoorsRemoveUser.sendHelp(sender, "", true, false);
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