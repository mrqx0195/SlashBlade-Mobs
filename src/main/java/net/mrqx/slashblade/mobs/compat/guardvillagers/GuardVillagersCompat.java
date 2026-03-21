package net.mrqx.slashblade.mobs.compat.guardvillagers;

import tallestegg.guardvillagers.entities.Guard;

import java.util.ArrayList;
import java.util.List;

public class GuardVillagersCompat {
    public static List<Class<?>> getSlashVillagerIgnores() {
        List<Class<?>> list = new ArrayList<>();
        list.add(Guard.class);
        return list;
    }
}
