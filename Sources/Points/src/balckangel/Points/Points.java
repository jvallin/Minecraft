/**
* 
* Points plugin Bukkit
* 
* @author Balckangel
* @version 2.0
* @date 23/12/2013
* @modification 09/09/2014
* 
* Principle : Permet de gerer les points (generaux et mission)
* Version de Bukkit : for MC 1.7.10
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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.enchantments.Enchantment;
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
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

public class Points extends JavaPlugin
{
	public PointsListener listener = new PointsListener();
	public static Map<String, Joueur> collection = new HashMap<String, Joueur>();
	public static ArrayList<String> inscrit = new ArrayList<String>();
    
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
	                collection = (Map<String, Joueur>) ois.readObject();
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
        inscrit.clear();
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
					sender.sendMessage(config.getString("Configuration.HighScore.Points.Messages") + config.getString("Configuration.HighScore.Points.Name") + " avec " + config.getInt("Configuration.HighScore.Points.Nombre") + " point(s).");
					return true;
				}
				else if(args[0].equalsIgnoreCase("all"))
				{
					ArrayList<Joueur> ordre = new ArrayList<Joueur>();

					ordre = classement();
					
					int nombre = Math.min(ordre.size(), 10);
					int i =1;
										
					sender.sendMessage("Le classements des " + nombre + " meilleurs joueurs : ");
					
					for(Joueur j : ordre)
					{
						sender.sendMessage(i+") "+j.toString());
						i++;
					}
					
					return true;
				}
				else if(args[0].equalsIgnoreCase("highcc"))
				{
					sender.sendMessage(config.getString("Configuration.HighScore.Mission.Messages") + config.getString("Configuration.HighScore.Mission.Name") + " avec " + config.getInt("Configuration.HighScore.Mission.Nombre") + " point(s) mission.");
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
						getServer().broadcastMessage(ChatColor.RED + config.getString("Configuration.Messages.Reload"));
						return true;
					}
					else if (args[0].equalsIgnoreCase("on"))					
					{
						config.set("Configuration.Active", true);
						getServer().broadcastMessage(ChatColor.RED + config.getString("Configuration.Messages.Active"));
						saveYML();
						return true;
					}
					else if (args[0].equalsIgnoreCase("off"))
					{
						config.set("Configuration.Active", false);
						getServer().broadcastMessage(ChatColor.RED + config.getString("Configuration.Messages.Desactive"));
						saveYML();
						return true;
					}
					else if (args[0].equals("clear"))
	                {
						Collection<Joueur> liste = collection.values();
						
						collection.clear();
						
	                    for (Joueur player : liste)
						{
	                    	player.clearAll();
							collection.put(player.getNom(), player);
						}
	                    
	                    getServer().broadcastMessage(ChatColor.RED + config.getString("Configuration.Messages.Clear"));
	                    
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
						player.sendMessage(collection.get(player.getName()).toStringLog());
						return true;
					}
					else if (args[0].equalsIgnoreCase("go"))
					{
						if((Boolean) config.get("Configuration.Running"))
						{
							if(! inscrit.contains(player.getName()))
							{
								inscrit.add(player.getName());
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
				int num = config.getInt("Configuration.Mission.Numero");
				
				if(event.getEntity().getKiller() instanceof Player)
				{
					Player killer = (Player) event.getEntity().getKiller();
					nom = killer.getName();
					
					List<ItemStack> itemDrop = event.getDrops();
					
	                if(event.getEntity() instanceof Player
	                		|| event.getEntity() instanceof Spider 
	                		|| event.getEntity() instanceof Ghast)
	            	{                   
	                	if(num == 3 && event.getEntity() instanceof Spider)
						{
		                	level = addPoints(nom, 1, 1);
						}
	                	else
	                	{
	                		level = addPoints(nom, 1, 0);	                    
	                	}
	            	}
	                else if (event.getEntity() instanceof Slime) //Slime ou MagmaCube
	                {
	                	Slime slime = (Slime) event.getEntity();
	                	
	                	if(slime.getSize() == 4)
	                	{
	                		level = addPoints(nom, 1, 0);
	                	}
	                }
	                else if  (event.getEntity() instanceof Zombie)
	                {
	                	Zombie zombie = (Zombie) event.getEntity();
	                	
	                	if(num == 1)
						{
							addPoints(nom, 0, 1);
						}
	                	
	                	if(zombie.isBaby())
	                	{
	                		level = addPoints(nom, 2, 0);
	                	}
	                	else if(event.getEntity() instanceof PigZombie)
	                	{
	                		level = addPoints(nom, 4, 0);
	                	}
	                	else
	                	{
	                		level = addPoints(nom, 1, 0);
	                	}
	                }
	             	else if(event.getEntity() instanceof CaveSpider)
	            	{
	                    level = addPoints(nom, 2, 0);	                    
	            	} 
	            	else if(event.getEntity() instanceof Skeleton 
	            			|| event.getEntity() instanceof Creeper 
	            			|| event.getEntity() instanceof Witch)
	            	{
	                    if(num == 2 && event.getEntity() instanceof Skeleton)
						{
	                    	level = addPoints(nom, 3, 1);
						}
	                    else if(num == 4 && event.getEntity() instanceof Creeper)
						{
	                    	level = addPoints(nom, 3, 1);
						}
	                    else if(num == 6 && event.getEntity() instanceof Witch)
						{
							level = addPoints(nom, 3, 1);
						}
	                    else
	                    {
							level = addPoints(nom, 3, 0);
						}
	            	} 
	            	else if(event.getEntity() instanceof Blaze)
	                {
	            		if(num == 7)
						{
	            			level = addPoints(nom, 4, 1);
						}
	            		else
	            		{
	            			level = addPoints(nom, 4, 0);
	            		}
	                }
	            	else if(event.getEntity() instanceof Enderman)
	                {
	            		if(num == 5)
						{
	            			level = addPoints(nom, 5, 1);
						}
	            		else
	            		{
	            			level = addPoints(nom, 5, 0);
	            		}
	                }
	                
	                for(ItemStack item : itemDrop)
	                {
	                	if(num == 10 && (item.getType().equals(Material.RECORD_3)
			                			|| item.getType().equals(Material.RECORD_4)
			                			|| item.getType().equals(Material.RECORD_5)
			                			|| item.getType().equals(Material.RECORD_6)
			                			|| item.getType().equals(Material.RECORD_7)
			                			|| item.getType().equals(Material.RECORD_8)
			                			|| item.getType().equals(Material.RECORD_9)
			                			|| item.getType().equals(Material.RECORD_10)
			                			|| item.getType().equals(Material.RECORD_11)
			                			|| item.getType().equals(Material.RECORD_12)))
	                	{
	                		addPoints(nom, 0, 1);
	                	}
	                	else if (num == 11 && item.getType().equals(Material.ENDER_PEARL))
	                	{
	                		addPoints(nom, 0, 1);
	                	}
	                }
	                
	                
	                if(level > 0)
	                {
	                	switch(level)
	                	{
		                	case 1 :
		                		killer.getInventory().addItem(new ItemStack(Material.WOOD_SWORD, 1));
		                		getServer().broadcastMessage(nom + " a gagne une epee en bois");
		                		break;
		                	case 2 :
		                		killer.getInventory().addItem(new ItemStack(Material.LEATHER_BOOTS, 1));
		                		getServer().broadcastMessage(nom + " a gagne une paire de botte en cuir");
		                		break;
		                	case 3 :
		                		killer.getInventory().addItem(new ItemStack(Material.LEATHER_HELMET, 1));
		                		getServer().broadcastMessage(nom + " a gagne un casque en cuir");
		                		break;
		                	case 4 :
		                		killer.getInventory().addItem(new ItemStack(Material.LEATHER_LEGGINGS, 1));
		                		getServer().broadcastMessage(nom + " a gagne un pantalon en cuir");
		                		break;
		                	case 5 :
		                		killer.getInventory().addItem(new ItemStack(Material.LEATHER_CHESTPLATE, 1));
		                		getServer().broadcastMessage(nom + " a gagne un plastron en cuir");
		                		break;
		                	case 6 :
		                		killer.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
		                		getServer().broadcastMessage(nom + " a gagne une epee en pierre");
		                		break;
		                	case 7 :
		                		killer.getInventory().addItem(new ItemStack(Material.GOLD_BOOTS, 1));
		                		getServer().broadcastMessage(nom + " a gagne une paire de botte en or");
		                		break;
		                	case 8 :
		                		killer.getInventory().addItem(new ItemStack(Material.GOLD_HELMET, 1));
		                		getServer().broadcastMessage(nom + " a gagne un casque en or");
		                		break;
		                	case 9 :
		                		killer.getInventory().addItem(new ItemStack(Material.GOLD_LEGGINGS, 1));
		                		getServer().broadcastMessage(nom + " a gagne un pantalon en or");
		                		break;
		                	case 10 :
		                		killer.getInventory().addItem(new ItemStack(Material.GOLD_CHESTPLATE, 1));
		                		getServer().broadcastMessage(nom + " a gagne un plastron en or");
		                		break;
		                	case 11 :
		                		killer.getInventory().addItem(new ItemStack(Material.GOLD_SWORD, 1));
		                		getServer().broadcastMessage(nom + " a gagne une epee en or");
		                		break;
		                	case 12 :
		                		killer.getInventory().addItem(new ItemStack(Material.IRON_BOOTS, 1));
		                		getServer().broadcastMessage(nom + " a gagne une paire de botte en fer");
		                		break;
		                	case 13 :
		                		killer.getInventory().addItem(new ItemStack(Material.IRON_HELMET, 1));
		                		getServer().broadcastMessage(nom + " a gagne un casque en fer");
		                		break;
		                	case 14 :
		                		killer.getInventory().addItem(new ItemStack(Material.IRON_LEGGINGS, 1));
		                		getServer().broadcastMessage(nom + " a gagne un pantalon en fer");
		                		break;
		                	case 15 :
		                		killer.getInventory().addItem(new ItemStack(Material.IRON_CHESTPLATE, 1));
		                		getServer().broadcastMessage(nom + " a gagne un plastron en fer");
		                		break;
		                	case 16 :
		                		killer.getInventory().addItem(new ItemStack(Material.IRON_SWORD, 1));
		                		getServer().broadcastMessage(nom + " a gagne une epee en fer");
		                		break;
		                	case 17 :
		                		killer.getInventory().addItem(new ItemStack(Material.CHAINMAIL_BOOTS, 1));
		                		getServer().broadcastMessage(nom + " a gagne une paire de botte en maille");
		                		break;
		                	case 18 :
		                		killer.getInventory().addItem(new ItemStack(Material.CHAINMAIL_HELMET, 1));
		                		getServer().broadcastMessage(nom + " a gagne un casque en maille");
		                		break;
		                	case 19 :
		                		killer.getInventory().addItem(new ItemStack(Material.CHAINMAIL_LEGGINGS, 1));
		                		getServer().broadcastMessage(nom + " a gagne un pantalon en maille");
		                		break;
		                	case 20 :
		                		killer.getInventory().addItem(new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1));
		                		getServer().broadcastMessage(nom + " a gagne un plastron en maille");
		                		break;
		                	case 21 :
		                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD, 1));
		                		getServer().broadcastMessage(nom + " a gagne une epee en diamant");
		                		break;
		                	case 22 :
		                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_BOOTS, 1));
		                		getServer().broadcastMessage(nom + " a gagne une paire de botte en diamant");
		                		break;
		                	case 23 :
		                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_HELMET, 1));
		                		getServer().broadcastMessage(nom + " a gagne un casque en diamant");
		                		break;
		                	case 24 :
		                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_LEGGINGS, 1));
		                		getServer().broadcastMessage(nom + " a gagne un pantalon en diamant");
		                		break;
		                	case 25 :
		                		killer.getInventory().addItem(new ItemStack(Material.DIAMOND_CHESTPLATE, 1));
		                		getServer().broadcastMessage(nom + " a gagne un plastron en diamant");
		                		break;
		                	default :
		                		break;
	                	}
	                	
	                	if (level > 25)
	                	{
	                		ItemStack item = new ItemStack(Material.ENCHANTED_BOOK, 1);
	                		item = addBookEnchantment(item);
	                		killer.getInventory().addItem(item);
	                		getServer().broadcastMessage(nom + " a gagne un livre enchanté");
	                	}
	                }
            	}
				
				if(event.getEntity() instanceof Player)
                { 
                    Player player = (Player) event.getEntity();
                    addPoints(player.getName(), -1, -1);
                    
                    if(player.getName().equals(config.getString("Configuration.HighScore.Mission.Name")))
                    {
                    	String newWinner = maxPoints();
                    	
	                    getServer().broadcastMessage(newWinner + " est desormais premier de la mission avec " + collection.get(newWinner).getPointsMission() + " point(s) mission.");
	                    config.set("Configuration.HighScore.Mission.Name", newWinner);
	    				config.set("Configuration.HighScore.Mission.Nombre", collection.get(newWinner).getPointsMission());			
	    				saveYML();
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
                    collection.put(nom, new Joueur(player.getUniqueId(), nom, 0, 0, 0));
                }

                player.sendMessage(collection.get(nom).toStringLog());
			}
        }
		
		@EventHandler
		public void onPlayerLogout(PlayerQuitEvent event)
        {
			if((Boolean) config.get("Configuration.Active"))
			{
                Player player = event.getPlayer();
                String nom = player.getName();
                
                if(inscrit.contains(nom))
                {
                	inscrit.remove(nom);
                	getServer().broadcastMessage(nom+" quitte la mission. Il avait "+collection.get(nom).getPointsMission()+"point(s) mission.");
                	collection.get(nom).clearPointsMission();
                }
			}
        }
	}
	
	public String maxPoints()
	{
		String nameMax = "personne";
		int pts = 0;
		int maxPts = 0;
		
		for (String name : inscrit)
		{
			pts = collection.get(name).getPointsMission();
			if(pts != 0 && pts > maxPts)
			{
				maxPts = pts;
				nameMax = name;
    		}
		}
		
		return nameMax;
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
		
		razPointsMission();

        getServer().broadcastMessage(config.getString("Configuration.Messages.Start") + " " +  getBut());       
	}
	
	public void stopMission()
	{
		getServer().broadcastMessage(config.getString("Configuration.Messages.Stop"));
		
		if(inscrit.size() >= 2)
		{			
			ArrayList<Joueur> winnerList = getWinner();
			
			if(winnerList.isEmpty())
			{
				getServer().broadcastMessage(config.getString("Configuration.Messages.NoBody"));
			}
			else if(winnerList.size() == 1)
			{
				getServer().broadcastMessage("Le gagnant est " + winnerList.get(0).getNom() + " avec " + winnerList.get(0).getPointsMission() + " point(s) mission.");
			}
			else
			{
				String message = "Les gagnants sont : ";
				
				for(Joueur winner : winnerList)
				{
					message = message + winner.getNom() + ", ";
				}
				
				getServer().broadcastMessage(message.substring(0, message.length()-2));
			}
			
			addVictoire(winnerList);
			inscrit.clear();
		}
		else
		{
			getServer().broadcastMessage(config.getString("Configuration.Messages.NombreInf"));
		}
        
        config.set("Configuration.Running", false);
        config.set("Configuration.Mission.Numero", 0);
        config.set("Configuration.HighScore.Mission.Name", "personne");	
        config.set("Configuration.HighScore.Mission.Nombre", 0);			
        saveYML();
        
        razPointsMission();
	}
	
	public void razPointsMission()
	{
		Collection<Joueur> liste = collection.values();
		
        for (Joueur player : liste)
		{
			collection.get(player.getNom()).clearPointsMission();
		}
	}
	
	public void addVictoire(ArrayList<Joueur> array)
	{
		int victoires = 0;
		boolean clear = false;
		
		for(Joueur joueur : array)
		{
			joueur.addVictoires(1);
			victoires = joueur.getVictoires();
			
			if(victoires == 3)
			{
				getServer().broadcastMessage(joueur.getNom() + " gagne un diamant.");
				
				Player winner = getServer().getPlayer(joueur.getUUID());
				winner.getInventory().addItem(new ItemStack(Material.DIAMOND, 1));
				
				clear = true;
			}
		}		
		
		Collection<Joueur> liste = collection.values();
		
        for (Joueur player : liste)
		{
        	collection.get(player.getNom()).clearPointsMission();
        	if (clear)
    		{
        		collection.get(player.getNom()).clearVictoires();
    		}
		}
		
	}
	
	public int addPoints(String nom, int pts, int ptsCc)
	{
		int points = collection.get(nom).getPoints();
		int pointsMission = collection.get(nom).getPointsMission();		
		
		if(pts < 0 || ptsCc < 0)
		{
			getServer().broadcastMessage(nom + " est mort. Son total etait de " + points + " point(s) et "+pointsMission+" point(s) mission.");
			collection.get(nom).clearPoints();
			collection.get(nom).clearPointsMission();
			return -1;
		}
		else
		{
			if (pts > 0)
			{
				collection.get(nom).addPoints(pts);
				points = points + pts;
				//getServer().broadcastMessage(nom + " a gagne " + pts + " point(s). Son total est de " + points + " point(s).");
				
				if(points > config.getInt("Configuration.HighScore.Points.Nombre"))
				{
					if(! nom.equals(config.getString("Configuration.HighScore.Points.Name")))
					{
						getServer().broadcastMessage(nom + " a battu le HighScore Points. Il est desormais de " + points + " point(s).");
						config.set("Configuration.HighScore.Points.Name", nom);
					}
					config.set("Configuration.HighScore.Points.Nombre", points);			
					saveYML();
				}				
			}

			if (ptsCc > 0)
			{
				if(inscrit.contains(nom))
				{
					collection.get(nom).addPointsMission(ptsCc);					
					pointsMission = pointsMission + ptsCc;
					//getServer().broadcastMessage(nom + " a gagne " + ptsCc + " point(s) mission. Son total est de " + pointsMission + " point(s) mission.");
					
					if(pointsMission > config.getInt("Configuration.HighScore.Mission.Nombre"))
					{
						if(! nom.equals(config.getString("Configuration.HighScore.Mission.Name")))
						{
							getServer().broadcastMessage(nom + " a battu le HighScore Points Mission. Il est desormais de " + pointsMission + " point(s) mission.");
							config.set("Configuration.HighScore.Mission.Name", nom);
						}
						config.set("Configuration.HighScore.Mission.Nombre", pointsMission);			
						saveYML();
					}
				}
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
	}
	
	public ArrayList<Joueur> getWinner()
	{
		ArrayList<Joueur> winner = new ArrayList<Joueur>();
		int max = 0;
		int pts = 0;
		
		for(String nom : inscrit)
		{
			pts = collection.get(nom).getPointsMission();
			
			if(pts != 0)
			{
				if(pts > max)
				{
					winner.clear();
					max = pts;
					winner.add(collection.get(nom));
				}
				else if (pts == max)
				{
					winner.add(collection.get(nom));				
				}
			}
		}
		
		return winner;
	}
	
	public ArrayList<Joueur> classement()
	{		
		Collection<Joueur> liste = collection.values();
		ArrayList<Joueur> classement = new ArrayList<Joueur>();
		
        for (Joueur player : liste)
		{
        	classement.add(collection.get(player.getNom()));
		}
		
		Collections.sort(classement, Collections.reverseOrder());

		return classement;
	}
	
	public ItemStack addBookEnchantment(ItemStack item)
	{
        EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
        meta.addStoredEnchant(randomEnchant(), 1, true);
        item.setItemMeta(meta);
        return item;
    }
	
	public Enchantment randomEnchant()
	{
		Enchantment enchant = Enchantment.DURABILITY;
		
		int random = (int)(Math.random() * (25-1)) + 1;
		
		switch(random)
		{
			case 1 :
				enchant = Enchantment.ARROW_DAMAGE;
				break;
			case 2 :
				enchant = Enchantment.ARROW_FIRE;
				break;
			case 3 :
	    		enchant = Enchantment.ARROW_INFINITE;
				break;
			case 4 :
	    		enchant = Enchantment.ARROW_KNOCKBACK;
				break;
			case 5 :
	    		enchant = Enchantment.DAMAGE_ALL;
				break;
			case 6 :
	    		enchant = Enchantment.DAMAGE_ARTHROPODS;
				break;
			case 7 :
	    		enchant = Enchantment.DAMAGE_UNDEAD;
				break;
			case 8 :
	    		enchant = Enchantment.DIG_SPEED;
				break;
			case 9 :
	    		enchant = Enchantment.DURABILITY;
				break;
			case 10 :
	    		enchant = Enchantment.FIRE_ASPECT;
				break;
			case 11 :
	    		enchant = Enchantment.KNOCKBACK;
				break;
			case 12 :
	    		enchant = Enchantment.LOOT_BONUS_BLOCKS;
				break;
			case 13 :
	    		enchant = Enchantment.LOOT_BONUS_MOBS;
				break;
			case 14 :
	    		enchant = Enchantment.LUCK;
				break;
			case 15 :
	    		enchant = Enchantment.LURE;
				break;
			case 16 :
	    		enchant = Enchantment.OXYGEN;
				break;
			case 17:
	    		enchant = Enchantment.PROTECTION_ENVIRONMENTAL;
				break;
			case 18 :
	    		enchant = Enchantment.PROTECTION_EXPLOSIONS;
				break;
			case 19 :
	    		enchant = Enchantment.PROTECTION_FALL;
				break;
			case 20 :
	    		enchant = Enchantment.PROTECTION_FIRE;
				break;
			case 21 :
	    		enchant = Enchantment.PROTECTION_PROJECTILE;
				break;
			case 22 :
	    		enchant = Enchantment.SILK_TOUCH;
				break;
			case 23 :
	    		enchant = Enchantment.THORNS;
				break;
			case 24 :
	    		enchant = Enchantment.WATER_WORKER;
				break;
			default :
				enchant = Enchantment.DURABILITY;
				break;
    	}	
		
		return enchant;
	}
	
	public String getBut()
	{
		String but = "";
		int num = config.getInt("Configuration.Mission.Numero");
		
		switch(num)
		{
			case 1 :
				but = config.getString("Configuration.Mission.Un.Description");
				break;
			case 2 :
        		but = config.getString("Configuration.Mission.Deux.Description");
				break;
			case 3 :
        		but = config.getString("Configuration.Mission.Trois.Description");
				break;
			case 4 :
        		but = config.getString("Configuration.Mission.Quatre.Description");
				break;
			case 5 :
        		but = config.getString("Configuration.Mission.Cinq.Description");
				break;
			case 6 :
        		but = config.getString("Configuration.Mission.Six.Description");
				break;
			case 7 :
        		but = config.getString("Configuration.Mission.Sept.Description");
				break;
			case 10 :
        		but = config.getString("Configuration.Mission.Dix.Description");
				break;
			case 11 :
        		but = config.getString("Configuration.Mission.Onze.Description");
				break;
        	default : 
				but = config.getString("Configuration.Messages.Stop");
				break;
        }
		
		return but;
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
			config.createSection("Configuration.Messages.Clear"); /* Lorsque l'on vide la Map */
			config.createSection("Configuration.Messages.Offline"); /* Si le destinataire n'est pas connecté */
			config.createSection("Configuration.Messages.Nombre"); /* Si le nombre de points n'est pas un entier */
			config.createSection("Configuration.Messages.StillRunning"); /* Si une mission est déjà en cours */
			config.createSection("Configuration.Messages.Start"); /* Lorsque l'on commence une mission */
			config.createSection("Configuration.Messages.Stop"); /* Lorsque la mission se termine */
			config.createSection("Configuration.Messages.Join"); /* Lorsqu'un joueur rejoind la mission */
			config.createSection("Configuration.Messages.StillJoin"); /* si le joueur a deja rejoind la mission */
			config.createSection("Configuration.Messages.NombreInf"); /* si le nombre de participant est inférieur à 2 */
			config.createSection("Configuration.Messages.NoBody"); /* si personne n'a plus de 0 points mission */
			
			config.createSection("Configuration.HighScore.Points.Messages"); /* Message pour afficher le HighScore Points */
			config.createSection("Configuration.HighScore.Points.Name"); /* Nom de la personne qui détient le HighScore Points */
			config.createSection("Configuration.HighScore.Points.Nombre"); /* HighScore Points */
			config.createSection("Configuration.HighScore.Mission.Messages"); /* Message pour afficher le HighScore Points Mission */
			config.createSection("Configuration.HighScore.Mission.Name"); /* Nom de la personne qui détient le HighScore Points Mission */
			config.createSection("Configuration.HighScore.Mission.Nombre"); /* HighScore Points Mission */

			config.createSection("Configuration.Mission.Numero"); /* Numéro de la mission en cours */
			config.createSection("Configuration.Mission.Un.Description"); /* Description de la mission 1 */
			config.createSection("Configuration.Mission.Deux.Description"); /* Description de la mission 2 */
			config.createSection("Configuration.Mission.Trois.Description"); /* Description de la mission 3 */
			config.createSection("Configuration.Mission.Quatre.Description"); /* Description de la mission 4 */
			config.createSection("Configuration.Mission.Cinq.Description"); /* Description de la mission 5 */
			config.createSection("Configuration.Mission.Six.Description"); /* Description de la mission 6 */
			config.createSection("Configuration.Mission.Sept.Description"); /* Description de la mission 7 */

			config.createSection("Configuration.Mission.Dix.Description"); /* Description de la mission 10 */
			config.createSection("Configuration.Mission.Onze.Description"); /* Description de la mission 10 */
			
			
			config.set("Configuration.Active", true);
			config.set("Configuration.Running", false);
			
			config.set("Configuration.Messages.Active", "Plugin Points active");
			config.set("Configuration.Messages.Desactive", "Plugin Points desactive");
			config.set("Configuration.Messages.Reload", "Plugin Points reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Clear", "Les points ont ete reinitialises");
			config.set("Configuration.Messages.Offline", "Joueur non connecte. Utilisez la commande list pour afficher les joueurs connectes");
			config.set("Configuration.Messages.Nombre", "Veuillez entrer un nombre entier positif pour le nombre de points");
			config.set("Configuration.Messages.StillRunning", "Une mission est deja en cours");
			config.set("Configuration.Messages.Start", "Une nouvelle mission commence. Inscrivez-vous pour participer !");
			config.set("Configuration.Messages.Stop", "La mission est terminee");
			config.set("Configuration.Messages.Join", " a rejoind la mission");
			config.set("Configuration.Messages.StillJoin", "Vous avez deja rejoind la mission");
			config.set("Configuration.Messages.NombreInf", "Il n'y avait pas assez de participant");
			config.set("Configuration.Messages.NoBody", "Personne ne gagne la mission");			

			config.set("Configuration.HighScore.Points.Messages", "Le High Score Points est attribue a ");
			config.set("Configuration.HighScore.Points.Name", "personne");
			config.set("Configuration.HighScore.Points.Nombre", 0);
			config.set("Configuration.HighScore.Mission.Messages", "Le High Score Points Mission est attribue a ");
			config.set("Configuration.HighScore.Mission.Name", "personne");
			config.set("Configuration.HighScore.Mission.Nombre", 0);

			config.set("Configuration.Mission.Numero", 0);
			config.set("Configuration.Mission.Un.Description", "Tuer le plus de zombies");
			config.set("Configuration.Mission.Deux.Description", "Tuer le plus de squelettes");
			config.set("Configuration.Mission.Trois.Description", "Tuer le plus d'araignees");
			config.set("Configuration.Mission.Quatre.Description", "Tuer le plus de creepers");
			config.set("Configuration.Mission.Cinq.Description", "Tuer le plus d'enderman");
			config.set("Configuration.Mission.Six.Description", "Tuer le plus de socieres");
			config.set("Configuration.Mission.Sept.Description", "Tuer le plus de blazes");

			config.set("Configuration.Mission.Dix.Description", "Obtenir le plus de CD");
			config.set("Configuration.Mission.Onze.Description", "Obtenir le plus d'Ender Pearl");
			
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