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

	private static String join(String[] arr, int offset) {
		return join(arr, offset, Messages.getString("ProtectedDoors.0")); //$NON-NLS-1$
	}

	private static String join(String[] arr, int offset, String delim) {
		String str = Messages.getString("ProtectedDoors.1"); //$NON-NLS-1$

		if (arr == null || arr.length == 0) {
			return str;
		}

		for (int i = offset; i < arr.length; i++) {
			str += arr[i] + delim;
		}

		return str.trim();
	}

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
		// TODO: Place any custom initialization code here

		// NOTE: Event registration should be done in onEnable not here as all
		// events are unregistered when a plugin is disabled
	}

	private boolean checkiConomy() {
		Plugin test = this.getServer().getPluginManager()
				.getPlugin(Messages.getString("ProtectedDoors.2")); //$NON-NLS-1$

		if (test != null) {
			this.useiConomy = true;
		} else {
			this.useiConomy = false;
		}

		return useiConomy;
	}

	private boolean checkPermissions() {
		Plugin test = this.getServer().getPluginManager()
				.getPlugin(Messages.getString("ProtectedDoors.3")); //$NON-NLS-1$

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
		String[] arguments = join(args, 0).split(
				Messages.getString("ProtectedDoors.4")); //$NON-NLS-1$
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length > 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.ADDG, arguments);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.5")); //$NON-NLS-1$
		} else {
			pDoorsCommand.sendHelp(sender,
					Messages.getString("ProtectedDoors.6"), true, true); //$NON-NLS-1$
		}
		return true;
	}

	public boolean onAddUser(CommandSender sender, String[] args) {
		Player p;
		String[] arguments = join(args, 0).split(
				Messages.getString("ProtectedDoors.7")); //$NON-NLS-1$
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length > 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.ADD, arguments);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.8")); //$NON-NLS-1$
		} else {
			pDoorsCommand.sendHelp(sender,
					Messages.getString("ProtectedDoors.9"), true, true); //$NON-NLS-1$
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
		String[] arguments = join(args, 0).split(
				Messages.getString("ProtectedDoors.10")); //$NON-NLS-1$
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}

		if (args.length == 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.CREATE, arguments);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.11")); //$NON-NLS-1$
		} else {
			pDoorsCommand.sendHelp(sender,
					Messages.getString("ProtectedDoors.12"), true, true); //$NON-NLS-1$
		}
		return true;

	}

	public boolean onDelete(CommandSender sender, String[] args) {
		Player p;
		String[] arguments = join(args, 0).split(
				Messages.getString("ProtectedDoors.13")); //$NON-NLS-1$
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length == 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.DELETE, arguments);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.14")); //$NON-NLS-1$
		} else {
			pDoorsCommand.sendHelp(sender,
					Messages.getString("ProtectedDoors.15"), true, true); //$NON-NLS-1$
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
				+ Messages.getString("ProtectedDoors.16") + pdfFile.getVersion() + Messages.getString("ProtectedDoors.17")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	@Override
	public void onEnable() {
		// TODO: Place any custom enable code here including the registration of
		// any events

		log = Logger.getLogger(Messages.getString("ProtectedDoors.18")); //$NON-NLS-1$

		Plugin checkForPlugin = this.getServer().getPluginManager()
				.getPlugin(Messages.getString("ProtectedDoors.19")); //$NON-NLS-1$
		if (checkForPlugin != null) {
			PersistencePlugin plugin = (PersistencePlugin) checkForPlugin;
			persistence = plugin.getPersistence();
		} else {
			log.warning(Messages.getString("ProtectedDoors.20")); //$NON-NLS-1$
			this.getServer().getPluginManager().disablePlugin(this);
		}

		doorHandler = new Door(this);

		if (checkiConomy()) {
			log.info(pdfFile.getName()
					+ Messages.getString("ProtectedDoors.21")); //$NON-NLS-1$
		}

		if (checkPermissions()) {
			log.info(pdfFile.getName()
					+ Messages.getString("ProtectedDoors.22")); //$NON-NLS-1$
			ProtectedDoors.Permissions = ((Permissions) this.getServer()
					.getPluginManager()
					.getPlugin(Messages.getString("ProtectedDoors.23"))).getHandler(); //$NON-NLS-1$
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

		pDoorsCommand = putils
				.getGeneralCommand(
						Messages.getString("ProtectedDoors.24"), Messages.getString("ProtectedDoors.25"), Messages.getString("ProtectedDoors.26")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pDoorsCommand.bind(Messages.getString("ProtectedDoors.27")); //$NON-NLS-1$

		PluginCommand pDoorsCreate = pDoorsCommand
				.getSubCommand(
						Messages.getString("ProtectedDoors.28"), Messages.getString("ProtectedDoors.29"), Messages.getString("ProtectedDoors.30")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pDoorsCreate.bind(Messages.getString("ProtectedDoors.31")); //$NON-NLS-1$

		PluginCommand pDoorsAddUser = pDoorsCommand
				.getSubCommand(
						Messages.getString("ProtectedDoors.32"), Messages.getString("ProtectedDoors.33"), Messages.getString("ProtectedDoors.34")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pDoorsAddUser.bind(Messages.getString("ProtectedDoors.35")); //$NON-NLS-1$

		PluginCommand pDoorsAddGroup = pDoorsCommand
				.getSubCommand(
						Messages.getString("ProtectedDoors.36"), Messages.getString("ProtectedDoors.37"), Messages.getString("ProtectedDoors.38")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pDoorsAddGroup.bind(Messages.getString("ProtectedDoors.39")); //$NON-NLS-1$

		PluginCommand pDoorsDeleteProtection = pDoorsCommand
				.getSubCommand(
						Messages.getString("ProtectedDoors.40"), Messages.getString("ProtectedDoors.41"), Messages.getString("ProtectedDoors.42")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pDoorsDeleteProtection.bind(Messages.getString("ProtectedDoors.43")); //$NON-NLS-1$

		PluginCommand pDoorsRemoveUser = pDoorsCommand
				.getSubCommand(
						Messages.getString("ProtectedDoors.44"), Messages.getString("ProtectedDoors.45"), Messages.getString("ProtectedDoors.46")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pDoorsRemoveUser.bind(Messages.getString("ProtectedDoors.47")); //$NON-NLS-1$

		PluginCommand pDoorsRemoveGroup = pDoorsCommand
				.getSubCommand(
						Messages.getString("ProtectedDoors.48"), Messages.getString("ProtectedDoors.49"), Messages.getString("ProtectedDoors.50")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		pDoorsRemoveGroup.bind(Messages.getString("ProtectedDoors.51")); //$NON-NLS-1$

		// EXAMPLE: Custom code, here we just output some info so we can check
		// all is well
		log.info(pdfFile.getName()
				+ Messages.getString("ProtectedDoors.52") + pdfFile.getVersion() + Messages.getString("ProtectedDoors.53")); //$NON-NLS-1$ //$NON-NLS-2$
	}

	public boolean onpDoors(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}

		pDoorsCommand.sendHelp(sender,
				Messages.getString("ProtectedDoors.54"), true, true); //$NON-NLS-1$

		return true;
	}

	public boolean onRemoveGroup(CommandSender sender, String[] args) {
		Player p;
		String[] arguments = join(args, 0).split(
				Messages.getString("ProtectedDoors.55")); //$NON-NLS-1$
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length > 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.REMOVEG, arguments);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.56")); //$NON-NLS-1$
		} else {
			pDoorsCommand.sendHelp(sender,
					Messages.getString("ProtectedDoors.57"), true, true); //$NON-NLS-1$
		}
		return true;
	}

	public boolean onRemoveUser(CommandSender sender, String[] args) {
		Player p;
		String[] arguments = join(args, 0).split(
				Messages.getString("ProtectedDoors.58")); //$NON-NLS-1$
		if (!(sender instanceof Player)) {
			return false;
		} else {
			p = (Player) sender;
		}
		if (args.length > 0) {
			DoorCmd data = new DoorCmd(p, DoorCommand.REMOVE, arguments);
			doorHandler.addCommand(data);
			p.sendMessage(Messages.getString("ProtectedDoors.59")); //$NON-NLS-1$
		} else {
			pDoorsCommand.sendHelp(sender,
					Messages.getString("ProtectedDoors.60"), true, true); //$NON-NLS-1$
		}
		return true;
	}

	public ArrayList<String> readOP() {
		ArrayList<String> ops = new ArrayList<String>();
		File opTxt = new File(Messages.getString("ProtectedDoors.61")); //$NON-NLS-1$
		try {
			String line = null;
			Scanner freader = new Scanner(opTxt);
			while (freader.hasNextLine()) {
				line = freader.nextLine();
				ops.add(line);
			}
			freader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return ops;
	}

	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}
}
