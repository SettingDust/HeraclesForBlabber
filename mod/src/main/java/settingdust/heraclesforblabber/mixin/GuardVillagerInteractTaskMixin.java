package settingdust.heraclesforblabber.mixin;

import com.mojang.datafixers.util.Pair;
import dev.sterner.guardvillagers.common.entity.GuardEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import settingdust.heraclesforblabber.TaskWithInterlocutor;
import settingdust.heraclesforvillagers.GuardVillagerInteractTask;

@Mixin(GuardVillagerInteractTask.class)
public class GuardVillagerInteractTaskMixin implements TaskWithInterlocutor<Pair<PlayerEntity, GuardEntity>> {
    @NotNull
    @Override
    public Entity getInterlocutor(final Pair<PlayerEntity, GuardEntity> input) {
        return input.getSecond();
    }
}
