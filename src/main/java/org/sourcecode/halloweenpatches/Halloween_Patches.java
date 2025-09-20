// Hallo! Ich habe diesen Code wegen besserer Übersicht auf Englisch und Deutsch mit kommentaren versehen.
// Hello! I've added comments to this code in English and German for clarity.
package org.sourcecode.halloweenpatches;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.event.player.PlayerJoinEvent;

import javax.print.attribute.standard.ReferenceUriSchemesSupported;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.io.File;

public final class Halloween_Patches extends JavaPlugin  implements Listener {

    // Set der feindlichen Mobs
    // All Hostile Mobs
    private static final Set<EntityType> HOSTILE_MOBS = EnumSet.of(
            EntityType.ZOMBIE, EntityType.SKELETON, EntityType.SPIDER, EntityType.CREEPER,
            EntityType.ENDERMAN, EntityType.WITCH, EntityType.BLAZE, EntityType.HUSK,
            EntityType.STRAY, EntityType.SLIME, EntityType.MAGMA_CUBE, EntityType.PILLAGER,
            EntityType.EVOKER, EntityType.VINDICATOR, EntityType.DROWNED, EntityType.WITHER_SKELETON
    );


    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("Halloween-Patches wurde gestartet! Happy Halloween\n" +
                "by SourceCodeDE\n" +
                "Halloween-Patches is now loaded and enabled! Happy Halloween\n" +
                "by SourceCodeDE");

        // Setze Kürbisse auf alle vorhandenen feindlichen Mobs beim Start
        // Put pumpkins on all existing Mobd
        equipPumpkinsOnExistingMobs();

        // Starte den zufälligen Geist-Spawner und Hexen-Spawner
        // Start Random Witch and Spirit Spawner
        startRandomSpiritSpawner();
        startWitchSpawner();
    }

    @Override
    public void onDisable() {
        // Entferne die Kürbisse von allen betroffenen Mobs beim Stoppen
        // Remove the Pumpkins from all Mobs
        removePumpkinsFromMobs();
        getLogger().info("Halloween-Patches wurde gestoppt. Happy Halloween\n" +
                "by SourceCodeDE\n" +
                "Halloween-Patches is now disabled. Happy Halloween\n" +
                "by SourceCodeDE");
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Befehl in der Konsole ausführen, um den Sound für alle Spieler abzuspielen
        // Run Command in Console, to play the Sound for all Players.
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "playsound custom:custom.join master @a ~ ~ ~ 10 -0");
    }



        @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        LivingEntity entity = event.getEntity();

        // Feindliche Mobs erhalten einen Kürbis auf dem Kopf
        // Hostile mobs receive a pumpkin on their head

        if (HOSTILE_MOBS.contains(entity.getType())) {
            entity.getEquipment().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
        }

        // Hexen sollten nicht in Dörfern spawnen, aber falls doch dann werden sie entfernt.
        // Witches should not spawn in villages, but if they do, they will be removed.

        if (entity.getType() == EntityType.WITCH) {
            Location location = entity.getLocation();
            if (location.getWorld().getNearbyEntities(location, 20, 20, 20, e -> e instanceof Villager).size() > 0) {
                entity.remove();
            }
        }
    }

    private void equipPumpkinsOnExistingMobs() {
        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (HOSTILE_MOBS.contains(entity.getType())) {
                    entity.getEquipment().setHelmet(new ItemStack(Material.CARVED_PUMPKIN));
                }
            }
        }
    }

    private void removePumpkinsFromMobs() {
        for (World world : Bukkit.getWorlds()) {
            for (LivingEntity entity : world.getLivingEntities()) {
                if (HOSTILE_MOBS.contains(entity.getType())) {
                    ItemStack helmet = entity.getEquipment().getHelmet();
                    if (helmet != null && helmet.getType() == Material.CARVED_PUMPKIN) {
                        entity.getEquipment().setHelmet(null);
                    }
                }
            }
        }
    }

    private void startWitchSpawner() {
        new BukkitRunnable() {
            Random random = new Random();

            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Player player : world.getPlayers()) {
                        if (random.nextDouble() < 0.1) { // Hexe spawnt mit 10% Wahrscheinlichkeit | 10% Chance to spawn witch
                            Location spawnLocation = player.getLocation().add(random.nextInt(20) - 10, 0, random.nextInt(20) - 10);
                            if (world.getNearbyEntities(spawnLocation, 20, 20, 20, e -> e instanceof Villager).size() == 0) {
                                world.spawnEntity(spawnLocation, EntityType.WITCH);
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 20 * 60); // Alle 60 Sekunden
    }

    private void startRandomSpiritSpawner() {
        new BukkitRunnable() {
            Random random = new Random();

            @Override
            public void run() {
                for (World world : Bukkit.getWorlds()) {
                    for (Player player : world.getPlayers()) {
                        if (random.nextDouble() < 0.5) {
                            spawnSpirit(player);
                        }
                    }
                }
            }
        }.runTaskTimer(this, 0, 20 * 30);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (label.equalsIgnoreCase("spawnspirit")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                spawnSpirit(player);
                player.sendMessage("§aEin Geist wurde über dir gespawnt!");
                player.sendMessage("§aA ghost has spawned above you!");
                return true;
            } else {
                sender.sendMessage("Dieser Befehl kann nur von einem Spieler ausgeführt werden.");
                sender.sendMessage("This command can only be executed by a player.");
                return false;
            }
        }
        return false;
    }

    private void spawnSpirit(Player player) {
        int height = 3 + new Random().nextInt(5);
        int xachse = 0 + new Random().nextInt(8);
        int zachse = 1 + new Random().nextInt(2);
        Location spawnLocation = player.getLocation().add(xachse, height, zachse);

        ArmorStand spirit = (ArmorStand) player.getWorld().spawnEntity(spawnLocation, EntityType.ARMOR_STAND);
        spirit.setGravity(false);
        spirit.setInvulnerable(true);
        spirit.setVisible(false);
        spirit.setCustomNameVisible(true);
        spirit.setCustomName("§c§lGeist");

        spirit.getEquipment().setHelmet(new ItemStack(Material.PUMPKIN));
        spirit.getEquipment().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
        spirit.getEquipment().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));

        player.playSound(player.getLocation(), "minecraft:ambient.cave", 1.0f, 0.0f);

        new BukkitRunnable() {
            int ticksLived = 0;

            @Override
            public void run() {
                if (ticksLived > 500) {
                    spirit.remove();
                    cancel();
                    return;
                }

                Location targetLocation = player.getLocation().add(0, height, 0);
                spirit.teleport(targetLocation);
                ticksLived += 5;
            }
        }.runTaskTimer(this, 0, 5);
    }
}
