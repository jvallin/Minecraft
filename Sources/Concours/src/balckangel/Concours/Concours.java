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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Concours extends JavaPlugin
{
	public ConcoursListener listener = new ConcoursListener();
	public static Map<String, Integer> collection = new HashMap<String, Integer>();
	public static Map<String, Integer> victoire = new HashMap<String, Integer>();
    
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/Concours/config.yml");
	
	@SuppressWarnings("unchecked")
	public void onEnable() /* Actions ex�cut�es au d�marrage du plugin */
	{
		load();
    	if((Boolean) config.get("Configuration.Active"))
		{
			getServer().getPluginManager().registerEvents(listener, this);
		}
    	
    	if((new File("plugins/Concours/victoire.points")).exists())
		{
            try
            {
                FileInputStream fichier = new FileInputStream("plugins/Concours/victoire.points");
                ObjectInputStream ois = new ObjectInputStream(fichier);
                victoire = (Map<String, Integer>) ois.readObject();
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
    	
    	getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
			public void run() {
				checker();
			}
		}, 10L, 20L); // 20L => repeter toutes les minutes
	}

	public void onDisable() /* Actions ex�cut�es � la fermeture du plugin */
	{
		try
        {
            FileOutputStream fichier = new FileOutputStream("plugins/Concours/victoire.points");
            ObjectOutputStream oos = new ObjectOutputStream(fichier);
            oos.writeObject(victoire);
            oos.flush();
            oos.close();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
		
        collection.clear();
        victoire.clear();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!(Boolean) config.get("Configuration.Active") && !sender.getName().equals("CONSOLE")) /* si le plugin est d�sactiv� et que ce n'est pas la console */
		{
			sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
			return true;			
		}
	
		if(commandLabel.equalsIgnoreCase("cc")) /* Repr�sente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
		{
			if (args.length == 1) /* Si il y a un argument */
			{
				if(args[0].equalsIgnoreCase("high"))
				{
					sender.sendMessage(config.getString("Configuration.HighScore.Messages") + config.getString("Configuration.HighScore.Name") + " avec " + config.getInt("Configuration.HighScore.Nombre") + " point(s) concours.");
					return true;
				}
				else if(args[0].equalsIgnoreCase("all"))
				{				
					sender.sendMessage("Liste des points et victoires par joueurs : ");
					
					for(Player onlinePlayer : getServer().getOnlinePlayers())
					{
						sender.sendMessage(onlinePlayer.getName() + " : " + collection.get(onlinePlayer.getName()) + " point(s) concours et " + victoire.get(onlinePlayer.getName()) + " victoire(s).");
					}					
					
					return true;
				}
				else if (args[0].equalsIgnoreCase("but"))
				{
					sender.sendMessage(getBut());					
					return true;
				}
				
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
					else if(args[0].equalsIgnoreCase("start"))
					{
						if(!(Boolean) config.get("Configuration.Running"))
						{
							startMission();
							return true;
						}
						else
						{
							sender.sendMessage(config.getString("Configuration.Messages.StillRunning"));
							return true;
						}
					}
					else if(args[0].equalsIgnoreCase("stop"))
					{
						if((Boolean) config.get("Configuration.Running"))
						{
							stopMission();
							return true;
						}
						else
						{
							sender.sendMessage(config.getString("Configuration.Messages.Stop"));
							return true;
						}
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
						player.sendMessage("Vous avez : "+ collection.get(player.getName()) +" point(s) concours et " + victoire.get(player.getName()) + " victoire(s).");
						return true;
					}
					else if (args[0].equalsIgnoreCase("go"))
					{
						if((Boolean) config.get("Configuration.Running"))
						{
							if(! collection.containsKey(player.getName()))
							{
								collection.put(player.getName(), 0);
								getServer().broadcastMessage(player.getName()+config.getString("Configuration.Messages.Join"));
							}
							else
							{
								player.sendMessage(config.getString("Configuration.Messages.StillJoin"));
							}
							return true;
						}
						else
						{
							player.sendMessage(config.getString("Configuration.Messages.Stop"));
							return true;
						}
					}					
				}			
			}		
		}
		
		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
	}
	
	private void checker()
	{
		if ((Boolean) config.get("Configuration.Active"))
		{
			for (World w : getServer().getWorlds())
			{
				Long now = w.getTime();
				
				if ((now >= 12500 && now <= 12519) || (now >= 18500 && now <= 18519))
				{
					if(!(Boolean) config.get("Configuration.Running"))
					{
						startMission();
					}
				}
				else if ((now >= 0 && now <= 19) || (now >= 18300 && now <= 18319))
				{
					if((Boolean) config.get("Configuration.Running"))
					{
						stopMission();
					}
				}
				
				break;
			}
		}
	}
	
	public void startMission()
	{		
		Random random = new Random();
		int alea = random.nextInt(6 - 1) + 1; //(max - min) + min => minimum inclus / maximum exclu
		
		config.set("Configuration.Running", true);
		config.set("Configuration.Mission.Numero", alea);		
		saveYML();

        getServer().broadcastMessage(config.getString("Configuration.Messages.Start") +  getBut());
        
        
	}
	
	public void stopMission()
	{
		getServer().broadcastMessage(config.getString("Configuration.Messages.Stop"));
		
		if(collection.size() >= 2)
		{
	        getServer().broadcastMessage("Le gagnant est " + config.getString("Configuration.HighScore.Name") + " avec " + config.getString("Configuration.HighScore.Nombre") + " point(s) concours.");
	
	        if(!config.getString("Configuration.HighScore.Name").equals("personne"))
	        {
	        	addVictoire(config.getString("Configuration.HighScore.Name"));
	        }
		}
		else
		{
			getServer().broadcastMessage(config.getString("Configuration.Messages.NombreInf"));
		}
        
        config.set("Configuration.Running", false);
        config.set("Configuration.Mission.Numero", 0);
        config.set("Configuration.HighScore.Name", "personne");
		config.set("Configuration.HighScore.Nombre", 0);
        saveYML();
        
        collection.clear();
	}
	
	public void addVictoire(String nom)
	{
		int points = victoire.get(nom);
		
		points++;
		
		if(points == 3)
		{
			getServer().broadcastMessage(nom + " gagne un diamant.");
			
			Player winner = getServer().getPlayer(nom);
			winner.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
			
			victoire.clear();
			
			for(Player onlinePlayer : getServer().getOnlinePlayers())
			{
				victoire.put(onlinePlayer.getName(), 0);
			}
		}
		else
		{
			victoire.remove(nom);			
			victoire.put(nom, points);
		}
	}
	
	public String getBut()
	{
		String but = "";
		int num = config.getInt("Configuration.Mission.Numero");
		
		if(num == 0)
		{
			but = config.getString("Configuration.Messages.Stop");
		}
		else if(num == 1)
        {
        	but = config.getString("Configuration.Mission.Un.Description");
        }
        else if (num == 2)
        {
        	but = config.getString("Configuration.Mission.Deux.Description");
        }
        else if (num == 3)
        {
        	but = config.getString("Configuration.Mission.Trois.Description");
        }
        else if (num == 4)
        {
        	but = config.getString("Configuration.Mission.Quatre.Description");
        }
        else if (num == 5)
        {
        	but = config.getString("Configuration.Mission.Cinq.Description");
        }
        else if (num == 6)
        {
        	but = config.getString("Configuration.Mission.Six.Description");
        }
		
		return but;
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

                if(!victoire.containsKey(nom))
                {
                	victoire.put(nom,  0);
                }
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
			
			if((Boolean) config.get("Configuration.Running") && collection.containsKey(event.getEntity().getKiller().getName()))
			{
				if(event.getEntity().getKiller() instanceof Player)
				{
					Player killer = (Player) event.getEntity().getKiller();
					nom = killer.getName();
					int num = config.getInt("Configuration.Mission.Numero");
				
					if(num == 1 && event.getEntity() instanceof Zombie)
					{
						addPoints(nom, 1);
					}
					else if(num == 2 && event.getEntity() instanceof Skeleton)
					{
						addPoints(nom, 1);
					}
					else if(num == 3 && event.getEntity() instanceof Spider)
					{
						addPoints(nom, 1);
					}
					else if(num == 4 && event.getEntity() instanceof Creeper)
					{
						addPoints(nom, 1);
					}
					else if(num == 5 && event.getEntity() instanceof Enderman)
					{
						addPoints(nom, 1);
					}
					else if(num == 6 && event.getEntity() instanceof Witch)
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
		String name = "personne";
		int pts = 0;
		
		for(Player onlinePlayer : getServer().getOnlinePlayers())
		{
			if(collection.containsKey(onlinePlayer.getName()))
			{
				if(collection.get(onlinePlayer.getName()) > pts)
				{
					pts = collection.get(onlinePlayer.getName());
					name = onlinePlayer.getName();
				}
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
		else /* Cr�ation du fichier sinon */
		{	
			config = new YamlConfiguration();
			config.createSection("Configuration.Active");
			config.createSection("Configuration.Running");
			
			config.createSection("Configuration.Messages.Active"); /* Lorsque l'on active le plugin */
			config.createSection("Configuration.Messages.Desactive"); /* Lorsque l'on d�sactive le plugin */
			config.createSection("Configuration.Messages.Reload"); /* Lorsque le plugin est reload */
			config.createSection("Configuration.Messages.Permit"); /* Si l'utilisateur n'a pas le droit utiliser une commande */
			config.createSection("Configuration.Messages.StillRunning"); /* Si une mission est d�j� en cours */
			config.createSection("Configuration.Messages.Start"); /* Lorsque l'on commence une mission */
			config.createSection("Configuration.Messages.Stop"); /* Lorsque la mission se termine */
			config.createSection("Configuration.Messages.Join"); /* Lorsqu'un joueur rejoind le concours */
			config.createSection("Configuration.Messages.StillJoin"); /* si le joueur a deja rejoind le concours */
			config.createSection("Configuration.Messages.NombreInf"); /* si le nombre de participant est inf�rieur � 2 */

			config.createSection("Configuration.HighScore.Messages"); /* Message pour afficher le HighScore */
			config.createSection("Configuration.HighScore.Name"); /* Nom de la personne qui d�tient le HighScore */
			config.createSection("Configuration.HighScore.Nombre"); /* HighScore */

			config.createSection("Configuration.Mission.Numero"); /* Num�ro de la mission en cours */
			config.createSection("Configuration.Mission.Temps"); /* Temps en minute d'une mission */
			config.createSection("Configuration.Mission.Un.Description"); /* Description de la mission 1 */
			config.createSection("Configuration.Mission.Deux.Description"); /* Description de la mission 2 */
			config.createSection("Configuration.Mission.Trois.Description"); /* Description de la mission 3 */
			config.createSection("Configuration.Mission.Quatre.Description"); /* Description de la mission 4 */
			config.createSection("Configuration.Mission.Cinq.Description"); /* Description de la mission 5 */
			config.createSection("Configuration.Mission.Six.Description"); /* Description de la mission 6 */
			

			config.set("Configuration.Active", true);
			config.set("Configuration.Running", false);
			
			config.set("Configuration.Messages.Active", "Plugin Concours active");
			config.set("Configuration.Messages.Desactive", "Plugin Concours desactive");
			config.set("Configuration.Messages.Reload", "Plugin Concours reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.StillRunning", "Une mission est deja en cours");
			config.set("Configuration.Messages.Start", "Une nouvelle mission commence. ");
			config.set("Configuration.Messages.Stop", "La mission est terminee");
			config.set("Configuration.Messages.Join", " a rejoind le concours");
			config.set("Configuration.Messages.StillJoin", "Vous avez deja rejoind le concours");
			config.set("Configuration.Messages.NombreInf", "Il n'y avait pas assez de participant");

			config.set("Configuration.HighScore.Messages", "Le High Score est attribue a ");
			config.set("Configuration.HighScore.Name", "personne");
			config.set("Configuration.HighScore.Nombre", 0);

			config.set("Configuration.Mission.Numero", 0);
			config.set("Configuration.Mission.Temps", 5);
			config.set("Configuration.Mission.Un.Description", "Tuer le plus de zombies");
			config.set("Configuration.Mission.Deux.Description", "Tuer le plus de squelettes");
			config.set("Configuration.Mission.Trois.Description", "Tuer le plus d'araignees");
			config.set("Configuration.Mission.Quatre.Description", "Tuer le plus de creepers");
			config.set("Configuration.Mission.Cinq.Description", "Tuer le plus d'enderman");
			config.set("Configuration.Mission.Six.Description", "Tuer le plus de socieres");
			
			
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