package com.frosr1o2.VillagerCarry;

import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public final class VillagerCarryPlugin extends JavaPlugin implements Listener {

    private NamespacedKey markerKey;
    private NamespacedKey nbtKey;
    private NamespacedKey typeKey;

    private final Map<String, FileConfiguration> langConfigs = new HashMap<>();

    private static final String VILLAGER_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODIyZDhlNzUxYzhmMmZkNGM4OTQyYzQ0YmRiMmY1Y2E0ZDhhZThlNTc1ZWQzZWIzNGMxOGE4NmU5M2IifX19";
    private static final String WANDERING_TRADER_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNWYxMzc5YTgyMjkwZDdhYmUxZWZhYWJiYzcwNzEwZmYyZWMwMmRkMzRhZGUzODZiYzAwYzkzMGM0NjFjZjkzMiJ9fX0=";
    private static final String ZOMBIE_VILLAGER_TEXTURE =
            "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQ1YzExZTAzMjcwMzU2NDljYTA2MDBlZjkzODkwMGUyNWZkMWUzODAxNzQyMmJjOTc0MGU0Y2RhMmNiYTg5MiJ9fX0=";

    private static final Map<String, String> PROFESSION_TEXTURES = Map.of(
        "butcher", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzQ3YjIyMjJlZDZmZTFkNDMxM2MzY2IzNDJiYTk2YTU1YTg5Yjc2ZTYyZDZiYTdhMTU4Y2QzZGU5NDNkZTNlZSJ9fX0=",
        "weaponsmith", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzI5Njc2ZWU2ZGE1YTM1MjFjYjQ3ODQ2Mzc3OWI4NzExZTMxODg3Y2Q5YTZkOWZkZWNmY2JjNTUwODNlNTUxNSJ9fX0=",
        "cleric", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjYxYzQwZjhkYTQwOWVlZjQ0MzJjZTEwNjNmNTAwNTIwMTcwNzUwMzhjZjNhOWE2NTQ4NWVhNjIxYzU0OWY0MCJ9fX0=",
        "cartographer", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWZjNmFjOWQ4NTU1NGJiZTU2YTY2hkZTBhNTRjYjFlZmY5M2UxNGY0YmNkZjcyNDVhNzQ0Y2MzNzcyMWYwYSJ9fX0=",
        "librarian", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTY5ODQ3N2Q3ZDUzMjc0OGFmNGUwOTExNWVmMTZiYjk1OTk0ZjBlNThiMDkwZTZkMzA2NDQ1OGJiOWFlMTY4NyJ9fX0=",
        "farmer", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDAxZTAzNWEzZDhkNjEyNjA3MmJjYmU1MmE5NzkxM2FjZTkzNTUyYTk5OTk1YjVkNDA3MGQ2NzgzYTMxZTkwOSJ9fX0=",
        "fisherman", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzlhOWViZGQyYzFiZjJkMGE2MWMzYjk4YzBiYzQyNzc0NDRhMWI4ZmVkYmIxNmNjYTRmYWFjYTlmN2VjMDU5MiJ9fX0="
    );

    @Override
    public void onEnable() {
        markerKey = new NamespacedKey(this, "villager_carry");
        nbtKey = new NamespacedKey(this, "entity_nbt");
        typeKey = new NamespacedKey(this, "entity_type");

        loadLanguages();

        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("VillagerCarry enabled with external language support.");
    }

    private void loadLanguages() {
        langConfigs.clear();
        File langFolder = new File(getDataFolder(), "lang");
        if (!langFolder.exists()) {
            langFolder.mkdirs();
        }

        String[] defaultLangs = {"en_us.yml", "ru_ru.yml", "de_de.yml", "fr_fr.yml", "es_es.yml", "uk_ua.yml", "pl_pl.yml"};
        for (String langFile : defaultLangs) {
            File file = new File(langFolder, langFile);
            if (!file.exists()) {
                try (InputStream in = getResource("lang/" + langFile)) {
                    if (in != null) {
                        Files.copy(in, file.toPath());
                    }
                } catch (IOException e) {
                    getLogger().warning("Could not save default lang file: " + langFile);
                }
            }
        }

        File[] files = langFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                String langKey = file.getName().replace(".yml", "").toLowerCase(Locale.ROOT);
                langConfigs.put(langKey, YamlConfiguration.loadConfiguration(file));
            }
        }
    }

    private FileConfiguration getLangConfig(Player p) {
        String locale = p.getLocale();
        if (locale != null) {
            locale = locale.toLowerCase(Locale.ROOT);
            if (langConfigs.containsKey(locale)) {
                return langConfigs.get(locale);
            }
            String shortLocale = locale.split("_")[0];
            for (Map.Entry<String, FileConfiguration> entry : langConfigs.entrySet()) {
                if (entry.getKey().startsWith(shortLocale)) {
                    return entry.getValue();
                }
            }
        }
        return langConfigs.getOrDefault("en_us", langConfigs.values().stream().findFirst().orElse(new YamlConfiguration()));
    }

    private String getMsg(Player p, String key) {
        FileConfiguration config = getLangConfig(p);
        String text = config.getString("messages." + key);
        if (text == null && !config.getName().equals("en_us.yml")) {
            FileConfiguration fallback = langConfigs.get("en_us");
            if (fallback != null) {
                text = fallback.getString("messages." + key);
            }
        }
        return text != null ? text : key;
    }

    private String getItemName(Player p, String type) {
        FileConfiguration config = getLangConfig(p);
        String name = config.getString("items." + type);
        if (name == null && !config.getName().equals("en_us.yml")) {
            FileConfiguration fallback = langConfigs.get("en_us");
            if (fallback != null) {
                name = fallback.getString("items." + type);
            }
        }
        return name != null ? name : type;
    }

    private boolean isCarryHead(ItemStack item) {
        if (item == null || item.getType() != Material.PLAYER_HEAD || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer()
                .has(markerKey, PersistentDataType.BYTE);
    }

    private void bar(Player p, String key) {
        String msg = getMsg(p, key);
        p.sendActionBar(ChatColor.translateAlternateColorCodes('&', msg));
    }

    private String carriedType(org.bukkit.entity.Entity e) {
        return switch (e.getType()) {
            case VILLAGER -> "villager";
            case WANDERING_TRADER -> "wandering_trader";
            case ZOMBIE_VILLAGER -> "zombie_villager";
            default -> null;
        };
    }

    private EntityType toBukkitType(String type) {
        return switch (type) {
            case "villager" -> EntityType.VILLAGER;
            case "wandering_trader" -> EntityType.WANDERING_TRADER;
            case "zombie_villager" -> EntityType.ZOMBIE_VILLAGER;
            default -> null;
        };
    }

    private CompoundTag saveEntity(Entity entity) {
        TagValueOutput out = TagValueOutput.createWithContext(
                net.minecraft.util.ProblemReporter.DISCARDING,
                entity.level().registryAccess());
        entity.saveWithoutId(out);
        return out.buildResult();
    }

    private String encode(CompoundTag tag) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        NbtIo.writeCompressed(tag, out);
        return Base64.getEncoder().encodeToString(out.toByteArray());
    }

    private CompoundTag decode(String value) throws IOException {
        return NbtIo.readCompressed(
                new ByteArrayInputStream(Base64.getDecoder().decode(value)),
                NbtAccounter.unlimitedHeap());
    }

    private String texture(String type, CompoundTag tag) {
        if (type.equals("wandering_trader")) return WANDERING_TRADER_TEXTURE;
        if (type.equals("zombie_villager")) return ZOMBIE_VILLAGER_TEXTURE;
        String s = tag.toString().toLowerCase(Locale.ROOT);
        for (Map.Entry<String,String> e : PROFESSION_TEXTURES.entrySet())
            if (s.contains(e.getKey())) return e.getValue();
        return VILLAGER_TEXTURE;
    }

    private ItemStack makeHead(Player player, String type, CompoundTag tag) throws IOException {
        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta)item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        pdc.set(markerKey, PersistentDataType.BYTE, (byte)1);
        pdc.set(typeKey, PersistentDataType.STRING, type);
        pdc.set(nbtKey, PersistentDataType.STRING, encode(tag));

        String displayName = getItemName(player, type);
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName));

        PlayerProfile profile = Bukkit.createProfile(java.util.UUID.randomUUID());
        profile.setProperty(new ProfileProperty("textures", texture(type, tag)));
        meta.setPlayerProfile(profile);
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        Player player = event.getPlayer();
        if (!player.isSneaking()) return;

        LivingEntity living = event.getRightClicked() instanceof LivingEntity le ? le : null;
        if (living == null) return;

        String type = carriedType(living);
        if (type == null) return;

        event.setCancelled(true);

        if (player.getInventory().firstEmpty() == -1) {
            bar(player, "full");
            return;
        }

        try {
            Entity nms = ((org.bukkit.craftbukkit.entity.CraftEntity)living).getHandle();
            CompoundTag tag = saveEntity(nms);
            ItemStack head = makeHead(player, type, tag);
            player.getInventory().addItem(head);
            living.remove();

            bar(player, switch(type) {
                case "wandering_trader" -> "trader";
                case "zombie_villager" -> "zombie";
                default -> "villager";
            });
        } catch (Exception ex) {
            getLogger().warning("Failed to save carried entity: " + ex.getMessage());
            bar(player, "failed");
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent event) {
        ItemStack item = event.getItem();
        if (!isCarryHead(item)) return;

        if (event.getAction() == Action.RIGHT_CLICK_AIR ||
            event.getAction() == Action.LEFT_CLICK_AIR) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent event) {
        ItemStack item = event.getItemInHand();
        if (!isCarryHead(item)) return;

        event.setCancelled(true);
        Player player = event.getPlayer();
        ItemMeta meta = item.getItemMeta();
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        String encoded = pdc.get(nbtKey, PersistentDataType.STRING);
        String type = pdc.get(typeKey, PersistentDataType.STRING);
        EntityType entityType = type == null ? null : toBukkitType(type);

        if (encoded == null || entityType == null) {
            bar(player, "restore");
            return;
        }

        try {
            CompoundTag tag = decode(encoded);
            tag.remove("UUID");
            Location loc = event.getBlock().getLocation().add(0.5, 1.0, 0.5);
            org.bukkit.entity.Entity spawned =
                    event.getBlock().getWorld().spawnEntity(loc, entityType);

            Entity nms = ((org.bukkit.craftbukkit.entity.CraftEntity)spawned).getHandle();
            ValueInput input = TagValueInput.create(
                    net.minecraft.util.ProblemReporter.DISCARDING,
                    nms.level().registryAccess(),
                    tag);
            nms.load(input);

            nms.snapTo(
                    loc.getX(),
                    loc.getY(),
                    loc.getZ(),
                    nms.getYRot(),
                    nms.getXRot()
            );

            if (player.getGameMode() != GameMode.CREATIVE) {
                if (item.getAmount() <= 1) {
                    if (event.getHand() == EquipmentSlot.OFF_HAND)
                        player.getInventory().setItemInOffHand(null);
                    else
                        player.getInventory().setItemInMainHand(null);
                } else {
                    item.setAmount(item.getAmount() - 1);
                }
            }
            bar(player, "place");
        } catch (Exception ex) {
            getLogger().warning("Failed to restore carried entity: " + ex.getMessage());
            bar(player, "restore");
        }
    }

    @Override
    public void onDisable() {
    }
}
