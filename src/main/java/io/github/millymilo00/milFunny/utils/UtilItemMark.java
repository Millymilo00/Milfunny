package io.github.millymilo00.milFunny.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class UtilItemMark {
    public static ItemStack markItem(ItemStack input) {
        ItemMeta meta = input.getItemMeta();
        if (!input.hasItemMeta()) {
            meta = Bukkit.getItemFactory().getItemMeta(Material.STONE);
        }
        if (meta.hasLore()) {
            List<String> lore = meta.getLore();
            lore.add("Admin Marked");
            meta.setLore(lore);
        }
        else {
            meta.setLore(Collections.singletonList("Admin Marked"));
        }
        input.setItemMeta(meta);
        return input;
    }
}
