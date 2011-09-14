/**
 * 
 */
package me.main__.WolfPay;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.iConomy.iConomy;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * @author main()
 *
 */
public class WolfPay extends JavaPlugin {
	
	public static Permissions permissions;
	public static iConomy iconomy;
	
	public static int price = 25;
	public static int freewolves = 2;
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	public void onDisable() {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	public void onEnable() {
		PluginManager pm = this.getServer().getPluginManager();
		
		try {
			//get Permissions
			permissions = (Permissions) pm.getPlugin("Permissions");
			iconomy = (iConomy) pm.getPlugin("iConomy");
		} catch (Throwable t) {
			t.printStackTrace();
			Util.log("Couldn't hook into Permissions and/or iConomy. Disabling...", Level.SEVERE);
			pm.disablePlugin(this);
		}
		
		pm.registerEvent(Type.ENTITY_TAME, new WolfPayEntityListener(), Priority.High, this);
		
		//config
		File dataDirectory = new File("plugins" + File.separator + "MakeUse");
		dataDirectory.mkdirs();
		File configFile = new File(dataDirectory, "config.yml");
		if (!configFile.exists())
			try {
				configFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
				Util.log("Couldn't create config. Disabling...", Level.SEVERE);
				pm.disablePlugin(this);
			}
		Configuration config = new Configuration(configFile);
		price = config.getInt("price", price);
		freewolves = config.getInt("freewolves", freewolves);
		config.save();
		
		PluginDescriptionFile pdfFile = this.getDescription();
        Util.log(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}

}
