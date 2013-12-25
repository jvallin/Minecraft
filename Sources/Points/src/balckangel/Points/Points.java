/**
* 
* Points plugin Bukkit
* 
* @author Balckangel
* @version 1.1
* @date 23/12/2013
* @modification 25/12/2013
* 
* Principle : Permet de gerer les points individuels
* Version de Bukkit : for MC 1.7.2
*
*/

package balckangel.Points;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;

public class Points extends JavaPlugin
{
	public PointsListener listener = new PointsListener();
	public static Map<String, Integer> collection = new HashMap<String, Integer>();
    
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/Points/config.yml");
	
	@SuppressWarnings("unchecked")
	public void onEnable() /* Actions ex�cut�es au d�marrage du plugin */
	{
		load();
    	if((Boolean) config.get("Configuration.Active"))
		{
			getServer().getPluginManager().registerEvents(listener, this);
			
			if((new File("plugins/Points/collection.points")).exists())
			{
	            try
	            {
	                FileInputStream fichier = new FileInputStream("plugins/Points/collection.points");
	                ObjectInputStream ois = new ObjectInputStream(fichier);
	                collection = (Map<String, Integer>) ois.readObject();
	                ois.close();
	            }
	            catch(IOException e)
	            {
	                e.printStackTrace();
	            }
	            catch(ClassNotFoundException e)
	            {
	                e.printStackTrace();
	            }
			}
		}
	}

	public void onDisable() /* Actions ex�cut�es � la fermeture du plugin */
	{
		try
        {
            FileOutputStream fichier = new FileOutputStream("plugins/Points/collection.points");
            ObjectOutputStream oos = new ObjectOutputStream(fichier);
            oos.writeObject(collection);
            oos.flush();
            oos.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        collection.clear();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!(Boolean) config.get("Configuration.Active") && !sender.getName().equals("CONSOLE")) /* si le plugin est d�sactiv� et que ce n'est pas la console */
		{
			sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
			return true;			
		}
	
		if(commandLabel.equalsIgnoreCase("p")) /* Repr�sente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
		{
			if (args.length == 1) /* Si il y a un argument */
			{
				if(sender.getName().equals("CONSOLE")) /* si c'est la console */
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
					else if (args[0].equals("clear"))
	                {
	                    collection.clear();
	                    
	                    for (Player player : getServer().getOnlinePlayers())
						{
							collection.put(player.getName(), 0);
						}
	                    
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
					
					if(args[0].equalsIgnoreCase("pts"))
					{
						player.sendMessage("Vous avez : "+collection.get(player.getName())+" point(s).");
						return true;
					}
				}
			}		
		}
		
		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
	}
	
	/* Listener */
	public class PointsListener implements Listener
	{
		
		@EventHandler
		public void OnEntityDeath(EntityDeathEvent event)
        {
			if((Boolean) config.get("Configuration.Active"))
            {
                if(event.getEntity() instanceof Player)
                {
                    Player player = (Player) event.getEntity();
                    
                    if(player.getKiller() instanceof Player)
                    {
                        String nom = player.getKiller().getName();
                        
                        addPoints(nom, 1);
                    }
                    
                    addPoints(player.getName(), -1);
                }
             	else if(event.getEntity() instanceof Zombie)
            	{
                    if(event.getEntity().getKiller() instanceof Player)
                    {
                        String nom = event.getEntity().getKiller().getName();                        
                        addPoints(nom, 1);
                    }
            	} 
             	else if(event.getEntity() instanceof Spider)
            	{
                    if(event.getEntity().getKiller() instanceof Player)
                    {
                        String nom = event.getEntity().getKiller().getName();
                        addPoints(nom, 1);
                    }
            	} 
            	else if(event.getEntity() instanceof Skeleton)
            	{
                    if(event.getEntity().getKiller() instanceof Player)
                    {
                        String nom = event.getEntity().getKiller().getName();
                        addPoints(nom, 3);
                    }
            	} 
            	else if(event.getEntity() instanceof Creeper)
            	{
                    if(event.getEntity().getKiller() instanceof Player)
                    {
                        String nom = event.getEntity().getKiller().getName();
                        addPoints(nom, 3);
                    }
            	} 
            	else if((event.getEntity() instanceof Enderman) && (event.getEntity().getKiller() instanceof Player))
                {
                    String nom = event.getEntity().getKiller().getName();
                    addPoints(nom, 5);
                }
            }
        }
		
		@EventHandler
		public void OnPlayerLog(PlayerJoinEvent event)
        {
			if((Boolean) config.get("Configuration.Active"))
			{
                Player player = event.getPlayer();
                String nom = player.getName();
                if(!collection.containsKey(nom))
                {
                    collection.put(nom, 0);
                }
                Integer points = (Integer)collection.get(nom);
                player.sendMessage("Vous avez "+points+" point(s).");
			}
        }
	}
	
	public void addPoints(String nom, int pts)
	{
		int points = collection.get(nom);
		
		if(points + pts < 0)
		{
			points = 0;
		}
		else
		{
			points = points + pts;
		}
		
		if(pts < 0)
		{
			getServer().broadcastMessage(nom + " a perdu " + Math.abs(pts) + " point(s). Son total est de " + points + " point(s).");
		}
		else
		{
			getServer().broadcastMessage(nom + " a gagne " + pts + " point(s). Son total est de " + points + " point(s).");
		}
		
		collection.remove(nom);
		
		collection.put(nom, points);				
	}
	
	/* Fichier YML */
	public void load()
	{
		if(configFile.exists()) /* Lecture du fichier si il existe */
		{
			config = YamlConfiguration.loadConfiguration(configFile);
		}
		else /* Cr�ation du fichier sinon */
		{	
			config = new YamlConfiguration();
			config.createSection("Configuration.Active");
			
			config.createSection("Configuration.Messages.Active"); /* Lorsque l'on active le plugin */
			config.createSection("Configuration.Messages.Desactive"); /* Lorsque l'on d�sactive le plugin */
			config.createSection("Configuration.Messages.Reload"); /* Lorsque le plugin est reload */
			config.createSection("Configuration.Messages.Permit"); /* Si l'utilisateur n'a pas le droit utiliser une commande */
			
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin Points active");
			config.set("Configuration.Messages.Desactive", "Plugin Points desactive");
			config.set("Configuration.Messages.Reload", "Plugin Points reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de Points a ete cree.");
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