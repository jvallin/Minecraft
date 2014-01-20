/**
* 
* Points plugin Bukkit
* 
* @author Balckangel
* @version 1.6
* @date 23/12/2013
* @modification 20/01/2014
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Blaze;
import org.bukkit.entity.CaveSpider;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Ghast;
import org.bukkit.entity.PigZombie;
import org.bukkit.entity.Player;
import org.bukkit.entity.Skeleton;
import org.bukkit.entity.Slime;
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
				if(args[0].equalsIgnoreCase("high"))
				{
					sender.sendMessage(config.getString("Configuration.Messages.HighScore") + config.getString("Configuration.Name.HighScore") + " avec " + config.getInt("Configuration.Nombre.HighScore") + " point(s).");
					return true;
				}
				else if(args[0].equalsIgnoreCase("all"))
				{
					OfflinePlayer[] liste = getServer().getOfflinePlayers();
					
					ArrayList<Joueur> ordre = new ArrayList<Joueur>();
					
					int nombre = Math.min(liste.length, 10);
					int i =1;
					
					ordre = classement(liste);
					
					sender.sendMessage("Le classements des " + nombre + " meilleurs joueurs : ");
					
					for(Joueur j : ordre)
					{
						sender.sendMessage(i+") "+j.toString());
						i++;
					}
					
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
			else if (args.length == 3) /* Si il y a trois argument */
			{
				if(sender.getName().equals("CONSOLE")) /* si c'est la console */
				{
					if(args[0].equalsIgnoreCase("add"))
					{
						Player dest = getServer().getPlayerExact(args[1]);
						
						if(dest != null && collection.containsKey(dest.getName()))
						{
							int pts = 0;
							try
							{
								pts = Integer.parseInt(args[2]);
							}
							catch (NumberFormatException e)
							{
								sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Nombre"));
								return false;
							}
							
							addPoints(dest.getName(), pts);
						}
						else
						{
							sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Offline"));
						}
						
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
					
	                if(event.getEntity() instanceof Player
	                		|| (event.getEntity() instanceof Zombie && !(event.getEntity() instanceof PigZombie))
	                		|| event.getEntity() instanceof Spider 
	                		|| event.getEntity() instanceof Ghast)
	            	{                   
	                	level = addPoints(nom, 1);	                    
	            	}
	                else if (event.getEntity() instanceof Slime) //Slime ou MagmaCube
	                {
	                	Slime slime = (Slime) event.getEntity();
	                	
	                	if(slime.getSize() == 4)
	                	{
	                		level = addPoints(nom, 1);
	                	}
	                }
	             	else if(event.getEntity() instanceof CaveSpider)
	            	{
	                    level = addPoints(nom, 2);	                    
	            	} 
	            	else if(event.getEntity() instanceof Skeleton 
	            			|| event.getEntity() instanceof Creeper 
	            			|| event.getEntity() instanceof Witch)
	            	{
	                    level = addPoints(nom, 3);	                    
	            	} 
	            	else if(event.getEntity() instanceof PigZombie 
	            			|| event.getEntity() instanceof Blaze)
	                {
	            		level = addPoints(nom, 4);
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
				
				if(event.getEntity() instanceof Player)
                {
                    Player player = (Player) event.getEntity();
                    addPoints(player.getName(), -1);
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
		
		if(points > config.getInt("Configuration.Nombre.HighScore"))
		{
			if(! nom.equals(config.getString("Configuration.Name.HighScore")))
			{
				getServer().broadcastMessage(nom + " a battu le HighScore. Il est desormais de " + points + " point(s).");
				config.set("Configuration.Name.HighScore", nom);
			}
			config.set("Configuration.Nombre.HighScore", points);			
			saveYML();
		}
		
		if(((points)%50 < (points-pts)%50))
		{
			return ((points)/50);
		}
		else
		{
			return -1;
		}
	}
	
	public ArrayList<Joueur> classement(OfflinePlayer[] players)
	{
		ArrayList<Joueur> liste = new ArrayList<Joueur>();
		
		for(int i=0; i<players.length; i++)
		{
			if(collection.get(players[i].getName()) != null)
			{
				liste.add(new Joueur(players[i].getName(), collection.get(players[i].getName())));
			}
		}
		
		Collections.sort(liste, Collections.reverseOrder());

		return liste;
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
			config.createSection("Configuration.Messages.HighScore"); /* Message pour afficher le HighScore */
			config.createSection("Configuration.Name.HighScore"); /* Message pour afficher le HighScore */
			config.createSection("Configuration.Nombre.HighScore"); /* HighScore */
			config.createSection("Configuration.Messages.Offline"); /* Si le destinataire n'est pas connecté */
			config.createSection("Configuration.Messages.Nombre"); /* Si le nombre de points n'est pas un entier */
			
			
			config.set("Configuration.Active", true);
			
			config.set("Configuration.Messages.Active", "Plugin Points active");
			config.set("Configuration.Messages.Desactive", "Plugin Points desactive");
			config.set("Configuration.Messages.Reload", "Plugin Points reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Clear", "Les points ont ete reinitialises");
			config.set("Configuration.Messages.HighScore", "Le High Score est attribue a ");
			config.set("Configuration.Name.HighScore", "personne");
			config.set("Configuration.Nombre.HighScore", 0);
			config.set("Configuration.Messages.Offline", "Joueur non connecte. Utilisez la commande list pour afficher les joueurs connectes");
			config.set("Configuration.Messages.Nombre", "Veuillez entrer un nombre entier positif pour le nombre de points");
			
			
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