/**
* 
* PvP plugin Bukkit
* 
* @author Balckangel
* @version 1.1
* @date 20/04/2013
* @modification 22/12/2013
* 
* Principle : Permet de gérer le PvP (gestion des équipes, des points, de la zone de PvP)
* Version de Bukkit : for MC 1.7.2
*
*/

package balckangel.PvP;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public class PvP extends JavaPlugin
{
	public PvPListener listener = new PvPListener();
	public List<String> areaList = new ArrayList<String>();
	public Equipe equipe1 = new Equipe();
	public Equipe equipe2 = new Equipe();
	
	/* Config.yml */
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/PvP/config.yml");
	
	public void onEnable() /* Actions exécutées au démarrage du plugin */
	{
		load();
    	if((Boolean) config.get("Configuration.Active"))
		{
			getServer().getPluginManager().registerEvents(listener, this);
    	
			if(new File("plugins/PvP/equipe1.pvp").exists())
			{
				try
				{
					FileInputStream fichier1 = new FileInputStream("plugins/PvP/equipe1.pvp");
					ObjectInputStream ois1 = new ObjectInputStream(fichier1);
					equipe1 = (Equipe) ois1.readObject();
					ois1.close();
				}
				catch (java.io.IOException e)
				{
					e.printStackTrace();
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
			
			if(new File("plugins/PvP/equipe2.pvp").exists())
			{
				try
				{
					FileInputStream fichier2 = new FileInputStream("plugins/PvP/equipe2.pvp");
					ObjectInputStream ois2 = new ObjectInputStream(fichier2);
					equipe2 = (Equipe) ois2.readObject();
					ois2.close();
				}
				catch (java.io.IOException e)
				{
					e.printStackTrace();
				}
				catch (ClassNotFoundException e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	public void onDisable() /* Actions exécutées à la fermeture du plugin */
	{
		if((Boolean) config.get("Configuration.Active"))
		{
			try
			{
				FileOutputStream fichier1 = new FileOutputStream("plugins/PvP/equipe1.pvp");
				ObjectOutputStream oos1 = new ObjectOutputStream(fichier1);
				oos1.writeObject(equipe1);
				oos1.flush();
				oos1.close();
			}
			catch (java.io.IOException e)
			{
				e.printStackTrace();
			}
			
			try
			{
				FileOutputStream fichier2 = new FileOutputStream("plugins/PvP/equipe2.pvp");
				ObjectOutputStream oos2 = new ObjectOutputStream(fichier2);
				oos2.writeObject(equipe2);
				oos2.flush();
				oos2.close();
			}
			catch (java.io.IOException e)
			{
				e.printStackTrace();
			}
			
			areaList.clear();
			equipe1.Vider();
			equipe2.Vider();	
		}
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
	{
		if(!(Boolean) config.get("Configuration.Active") && !sender.getName().equals("CONSOLE")) /* si le plugin est désactivé et que ce n'est pas la console */
		{
			sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
			return true;			
		}
	
		if(commandLabel.equalsIgnoreCase("pvp")) /* Représente la commande que doit taper l'utilisateur pour utiliser le plugin (sans le "/") */
		{
			if (args.length == 1) /* Si il y a un argument */
			{
				if (args[0].equals("liste"))
				{
					sender.sendMessage("Equipe 1 : " + equipe1.toString());
					sender.sendMessage("Equipe 2 : " + equipe2.toString());
					
					return true;
				}
				
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
						
						for (Player player : getServer().getOnlinePlayers())
						{
							player.setSneaking(true);
						}
						
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Active"));
						saveYML();						
						return true;
					}
					else if (args[0].equalsIgnoreCase("off"))
					{
						config.set("Configuration.Active", false);
						
						for (Player player : getServer().getOnlinePlayers())
						{
							player.setSneaking(false);
						}
						
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Desactive"));
						saveYML();
						return true;
					}
					else if (args[0].equals("clear"))
					{
						equipe1.Vider();
						equipe2.Vider();
						
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
						sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
						return false;
					}
					
					String nom = player.getName();
					
					if(args[0].equals("go"))
					{
						if(!equipe1.Contient(nom) && !equipe2.Contient(nom))
						{
							if(equipe1.getSize() <= equipe2.getSize())
							{
								equipe1.ajouter(nom);
								player.sendMessage(config.getString("Configuration.Messages.Rejoint") + "1 !");
								getServer().broadcastMessage(nom+" a rejoint l'equipe 1");
							}
							else
							{
								equipe2.ajouter(nom);
								player.sendMessage(config.getString("Configuration.Messages.Rejoint") + "2 !");
								getServer().broadcastMessage(nom+" a rejoint l'equipe 2");
							}
						}
						else
						{
							player.sendMessage(config.getString("Configuration.Messages.Present"));
						}
						
						return true;
					}
				}
			}
			else if (args.length == 2) /* Si il y a deux arguments */
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
				
				if(args[0].equals("area"))
				{
					int rayon = 0;
					Location pos = null;
					
					try
					{
						rayon = Integer.parseInt(args[1]);
					}
					catch (NumberFormatException e)
					{
						player.sendMessage(config.getString("Configuration.Messages.Rayon"));
						return false;
					}
					
					if(rayon < 0)
					{
						player.sendMessage(config.getString("Configuration.Messages.Rayon"));
						return false;
					}
					
					if(rayon > config.getInt("Configuration.Nombre.Max"))
					{
						player.sendMessage(config.getString("Configuration.Messages.Max") + config.getInt("Configuration.Nombre.Max"));
						return false;
					}
					
					pos = player.getLocation();
					
					Location temp = pos.clone();
					
					int x = pos.getBlockX() + rayon;
					int z = pos.getBlockZ() + rayon;
					
					temp.setZ(z);
					
					for(int i = 0; i < (rayon*2)+1; i++)
					{					
						temp.setX(x);
						for(int j = 0; j < 200; j++)
						{
							temp.setY(j);
							temp.getBlock().setType(Material.BEDROCK);
						}
						x--;
					}
					x++;
					
					for(int i = 0; i < (rayon*2)+1; i++)
					{
						temp.setZ(z);
						z--;						
						for(int j = 0; j < 200; j++)
						{
							temp.setY(j);
							temp.getBlock().setType(Material.BEDROCK);
						}
					}
					z++;
					
					for(int i = 0; i < (rayon*2)+1; i++)
					{
						temp.setX(x);
						x++;						
						for(int j = 0; j < 200; j++)
						{
							temp.setY(j);
							temp.getBlock().setType(Material.BEDROCK);
						}
					}
					x--;
					
					for(int i = 0; i < (rayon*2); i++)
					{
						temp.setZ(z);
						z++;						
						for(int j = 0; j < 200; j++)
						{
							temp.setY(j);
							temp.getBlock().setType(Material.BEDROCK);
						}
					}
					
					return true;
				}
			}
		}
		
		sender.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Permit"));
		return false;
	}
	
	/* Listener */
	public class PvPListener implements Listener
	{
		@EventHandler
		public void OnPlayerMove(PlayerMoveEvent event)
		{
			if((Boolean) config.get("Configuration.Active")) /* si le plugin est activé */
			{
				Player player = (Player) event.getPlayer();
				String nom = player.getName();
				double y = player.getLocation().getY();
				
				if (y <= 188)
				{
					if(areaList.contains(nom))
					{
						areaList.remove(nom);
						player.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.Air"));
					}
				}
				else if(y > 190)
				{
					if(!areaList.contains(nom))
					{
						areaList.add(nom);
						player.sendMessage(ChatColor.RED + config.getString("Configuration.Messages.NoAir"));
					}
					player.damage(1);
				}
			}
		}
		
		@EventHandler
		public void OnEntityDeath(EntityDeathEvent event)
		{
			if((Boolean) config.get("Configuration.Active")) /* si le plugin est activé */
			{
				if (event.getEntity() instanceof Player) /* Si c'est un joueur */
				{
					Player player = (Player) event.getEntity();
					
					if(player.getKiller() instanceof Player)
					{
						Player tueur = player.getKiller();
						
						if(equipe1.Contient(tueur.getName()))
						{
							if(equipe1.Contient(player.getName()))
							{
								equipe1.moins(1);
								getServer().broadcastMessage("L'equipe 1 perd 1 point");
							}
							else
							{
								equipe1.plus(1);
								getServer().broadcastMessage("L'equipe 1 gagne 1 point");
							}
						}
						else if(equipe2.Contient(tueur.getName()))
						{
							if(equipe2.Contient(player.getName()))
							{
								equipe2.moins(1);
								getServer().broadcastMessage("L'equipe 2 perd 1 point");
							}
							else
							{
								equipe2.plus(1);
								getServer().broadcastMessage("L'equipe 2 gagne 1 point");
							}
						}
					}
				}
			}
		}
		
		@EventHandler
		public void OnPlayerLog(PlayerJoinEvent event)
		{
			if((Boolean) config.get("Configuration.Active")) /* si le plugin est activé */
			{
				Player player = event.getPlayer();
				player.setSneaking(true); /* Pour cacher le nom des joueurs */
			}
		}
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
			config.createSection("Configuration.Messages.Rayon"); /* Si le rayon n'est pas un nombre */
			config.createSection("Configuration.Messages.Max"); /* Si le joueur dépasse le rayon max */
			config.createSection("Configuration.Messages.Air"); /* Si le joueur est à une hauteur inférieur à 188 */
			config.createSection("Configuration.Messages.NoAir"); /* Si le joueur est à une hauteur supérieur à 190 */
			config.createSection("Configuration.Messages.Rejoint"); /* Lorsqu'un joueur rejoint une équipe */
			config.createSection("Configuration.Messages.Present"); /* Si le joueur est déjà dans une équipe */
			config.createSection("Configuration.Messages.Clear"); /* Lorsque l'on vide les équipes */
			
			config.createSection("Configuration.Nombre.Max"); /* Rayon maximum */
			
			
			config.set("Configuration.Active", false);
			
			config.set("Configuration.Messages.Active", "Plugin PvP active");
			config.set("Configuration.Messages.Desactive", "Plugin PvP desactive");
			config.set("Configuration.Messages.Reload", "Plugin PvP reload");
			config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
			config.set("Configuration.Messages.Rayon", "Veuillez entrer un nombre entier positif pour le rayon");
			config.set("Configuration.Messages.Max", "Veuillez entrer un rayon inferieur a ");
			config.set("Configuration.Messages.Air", "Vous revoila a l'air frais ...");
			config.set("Configuration.Messages.NoAir", "Attention, l'air se fait rare a cette hauteur, vous risquez de mourrir ...");
			config.set("Configuration.Messages.Rejoint", "Bienvenue dans l'equipe ");
			config.set("Configuration.Messages.Present", "Vous etes deja dans une equipe");
			config.set("Configuration.Messages.Clear", "Les equipes ont ete videes");
			
			config.set("Configuration.Nombre.Max", 500);
			
			
			saveYML();
			config = YamlConfiguration.loadConfiguration(configFile);
			System.out.println("Le fichier de configuration de PvP a ete cree.");
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