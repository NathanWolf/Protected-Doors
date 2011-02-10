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

		// Register Commands
		putils = persistence.getUtilities(this);

		pDoorsCommand = putils.getGeneralCommand(
				Messages.getString("ProtectedDoors.15"),
				Messages.getString("ProtectedDoors.16"),
				Messages.getString("ProtectedDoors.17"));
		pDoorsCommand.bind(Messages.getString("ProtectedDoors.18"));

		PluginCommand pDoorsCreate = pDoorsCommand.getSubCommand(
				Messages.getString("ProtectedDoors.19"),
				Messages.getString("ProtectedDoors.20"),
				Messages.getString("ProtectedDoors.21"));
		pDoorsCreate.bind(Messages.getString("ProtectedDoors.22"));

		PluginCommand pDoorsAddUser = pDoorsCommand.getSubCommand(
				Messages.getString("ProtectedDoors.23"),
				Messages.getString("ProtectedDoors.24"),
				Messages.getString("ProtectedDoors.25"));
		pDoorsAddUser.bind(Messages.getString("ProtectedDoors.26"));

		PluginCommand pDoorsAddGroup = pDoorsCommand.getSubCommand(
				Messages.getString("ProtectedDoors.27"),
				Messages.getString("ProtectedDoors.28"),
				Messages.getString("ProtectedDoors.29"));
		pDoorsAddGroup.bind(Messages.getString("ProtectedDoors.30"));

		PluginCommand pDoorsDeleteProtection = pDoorsCommand.getSubCommand(
				Messages.getString("ProtectedDoors.31"),
				Messages.getString("ProtectedDoors.32"),
				Messages.getString("ProtectedDoors.33"));
		pDoorsDeleteProtection.bind(Messages.getString("ProtectedDoors.34"));

		PluginCommand pDoorsRemoveUser = pDoorsCommand.getSubCommand(
				Messages.getString("ProtectedDoors.35"),
				Messages.getString("ProtectedDoors.36"),
				Messages.getString("ProtectedDoors.37"));
		pDoorsRemoveUser.bind(Messages.getString("ProtectedDoors.38"));

		PluginCommand pDoorsRemoveGroup = pDoorsCommand.getSubCommand(
				Messages.getString("ProtectedDoors.39"),
				Messages.getString("ProtectedDoors.40"),
				Messages.getString("ProtectedDoors.41"));
		pDoorsRemoveGroup.bind(Messages.getString("ProtectedDoors.42"));

		// EXAMPLE: Custom code, here we just output some info so we can check
		// all is well
		log.info(pdfFile.getName() + Messages.getString("ProtectedDoors.7")
				+ pdfFile.getVersion()
				+ Messages.getString("ProtectedDoors.44"));
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