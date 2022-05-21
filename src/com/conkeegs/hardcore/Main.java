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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

    private ArrayList <Double> zombieSpeeds = new ArrayList <Double> (Arrays.asList(.38, .4, .42, .39, .41));
    private ArrayList <Double> creeperSpeeds = new ArrayList <Double> (Arrays.asList(.30, .28, .35, .32));
    private List <World> worldList = null;
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
    				World currentWorld = worldList.get(i);

    				Bukkit.unloadWorld(currentWorld, false);
    				deleteWorld(currentWorld.getWorldFolder());

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
    public void onPlayerSleep(PlayerBedEnterEvent event) {
    	event.getPlayer().sendMessage("There is NOOOO sleep..for a sumuraiii...");
    	event.setCancelled(true);
    }
    
    @EventHandler
    public void onPlayerUse(PlayerInteractEvent event) {
    	DecimalFormat format = new DecimalFormat(".00");
        Player player = event.getPlayer();
        boolean notHoldingCompass = !(event.getHand() == EquipmentSlot.HAND && player.getInventory().getItemInMainHand().getType() == Material.COMPASS);
     
        if (notHoldingCompass) {
        	return;
        }
        
        Location playerLocation = event.getPlayer().getLocation();
    	boolean notInOverworld = event.getPlayer().getWorld().getName().equals("world_nether") || event.getPlayer().getWorld().getName().equals("world_the_end");
    	boolean noBedLocation = event.getPlayer().getBedSpawnLocation() == null;
    	
    	if (notInOverworld) {
    		event.getPlayer().sendMessage(ChatColor.RED + "You can only get your location in the overworld.");
    	} else if (noBedLocation) {
    		event.getPlayer().sendMessage("You are at " + ChatColor.BLUE + "X: " + playerLocation.getBlockX() + ", Y: " + playerLocation.getBlockY() + ", Z: " + playerLocation.getBlockZ() + ChatColor.WHITE + ". You have no " + ChatColor.BLUE + "bed" + ChatColor.WHITE + " location");
    	} else {
    		double bedDistance = event.getPlayer().getLocation().distance(event.getPlayer().getBedSpawnLocation());
            double minutesFromBed = (bedDistance / 7) / 60;
            
            player.setCompassTarget(player.getBedSpawnLocation());
        	
        	if (minutesFromBed < 1.0) {
        		event.getPlayer().sendMessage("You are near " + ChatColor.BLUE + "home.");
        	} else {
        		event.getPlayer().sendMessage("You are at " + ChatColor.BLUE + "X: " + playerLocation.getBlockX() + ", Y: " + playerLocation.getBlockY() + ", Z: " + playerLocation.getBlockZ() + ChatColor.WHITE + ". You are about " + ChatColor.BLUE + Math.round(bedDistance) + ChatColor.WHITE + " blocks away from your " + ChatColor.BLUE + "bed" + ChatColor.WHITE + ". That's about " + ChatColor.BLUE + format.format(minutesFromBed) + ChatColor.WHITE + " minutes away.");
        	}
    	}
    	
    	List<Player> playersInWorld = event.getPlayer().getWorld().getPlayers();
    	
    	for (int i = 0; i < playersInWorld.size(); i++) {
    		boolean otherPlayerInOverworld = !playersInWorld.get(i).getWorld().getName().equals("world_nether") && !playersInWorld.get(i).getWorld().getName().equals("world_the_end");
    		boolean isOtherPlayer = !event.getPlayer().getDisplayName().equals(playersInWorld.get(i).getDisplayName());
    		Location otherPlayerLocation = playersInWorld.get(i).getLocation();
    		
    		if (!otherPlayerInOverworld || !isOtherPlayer) {
    			continue;
    		}
			
			if (noBedLocation) {
				event.getPlayer().sendMessage("- " + ChatColor.BLUE + event.getPlayer().getWorld().getPlayers().get(i).getDisplayName() + ChatColor.WHITE + " is at " + ChatColor.BLUE + "X: " + otherPlayerLocation.getBlockX() + ", Y: " + otherPlayerLocation.getBlockY() + ", Z: " + otherPlayerLocation.getBlockZ());
				continue;
			}
			
			double othersBedDistance = otherPlayerLocation.distance(event.getPlayer().getBedSpawnLocation());
            double othersBedTime = (othersBedDistance / 7) / 60;
			
			if (((otherPlayerLocation.distance(event.getPlayer().getBedSpawnLocation()) / 7) / 60) >= 1.0) {
    			event.getPlayer().sendMessage("- " + ChatColor.BLUE + playersInWorld.get(i).getDisplayName() + ChatColor.WHITE + " is at " + ChatColor.BLUE + "X: " + otherPlayerLocation.getBlockX() + ", Y: " + otherPlayerLocation.getBlockY() + ", Z: " + otherPlayerLocation.getBlockZ()+ ChatColor.WHITE + ". They are about " + ChatColor.BLUE + Math.round(othersBedDistance) + ChatColor.WHITE + " blocks away from your " + ChatColor.BLUE + "bed" + ChatColor.WHITE + ". That's about " + ChatColor.BLUE + format.format(othersBedTime) + ChatColor.WHITE + " minutes away.");
    		} else {
    			event.getPlayer().sendMessage("- " + ChatColor.BLUE + playersInWorld.get(i).getDisplayName() + ChatColor.WHITE + " is near " + ChatColor.BLUE + "home.");
    		}
    	}
    }
    
    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
    	Bukkit.broadcastMessage(ChatColor.BLUE + event.getPlayer().getDisplayName() + ChatColor.WHITE + " has issued the command: " + ChatColor.BLUE + "\"" + event.getMessage() + "\"" + ChatColor.WHITE + " (oh no bro...funny?...)");
    }
    
    @EventHandler
    public void onServerCommand(ServerCommandEvent event) {
    	Bukkit.broadcastMessage(ChatColor.BLUE + "SERVER" + ChatColor.WHITE + " has issued the command: " + ChatColor.BLUE + "\"" + event.getCommand() + "\"" + ChatColor.WHITE + " (DUG...)");
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
            event.setDamage(11);
        } else if (event.getDamager() instanceof Arrow) {
            event.setDamage(9);
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
            event.setDamage(11);
        } else if (event.getDamager() instanceof Husk) {
            event.setDamage(11);
        } else if (event.getDamager() instanceof Drowned) {
            event.setDamage(11);
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
            event.setDamage(9);
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
            event.setDamage(7);
        } else if (event.getDamager() instanceof EvokerFangs) {
            event.setDamage(9);
        } else if (event.getDamager() instanceof Evoker) {
            event.setDamage(9);
        } else if (event.getDamager() instanceof Stray) {
            event.setDamage(9);
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
    public void onEntitySpawn(CreatureSpawnEvent event) {
    	LivingEntity entity = (LivingEntity) event.getEntity();

        switch (entity.getName().toLowerCase()) {
        	case "zombie":
                if (event.getEntity() instanceof Zombie) {
                    Zombie zombie = (Zombie) entity;
                    
                    if (zombie.isBaby()) {
                        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.25);
                    } else {
                        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(zombieSpeeds.size())));
                    }

                }

                break;

            case "husk":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(zombieSpeeds.size())));
                break;

            case "creeper":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(creeperSpeeds.get(new Random().nextInt(creeperSpeeds.size())));
                break;

            case "drowned":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(zombieSpeeds.size())));
                break;

            case "zombie_villager":
            	entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(zombieSpeeds.size())));
                break;

            case "spider":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.36);
                break;
            case "piglin":
                if (event.getEntity() instanceof Piglin) {
                    Piglin piglin = (Piglin) entity;

                    if (piglin.isBaby()) {
                        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.25);
                    } else {
                        entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(zombieSpeeds.get(new Random().nextInt(zombieSpeeds.size())));
                    }

                }

                break;

            case "cavespider":
            case "cave_spider":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.30);
                break;

            case "hoglin":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.33);
                break;

            case "slime":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.4);
                break;

            case "witch":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.28);
                break;

            case "zoglin":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.34);
                break;

            case "pigzombie":
                entity.getAttribute(Attribute.GENERIC_MOVEMENT_SPEED).setBaseValue(.38);
                break;

        }

    }

}