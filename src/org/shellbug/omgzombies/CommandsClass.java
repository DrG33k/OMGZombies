package org.shellbug.omgzombies;

import java.util.HashMap;
import java.util.Random;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;

public class CommandsClass implements CommandExecutor {

	public static int wave;
	public static int iTaskId;
	public Random random;
	public boolean bRoyals;

	private Player player;
	private World world;
	private Server server;
	private MainClass mainclass;

	private Boolean bInvasion;
	private HashMap<String, Integer> iZombieCount;
	Logger logger;

	public CommandsClass(MainClass plugin) {
		logger = plugin.getLogger();
		random = new Random();
		mainclass = plugin;
		bInvasion = plugin.bInvasion;
		iZombieCount = plugin.iZombieCount;
	}

	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

		// label args[0] args[1]
		// /msg Pippo Hello
		// Se non viene scritto dal server
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player to use this command!");
			return false;
		}

		PluginDescriptionFile pdfFile = mainclass.getDescription();

		player = (Player) sender;
		world = (World) player.getWorld();
		server = (Server) player.getServer();

		switch (args.length) {
		case 1:
			if (args[0].equalsIgnoreCase("start")) {
				if (!(player.isOp())) {
					player.sendMessage(ChatColor.RED + "You must be a Admin to use this command!");
					return true;
				}
				if (!bInvasion) {
					bInvasion = true;
					Bukkit.broadcastMessage( ChatColor.GREEN + player.getName() + ChatColor.RED + " starts the invasion!");
					world.setMonsterSpawnLimit(200);
					player.getServer().dispatchCommand(server.getConsoleSender(), "gamerule mobGriefing false");
					player.getServer().dispatchCommand(server.getConsoleSender(), "gamerule doDaylightCycle false");
					player.getServer().dispatchCommand(server.getConsoleSender(), "time set 13000 " + world.getName());
					player.getServer().dispatchCommand(server.getConsoleSender(), "mvm set diff hard " + world.getName());
					player.getServer().dispatchCommand(server.getConsoleSender(), "gamerule doDaylightCycle false");
					player.getServer().dispatchCommand(server.getConsoleSender(), "mv modify set animals false " + world.getName());
					player.getServer().dispatchCommand(server.getConsoleSender(), "mv modify set monsters false " + world.getName());
					player.getServer().dispatchCommand(server.getConsoleSender(), "mv modify add zombie monsters " + world.getName());
					// Se nella config c'e' scritto che è possibile volare
					if (mainclass.getConfig().getBoolean("AllowFly")) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.setAllowFlight(true);
							p.setFlying(true);
						}
					}
					for (Player p : Bukkit.getOnlinePlayers()) {
						p.playSound(p.getLocation(), Sound.AMBIENCE_THUNDER, 1, 0);
					}
					bRoyals = mainclass.getConfig().getBoolean("Royals");
					vStartTimer();
				}
			} else if (args[0].equalsIgnoreCase("stop")) {
				if (!(player.isOp())) {
					player.sendMessage(ChatColor.RED + "You must be a Admin to use this command!");
					return true;
				}
				if (bInvasion) { // Se nella configurazione c'e' scritto che è possibile volare
					if (mainclass.getConfig().getBoolean("AllowFly")) {
						for (Player p : Bukkit.getOnlinePlayers()) {
							p.setFlying(false);
							p.setAllowFlight(false);
						}
					}
					Bukkit.broadcastMessage( ChatColor.AQUA + player.getName() + ChatColor.GREEN + " stops the invasion!");
					bInvasion = false;
					vStopTimer();
				}
			} else if (args[0].equalsIgnoreCase("reward")) {
				player.sendMessage(ChatColor.GREEN + "Reward set at " + mainclass.getConfig().getInt("Reward") + "$");
			} else if (args[0].equalsIgnoreCase("version")) {
				player.sendMessage(ChatColor.GREEN + pdfFile.getName() + " V." + pdfFile.getVersion());
			} else if (args[0].equalsIgnoreCase("count")) {
				if (iZombieCount.containsKey(player.getName())) {
					player.sendMessage(ChatColor.GREEN + "You kill " + iZombieCount.get(player.getName()) + " zombies!");
				}
			} else {
				return false;
			}
			return true;
		case 2:
			if (args[0].equalsIgnoreCase("reward")) {
				if (!(player.isOp())) {
					player.sendMessage(ChatColor.RED + "You must be a Admin to use this command!");
					return true;
				}
				if (args[1].matches("[0-9]+")) {
					if (Integer.parseInt(args[1]) <= 10000) {
						mainclass.getConfig().set("Reward", Integer.parseInt(args[1]));
						mainclass.saveConfig();
						Bukkit.broadcastMessage(ChatColor.GREEN + "Reward set at " + Integer.parseInt(args[1]) + "$");
					} else {
						player.sendMessage(ChatColor.RED + "Do you really want this reward? (from 0 to 10000)");
					}
					return true;
				} else {
					return false;
				}
			} else if (args[0].equalsIgnoreCase("frequency")) {
				if (!(player.isOp())) {
					player.sendMessage(ChatColor.RED + "You must be a Admin to use this command!");
					return true;
				}
				if (bInvasion) {
					player.sendMessage(ChatColor.RED + "Stop the invasion before type this command!");
					return true;
				}
				if (args[1].matches("[0-9]+")) {
					if ((Integer.parseInt(args[1]) > 0) && (Integer.parseInt(args[1]) <= 50)) {
						mainclass.getConfig().set("Frequency", Integer.parseInt(args[1]));
						mainclass.saveConfig();
						Bukkit.broadcastMessage(ChatColor.GREEN + "Spawn frequency set at " + Integer.parseInt(args[1]) + " seconds");
					} else {
						player.sendMessage(ChatColor.RED + "Do you really want this Spawn Frequency? (from 1 to 50)");
					}
					return true;
				} else {
					return false;
				}
			} else if (args[0].equalsIgnoreCase("AllowFly")) {
				if (!(player.isOp())) {
					player.sendMessage(ChatColor.RED + "You must be a Admin to use this command!");
					return true;
				}
				if (bInvasion) {
					player.sendMessage(ChatColor.RED + "Stop the invasion before type this command!");
					return true;
				}
				if (args[1].equalsIgnoreCase("true")) {
					mainclass.getConfig().set("AllowFly", true);
					mainclass.saveConfig();
					player.sendMessage(ChatColor.RED + "Fly allowed.");
				} else if (args[1].equalsIgnoreCase("false")) {
					mainclass.getConfig().set("AllowFly", false);
					mainclass.saveConfig();
					player.sendMessage(ChatColor.GREEN + "Fly disabled.");
				} else {
					return false;
				}
			} else if (args[0].equalsIgnoreCase("Royals")) {
				if (!(player.isOp())) {
					player.sendMessage(ChatColor.RED + "You must be a Admin to use this command!");
					return true;
				}
				if (bInvasion) {
					player.sendMessage(ChatColor.RED + "Stop the invasion before type this command!");
					return true;
				}
				if (args[1].equalsIgnoreCase("true")) {
					mainclass.getConfig().set("Royals", true);
					mainclass.saveConfig();
					player.sendMessage(ChatColor.GREEN + "Royals spawn allowed.");
				} else if (args[1].equalsIgnoreCase("false")) {
					mainclass.getConfig().set("Royals", false);
					mainclass.saveConfig();
					player.sendMessage(ChatColor.GREEN + "Royals spawn disabled.");
				} else {
					return false;
				}
			} else {
				return false;
			}
			return true;
		default:
			return false;
		}

	}

	private void vStartTimer() {
		Long period = new Long(mainclass.getConfig().getInt("Frequency") * 20);
		wave = 0;

		iTaskId = Bukkit.getScheduler().scheduleSyncRepeatingTask((Plugin) mainclass, new Runnable() {
			public void run() {
				if (!bInvasion) {
					// logger.warning("t4 cancelTask 1");
					Bukkit.getScheduler().cancelTask(iTaskId);
					// logger.warning("t4 cancelTask 2");
				}

				wave++;
				for (Player p : Bukkit.getOnlinePlayers()) {
					// logger.warning("t4 " + p.getName());
					world = (World) p.getWorld();
					Location location = p.getLocation();
					switch (wave) {
					case 1:
						location.add(10, 0, 0);
						break;
					case 2:
						location.add(0, 0, 10);
						break;
					case 3:
						location.add(-10, 0, 0);
						break;
					case 4:
						location.add(0, 0, -10);
						wave = 0;
						break;
					}
					location.setY(world.getHighestBlockYAt(location));
					// world.spawnEntity(location, EntityType.ZOMBIE);
					Zombie zombie = (Zombie) world.spawnEntity(location, EntityType.ZOMBIE);

					// ItemStack item = new
					// ItemStack(Material.IRON_SWORD);
					ItemStack item;

					if (bRoyals) {
						Integer iSpecial = random.nextInt(50);
						// logger.warning("t5 " + iSpecial.toString());
						switch (iSpecial) {
						case 1:
							item = new ItemStack(Material.DIAMOND_SWORD);
							item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
							item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
							item.addEnchantment(Enchantment.KNOCKBACK, 1);
							zombie.setMaxHealth(zombie.getMaxHealth() * 4);
							zombie.setHealth(zombie.getMaxHealth());
							zombie.setCustomName("King");
							zombie.setCustomNameVisible(true);
							zombie.setTarget(p);
							zombie.getEquipment().setItemInHand(item);
							zombie.getEquipment().setHelmet(new ItemStack(Material.DIAMOND_HELMET));
							zombie.getEquipment().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
							zombie.getEquipment().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
							zombie.getEquipment().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
							zombie.getEquipment().setItemInHandDropChance(0);
							zombie.getEquipment().setHelmetDropChance(0);
							zombie.getEquipment().setLeggingsDropChance(0);
							zombie.getEquipment().setBootsDropChance(0);
							zombie.getEquipment().setChestplateDropChance(0);
							break;
						case 2:
						case 3:
							item = new ItemStack(Material.GOLD_SWORD);
							item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
							item.addEnchantment(Enchantment.FIRE_ASPECT, 1);
							item.addEnchantment(Enchantment.KNOCKBACK, 1);
							zombie.setMaxHealth(zombie.getMaxHealth() * 2);
							zombie.setHealth(zombie.getMaxHealth());
							zombie.setCustomName("Prince");
							zombie.setCustomNameVisible(true);
							zombie.setTarget(p);
							// zombie.setIAttribute(CustomIAttribute.FOLLOW_RANGE,40.0D);
							zombie.getEquipment().setItemInHand(item);
							zombie.getEquipment().setHelmet(new ItemStack(Material.GOLD_HELMET));
							zombie.getEquipment().setLeggings(new ItemStack(Material.GOLD_LEGGINGS));
							zombie.getEquipment().setBoots(new ItemStack(Material.GOLD_BOOTS));
							zombie.getEquipment().setChestplate(new ItemStack(Material.GOLD_CHESTPLATE));
							zombie.getEquipment().setItemInHandDropChance(0);
							zombie.getEquipment().setHelmetDropChance(0);
							zombie.getEquipment().setLeggingsDropChance(0);
							zombie.getEquipment().setBootsDropChance(0);
							zombie.getEquipment().setChestplateDropChance(0);
							break;
						}
					}
				}
			}
		}, 100L, period);
	}

	/*
	 * private Boolean takeInvasion(String string) { if
	 * (bInvasion.containsKey(string)) { return bInvasion.get(string); } return
	 * false; }
	 */
	private void vStopTimer() {
		// Ferma il task ne avvia un secondo
		Bukkit.getScheduler().cancelTask(iTaskId);
	}

	/*
	 * private MultiverseCore getMultiverseCore() { method stub Plugin plugin =
	 * server.getPluginManager().getPlugin("MultiverseCore");
	 * 
	 * if (plugin instanceof MultiverseCore) { return (MultiverseCore) plugin; }
	 * 
	 * throw new RuntimeException("MultiVerse not found!"); }
	 */
}
