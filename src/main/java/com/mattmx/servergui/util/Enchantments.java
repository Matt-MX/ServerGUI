package com.mattmx.servergui.util;

public enum Enchantments {
    MENDING,
    UNBREAKING,
    CURSE_OF_VANISHING,
    AQUA_AFFINITY,
    BLAST_PROTECTION,
    CURSE_OF_BINDING,
    DEPTH_STRIDER,
    FEATHER_FALLING,
    FIRE_PROTECTION,
    FROST_WALKER,
    PROJECTILE_PROTECTION,
    PROTECTION,
    RESPIRATION,
    SOUL_SPEED,
    THORNS,
    BANE_OF_ARTHROPODS,
    FIRE_ASPECT,
    LOOTING,
    IMPALING,
    KNOCKBACK,
    SHARPNESS,
    SMITE,
    SWEEPING_EDGE,
    CHANNELING,
    FLAME,
    INFINITY,
    LOYALTY,
    RIPTIDE,
    MULTISHOT,
    PIERCING,
    POWER,
    PUNCH,
    QUICK_CHARGE,
    EFFICIENCY,
    FORTUNE,
    LUCK_OF_THE_SEA,
    LURE,
    SILK_TOUCH;
    public String getString() {
        return "minecraft:" + this.toString().toLowerCase();
    }
    public String display() {
        String s = this.toString().toLowerCase().replace("_", " ");
        return s.toLowerCase();
    }
}