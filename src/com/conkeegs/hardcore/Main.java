package com.conkeegs.hardcore;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    private ArrayList < Double > zombieSpeeds = new ArrayList < Double > (Arrays.asList(.357, .30, .33, .35, .34, .37, .344, .333, .36, .352, .31, .32, .348, .355, .375));
    private ArrayList < Double > creeperSpeeds = new ArrayList < Double > (Arrays.asList(.30, .28, .29, .31));
    private List < World > worldList = null;
    private boolean someoneDied = false;

    @Override
    public void onEnable() {
    	getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
    	if (someoneDied) {

    		for (int i = 0; i < worldList.size(); i++) {

    			if (worldList.get(i) != null && !(worldList.get(i).getName().equals("world"))) {

    				World currWorld = worldList.get(i);

    				Bukkit.unloadWorld(currWorld, false);
    				deleteWorld(currWorld.getWorldFolder());

    			}

    		}

    	}
    }

    public boolean deleteWorld(File path) {
    	if (path.exists()) {
    		File files[] = path.listFiles();

            for (int i = 0; i < files.length; i++) {
            	if (files[i].isDirectory()) {
            		deleteWorld(files[i]);
            	} else {
            		files[i].delete();
            	}
            }
    	}
    	return (path.delete());
    }
    
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
    	DecimalFormat format = new DecimalFormat(".00");
        Player p = event.getPlayer();
     
        if(event.getHand() == EquipmentSlot.HAND && (p.getInventory().getItemInMainHand().getType() == Material.COMPASS)) {
        	
        	if (event.getPlayer().getWorld().getName().equals("world_nether") || event.getPlayer().getWorld().getName().equals("world_the_end")) {
        		event.getPlayer().sendMessage("You can only use your " + ChatColor.BLUE + "compass" + ChatColor.WHITE + " in the overworld.");
        		return;
        	} else if (event.getPlayer().getBedSpawnLocation() == null) {
        		event.getPlayer().sendMessage("You do not have a " + ChatColor.BLUE + "bed" + ChatColor.WHITE + " location to point to yet.");
        		return;
        	}
        	
        	double bedDistance = event.getPlayer().getLocation().distance(event.getPlayer().getBedSpawnLocation());
            double bedTime = (bedDistance / 7) / 60;
        	p.setCompassTarget(p.getBedSpawnLocation());
        	Location playerLocation = event.getPlayer().getLocation();
        	
        	if (bedTime < 1.0) {
        		event.getPlayer().sendMessage("You are at " + ChatColor.BLUE + "X: " + playerLocation.getBlockX() + ", Y: " + playerLocation.getBlockY() + ", Z: " + playerLocation.getBlockZ() + ChatColor.WHITE + ". You are about " + ChatColor.BLUE + Math.round(bedDistance) + ChatColor.WHITE + " block(s) away from your " + ChatColor.BLUE + "bed" + ChatColor.WHITE + ". That's " + ChatColor.BLUE + "less than a minute away.");
        	} else {
        		event.getPlayer().sendMessage("You are at " + ChatColor.BLUE + "X: " + playerLocation.getBlockX() + ", Y: " + playerLocation.getBlockY() + ", Z: " + playerLocation.getBlockZ() + ChatColor.WHITE + ". You are about " + ChatColor.BLUE + Math.round(bedDistance) + ChatColor.WHITE + " blocks away from your " + ChatColor.BLUE + "bed" + ChatColor.WHITE + ". That's about " + ChatColor.BLUE + format.format(bedTime) + ChatColor.WHITE + " minutes out.");
        	}

        	for (int i = 0; i < event.getPlayer().getWorld().getPlayers().size(); i++) {
        		if (!event.getPlayer().getDisplayName().equals(event.getPlayer().getWorld().getPlayers().get(i).getDisplayName())) {
        			Location othersLocation = event.getPlayer().getWorld().getPlayers().get(i).getLocation();
        			
        			if (((event.getPlayer().getWorld().getPlayers().get(i).getLocation().distance(event.getPlayer().getBedSpawnLocation()) / 7) / 60) >= 1.0) {
            			event.getPlayer().sendMessage("- " + ChatColor.BLUE + event.getPlayer().getWorld().getPlayers().get(i).getDisplayName() + ChatColor.WHITE + " is at " + ChatColor.BLUE + "X: " + othersLocation.getBlockX() + ", Y: " + othersLocation.getBlockY() + ", Z: " + othersLocation.getBlockZ());
            		} else {
            			event.getPlayer().sendMessage("- " + ChatColor.BLUE + event.getPlayer().getWorld().getPlayers().get(i).getDisplayName() + ChatColor.WHITE + " is at " + ChatColor.BLUE + "home.");
            		}
        		}
        	}
        }
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
    	Bukkit.broadcastMessage(ChatColor.BLUE + event.getPlayer().getDisplayName() + ChatColor.WHITE + " has issued the command: " + ChatColor.BLUE + "\"" + event.getMessage() + "\"" + ChatColor.WHITE + " (oh no bro...funny?...)");
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
    	if (event.getEntity() instanceof Player) {
        	Player player = event.getEntity();
            worldList = Bukkit.getWorlds();
            List < Player > players = player.getWorld().getPlayers();
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();

            for (int i = 0; i < players.size(); i++) {
            	String command = "clear " + players.get(i).getName();
                Bukkit.dispatchCommand(console, command);
                Player playerTemp = players.get(i);

              playerTemp.kickPlayer(ChatColor.BLUE + event.getDeathMessage() + ChatColor.WHITE + "...YOU GUYS ARE SO " + ChatColor.BOLD + "BAAAAAD!!! " + ChatColor.RESET + "World gone Foreveriano Ronaldo...");

                Bukkit.shutdown();
            }

            someoneDied = true;
        } else {
        	return;
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
    	if (event.getDamager() instanceof Zombie) {

            event.setDamage(9);

        } else if (event.getDamager() instanceof Arrow) {

            event.setDamage(6);

        } else if (event.getDamager() instanceof Creeper) {
        	double distanceFromCreeper = event.getDamager().getLocation().distance(event.getEntity().getLocation());
        	Creeper tempCreeper = (Creeper)event.getDamager();
        	
        	if (distanceFromCreeper >= 0 && distanceFromCreeper <= 2) {
        		event.setDamage(30);
        	} else {
        		event.setDamage((30 / distanceFromCreeper) + 11);
        	}
        	
            tempCreeper.setExplosionRadius(30);
        } else if (event.getDamager() instanceof Enderman) {

            event.setDamage(12);

        } else if (event.getDamager() instanceof Spider) {

            event.setDamage(9);

        } else if (event.getDamager() instanceof CaveSpider) {

            event.setDamage(9);

        } else if (event.getDamager() instanceof ZombieVillager) {

            event.setDamage(9);

        } else if (event.getDamager() instanceof Husk) {

            event.setDamage(9);

        } else if (event.getDamager() instanceof Drowned) {

            event.setDamage(9);

        } else if (event.getDamager() instanceof EnderDragon) {

            event.setDamage(20);

        } else if (event.getDamager() instanceof Fireball) {

            event.setDamage(8);

        } else if (event.getDamager() instanceof LargeFireball) {

            event.setDamage(20);

        } else if (event.getDamager() instanceof MagmaCube) {

            event.setDamage(6);

        } else if (event.getDamager() instanceof Phantom) {

            event.setDamage(7);

        } else if (event.getDamager() instanceof Piglin) {

            event.setDamage(7);

        } else if (event.getDamager() instanceof Ravager) {

            event.setDamage(12);

        } else if (event.getDamager() instanceof Hoglin) {

            event.setDamage(9);

        } else if (event.getDamager() instanceof Trident) {

            event.setDamage(10);

        } else if (event.getDamager() instanceof WitherSkull) {

            event.setDamage(10);

        } else if (event.getDamager() instanceof SmallFireball) {

            event.setDamage(6);

        } else if (event.getDamager() instanceof Slime) {

            event.setDamage(6);

        } else if (event.getDamager() instanceof Pillager) {

            event.setDamage(7);

        } else if (event.getDamager() instanceof Illusioner) {

            event.setDamage(6);

        } else if (event.getDamager() instanceof EvokerFangs) {

            event.setDamage(7);

        } else if (event.getDamager() instanceof Evoker) {

            event.setDamage(9);

        } else if (event.getDamager() instanceof Stray) {

            event.setDamage(8);

        } else if (event.getDamager() instanceof Vindicator) {

            event.setDamage(10);

        } else if (event.getDamager() instanceof ThrownExpBottle) {

            event.setDamage(10);

        } else if (event.getDamager() instanceof Zoglin) {

            event.setDamage(10);

        } else if (event.getDamager() instanceof PigZombie) {

            event.setDamage(9);

        } else if (event.getDamager() instanceof Fireball) {
            event.setDamage(6);
        } else if (event.getDamager() instanceof PiglinBrute) {
        	event.setDamage(7);
        } else if (event.getDamager() instanceof Shulker) {
        	event.setDamage(7);
        } 
    }

    @EventHandler
    public void onEntitySpawnn(CreatureSpawnEvent event) {
    	LivingEntity entity = (LivingEntity) event.getEntity();

        switch (entity.getName().toLowerCase()) {
        case "zombie":

                if (event.getEntity() instanceof Zombie) {

                    Zombie zombie = (Zombie) entity;

                    if (zombie.isBaby()) {

                        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.25);

                    } else {

                        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(15)));

                    }

                }

                break;

            case "husk":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(15)));

                break;

            case "creeper":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(creeperSpeeds.get(new Random().nextInt(4)));

                break;

            case "drowned":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(15)));

                break;

            case "zombie_villager":

            	entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(15)));

                break;

            case "spider":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.33);

                break;

            case "piglin":

                if (event.getEntity() instanceof Piglin) {

                    Piglin piglin = (Piglin) entity;

                    if (piglin.isBaby()) {

                        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.25);

                    } else {

                        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(15)));

                    }

                }

                break;

            case "cavespider":
            case "cave_spider":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.30);

                break;

            case "hoglin":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.3);

                break;

            case "slime":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.4);

                break;

            case "witch":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.28);

                break;

            case "zoglin":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.3);

                break;

            case "pigzombie":

                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.34);

                break;

        }

    }

}