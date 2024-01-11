package settingdust.heraclesforblabber.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import settingdust.heraclesforblabber.TaskWithInterlocutor;
import settingdust.heraclesforvillagers.VillagerInteractTask;

@Mixin(VillagerInteractTask.class)
public class VillagerInteractTaskMixin implements TaskWithInterlocutor<Pair<PlayerEntity, VillagerEntity>> {
    @NotNull
    @Override
    public Entity getInterlocutor(final Pair<PlayerEntity, VillagerEntity> input) {
        return input.getSecond();
    }
}
