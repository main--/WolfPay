package me.main__.WolfPay;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import javax.persistence.PersistenceException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.iConomy.iConomy;

/**
 * @author main()
 *
 */
public class WolfPay extends JavaPlugin {
	
	public static iConomy iconomy;
	
	public static int price = 25;
	public static int freewolves = 2;
	public static boolean savebought = true;
	
	public static HashMap<String, String> messages = new HashMap<String, String>();
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	public void onDisable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		Util.log(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
	}

	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	public void onEnable() {
		WolfPayEntityListener.plugin = this;
		
		PluginManager pm = this.getServer().getPluginManager();
		
		try {
			iconomy = (iConomy) pm.getPlugin("iConomy");
		} catch (Throwable t) {
			t.printStackTrace();
			Util.log("Couldn't hook into Permissions and/or iConomy. Disabling...", Level.SEVERE);
			pm.disablePlugin(this);
		}
		
		pm.registerEvent(Type.ENTITY_TAME, new WolfPayEntityListener(), Priority.High, this);
		
		FileConfiguration config = this.getConfig();
		price = config.getInt("price", price);
		freewolves = config.getInt("freewolves", freewolves);
		savebought = config.getBoolean("savebought", savebought);
		
		config.set("price", price);
		config.set("freewolves", freewolves);
		config.set("savebought", savebought);

		try {
			config.save(new File(this.getDataFolder(), "config.yml"));
		} catch (IOException e) {
			e.printStackTrace();
			Util.log("Couldn't save the config! Disabling...", Level.SEVERE);
			this.getServer().getPluginManager().disablePlugin(this);
		}
		
		if (savebought)
			setupDatabase();
		//if not, we don't even need the database.
		
		//first put the default values into the HashMap, then read
		WolfPay.messages.put("havenow", "You have now x of y wolves.");
		WolfPay.messages.put("nextpay", "For the next wolf you'll have to pay.");
		WolfPay.messages.put("successpay", "You have successfully paid money and tamed a wolf!");
		WolfPay.messages.put("notenoughmoney", "Sorry, but you don't have enough money to do this.");
		WolfPay.messages.put("nopermission", "Sorry, but you don't have the permission to do this.");
		
		File messagesFile = new File(this.getDataFolder(), "messages.yml");
		FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);
				
		for (String s : WolfPay.messages.keySet())
		{
			WolfPay.messages.put(s, messagesConfig.getString(s, WolfPay.messages.get(s)));
			messagesConfig.set(s, WolfPay.messages.get(s));
		}
		
		try {
			messagesConfig.save(messagesFile);
		} catch (IOException e) {
			e.printStackTrace();
			Util.log("Couldn't save messages! Disabling...", Level.SEVERE);
			this.getServer().getPluginManager().disablePlugin(this);
		}
		
		PluginDescriptionFile pdfFile = this.getDescription();
		Util.log(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public static String getMessage(String key)
	{
		return messages.get(key);
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
