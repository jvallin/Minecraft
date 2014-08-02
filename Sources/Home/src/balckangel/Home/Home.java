/**
* 
* Home plugin Bukkit
* 
* @author Balckangel
* @version 1.1
* @date 14/08/2012
* @modification 02/08/2014
* 
* Principle : Permet de définir un point de téléportation permanent
* Version de Bukkit : for MC 1.7.10
*
*/

package balckangel.Home;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class Home extends JavaPlugin
{
	public static Map<String, Location> collection = new HashMap<String, Location>();
	public static List<String> liste = new ArrayList<String>();
    
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/Home/config.yml");
	
	public void onEnable() /* Actions exécutées au démarrage du plugin */
	{
		load();		
		/*
		if(new File("plugins/Home/positions.home").exists())
		{
			try
			{
				FileInputStream fichier = new FileInputStream("plugins/Home/positions.home");
				ObjectInputStream ois = new ObjectInputStream(fichier);
				collection = (HashMap<String, Location>) ois.readObject();
			}
			catch (java.io.IOException e)
			{
				e.printStackTrace();
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
		}*/
	}

	public void onDisable() /* Actions exécutées à la fermeture du plugin */
	{
		/*try
		{
			FileOutputStream fichier = new FileOutputStream("plugins/Home/positions.home");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(collection);
			oos.flush();
			oos.close();
		}
		catch (java.io.IOException e)
		{
			e.printStackTrace();
		}*/

		collection.clear();
		liste.clear();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!(Boolean) config.get("Configuration.Active") && !sender.getName().equals("CONSOLE")) /* si le plugin est désactivé et que ce n'est pas la console */
		{
			sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
			return true;			
		}
	
		if(commandLabel.equalsIgnoreCase("h")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
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
						sender.sendMessage(ChatColor.RED  + config.getString("Configuration.Messages.Permit"));
						return false;
					}
					
					String nom = player.getName(); /* nom du joueur */
					Location position = player.getLocation(); /* position courante du joueur */
					
					if (args[0].equalsIgnoreCase("set")) /* Si l'argument est "set" */
					{
						if(!collection.containsKey(nom)) /* Si le joueur n'est pas dans la map */
						{
							collection.put(nom, position);
							player.sendMessage(config.getString("Configuration.Messages.Set"));
						}
						else
						{
							liste.add(nom);
							player.sendMessage(config.getString("Configuration.Messages.Replace"));
						}					
						return true;
					}			
					else if (args[0].equalsIgnoreCase("go")) /* Si l'argument est "go" */
					{
						if(collection.containsKey(nom)) /* Si le joueur est dans la map */
						{
							player.teleport(collection.get(nom));
						}
						else
						{
							player.sendMessage(config.getString("Configuration.Messages.NoSet"));
						}
						return true;
					}			
					else if (args[0].equalsIgnoreCase("oui")) /* Si l'argument est "oui" */
					{
						if(liste.contains(nom))
						{
							collection.remove(nom);
							collection.put(nom, position);
							player.sendMessage(config.getString("Configuration.Messages.Set"));
						}
						else
						{
							player.sendMessage(config.getString("Configuration.Messages.Error"));
						}
						liste.remove(nom);
						return true;
					}
					else if (args[0].equalsIgnoreCase("non")) /* Si l'argument est "non" */
					{
						if(liste.contains(nom))
						{
							player.sendMessage(config.getString("Configuration.Messages.NoReplace"));
						}
						else
						{
							player.sendMessage(config.getString("Configuration.Messages.Error"));
						}
						liste.remove(nom);
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
			config.createSection("Configuration.Messages.Set"); /* Lorsque le joueur set la position */
			config.createSection("Configuration.Messages.NoSet"); /* Si le joueur n'a jamais set de position */
			config.createSection("Configuration.Messages.Replace"); /* Lorsque le joueur demande à set une nouvelle position */
			config.createSection("Configuration.Messages.NoReplace"); /* Lorsque le joueur dit "non" à la question */
			config.createSection("Configuration.Messages.Error"); /* Lorsque le joueur dit "oui" ou "non" alors que rien ne le demande */
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin Home Active");
			config.set("Configuration.Messages.Desactive", "Plugin Home Desactive");
			config.set("Configuration.Messages.Reload", "Plugin Home reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Set", "Position definie");
			config.set("Configuration.Messages.NoSet", "Position non definie");
			config.set("Configuration.Messages.Replace", "Une position est deja definie, voulez-vous la remplacer ?");
			config.set("Configuration.Messages.NoReplace", "Position inchangee");
			config.set("Configuration.Messages.Error", "Sans effet ...");
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de Home a ete cree.");
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