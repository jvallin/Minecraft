/**
* 
* Niveler plugin Bukkit
* 
* @author Balckangel
* @version 1.1
* @date 21/08/2012
* @modification 25/12/2013
* 
* Principle : Permet de niveler le terrain.
* Version de Bukkit : for MC 1.7.2
*
*/

package balckangel.Niveler;

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

public class Niveler extends JavaPlugin
{    
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/Niveler/config.yml");
	
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
	
		if(commandLabel.equalsIgnoreCase("n")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
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
			else if (args.length == 3) /* Si il y a deux arguments */
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

				int h = 0; /* hauteur */
				int r = 0; /* rayon de recherche */
				String nom = "";
				Material bloc = null;
				Location pos = player.getLocation();
				
				try
				{
					h = Integer.parseInt(args[0]);
					r = Integer.parseInt(args[1]);
				}
				catch (NumberFormatException e)
				{
					player.sendMessage(config.getString("Configuration.Messages.Entier"));
					return false;
				}
				
				try
				{
					nom = args[2];
					bloc = Material.getMaterial(nom);
				}
				catch (Exception e)
				{
					player.sendMessage(config.getString("Configuration.Messages.Unknown"));
					return false;
				}
				
				if(r > config.getInt("Configuration.Nombre.Rayon.Max"))
				{
					player.sendMessage(config.getString("Configuration.Messages.Rayon.Max") + config.getInt("Configuration.Nombre.Rayon.Max"));
					return false;
				}
				
				if(h > config.getInt("Configuration.Nombre.Haut.Max"))
				{
					player.sendMessage(config.getString("Configuration.Messages.Haut.Max") + config.getInt("Configuration.Nombre.Haut.Max"));
					return false;
				}
				
				if(r < 0 || h < 0)
				{
					player.sendMessage(config.getString("Configuration.Messages.Entier"));
					return false;
				}
				
				player.sendMessage(config.getString("Configuration.Messages.Progress"));
				
				Location temp = pos.clone();
				
				for(int y=pos.getBlockY(); y<=(pos.getBlockY()+h); y++) /* on varie la hauteur */
				{
					temp.setY(y);
					for(int x=-r; x<=r; x++)
					{
						temp.setX(pos.getX()+x);
						for(int z=-r; z<=r; z++)
						{
							temp.setZ(pos.getZ()+z);
							temp.getBlock().setType(bloc);
						}						
					}					
				}
				
				player.sendMessage(config.getString("Configuration.Messages.Finish"));
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
			config.createSection("Configuration.Messages.Entier"); /* Si le rayon n'est pas un nombre */
			config.createSection("Configuration.Messages.Rayon.Max"); /* Si le joueur dépasse le rayon max */
			config.createSection("Configuration.Messages.Haut.Max"); /* Si le joueur dépasse la hauteur max */
			config.createSection("Configuration.Nombre.Rayon.Max"); /* Rayon maximum */
			config.createSection("Configuration.Nombre.Haut.Max"); /* Hauteur maximum */
			config.createSection("Configuration.Messages.Progress"); /* Lorsque le plugin aplani */
			config.createSection("Configuration.Messages.Finish"); /* Lorsque le plugin à fini */
			config.createSection("Configuration.Messages.Unknown"); /* Si le nom du bloc n'existe pas */
			
			
			config.set("Configuration.Active", false);
			
			config.set("Configuration.Messages.Active", "Plugin Niveler active");
			config.set("Configuration.Messages.Desactive", "Plugin Niveler desactive");
			config.set("Configuration.Messages.Reload", "Plugin Niveler reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Entier", "Veuillez entrer un nombre entier positif pour la hauteur et le rayon");
			config.set("Configuration.Messages.Rayon.Max", "Veuillez entrer un rayon inférieur à ");
			config.set("Configuration.Messages.Haut.Max", "Veuillez entrer une hauteur inférieur à ");
			config.set("Configuration.Nombre.Rayon.Max", 30);
			config.set("Configuration.Nombre.Haut.Max", 100);
			config.set("Configuration.Messages.Progress", "Aplanissement en cours ...");
			config.set("Configuration.Messages.Finish", "Aplanissement termine");
			config.set("Configuration.Messages.Unknown", "Votre bloc n'existe pas");
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de Niveler a ete cree.");
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