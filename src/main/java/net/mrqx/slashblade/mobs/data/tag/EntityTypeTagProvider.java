package net.mrqx.slashblade.mobs.data.tag;

import mods.flammpfeil.slashblade.data.tag.SlashBladeEntityTypeTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.EntityTypeTagsProvider;
import net.mrqx.slashblade.mobs.registy.SlashMobsEntities;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import javax.annotation.Nullable;
import java.util.concurrent.CompletableFuture;

public class EntityTypeTagProvider extends EntityTypeTagsProvider {
    public EntityTypeTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, modId, existingFileHelper);
    }
    
    @Override
    protected void addTags(HolderLookup.Provider lookupProvider) {
        this.tag(SlashBladeEntityTypeTagProvider.EntityTypeTags.RENDER_LAYER_BLACKLIST)
            .add(SlashMobsEntities.SLASH_ZOMBIE.get())
            .add(SlashMobsEntities.SLASH_HUSK.get())
            .add(SlashMobsEntities.SLASH_DROWNED.get())
            .add(SlashMobsEntities.SLASH_SKELETON.get())
            .add(SlashMobsEntities.SLASH_STRAY.get())
            .add(SlashMobsEntities.SLASH_WITHER_SKELETON.get())
            .add(SlashMobsEntities.SLASH_VILLAGER.get())
            .add(SlashMobsEntities.SLASH_ILLAGER.get());
    }
}
