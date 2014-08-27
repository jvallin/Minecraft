/**
* 
* Concours plugin Bukkit
* 
* @author Balckangel
* @version 1.0
* @date 25/05/2014
* @modification 27/05/2014
* 
* Principle : Permet de lancer des missions
* Version de Bukkit : for MC 1.7.10
*
*/

package balckangel.Concours;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class Concours extends JavaPlugin
{
	public ConcoursListener listener = new ConcoursListener();
	public static Map<String, Integer> collection = new HashMap<String, Integer>();
    
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/Concours/config.yml");
	
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
	
		if(commandLabel.equalsIgnoreCase("cc")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
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
						player.sendMessage("Vous avez : "+collection.get(player.getName())+" point(s) concours.");
						return true;
					}					
				}
				
				if(args[0].equalsIgnoreCase("start"))
				{
					if(!(Boolean) config.get("Configuration.Running"))
					{
						mission();
						return true;
					}
					else
					{
						sender.sendMessage(config.getString("Configuration.Messages.StillRunning"));
						return true;
					}
				}
				else if(args[0].equalsIgnoreCase("high"))
				{
					sender.sendMessage(config.getString("Configuration.HighScore.Messages") + config.getString("Configuration.HighScore.Name") + " avec " + config.getInt("Configuration.HighScore.Nombre") + " point(s) concours.");
					return true;
				}
				else if(args[0].equalsIgnoreCase("all"))
				{				
					sender.sendMessage("Liste des joueurs concours : ");
					
					for(Player onlinePlayer : getServer().getOnlinePlayers())
					{
						sender.sendMessage(onlinePlayer.getName() + " : " + collection.get(onlinePlayer.getName()) + " point(s) concours.");
					}					
					
					return true;
				}
			}		
		}
		
		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
	}
	
	public void mission()
	{		
		Random random = new Random();
		int alea = random.nextInt(5 - 1) + 1; //(max - min) + min => minimum inclus / maximum exclu
		
		config.set("Configuration.Running", true);
		config.set("Configuration.Mission.Numero", alea);		
		saveYML();

        getServer().broadcastMessage(config.getString("Configuration.Messages.Start"));
        
        if(alea == 1)
        {
        	getServer().broadcastMessage(config.getString("Configuration.Mission.Un.Description"));
        }
        else if (alea == 2)
        {
        	getServer().broadcastMessage(config.getString("Configuration.Mission.Deux.Description"));
        }
        else if (alea == 3)
        {
        	getServer().broadcastMessage(config.getString("Configuration.Mission.Trois.Description"));
        }
        else if (alea == 4)
        {
        	getServer().broadcastMessage(config.getString("Configuration.Mission.Quatre.Description"));
        }
		
		BukkitScheduler scheduler = getServer().getScheduler();
        scheduler.scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                getServer().broadcastMessage(config.getString("Configuration.Messages.Stop"));
                getServer().broadcastMessage("Le gagnant est " + config.getString("Configuration.HighScore.Name") + " avec " + config.getString("Configuration.HighScore.Nombre") + " point(s).");
                
                config.set("Configuration.Running", false);
                config.set("Configuration.Mission.Numero", 0);
                config.set("Configuration.HighScore.Name", "personne");
    			config.set("Configuration.HighScore.Nombre", 0);
                saveYML();
                
                collection.clear();
				
				for(Player onlinePlayer : getServer().getOnlinePlayers())
				{
					collection.put(onlinePlayer.getName(), 0);
				}
            }
        }, ((1*60)+(0*1))*20L); //20L = 1 seconde    minute*60 + seconde*1
	}
	
	/* Listener */
	public class ConcoursListener implements Listener
	{
		@EventHandler
		public void OnPlayerLog(PlayerJoinEvent event)
        {
			if((Boolean) config.get("Configuration.Active"))
			{
                Player player = event.getPlayer();
                String nom = player.getName();

                collection.put(nom, 0);
			}
        }
		
		@EventHandler
		public void onPlayerLogout(PlayerQuitEvent event)
        {
			if((Boolean) config.get("Configuration.Active"))
			{
                Player player = event.getPlayer();
                String nom = player.getName();

                collection.remove(nom);
			}
        }
		
		@EventHandler
		public void OnEntityDeath(EntityDeathEvent event)
		{
			String nom = "";
			
			if((Boolean) config.get("Configuration.Running"))
			{
				if(event.getEntity().getKiller() instanceof Player)
				{
					Player killer = (Player) event.getEntity().getKiller();
					nom = killer.getName();
				
					if(config.getInt("Configuration.Mission.Numero") == 1 && event.getEntity() instanceof Zombie)
					{
						addPoints(nom, 1);
					}
					else if(config.getInt("Configuration.Mission.Numero") == 2 && event.getEntity() instanceof Skeleton)
					{
						addPoints(nom, 1);
					}
					else if(config.getInt("Configuration.Mission.Numero") == 3 && event.getEntity() instanceof Spider)
					{
						addPoints(nom, 1);
					}
					else if(config.getInt("Configuration.Mission.Numero") == 4 && event.getEntity() instanceof Creeper)
					{
						addPoints(nom, 1);
					}
				}
				
				if(event.getEntity() instanceof Player)
                {
                    Player player = (Player) event.getEntity();
                    addPoints(player.getName(), -1);
                    
                    getServer().broadcastMessage(maxPoints() + " est desormais premier du concours avec " + collection.get(maxPoints()) + " point(s).");
                    config.set("Configuration.HighScore.Name", maxPoints());
    				config.set("Configuration.HighScore.Nombre", collection.get(maxPoints()));			
    				saveYML();
                }
			}
		}
	}
	
	public void addPoints(String nom, int pts)
	{
		int points = collection.get(nom);
		
		if(pts < 0)
		{
			getServer().broadcastMessage(nom + " est mort. Son total etait de " + points + " point(s) concours.");
			points = 0;		
		}
		else
		{
			points = points + pts;
		}
		
		if(points > config.getInt("Configuration.HighScore.Nombre"))
		{
			if(! nom.equals(config.getString("Configuration.HighScore.Name")))
			{
				getServer().broadcastMessage(nom + " est desormais premier du concours avec " + points + " point(s).");
				config.set("Configuration.HighScore.Name", nom);
			}
			config.set("Configuration.HighScore.Nombre", points);			
			saveYML();
		}
		
		collection.remove(nom);
		
		collection.put(nom, points);
	}
	
	public String maxPoints()
	{
		String name = "";
		int pts = 0;
		
		for(Player onlinePlayer : getServer().getOnlinePlayers())
		{
			if(collection.get(onlinePlayer.getName()) > pts)
			{
				pts = collection.get(onlinePlayer.getName());
				name = onlinePlayer.getName();
			}
		}
		
		return name;
	}
	
	/* Fichier YML */
	public void load()
	{
		if(configFile.exists()) /* Lecture du fichier si il existe */
		{
			config = YamlConfiguration.loadConfiguration(configFile);
		}
		else /* Création du fichier sinon */
		{	
			config = new YamlConfiguration();
			config.createSection("Configuration.Active");
			config.createSection("Configuration.Running");
			
			config.createSection("Configuration.Messages.Active"); /* Lorsque l'on active le plugin */
			config.createSection("Configuration.Messages.Desactive"); /* Lorsque l'on désactive le plugin */
			config.createSection("Configuration.Messages.Reload"); /* Lorsque le plugin est reload */
			config.createSection("Configuration.Messages.Permit"); /* Si l'utilisateur n'a pas le droit utiliser une commande */
			config.createSection("Configuration.Messages.StillRunning"); /* Si une mission est déjà en cours */
			config.createSection("Configuration.Messages.Start"); /* Lorsque l'on commence une mission */
			config.createSection("Configuration.Messages.Stop"); /* Lorsque la mission se termine */

			config.createSection("Configuration.HighScore.Messages"); /* Message pour afficher le HighScore */
			config.createSection("Configuration.HighScore.Name"); /* Nom de la personne qui détient le HighScore */
			config.createSection("Configuration.HighScore.Nombre"); /* HighScore */

			config.createSection("Configuration.Mission.Numero"); /* Numéro de la mission en cours */
			config.createSection("Configuration.Mission.Un.Description"); /* Description de la mission 1 */
			config.createSection("Configuration.Mission.Deux.Description"); /* Description de la mission 2 */
			config.createSection("Configuration.Mission.Trois.Description"); /* Description de la mission 3 */
			config.createSection("Configuration.Mission.Quatre.Description"); /* Description de la mission 4 */
			

			config.set("Configuration.Active", true);
			config.set("Configuration.Running", false);
			
			config.set("Configuration.Messages.Active", "Plugin Concours active");
			config.set("Configuration.Messages.Desactive", "Plugin Concours desactive");
			config.set("Configuration.Messages.Reload", "Plugin Concours reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.StillRunning", "Une mission est deja en cours");
			config.set("Configuration.Messages.Start", "Une nouvelle mission commence");
			config.set("Configuration.Messages.Stop", "La mission est terminee");

			config.set("Configuration.HighScore.Messages", "Le High Score est attribue a ");
			config.set("Configuration.HighScore.Name", "personne");
			config.set("Configuration.HighScore.Nombre", 0);

			config.set("Configuration.Mission.Numero", 0);
			config.set("Configuration.Mission.Un.Description", "Tuer le plus de zombies");
			config.set("Configuration.Mission.Deux.Description", "Tuer le plus de squelettes");
			config.set("Configuration.Mission.Trois.Description", "Tuer le plus d'araignees");
			config.set("Configuration.Mission.Quatre.Description", "Tuer le plus de creepers");
			
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de Concours a ete cree.");
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