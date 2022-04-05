package com.mattmx.servergui.util;

public class Enchantment {
    // Enchantments: [{id:"minecraft:efficiency", lvl: 1s}, {id:"minecraft:blast_protection", lvl: 1s}
    private Enchantments enchantment;
    private int level;

    public Enchantment(Enchantments enchantment) {
        this(enchantment, 1);
    }

    public Enchantment(Enchantments enchantment, int level) {
        this.enchantment = enchantment;
        this.level = level;
    }

    public Enchantments getEnchantment() {
        return enchantment;
    }

    public int getLevel() {
        return level;
    }

    public String toString() {
        return "{id: \"" + enchantment.getString() + "\", lvl: " + level + "s}";
    }
}
