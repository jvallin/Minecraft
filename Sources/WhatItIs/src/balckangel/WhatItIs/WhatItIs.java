/**
* 
* WhatItIs plugin Bukkit
* 
* @author Balckangel
* @version 1.1
* @date 09/08/2012
* @modification 02/08/2014
* 
* Principle : Permet de savoir le nom et la position d'un bloc
* Version de Bukkit : for MC 1.7.10
*
*/

package balckangel.WhatItIs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class WhatItIs extends JavaPlugin
{
	public static List<String> collection = new ArrayList<String>();
	public WhatItIsListener listener = new WhatItIsListener();	

	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/WhatItIs/config.yml");
	
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
	
		if(commandLabel.equalsIgnoreCase("wii")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
		{
			if (args.length == 0) /* Si il n'y a pas d'argument */
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
				
				if(!collection.contains(player.getName())) /* Si il n'est pas dans la liste */
				{
					collection.add(player.getName());
				}
				
				player.sendMessage(config.getString("Configuration.Messages.Activer"));					
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
	public class WhatItIsListener implements Listener
	{
		@EventHandler
		public void onPlayerInteract(PlayerInteractEvent event)
		{
			if((Boolean) config.get("Configuration.Active"))
			{
				final Player player = event.getPlayer();
				final Action clic = event.getAction();
				String player_name = player.getName();
				
				if(Action.RIGHT_CLICK_BLOCK == clic) /* si le joueur clic droit */
				{
					if(collection.contains(player_name)) /* Si il est dans la liste */
					{	
						collection.remove(collection.indexOf(player_name));
						
						String nom = event.getClickedBlock().getType().toString();
						Location pos = event.getClickedBlock().getLocation();
						
						player.sendMessage("Le nom est : " + nom);
						player.sendMessage("Sa position est : x="+pos.getBlockX()+" y="+pos.getBlockY()+" z="+pos.getBlockZ());
					}
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
			config.createSection("Configuration.Messages.Activer"); /* pour activer la fonction */
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin WhatItIs active");
			config.set("Configuration.Messages.Desactive", "Plugin WhatItIs desactive");
			config.set("Configuration.Messages.Reload", "Plugin WhatItIs reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Activer", "Cliquez droit pour connaitre le nom et la position d'un objet");
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de WhatItIs a ete cree.");
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