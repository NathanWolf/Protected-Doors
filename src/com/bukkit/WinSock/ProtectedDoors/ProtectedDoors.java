package com.bukkit.WinSock.ProtectedDoors;

import java.io.File;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import com.bukkit.WinSock.ProtectedDoors.DoorCmd.DoorCommand;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijiko.permissions.PermissionHandler;

import com.elmakers.mine.bukkit.plugins.persistence.Messaging;
import com.elmakers.mine.bukkit.plugins.persistence.Persistence;
import com.elmakers.mine.bukkit.plugins.persistence.PersistencePlugin;
import com.elmakers.mine.bukkit.plugins.persistence.dao.PluginCommand;

/**
 * ICSings for Bukkit
 *
 * @author WinSock
 */
public class ProtectedDoors extends JavaPlugin {
    private final DoorBlockListener blockListener = new DoorBlockListener(this);
    private final HashMap<Player, Boolean> debugees = new HashMap<Player, Boolean>();
    private PluginDescriptionFile pdfFile = this.getDescription();
    public static Logger log;
    public boolean useiConomy = false;
    public boolean useiPermissions = false;
    public static PermissionHandler Permissions = null;
    public Door doorHandler = null; 
    public Persistence persistence = null;
    private Messaging messaging = null;
    
    public ProtectedDoors(PluginLoader pluginLoader, Server instance,
            PluginDescriptionFile desc, File folder, File plugin,
            ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
        // TODO: Place any custom initialization code here

        // NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
    }

    

    public void onEnable() {
        // TODO: Place any custom enable code here including the registration of any events
    	
    	log = Logger.getLogger("Minecraft");
    	
    	Plugin checkForPlugin = this.getServer().getPluginManager().getPlugin("Persistence");
        if (checkForPlugin != null)
        {
            PersistencePlugin plugin = (PersistencePlugin)checkForPlugin;
            persistence = plugin.getPersistence();
        }
        else
        {
            log.warning("The XXX plugin depends on Persistence - please install it!");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    	
    	doorHandler = new Door(this);
    	
    	if(checkiConomy())
    	{
    		log.info( pdfFile.getName() + " Using iConomy!" );
    	}
    	
    	if (checkPermissions())
    	{
    		log.info( pdfFile.getName() + " Using Permissions!" );
    		ProtectedDoors.Permissions = ((Permissions)this.getServer().getPluginManager().getPlugin("Permissions")).getHandler();
    	}
    	
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.BLOCK_INTERACT, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.REDSTONE_CHANGE, blockListener, Priority.Highest, this);
        
        // Register Comamnds
        messaging = new Messaging(this, persistence);
        
        PluginCommand pDoorsCommand = messaging.getGeneralCommand("pdoor", "Protected Doors Main Command", "pdoor <parameters>");
        pDoorsCommand.bind("onpDoors");
        
        PluginCommand pDoorsCreate = pDoorsCommand.getSubCommand("create", "Create a protected door", "create");
        pDoorsCreate.bind("onCreate");
        
        PluginCommand pDoorsAddUser = pDoorsCommand.getSubCommand("addu", "Add users to a protected door", "addu <List of users seperated by space>");
        pDoorsAddUser.bind("onAddUser");
        
        PluginCommand pDoorsAddGroup = pDoorsCommand.getSubCommand("addg", "Add groups to a protected door", "addg <List of groups seperated by space>");
        pDoorsAddGroup.bind("onAddGroup");
        
        PluginCommand pDoorsDeleteProtection = pDoorsCommand.getSubCommand("delete", "Remove door protection", "delete");
        pDoorsDeleteProtection.bind("onDelete");
        
        PluginCommand pDoorsRemoveUser = pDoorsCommand.getSubCommand("removeu", "Remove users from allowed list", "removeu <List of users seperated by space>");
        pDoorsRemoveUser.bind("onRemoveUser");
        
        PluginCommand pDoorsRemoveGroup = pDoorsCommand.getSubCommand("removeg", "Remove groups from allowed list", "removeg <List of groups seperated by space>");
        pDoorsRemoveGroup.bind("onRemoveGroup");
        
        // EXAMPLE: Custom code, here we just output some info so we can check all is well
        log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }
    public void onDisable() {
        // TODO: Place any custom disable code here

        // NOTE: All registered events are automatically unregistered when a plugin is disabled

        // EXAMPLE: Custom code, here we just output some info so we can check all is well
    	log.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }
    public ArrayList<String> readOP()
    {
    	ArrayList<String> ops = new ArrayList<String>();
    	File opTxt = new File("ops.txt");
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
    public boolean isDebugging(final Player player) {
        if (debugees.containsKey(player)) {
            return debugees.get(player);
        } else {
            return false;
        }
    }
    
    private boolean checkiConomy() {
        Plugin test = this.getServer().getPluginManager().getPlugin("iConomy");

        if (test != null) {
            this.useiConomy = true;
        } else {
            this.useiConomy = false;
        }

        return useiConomy;
    }
    
    private boolean checkPermissions() {
        Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

        if (test != null) {
            this.useiPermissions = true;
        } else {
            this.useiPermissions = false;
        }

        return useiPermissions;
    }

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
    
    public boolean onpDoors(CommandSender sender, String[] args)
    {
    	Player p;
    	if(!(sender instanceof Player)) {
			return false;
		}
    	else
    	{
    		p = (Player)sender;
    	}
    	
    	p.sendMessage("Usage: pdoor <option>");
    	p.sendMessage("create: Used to protect a door");
    	p.sendMessage("addu: Add users to the door");
    	p.sendMessage("addg: Add groups to the door");
    	p.sendMessage("delete: Remove door protection");
    	p.sendMessage("removeu: Remove users from the door");
    	p.sendMessage("removeg: Remove groups from the door");
    	return true;
    }
    
    public boolean onCreate(CommandSender sender, String[] args)
    {
    	Player p;
    	String[] arguments = join(args, 0).split(" ");
    	if(!(sender instanceof Player)) {
			return false;
		}
    	else
    	{
    		p = (Player)sender;
    	}
    	
    	if (args.length > 0)
		{
    		DoorCmd data = new DoorCmd(p, DoorCommand.CREATE, arguments);
			doorHandler.addCommand(data);
			p.sendMessage("Right click the door to create!");
		}
		return true;
    }
    
    public boolean onAddUser(CommandSender sender, String[] args)
    {
    	Player p;
    	String[] arguments = join(args, 0).split(" ");
    	if(!(sender instanceof Player)) {
			return false;
		}
    	else
    	{
    		p = (Player)sender;
    	}
    	if (args.length > 0)
		{
    		DoorCmd data = new DoorCmd(p, DoorCommand.ADD, arguments);
			doorHandler.addCommand(data);
			p.sendMessage("Right click the door to add the users!");
		}
    	return true;
    }
    
    public boolean onAddGroup(CommandSender sender, String[] args)
    {
    	Player p;
    	String[] arguments = join(args, 0).split(" ");
    	if(!(sender instanceof Player)) {
			return false;
		}
    	else
    	{
    		p = (Player)sender;
    	}
    	if (args.length > 0)
		{
    		DoorCmd data = new DoorCmd(p, DoorCommand.ADDG, arguments);
			doorHandler.addCommand(data);
			p.sendMessage("Right click the door to add the groups!");
		}
    	return true;
    }
    
    public boolean onDelete(CommandSender sender, String[] args)
    {
    	Player p;
    	String[] arguments = join(args, 0).split(" ");
    	if(!(sender instanceof Player)) {
			return false;
		}
    	else
    	{
    		p = (Player)sender;
    	}
    	if (args.length > 0)
		{
    		DoorCmd data = new DoorCmd(p, DoorCommand.DELETE, arguments);
			doorHandler.addCommand(data);
			p.sendMessage("Right click the door to remove the protection!");
		}
    	return true;
    }
    
    public boolean onRemoveUser(CommandSender sender, String[] args)
    {
    	Player p;
    	String[] arguments = join(args, 0).split(" ");
    	if(!(sender instanceof Player)) {
			return false;
		}
    	else
    	{
    		p = (Player)sender;
    	}
    	if (args.length > 0)
		{
    		DoorCmd data = new DoorCmd(p, DoorCommand.REMOVE, arguments);
			doorHandler.addCommand(data);
			p.sendMessage("Right click the door to remove the users!");
		}
    	return true;
    }
    
    public boolean onRemoveGroup(CommandSender sender, String[] args)
    {
    	Player p;
    	String[] arguments = join(args, 0).split(" ");
    	if(!(sender instanceof Player)) {
			return false;
		}
    	else
    	{
    		p = (Player)sender;
    	}
    	if (args.length > 0)
		{
    		DoorCmd data = new DoorCmd(p, DoorCommand.REMOVEG, arguments);
			doorHandler.addCommand(data);
			p.sendMessage("Right click the door to remove the groups!");
		}
    	return true;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
      return messaging.dispatch(this, sender, cmd.getName(), args);
    }
    
    private static String join(String[] arr, int offset) {
		return join(arr, offset, " ");
	}
    

    private static String join(String[] arr, int offset, String delim) {
		String str = "";

		if (arr == null || arr.length == 0) {
			return str;
		}

		for (int i = offset; i < arr.length; i++) {
			str += arr[i] + delim;
		}

		return str.trim();
	}
}
