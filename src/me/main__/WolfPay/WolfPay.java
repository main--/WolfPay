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

import com.fernferret.allpay.AllPay;
import com.fernferret.allpay.GenericBank;

/**
 * @author main()
 * 
 */
public class WolfPay extends JavaPlugin {

    private static GenericBank bank = null;
    private static AllPay banker;
    private double allpayversion = 3;

    public static int price = 25;
    public static int freewolves = 2;
    public static boolean savebought = true;

    public static HashMap<String, String> messages = new HashMap<String, String>();

    public GenericBank getBank() {
        return bank;
    }

    /*
     * (non-Javadoc)
     * @see org.bukkit.plugin.Plugin#onDisable()
     */
    public void onDisable() {
        this.banker = null;
        this.bank = null;

        PluginDescriptionFile pdfFile = this.getDescription();
        Util.log(pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!");
    }

    /*
     * (non-Javadoc)
     * @see org.bukkit.plugin.Plugin#onEnable()
     */
    public void onEnable() {
        WolfPayEntityListener.plugin = this;

        PluginManager pm = this.getServer().getPluginManager();

        // Perform initial checks for AllPay
        if (!this.validateAllpay()) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }
        this.banker = new AllPay(this, "[WolfPay] ");
        this.bank = this.banker.loadEconPlugin();

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
        }
        catch (IOException e) {
            e.printStackTrace();
            Util.log("Couldn't save the config! Disabling...", Level.SEVERE);
            this.getServer().getPluginManager().disablePlugin(this);
        }

        if (savebought)
            setupDatabase();
        // if not, we don't even need the database.

        // first put the default values into the HashMap, then read
        WolfPay.messages.put("havenow", "You have now x of y wolves.");
        WolfPay.messages.put("nextpay", "For the next wolf you'll have to pay.");
        WolfPay.messages.put("successpay", "You have successfully paid money and tamed a wolf!");
        WolfPay.messages.put("notenoughmoney", "Sorry, but you don't have enough money to do this.");
        WolfPay.messages.put("nopermission", "Sorry, but you don't have the permission to do this.");

        File messagesFile = new File(this.getDataFolder(), "messages.yml");
        FileConfiguration messagesConfig = YamlConfiguration.loadConfiguration(messagesFile);

        for (String s : WolfPay.messages.keySet()) {
            WolfPay.messages.put(s, messagesConfig.getString(s, WolfPay.messages.get(s)));
            messagesConfig.set(s, WolfPay.messages.get(s));
        }

        try {
            messagesConfig.save(messagesFile);
        }
        catch (IOException e) {
            e.printStackTrace();
            Util.log("Couldn't save messages! Disabling...", Level.SEVERE);
            this.getServer().getPluginManager().disablePlugin(this);
        }

        PluginDescriptionFile pdfFile = this.getDescription();
        Util.log(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    private boolean validateAllpay() {
        try {
            this.banker = new AllPay(this, "Verify");
            if (this.banker.getVersion() >= allpayversion) {
                return true;
            }
            else {
                Util.log(" - Version " + this.getDescription().getVersion() + " was NOT ENABLED!!!");
                Util.log(" A plugin that has loaded before " + this.getDescription().getName() + " has an incompatible version of AllPay!");
                Util.log(" The Following Plugins MAY out of date!");
                Util.log(" This plugin needs AllPay v" + allpayversion + " or higher and another plugin has loaded v" + this.banker.getVersion()
                        + "!");
                Util.log(AllPay.pluginsThatUseUs.toString());
                return false;
            }
        }
        catch (Throwable t) {
        }
        Util.log(" - Version " + this.getDescription().getVersion() + " was NOT ENABLED!!!");
        Util.log(" A plugin that has loaded before " + this.getDescription().getName() + " has an incompatible version of AllPay!");
        Util.log(" Check the logs for [AllPay] - Version ... for PLUGIN NAME to find the culprit! Then Yell at that dev!");
        Util.log(" Or update that plugin :P");
        Util.log(" This plugin needs AllPay v" + allpayversion + " or higher!");
        return false;
    }

    public static String getMessage(String key) {
        return messages.get(key);
    }

    private void setupDatabase() {
        try {
            getDatabase().find(Account.class).findRowCount();
        }
        catch (PersistenceException e) {
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
