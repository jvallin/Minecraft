/**
* 
* Points plugin Bukkit
* 
* @author Balckangel
* @version 1.2
* @date 23/12/2013
* @modification 26/12/2013
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
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Spider;
import org.bukkit.entity.Witch;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.ItemStack;

public class Points extends JavaPlugin
{
	public PointsListener listener = new PointsListener();
	public static Map<String, Integer> collection = new HashMap<String, Integer>();
    
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/Points/config.yml");
	
	@SuppressWarnings("unchecked")
	public void onEnable() /* Actions exécutées au démarrage du plugin */
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

	public void onDisable() /* Actions exécutées à la fermeture du plugin */
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
		if(!(Boolean) config.get("Configuration.Active") && !sender.getName().equals("CONSOLE")) /* si le plugin est désactivé et que ce n'est pas la console */
		{
			sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
			return true;			
		}
	
		if(commandLabel.equalsIgnoreCase("p")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
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
	                    
	                    getServer().broadcastMessage(ChatColor.RED + config.getString("Configuration.Messages.Clear"));
	                    
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
				int level = 0;
				String nom = "";
				
				if(event.getEntity().getKiller() instanceof Player)
				{
					Player killer = (Player) event.getEntity().getKiller();
					nom = killer.getName();
					
	                if(event.getEntity() instanceof Player || event.getEntity() instanceof Zombie || event.getEntity() instanceof Spider)
	            	{                      
	                    level = addPoints(nom, 1);	                    
	            	}
	             	else if(event.getEntity() instanceof CaveSpider)
	            	{
	                    level = addPoints(nom, 2);	                    
	            	} 
	            	else if(event.getEntity() instanceof Skeleton || event.getEntity() instanceof Creeper || event.getEntity() instanceof Witch)
	            	{
	                    level = addPoints(nom, 3);	                    
	            	} 
	            	else if(event.getEntity() instanceof Enderman)
	                {
	            		level = addPoints(nom, 5);
	                }
	                
	                if(level > 0)
	                {
	                	if(level == 1)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
	                		getServer().broadcastMessage(nom + " a gagne une epee en bois");
	                	}
	                	else if (level == 2)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.LEATHER_BOOTS, 1));
	                		getServer().broadcastMessage(nom + " a gagne une paire de botte en cuir");
	                	}
	                	else if (level == 3)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.LEATHER_HELMET, 1));
	                		getServer().broadcastMessage(nom + " a gagne un casque en cuir");
	                	}
	                	else if (level == 4)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.LEATHER_LEGGINGS, 1));
	                		getServer().broadcastMessage(nom + " a gagne un pantalon en cuir");
	                	}
	                	else if (level == 5)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
	                		getServer().broadcastMessage(nom + " a gagne un plastron en cuir");
	                	}
	                	else if (level == 6)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
	                		getServer().broadcastMessage(nom + " a gagne une epee en pierre");
	                	}
	                	else if (level == 7)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.GOLD_BOOTS, 1));
	                		getServer().broadcastMessage(nom + " a gagne une paire de botte en or");
	                	}
	                	else if (level == 8)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.GOLD_HELMET, 1));
	                		getServer().broadcastMessage(nom + " a gagne un casque en or");
	                	}
	                	else if (level == 9)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.GOLD_LEGGINGS, 1));
	                		getServer().broadcastMessage(nom + " a gagne un pantalon en or");
	                	}
	                	else if (level == 10)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.GOLD_CHESTPLATE, 1));
	                		getServer().broadcastMessage(nom + " a gagne un plastron en or");
	                	}
	                	else if (level == 11)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.GOLD_SWORD, 1));
	                		getServer().broadcastMessage(nom + " a gagne une epee en or");
	                	}
	                	else if (level == 12)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.IRON_BOOTS, 1));
	                		getServer().broadcastMessage(nom + " a gagne une paire de botte en fer");
	                	}
	                	else if (level == 13)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.IRON_HELMET, 1));
	                		getServer().broadcastMessage(nom + " a gagne un casque en fer");
	                	}
	                	else if (level == 14)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.IRON_LEGGINGS, 1));
	                		getServer().broadcastMessage(nom + " a gagne un pantalon en fer");
	                	}
	                	else if (level == 15)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.IRON_CHESTPLATE, 1));
	                		getServer().broadcastMessage(nom + " a gagne un plastron en fer");
	                	}
	                	else if (level == 16)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
	                		getServer().broadcastMessage(nom + " a gagne une epee en fer");
	                	}
	                	else if (level == 17)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
	                		getServer().broadcastMessage(nom + " a gagne une paire de botte en maille");
	                	}
	                	else if (level == 18)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.CHAINMAIL_HELMET, 1));
	                		getServer().broadcastMessage(nom + " a gagne un casque en maille");
	                	}
	                	else if (level == 19)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
	                		getServer().broadcastMessage(nom + " a gagne un pantalon en maille");
	                	}
	                	else if (level == 20)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
	                		getServer().broadcastMessage(nom + " a gagne un plastron en maille");
	                	}
	                	else if (level == 21)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
	                		getServer().broadcastMessage(nom + " a gagne une epee en diamant");
	                	}
	                	else if (level == 22)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_BOOTS, 1));
	                		getServer().broadcastMessage(nom + " a gagne une paire de botte en diamant");
	                	}
	                	else if (level == 23)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_HELMET, 1));
	                		getServer().broadcastMessage(nom + " a gagne un casque en diamant");
	                	}
	                	else if (level == 24)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
	                		getServer().broadcastMessage(nom + " a gagne un pantalon en diamant");
	                	}
	                	else if (level == 25)
	                	{
	                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
	                		getServer().broadcastMessage(nom + " a gagne un plastron en diamant");
	                	}
	                }
            	}
				else
				{
					if(event.getEntity() instanceof Player)
	                {
	                    Player player = (Player) event.getEntity();
	                    addPoints(player.getName(), -1);
	                }
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
	
	public int addPoints(String nom, int pts)
	{
		int points = collection.get(nom);
		
		if(pts < 0)
		{
			getServer().broadcastMessage(nom + " est mort. Son total etait de " + points + " point(s).");
			points = 0;
		}
		else
		{
			points = points + pts;
			getServer().broadcastMessage(nom + " a gagne " + pts + " point(s). Son total est de " + points + " point(s).");
		}
		
		collection.remove(nom);
		
		collection.put(nom, points);
		
		if(((points)%50 < (points-pts)%50))
		{
			return ((points)/50);
		}
		else
		{
			return -1;
		}
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
			
			config.createSection("Configuration.Messages.Active"); /* Lorsque l'on active le plugin */
			config.createSection("Configuration.Messages.Desactive"); /* Lorsque l'on désactive le plugin */
			config.createSection("Configuration.Messages.Reload"); /* Lorsque le plugin est reload */
			config.createSection("Configuration.Messages.Permit"); /* Si l'utilisateur n'a pas le droit utiliser une commande */
			config.createSection("Configuration.Messages.Clear"); /* Lorsque l'on vide la Map */
			
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin Points active");
			config.set("Configuration.Messages.Desactive", "Plugin Points desactive");
			config.set("Configuration.Messages.Reload", "Plugin Points reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Clear", "Les points ont ete reinitialises");
			
			
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