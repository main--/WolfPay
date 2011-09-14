/**
 * 
 */
package me.main__.WolfPay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

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
	public static boolean savebought = true;
	
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
		WolfPayEntityListener.plugin = this;
		
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
		File dataDirectory = new File("plugins" + File.separator + "WolfPay");
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
		savebought = config.getBoolean("freewolves", savebought);
		config.save();
		
		if (savebought)
			setupDatabase();
		//if not, we don't even need the database.
		
		PluginDescriptionFile pdfFile = this.getDescription();
        Util.log(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}

	private void setupDatabase() {
		try
		{
			getDatabase().find(Account.class).findRowCount();
		}
		catch (PersistenceException e)
		{
			Util.log("Installing database!");
            installDDL();
		}
	}
	
    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(Account.class);
        return list;
    }
}
