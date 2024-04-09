package settingdust.heraclesforblabber.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import earth.terrarium.heracles.api.quests.Quest;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import settingdust.heraclesforblabber.HeraclesTaskInterlocutorTracker;

@Mixin(Quest.class)
public class QuestMixin {
    @Inject(method = "claimRewards", at = @At("HEAD"))
    private void storingRewardingQuest(final CallbackInfo ci, @Local(argsOnly = true) ServerPlayerEntity player) {
        final var tracker = HeraclesTaskInterlocutorTracker.Companion.getHeraclesTaskInterlocutorTracker(player);
        tracker.setRewardingQuest((Quest) (Object) this);
    }

    @Inject(method = "claimRewards", at = @At("RETURN"))
    private void clearRewardingQuest(final CallbackInfo ci, @Local(argsOnly = true) ServerPlayerEntity player) {
        final var tracker = HeraclesTaskInterlocutorTracker.Companion.getHeraclesTaskInterlocutorTracker(player);
        tracker.setRewardingQuest(null);
    }
}
