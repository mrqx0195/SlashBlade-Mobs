package net.mrqx.slashblade.mobs.compat;

import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.raid.Raider;
import net.mrqx.slashblade.mobs.compat.guardvillagers.GuardVillagersCompat;
import org.apache.logging.log4j.util.LoaderUtil;

import java.util.ArrayList;
import java.util.List;

public class SlashBladeMobCompat {
    private static final SlashBladeMobCompat INSTANCE = new SlashBladeMobCompat();
    
    public static SlashBladeMobCompat getInstance() {
        return INSTANCE;
    }
    
    public final boolean hasGuardVillagers;
    
    private SlashBladeMobCompat() {
        this.hasGuardVillagers = LoaderUtil.isClassAvailable("tallestegg.guardvillagers.GuardVillagers");
    }
    
    public static class Factories {
        private static final List<Class<?>> SLASH_VILLAGER_IGNORES = new ArrayList<>();
        private static final List<Class<?>> SLASH_ILLAGER_IGNORES = new ArrayList<>();
        
        public static List<Class<?>> getSlashVillagerIgnores() {
            if (SLASH_VILLAGER_IGNORES.isEmpty()) {
                List<Class<?>> list = new ArrayList<>();
                list.add(AbstractVillager.class);
                list.add(IronGolem.class);
                if (SlashBladeMobCompat.getInstance().hasGuardVillagers) {
                    list.addAll(GuardVillagersCompat.getSlashVillagerIgnores());
                }
                SLASH_VILLAGER_IGNORES.addAll(list);
            }
            return SLASH_VILLAGER_IGNORES;
        }
        
        public static List<Class<?>> getSlashIllagerIgnores() {
            if (SLASH_ILLAGER_IGNORES.isEmpty()) {
                List<Class<?>> list = new ArrayList<>();
                list.add(Raider.class);
                SLASH_ILLAGER_IGNORES.addAll(list);
            }
            return SLASH_ILLAGER_IGNORES;
        }
    }
}
