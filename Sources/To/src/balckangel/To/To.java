/**
* 
* To plugin Bukkit
* 
* @author Balckangel
* @version 1.0
* @date 28/08/2012
* @modification 11/12/2013
* 
* Principle : Permet de se téléporter vers un autre joueur
* Version de Bukkit : for MC 1.7.2
*
*/

package balckangel.To;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class To extends JavaPlugin
{
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/To/config.yml");
	
	public void onEnable() /* Actions exécutées au démarrage du plugin */
	{
		load();
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
					
					Player dest = getServer().getPlayerExact(args[0]);
					
					if(dest != null)
					{
						player.teleport(dest);
						getServer().broadcastMessage(player.getName() + config.getString("Configuration.Messages.Teleport") + dest.getName());
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
		
		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
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
			
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin To active");
			config.set("Configuration.Messages.Desactive", "Plugin To desactive");
			config.set("Configuration.Messages.Reload", "Plugin To reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Offline", "Joueur non connecte. Appuyer sur 'TAB' pour afficher les joueurs connectes");
			config.set("Configuration.Messages.Teleport", " s'est teleporte vers ");
			
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