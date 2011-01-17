package com.bukkit.WinSock.ProtectedDoors;

import java.io.File;
import java.util.logging.Logger;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

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
    	
        // Register our events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.BLOCK_INTERACT, blockListener, Priority.Highest, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGED, blockListener, Priority.Highest, this);

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

    public void setDebugging(final Player player, final boolean value) {
        debugees.put(player, value);
    }
}
