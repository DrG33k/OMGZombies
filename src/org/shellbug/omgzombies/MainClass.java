package org.shellbug.omgzombies;

import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class MainClass extends JavaPlugin {

	final boolean DEBUG = false;
	public Boolean bInvasion = false;
	public Boolean bEssentials = false;
	public Boolean bMultiverse = false;
	public String sServerName;
	public HashMap<String, Boolean> bLand = new HashMap<String, Boolean>();
	public HashMap<String, Integer> iZombieCount = new HashMap<String, Integer>();

	Logger logger;

	public void onEnable() {
		PluginDescriptionFile pdfFile = getDescription();
		logger = getLogger();

		Plugin EssentialsPlugin = this.getServer().getPluginManager().getPlugin("Essentials");
		if (EssentialsPlugin == null) {
			bEssentials = false;
			// getServer().getPluginManager().disablePlugin(this);
			logger.warning(pdfFile.getName() + " V." + pdfFile.getVersion() + ": You need Essentials plugin for a total experience");
			// return;
		} else {
			bEssentials = true;
		}

		Plugin MultiverseCorePlugin = this.getServer().getPluginManager().getPlugin("Multiverse-Core");
		if (MultiverseCorePlugin == null) {
			bMultiverse = false;
			// getServer().getPluginManager().disablePlugin(this);
			logger.warning(pdfFile.getName() + " V." + pdfFile.getVersion() + ": You need Multiverse-Core plugin for a total experience");
			// return;
		} else {
			bMultiverse = true;
		}

		registerConfig();

		if (bEssentials) {
			new ListenerClass(this);
		} else {
			new ListenerClassNoPlugin(this);
		}
		// new SpawnZombies( this );

		getCommand("omgz").setExecutor(new CommandsClass(this));

		logger.info(pdfFile.getName() + " V." + pdfFile.getVersion() + ": has been enabled!");

		// Check for updates
		// if (getConfig().getBoolean("global-settings.update-notification",
		// false)) {
		// VersionChecker.checkForUpdates(this, null);
		// }
	}

	private void registerConfig() {
		getConfig().options().copyDefaults(true);
		saveConfig();
	}

	public void onDisable() {
		PluginDescriptionFile pdfFile = getDescription();
		Logger logger = getLogger();
		// VersionChecker.shutdown();
		logger.info(pdfFile.getName() + " V." + pdfFile.getVersion() + ": has been disabled!");
	}

}
