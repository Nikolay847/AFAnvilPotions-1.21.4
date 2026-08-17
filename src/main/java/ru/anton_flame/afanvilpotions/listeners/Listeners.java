package ru.anton_flame.afanvilpotions.listeners;

import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import ru.anton_flame.afanvilpotions.utils.ConfigManager;
import ru.anton_flame.afanvilpotions.utils.Hex;

import java.util.HashMap;
import java.util.Map;

public class Listeners implements Listener {

    private final Map<ItemStack, ItemStack> remainingItems = new HashMap<>();

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        AnvilInventory inventory = event.getInventory();
        ItemStack firstItem = inventory.getItem(0);
        ItemStack secondItem = inventory.getItem(1);

        if (!isPotion(firstItem) || !isPotion(secondItem)) {
            return;
        }

        if (firstItem.getType() != secondItem.getType()) {
            return;
        }

        PotionMeta firstMeta = (PotionMeta) firstItem.getItemMeta();
        PotionMeta secondMeta = (PotionMeta) secondItem.getItemMeta();

        PotionEffectType firstType = getEffectType(firstMeta);
        PotionEffectType secondType = getEffectType(secondMeta);

        if (firstType == null || secondType == null || firstType != secondType) {
            return;
        }

        if (isUpgraded(firstMeta) && isUpgraded(secondMeta)) {
            if (ConfigManager.enabledLevel3) {
                upgradePotion(event, inventory, ConfigManager.checkPermissionLevel3,
                        "afanvilpotions.upgrade.level.3", firstItem, secondItem,
                        ConfigManager.potionsLevel3, 2);
            }
        } else if (!isUpgraded(firstMeta) && !isUpgraded(secondMeta)) {
            if (ConfigManager.enabledLevel2) {
                upgradePotion(event, inventory, ConfigManager.checkPermissionLevel2,
                        "afanvilpotions.upgrade.level.2", firstItem, secondItem,
                        ConfigManager.potionsLevel2, 1);
            }
        }
    }

    private void upgradePotion(PrepareAnvilEvent event, AnvilInventory inventory,
                               boolean checkPermission, String permission,
                               ItemStack firstItem, ItemStack secondItem,
                               ConfigurationSection potions, int level) {

        PotionEffectType firstType = getEffectType((PotionMeta) firstItem.getItemMeta());

        if (firstType == null) {
            return;
        }

        if (checkPermission) {
            for (HumanEntity entity : event.getViewers()) {
                if (!entity.hasPermission(permission)) {
                    event.setResult(null);
                    return;
                }
            }
        }

        for (String potion : potions.getKeys(false)) {
            if (!firstType.getName().equalsIgnoreCase(potion)) {
                continue;
            }

            ItemStack potionItem = firstItem.clone();
            PotionMeta potionMeta = (PotionMeta) potionItem.getItemMeta();

            potionMeta.clearCustomEffects();
            potionMeta.addCustomEffect(new PotionEffect(
                    PotionEffectType.getByName(potion),
                    potions.getInt(potion + ".duration") * 20,
                    level
            ), true);

            potionMeta.setDisplayName(Hex.color(potions.getString(potion + ".potion-name")));

            String[] color = potions.getString(potion + ".potion-color").split(",");
            potionMeta.setColor(Color.fromRGB(
                    Integer.parseInt(color[0]),
                    Integer.parseInt(color[1]),
                    Integer.parseInt(color[2])
            ));

            potionItem.setItemMeta(potionMeta);

            int amount = Math.min(firstItem.getAmount(), secondItem.getAmount());
            if (firstItem.getAmount() > 1 && secondItem.getAmount() > 1) {
                potionItem.setAmount(amount);
            } else {
                potionItem.setAmount(1);
            }

            int difference = Math.abs(firstItem.getAmount() - secondItem.getAmount());
            if (difference > 0) {
                ItemStack remaining = firstItem.getAmount() > secondItem.getAmount()
                        ? firstItem.clone()
                        : secondItem.clone();
                remaining.setAmount(difference);
                remainingItems.put(potionItem, remaining);
            }

            event.setResult(potionItem);
            inventory.setRepairCost(potions.getInt(potion + ".exp-price") * potionItem.getAmount());
            return;
        }
    }

    private boolean isPotion(ItemStack item) {
        return item != null && item.getItemMeta() instanceof PotionMeta;
    }

    private boolean isUpgraded(PotionMeta meta) {
        return meta.getBasePotionData() != null && meta.getBasePotionData().isUpgraded();
    }

    private PotionEffectType getEffectType(PotionMeta meta) {
        if (meta == null) {
            return null;
        }

        if (!meta.getCustomEffects().isEmpty()) {
            return meta.getCustomEffects().get(0).getType();
        }

        if (meta.getBasePotionData() != null && meta.getBasePotionData().getType() != null) {
            return meta.getBasePotionData().getType().getEffectType();
        }

        return null;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getInventory() instanceof AnvilInventory)) {
            return;
        }

        if (event.getSlotType() != InventoryType.SlotType.RESULT) {
            return;
        }

        ItemStack result = ((AnvilInventory) event.getInventory()).getResult();
        if (result != null && remainingItems.containsKey(result)) {
            event.getWhoClicked().getInventory().addItem(remainingItems.remove(result));
        }
    }
}
