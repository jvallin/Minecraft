/**
* 
* Back plugin Bukkit
* 
* @author Balckangel
* @version 1.0
* @date 01/01/2012
* @modification 11/12/2013
* 
* Principle : Téléporte le joueur à sa position avant de mourir
* Version de Bukkit : for MC 1.7.2
*
*/

package balckangel.Back;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class Back extends JavaPlugin
{
	public static Map<String, Location> collection = new HashMap<String, Location>();    
    public BackListener listener = new BackListener();
    
    /* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/Back/config.yml");
    
	public void onEnable() /* Actions exécutées au démarrage du plugin */
	{
		load();
    	if((Boolean) config.get("Configuration.Active"))
		{
			getServer().getPluginManager().registerEvents(listener, this);
		}
	}

	public void onDisable() /* Actions exécutées à la fermeture du plugin */
	{
		collection.clear();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!(Boolean) config.get("Configuration.Active") && !sender.getName().equals("CONSOLE")) /* si le plugin est désactivé et que ce n'est pas la console */
		{
			sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
			return true;
		}
		
		String nom;
	
		if(commandLabel.equalsIgnoreCase("back")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
		{
			if(args.length == 0) /* Si il n'y a pas d'argument */
			{
				Player player = null;
				
				try
				{
					player = (Player) sender;
				}
				catch (Exception e)
				{
					sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
					return false;
				}
				
				nom = player.getName();
				
				if(collection.containsKey(nom)) /* Si le joueur est dans la collection */
				{
					player.teleport(collection.get(nom));
					collection.remove(nom);
				}
				else
				{
					sender.sendMessage(config.getString("Configuration.Messages.NonDefinit"));
				}
				return true;
			}
			if (args.length == 1) /* Si il y a un argument */
			{
				if(sender.getName().equals("CONSOLE"))
				{
					if (args[0].equalsIgnoreCase("reload"))
					{
						load();
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Reload"));
						return true;
					}
					else if (args[0].equalsIgnoreCase("on"))					
					{
						config.set("Configuration.Active", true);
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Active"));
						saveYML();
						return true;
					}
					else if (args[0].equalsIgnoreCase("off"))
					{
						config.set("Configuration.Active", false);
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Desactive"));
						saveYML();
						return true;
					}
					else
					{
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
						return false;
					}
				}
			}
		}

		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
	}
	
	/* Listener */
	public class BackListener implements Listener
	{
		@EventHandler
		public void OnEntityDeath(EntityDeathEvent event)
		{
			if((Boolean) config.get("Configuration.Active")) /* si le plugin est activé */
			{
				if (event.getEntity() instanceof Player) /* Si c'est un joueur */
				{
					Player player = (Player) event.getEntity();
	
					Back.collection.put(player.getName(), player.getLocation());
				}
			}
		}
	}
	
	/* Fichier YML */
	public void load()
	{
		if(configFile.exists()) /* Lecture du fichier */
		{
			config = YamlConfiguration.loadConfiguration(configFile);
		}
		else /* Création du fichier */
		{	
			config = new YamlConfiguration();
			config.createSection("Configuration.Active");
			
			config.createSection("Configuration.Messages.Active"); /* Lorsque l'on active le plugin */
			config.createSection("Configuration.Messages.Desactive"); /* Lorsque l'on désactive le plugin */
			config.createSection("Configuration.Messages.Reload"); /* Lorsque le plugin est reload */
			config.createSection("Configuration.Messages.Permit"); /* Si l'utilisateur n'a pas le droit utiliser une commande */
			config.createSection("Configuration.Messages.NonDefinit"); /* Si le joueur n'est pas mort depuis le reload */
			
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin Back active");
			config.set("Configuration.Messages.Desactive", "Plugin Back desactive");
			config.set("Configuration.Messages.Reload", "Plugin Back reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.NonDefinit", "Vous ne pouvez pas vous teleporter");
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de Back a ete cree.");
		}    
    }
	
	public void saveYML()
	{
		try
		{
			config.save(configFile);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}