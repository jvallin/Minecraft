/**
* 
* To plugin Bukkit
* 
* @author Balckangel
* @version 1.1
* @date 28/08/2012
* @modification 22/12/2013
* 
* Principle : Permet de se téléporter vers un autre joueur
* Version de Bukkit : for MC 1.7.2
*
*/

package balckangel.To;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class To extends JavaPlugin
{
	public ToListener listener = new ToListener();
    public static Map<String, String> autorisation = new HashMap<String, String>();
	
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/To/config.yml");
	
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
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!(Boolean) config.get("Configuration.Active") && !sender.getName().equals("CONSOLE")) /* si le plugin est désactivé et que ce n'est pas la console */
		{
			sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
			return true;			
		}
	
		if(commandLabel.equalsIgnoreCase("to")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
		{
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
				else
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
					
					if (args[0].equalsIgnoreCase("oui"))
					{
						autorisation.remove(player.getName());						
						autorisation.put(player.getName(), "oui");
						player.sendMessage(config.getString("Configuration.Messages.Autorise.Oui"));
						
						return true;
					}
					else if (args[0].equalsIgnoreCase("non"))
					{
						autorisation.remove(player.getName());						
						autorisation.put(player.getName(), "non");
						player.sendMessage(config.getString("Configuration.Messages.Autorise.Non"));
						return true;
					}
					else
					{					
						Player dest = getServer().getPlayerExact(args[0]);
						
						if(dest != null)
						{
							if(autorisation.get(dest.getName()) != null && autorisation.get(dest.getName()).equals("oui"))
							{
								player.teleport(dest);
								getServer().broadcastMessage(player.getName() + config.getString("Configuration.Messages.Teleport") + dest.getName());
							}
							else
							{
								player.sendMessage(config.getString("Configuration.Messages.Autorise.Pas"));
							}
							
							return true;
						}
						else
						{
							sender.sendMessage(config.getString("Configuration.Messages.Offline"));
							return true;
						}
					}
				}
			}			
		}
		
		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
	}
	
	/* Listener */
	public class ToListener implements Listener
	{
		@EventHandler
		public void OnPlayerLog(PlayerJoinEvent event)
		{
			if((Boolean) config.get("Configuration.Active")) /* si le plugin est activé */
			{
				Player player = event.getPlayer();
				if(!autorisation.containsKey(player.getName()))
				{
					autorisation.put(player.getName(), "non");
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
			config.createSection("Configuration.Messages.Offline"); /* Si le destinataire n'est pas connecté */
			config.createSection("Configuration.Messages.Teleport"); /* Lorsque le joueur s'est téléporté */
			config.createSection("Configuration.Messages.Autorise.Oui"); /* Lorsque le joueur accepte que l'on se téléporte vers lui */
			config.createSection("Configuration.Messages.Autorise.Non"); /* Lorsque le joueur n'accepte plus que l'on se téléporte vers lui */
			config.createSection("Configuration.Messages.Autorise.Pas"); /* Lorsque le joueur n'accepte pas que l'on se téléporte vers lui */
			
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin To active");
			config.set("Configuration.Messages.Desactive", "Plugin To desactive");
			config.set("Configuration.Messages.Reload", "Plugin To reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Offline", "Joueur non connecte. Appuyer sur 'TAB' pour afficher les joueurs connectes");
			config.set("Configuration.Messages.Teleport", " s'est teleporte vers ");
			config.set("Configuration.Messages.Autorise.Oui", "Vous acceptez les teleportations vers vous");
			config.set("Configuration.Messages.Autorise.Non", "Vous n'acceptez plus les teleportations vers vous");
			config.set("Configuration.Messages.Autorise.Pas", "Ce joueur n'accepte pas les teleportations");
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de To a ete cree.");
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