// Decompiled by Jad v1.5.8g. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 
// Source File Name:   Quest.java

package balckangel.Quest;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Quest extends JavaPlugin
{
    public class QuestListener
        implements Listener
    {

        public void OnPlayerLog(PlayerJoinEvent event)
        {
            if(((Boolean)Quest.config.get("Configuration.Active")).booleanValue())
            {
                Player player = event.getPlayer();
                String nom = player.getName();
                if(!Quest.collection.containsKey(nom))
                    Quest.collection.put(nom, Integer.valueOf(0));
                Integer points = (Integer)Quest.collection.get(nom);
                player.sendMessage((new StringBuilder()).append(ChatColor.RED).append("Vous avez ").append(points).append(" point(s) de quetes.").toString());
            }
        }

        public void OnEntityDeath(EntityDeathEvent event)
        {
            if(((Boolean)Quest.config.get("Configuration.Active")).booleanValue())
                if(event.getEntity() instanceof Player)
                {
                    Player player = (Player)event.getEntity();
                    if(player.getKiller() instanceof Player)
                    {
                        String nom = player.getKiller().getName();
                        Succes(nom, "Configuration.Defi.Mort.Statut", "Configuration.Defi.Mort.Pts", "Configuration.Defi.Mort.Succes", "Configuration.Defi.Mort.Desc");
                    }
                } else
                if(event.getEntity() instanceof Zombie)
                {
                    if(event.getEntity().getKiller() instanceof Player)
                    {
                        String nom = event.getEntity().getKiller().getName();
                        Succes(nom, "Configuration.Defi.Zombie.Statut", "Configuration.Defi.Zombie.Pts", "Configuration.Defi.Zombie.Succes", "Configuration.Defi.Zombie.Desc");
                    }
                } else
                if(event.getEntity() instanceof Spider)
                {
                    if(event.getEntity().getKiller() instanceof Player)
                    {
                        String nom = event.getEntity().getKiller().getName();
                        Succes(nom, "Configuration.Defi.Araignee.Statut", "Configuration.Defi.Araignee.Pts", "Configuration.Defi.Araignee.Succes", "Configuration.Defi.Araignee.Desc");
                    }
                } else
                if(event.getEntity() instanceof Skeleton)
                {
                    if(event.getEntity().getKiller() instanceof Player)
                    {
                        String nom = event.getEntity().getKiller().getName();
                        Succes(nom, "Configuration.Defi.Squelette.Statut", "Configuration.Defi.Squelette.Pts", "Configuration.Defi.Squelette.Succes", "Configuration.Defi.Squelette.Desc");
                    }
                } else
                if(event.getEntity() instanceof Creeper)
                {
                    if(event.getEntity().getKiller() instanceof Player)
                    {
                        String nom = event.getEntity().getKiller().getName();
                        Succes(nom, "Configuration.Defi.Creeper.Statut", "Configuration.Defi.Creeper.Pts", "Configuration.Defi.Creeper.Succes", "Configuration.Defi.Creeper.Desc");
                    }
                } else
                if((event.getEntity() instanceof Enderman) && (event.getEntity().getKiller() instanceof Player))
                {
                    String nom = event.getEntity().getKiller().getName();
                    Succes(nom, "Configuration.Defi.Enderman.Statut", "Configuration.Defi.Enderman.Pts", "Configuration.Defi.Enderman.Succes", "Configuration.Defi.Enderman.Desc");
                }
        }

        public void onPlayerPickup(PlayerPickupItemEvent event)
        {
            if(((Boolean)Quest.config.get("Configuration.Active")).booleanValue())
            {
                int item = event.getItem().getItemStack().getType().getId();
                Player player = event.getPlayer();
                player.sendMessage((new StringBuilder("item : ")).append(item).toString());
                String nom = player.getName();
                if(item == 17)
                    Succes(nom, "Configuration.Defi.Bois.Statut", "Configuration.Defi.Bois.Pts", "Configuration.Defi.Bois.Succes", "Configuration.Defi.Bois.Desc");
                if(item == 263)
                    Succes(nom, "Configuration.Defi.Charbon.Statut", "Configuration.Defi.Charbon.Pts", "Configuration.Defi.Charbon.Succes", "Configuration.Defi.Charbon.Desc");
                if(item == 264)
                    Succes(nom, "Configuration.Defi.Diamant.Statut", "Configuration.Defi.Diamant.Pts", "Configuration.Defi.Diamant.Succes", "Configuration.Defi.Diamant.Desc");
                if(item == 265)
                    Succes(nom, "Configuration.Defi.Fer.Statut", "Configuration.Defi.Fer.Pts", "Configuration.Defi.Fer.Succes", "Configuration.Defi.Fer.Desc");
                if(item == 266)
                    Succes(nom, "Configuration.Defi.Or.Statut", "Configuration.Defi.Or.Pts", "Configuration.Defi.Or.Succes", "Configuration.Defi.Or.Desc");
                if(item == 296)
                    Succes(nom, "Configuration.Defi.Ble.Statut", "Configuration.Defi.Ble.Pts", "Configuration.Defi.Ble.Succes", "Configuration.Defi.Ble.Desc");
                if(item == 388)
                    Succes(nom, "Configuration.Defi.Emeraude.Statut", "Configuration.Defi.Emeraude.Pts", "Configuration.Defi.Emeraude.Succes", "Configuration.Defi.Emeraude.Desc");
            }
        }

        public void onFurnaceExtractPickup(FurnaceExtractEvent event)
        {
            if(((Boolean)Quest.config.get("Configuration.Active")).booleanValue())
            {
                int item = event.getItemType().getId();
                Player player = event.getPlayer();
                player.sendMessage((new StringBuilder("item : ")).append(item).toString());
                String nom = player.getName();
                if(item == 263)
                    Succes(nom, "Configuration.Defi.Charbon.Statut", "Configuration.Defi.Charbon.Pts", "Configuration.Defi.Charbon.Succes", "Configuration.Defi.Charbon.Desc");
                if(item == 265)
                    Succes(nom, "Configuration.Defi.Fer.Statut", "Configuration.Defi.Fer.Pts", "Configuration.Defi.Fer.Succes", "Configuration.Defi.Fer.Desc");
                if(item == 266)
                    Succes(nom, "Configuration.Defi.Or.Statut", "Configuration.Defi.Or.Pts", "Configuration.Defi.Or.Succes", "Configuration.Defi.Or.Desc");
            }
        }

        final Quest this$0;

        public QuestListener()
        {
            this$0 = Quest.this;
            super();
        }
    }


    public Quest()
    {
        listener = new QuestListener();
    }

    public void onEnable()
    {
        load();
        if(((Boolean)config.get("Configuration.Active")).booleanValue())
            getServer().getPluginManager().registerEvents(listener, this);
        if((new File("plugins/Quest/liste.quest")).exists())
            try
            {
                FileInputStream fichier = new FileInputStream("plugins/Quest/liste.quest");
                ObjectInputStream ois = new ObjectInputStream(fichier);
                collection = (HashMap)ois.readObject();
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

    public void onDisable()
    {
        try
        {
            FileOutputStream fichier = new FileOutputStream("plugins/Quest/liste.quest");
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

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String args[])
    {
        if(!((Boolean)config.get("Configuration.Active")).booleanValue() && !sender.getName().equals("CONSOLE"))
        {
            sender.sendMessage(config.getString("Configuration.Messages.Desactive"));
            return true;
        }
        if(commandLabel.equalsIgnoreCase("q"))
            if(args.length == 1)
            {
                if(sender.getName().equals("CONSOLE"))
                {
                    if(args[0].equalsIgnoreCase("reload"))
                    {
                        load();
                        sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append(config.getString("Configuration.Messages.Reload")).toString());
                        return true;
                    }
                    if(args[0].equalsIgnoreCase("on"))
                    {
                        config.set("Configuration.Active", Boolean.valueOf(true));
                        sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append(config.getString("Configuration.Messages.Active")).toString());
                        saveYML();
                        return true;
                    }
                    if(args[0].equalsIgnoreCase("off"))
                    {
                        config.set("Configuration.Active", Boolean.valueOf(false));
                        sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append(config.getString("Configuration.Messages.Desactive")).toString());
                        saveYML();
                        return true;
                    } else
                    {
                        sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append(config.getString("Configuration.Messages.Permit")).toString());
                        return false;
                    }
                }
                Player player = null;
                try
                {
                    player = (Player)sender;
                }
                catch(Exception e)
                {
                    sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append(config.getString("Configuration.Messages.Permit")).toString());
                    return false;
                }
                String nom = player.getName();
                if(args[0].equals("pts"))
                {
                    player.sendMessage((new StringBuilder("Vous avez ")).append(collection.get(nom)).append(" point(s).").toString());
                    return true;
                }
                if(args[0].equals("clear"))
                {
                    collection.clear();
                    return true;
                }
            } else
            if(args.length == 2 && args[0].equals("liste"))
            {
                int page = 0;
                try
                {
                    page = Integer.parseInt(args[1]);
                }
                catch(NumberFormatException e)
                {
                    sender.sendMessage(config.getString("Configuration.Messages.Entier"));
                    return false;
                }
                if(page == 0)
                {
                    sender.sendMessage(config.getString("Configuration.Defi.Bois.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Charbon.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Ble.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Mort.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Zombie.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Araignee.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Squelette.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Creeper.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Enderman.Desc"));
                    sender.sendMessage(config.getString("Configuration.Messages.Suivant"));
                } else
                if(page == 1)
                {
                    sender.sendMessage(config.getString("Configuration.Defi.Fer.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Or.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Diamant.Desc"));
                    sender.sendMessage(config.getString("Configuration.Defi.Emeraude.Desc"));
                } else
                {
                    sender.sendMessage(config.getString("Configuration.Messages.Fin"));
                }
                return true;
            }
        sender.sendMessage((new StringBuilder()).append(ChatColor.RED).append(config.getString("Configuration.Messages.Permit")).toString());
        return false;
    }

    public void Succes(String nom, String statut, String pts, String succes, String desc)
    {
        if(((Boolean)config.get(statut)).booleanValue())
        {
            config.set(statut, Boolean.valueOf(false));
            config.set(desc, (new StringBuilder(String.valueOf(config.getString(desc)))).append(" - Accompli par ").append(nom).toString());
            Integer points = (Integer)collection.get(nom);
            points = Integer.valueOf(points.intValue() + config.getInt(pts));
            collection.remove(nom);
            collection.put(nom, points);
            getServer().broadcastMessage((new StringBuilder(String.valueOf(nom))).append(config.getString(succes)).toString());
            saveYML();
        }
    }

    public void load()
    {
        if(configFile.exists())
        {
            config = YamlConfiguration.loadConfiguration(configFile);
        } else
        {
            config = new YamlConfiguration();
            config.createSection("Configuration.Active");
            config.createSection("Configuration.Messages.Active");
            config.createSection("Configuration.Messages.Desactive");
            config.createSection("Configuration.Messages.Reload");
            config.createSection("Configuration.Messages.Permit");
            config.createSection("Configuration.Messages.Entier");
            config.createSection("Configuration.Messages.Suivant");
            config.createSection("Configuration.Messages.Fin");
            config.createSection("Configuration.Defi.Bois.Statut");
            config.createSection("Configuration.Defi.Bois.Desc");
            config.createSection("Configuration.Defi.Bois.Pts");
            config.createSection("Configuration.Defi.Bois.Succes");
            config.createSection("Configuration.Defi.Charbon.Statut");
            config.createSection("Configuration.Defi.Charbon.Desc");
            config.createSection("Configuration.Defi.Charbon.Pts");
            config.createSection("Configuration.Defi.Charbon.Succes");
            config.createSection("Configuration.Defi.Ble.Statut");
            config.createSection("Configuration.Defi.Ble.Desc");
            config.createSection("Configuration.Defi.Ble.Pts");
            config.createSection("Configuration.Defi.Ble.Succes");
            config.createSection("Configuration.Defi.Mort.Statut");
            config.createSection("Configuration.Defi.Mort.Desc");
            config.createSection("Configuration.Defi.Mort.Pts");
            config.createSection("Configuration.Defi.Mort.Succes");
            config.createSection("Configuration.Defi.Zombie.Statut");
            config.createSection("Configuration.Defi.Zombie.Desc");
            config.createSection("Configuration.Defi.Zombie.Pts");
            config.createSection("Configuration.Defi.Zombie.Succes");
            config.createSection("Configuration.Defi.Araignee.Statut");
            config.createSection("Configuration.Defi.Araignee.Desc");
            config.createSection("Configuration.Defi.Araignee.Pts");
            config.createSection("Configuration.Defi.Araignee.Succes");
            config.createSection("Configuration.Defi.Squelette.Statut");
            config.createSection("Configuration.Defi.Squelette.Desc");
            config.createSection("Configuration.Defi.Squelette.Pts");
            config.createSection("Configuration.Defi.Squelette.Succes");
            config.createSection("Configuration.Defi.Creeper.Statut");
            config.createSection("Configuration.Defi.Creeper.Desc");
            config.createSection("Configuration.Defi.Creeper.Pts");
            config.createSection("Configuration.Defi.Creeper.Succes");
            config.createSection("Configuration.Defi.Enderman.Statut");
            config.createSection("Configuration.Defi.Enderman.Desc");
            config.createSection("Configuration.Defi.Enderman.Pts");
            config.createSection("Configuration.Defi.Enderman.Succes");
            config.createSection("Configuration.Defi.Fer.Statut");
            config.createSection("Configuration.Defi.Fer.Desc");
            config.createSection("Configuration.Defi.Fer.Pts");
            config.createSection("Configuration.Defi.Fer.Succes");
            config.createSection("Configuration.Defi.Or.Statut");
            config.createSection("Configuration.Defi.Or.Desc");
            config.createSection("Configuration.Defi.Or.Pts");
            config.createSection("Configuration.Defi.Or.Succes");
            config.createSection("Configuration.Defi.Diamant.Statut");
            config.createSection("Configuration.Defi.Diamant.Desc");
            config.createSection("Configuration.Defi.Diamant.Pts");
            config.createSection("Configuration.Defi.Diamant.Succes");
            config.createSection("Configuration.Defi.Emeraude.Statut");
            config.createSection("Configuration.Defi.Emeraude.Desc");
            config.createSection("Configuration.Defi.Emeraude.Pts");
            config.createSection("Configuration.Defi.Emeraude.Succes");
            config.set("Configuration.Active", Boolean.valueOf(true));
            config.set("Configuration.Messages.Active", "Plugin Quest active");
            config.set("Configuration.Messages.Desactive", "Plugin Quest desactive");
            config.set("Configuration.Messages.Reload", "Plugin Quest reload");
            config.set("Configuration.Messages.Permit", "Vous ne pouvez pas utiliser cette commande");
            config.set("Configuration.Messages.Entier", "Veuillez entrer un nombre entier positif pour le numero de page");
            config.set("Configuration.Messages.Suivant", "Pour consulter la suite de la liste, entrez un autre numero de page");
            config.set("Configuration.Messages.Fin", "Pas de defis supplementaires");
            config.set("Configuration.Defi.Bois.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Bois.Desc", "Etre le premier a trouver du bois");
            config.set("Configuration.Defi.Bois.Pts", Integer.valueOf(1));
            config.set("Configuration.Defi.Bois.Succes", " a accompli le defi : Mon premier bout de bois !");
            config.set("Configuration.Defi.Charbon.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Charbon.Desc", "Etre le premier a trouver/faire du charbon");
            config.set("Configuration.Defi.Charbon.Pts", Integer.valueOf(2));
            config.set("Configuration.Defi.Charbon.Succes", " a accompli le defi : Mon premier bout de charbon !");
            config.set("Configuration.Defi.Ble.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Ble.Desc", "Etre le premier a trouver une gerbe de ble");
            config.set("Configuration.Defi.Ble.Pts", Integer.valueOf(4));
            config.set("Configuration.Defi.Ble.Succes", " a accompli le defi : Ma premirere gerbe de ble !");
            config.set("Configuration.Defi.Mort.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Mort.Desc", "Etre le premier a tuer un adversaire humain");
            config.set("Configuration.Defi.Mort.Pts", Integer.valueOf(5));
            config.set("Configuration.Defi.Mort.Succes", "a accompli le defi : Premier sang !");
            config.set("Configuration.Defi.Zombie.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Zombie.Desc", "Etre le premier a tuer un zombie");
            config.set("Configuration.Defi.Zombie.Pts", Integer.valueOf(5));
            config.set("Configuration.Defi.Zombie.Succes", "a accompli le defi : Tueur de zombie !");
            config.set("Configuration.Defi.Araignee.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Araignee.Desc", "Etre le premier a tuer une araignee");
            config.set("Configuration.Defi.Araignee.Pts", Integer.valueOf(7));
            config.set("Configuration.Defi.Araignee.Succes", " a accompli le defi : Tueur d'araignee !");
            config.set("Configuration.Defi.Squelette.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Squelette.Desc", "Etre le premier a tuer un squelette");
            config.set("Configuration.Defi.Squelette.Pts", Integer.valueOf(7));
            config.set("Configuration.Defi.Squelette.Succes", " a accompli le defi : Tueur de squelette !");
            config.set("Configuration.Defi.Creeper.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Creeper.Desc", "Etre le premier a tuer un creeper");
            config.set("Configuration.Defi.Creeper.Pts", Integer.valueOf(10));
            config.set("Configuration.Defi.Creeper.Succes", " a accompli le defi : Tueur de creeper !");
            config.set("Configuration.Defi.Enderman.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Enderman.Desc", "Etre le premier a tuer un enderman");
            config.set("Configuration.Defi.Enderman.Pts", Integer.valueOf(15));
            config.set("Configuration.Defi.Enderman.Succes", " a accompli le defi : Tueur d'enderman !");
            config.set("Configuration.Defi.Fer.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Fer.Desc", "Etre le premier a trouver/faire un lingot de fer");
            config.set("Configuration.Defi.Fer.Pts", Integer.valueOf(15));
            config.set("Configuration.Defi.Fer.Succes", " a accompli le defi : Mon premier lingot de fer !");
            config.set("Configuration.Defi.Or.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Or.Desc", "Etre le premier a trouver/faire un lingot d'or");
            config.set("Configuration.Defi.Or.Pts", Integer.valueOf(20));
            config.set("Configuration.Defi.Or.Succes", " a accompli le defi : Mon premier lingot d'or !");
            config.set("Configuration.Defi.Diamant.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Diamant.Desc", "Etre le premier a trouver un diamant");
            config.set("Configuration.Defi.Diamant.Pts", Integer.valueOf(30));
            config.set("Configuration.Defi.Diamant.Succes", " a accompli le defi : Mon premier diamant !");
            config.set("Configuration.Defi.Emeraude.Statut", Boolean.valueOf(true));
            config.set("Configuration.Defi.Emeraude.Desc", "Etre le premier a trouver une emeraude");
            config.set("Configuration.Defi.Emeraude.Pts", Integer.valueOf(30));
            config.set("Configuration.Defi.Emeraude.Succes", " a accompli le defi : Ma premiere emeraude !");
            saveYML();
            config = YamlConfiguration.loadConfiguration(configFile);
            System.out.println("Le fichier de configuration de Quest a ete cree.");
        }
    }

    public void saveYML()
    {
        try
        {
            config.save(configFile);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public QuestListener listener;
    public static Map collection = new HashMap();
    static YamlConfiguration config = null;
    public static final File configFile = new File("plugins/Quest/config.yml");

}
