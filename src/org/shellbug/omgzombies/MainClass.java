package org.shellbug.omgzombies;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

public class MainClass extends JavaPlugin {

//	public HashMap<String, Boolean> bInvasion = new HashMap<String, Boolean>();
	public Boolean bInvasion = false;
	public HashMap<String, Boolean> bLand = new HashMap<String, Boolean>();
	public HashMap<String, Integer> iZombieCount = new HashMap<String, Integer>();

	Logger logger;

	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger = getLogger();

		if (getServer().getPluginManager().isPluginEnabled("Essentials")) {
			logger.warning(pdfFile.getName() + " you need Essentials plugin");
			onDisable();
		}
		if (getServer().getPluginManager().isPluginEnabled("MultiverseCore")) {
			logger.warning(pdfFile.getName() + " you need MultiverseCore plugin");
			onDisable();
		}

		registerConfig();

		new ListenerClass(this);
		// new SpawnZombies( this );

		getCommand("omgz").setExecutor(new CommandsClass(this));

		logger.info(pdfFile.getName() + " has been enabled (V." + pdfFile.getVersion() + ")");
		
	}

	private void registerConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		Logger logger = getLogger();

		logger.info(pdfFile.getName() + " has been disabled (V." + pdfFile.getVersion() + ")");
	}

}
