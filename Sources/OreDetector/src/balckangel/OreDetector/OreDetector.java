/**
* 
* OreDetector plugin Bukkit
* 
* @author Balckangel
* @version 1.1
* @date 15/08/2012
* @modification 25/12/2013
* 
* Principle : Permet au joueur de localiser le bloc spécifié le plus proche
* Version de Bukkit : for MC 1.7.2
*
*/

package balckangel.OreDetector;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

public class OreDetector extends JavaPlugin
{
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/OreDetector/config.yml");
	
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
	
		if(commandLabel.equalsIgnoreCase("od")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
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
			}
			else if (args.length == 4) /* Si il y a quatres arguments */
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
				
				String nom = ""; /* nom du bloc a rechercher */
				int r = 0; /* rayon de recherche */
				int up = 0; /* hauteur de recherche en dessus */
				int down = 0; /* hauteur de recherche en dessous */
				Location pos = player.getLocation();
				Material bloc = null;
				
				try
				{
					nom = args[0];
					r = Integer.parseInt(args[1]);
					up = Integer.parseInt(args[2]);
					down = Integer.parseInt(args[3]);
				}
				catch (NumberFormatException e)
				{
					player.sendMessage(config.getString("Configuration.Messages.Nombre"));
					return false;
				}
				
				if(r > config.getInt("Configuration.Nombre.Max") || up > config.getInt("Configuration.Nombre.Max") || down > config.getInt("Configuration.Nombre.Max"))
				{
					player.sendMessage(config.getString("Configuration.Messages.Max") + config.getInt("Configuration.Nombre.Max"));
					return false;
				}
				
				if(r < 0 || up < 0 || down <0)
				{
					player.sendMessage(config.getString("Configuration.Messages.Nombre"));
					return false;
				}
				
				try
				{
					bloc = Material.getMaterial(nom);
				}
				catch (Exception e)
				{
					player.sendMessage(config.getString("Configuration.Messages.Unknown"));
					return false;
				}
				
				player.sendMessage(config.getString("Configuration.Messages.Search"));
				
				Location temp = pos.clone();
				
				for(int y=pos.getBlockY()+up; y>=0 && y>=(pos.getBlockY()-down); y--) /* on varie la hauteur */
				{
					temp.setY(y);
					for(int x=-r; x<=r; x++)
					{
						temp.setX(pos.getX()+x);
						for(int z=-r; z<=r; z++)
						{
							temp.setZ(pos.getZ()+z);
							if(temp.getBlock().getType().equals(bloc))
							{
								player.sendMessage(config.getString("Configuration.Messages.Find")+"x="+temp.getBlockX()+" y="+temp.getBlockY()+" z="+temp.getBlockZ());
								return true;
							}
						}						
					}					
				}
				
				player.sendMessage(config.getString("Configuration.Messages.NotFind"));
				return true;
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
			config.createSection("Configuration.Messages.Nombre"); /* Si le rayon n'est pas un nombre */
			config.createSection("Configuration.Messages.Unknown"); /* Si le nom du bloc n'existe pas */
			config.createSection("Configuration.Messages.Search"); /* Lorsque le plugin recherche */
			config.createSection("Configuration.Messages.Max"); /* Si le joueur dépasse le rayon max */
			config.createSection("Configuration.Messages.Find"); /* Si une bloc est trouvé */
			config.createSection("Configuration.Messages.NotFind"); /* Si aucun bloc n'est trouvé */
			config.createSection("Configuration.Nombre.Max"); /* Rayon maximum de recherche */
			
			
			config.set("Configuration.Active", false);
			
			config.set("Configuration.Messages.Active", "Plugin OreDetector active");
			config.set("Configuration.Messages.Desactive", "Plugin OreDetector desactive");
			config.set("Configuration.Messages.Reload", "Plugin OreDetector reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Nombre", "Veuillez entrer un nombre entier positif pour l'ID, le rayon et les hauteurs");
			config.set("Configuration.Messages.Unknown", "Votre bloc n'existe pas");
			config.set("Configuration.Messages.Search", "Recherche en cours ...");
			config.set("Configuration.Messages.Max", "Veuillez entrer un rayon et des hauteurs inférieur à ");
			config.set("Configuration.Messages.Find", "Un bloc est situé à ");
			config.set("Configuration.Messages.NotFind", "Aucun bloc trouvé");
			config.set("Configuration.Nombre.Max", 200);
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de OreDetector a ete cree.");
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