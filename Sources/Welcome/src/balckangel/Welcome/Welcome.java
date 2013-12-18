/**
* 
* Welcome plugin Bukkit
* 
* @author Balckangel
* @version 1.0
* @date 01/02/2012
* @modification 11/12/2013
* 
* Principle : Permet d'afficher un message d'accueil lorsqu'un joueur se connecte
* Version de Bukkit : for MC 1.7.2
*
*/

package balckangel.Welcome;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class Welcome extends JavaPlugin
{
	public WelcomeListener listener = new WelcomeListener();
    
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/Welcome/config.yml");
	
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
	
		if(commandLabel.equalsIgnoreCase("w")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
		{
			if (args.length == 1) /* Si il y a un argument */
			{
				if (args[0].equalsIgnoreCase("show")) /* Si l'argument est "show" */
				{
					sender.sendMessage("1 : " + config.getString("Configuration.Messages.1"));
					sender.sendMessage("2 : " + config.getString("Configuration.Messages.2"));
					sender.sendMessage("3 : " + config.getString("Configuration.Messages.3"));
					sender.sendMessage("4 : " + config.getString("Configuration.Messages.4"));
					return true;
				}
				else if (args[0].equalsIgnoreCase("clear")) /* Si l'argument est "clear" */
				{
					config.set("Configuration.Messages.1", "");
					config.set("Configuration.Messages.2", "");
					config.set("Configuration.Messages.3", "");
					config.set("Configuration.Messages.4", "");
					
					saveYML();
					
					sender.sendMessage("Phrases réinitialisees");
					
					return true;
				}
				
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
			else if (args.length >= 2) /* Si il y a deux ou plus arguments */
			{
				int numero;
				
				try
				{
					numero = Integer.parseInt(args[0]);
				}
				catch (NumberFormatException e)
				{
					return false;
				}
				
				if (numero < 5 && numero > 0)
				{					
					if (args[1].equalsIgnoreCase("set")) /* Si l'argument est "set" */
					{
						String phrase = "";
						for(int i=2; i<args.length; i++)
						{
							phrase += args[i];
							if(i+1 != args.length)
							{
								phrase += " "; 
							}
						}
						
						if (numero == 1)
						{
							config.set("Configuration.Messages.1", phrase);
						}
						else if (numero == 2)
						{
							config.set("Configuration.Messages.2", phrase);
						}
						else if (numero == 3)
						{
							config.set("Configuration.Messages.3", phrase);
						}
						else if (numero == 4)
						{
							config.set("Configuration.Messages.4", phrase);
						}						
						
						saveYML();
						
						sender.sendMessage("Phrase " + numero + " changee en : " + phrase);
						
						return true;
					}
					else if (args[1].equalsIgnoreCase("read")) /* Si l'argument est "read" */
					{
						String phrase = "";
						
						if (numero == 1)
						{
							phrase = config.getString("Configuration.Messages.1");
						}
						else if (numero == 2)
						{
							phrase = config.getString("Configuration.Messages.2");
						}
						else if (numero == 3)
						{
							phrase = config.getString("Configuration.Messages.3");
						}
						else if (numero == 4)
						{
							phrase = config.getString("Configuration.Messages.4");
						}
						
						sender.sendMessage("La phrase "+ numero +" est : " + phrase);
						
						return true;					
					}
				}
			}			
		}
		
		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
	}
	
	/* Listener */
	public class WelcomeListener implements Listener
	{
		@EventHandler
		public void OnPlayerLog(PlayerJoinEvent event)
		{
			if((Boolean) config.get("Configuration.Active")) /* si le plugin est activé */
			{
				Player player = event.getPlayer();
				player.sendMessage(ChatColor.RED + "Bienvenue " + player.getName() + " !");
				player.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.1"));
				player.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.2"));
				player.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.3"));
				player.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.4"));
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
			config.createSection("Configuration.Messages.1");
			config.createSection("Configuration.Messages.2");
			config.createSection("Configuration.Messages.3");
			config.createSection("Configuration.Messages.4");
			
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin Welcome active");
			config.set("Configuration.Messages.Desactive", "Plugin Welcome desactive");
			config.set("Configuration.Messages.Reload", "Plugin Welcome reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.1", "");
			config.set("Configuration.Messages.2", "");
			config.set("Configuration.Messages.3", "");
			config.set("Configuration.Messages.4", "");
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de Welcome a ete cree.");
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