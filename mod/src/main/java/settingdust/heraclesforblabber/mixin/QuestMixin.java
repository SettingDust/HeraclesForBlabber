package settingdust.heraclesforblabber.mixin;

import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.rewards.QuestReward;
import java.util.stream.Stream;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import settingdust.heraclesforblabber.HeraclesTaskInterlocutorTracker;

@Mixin(Quest.class)
public class QuestMixin {
    @Inject(method = "claimRewards", at = @At("HEAD"))
    private void storingRewardingQuest(
            final String questId,
            final ServerPlayerEntity player,
            final Stream<? extends QuestReward<?>> rewards,
            final CallbackInfo ci) {
        final var tracker = HeraclesTaskInterlocutorTracker.Companion.getHeraclesTaskInterlocutorTracker(player);
        tracker.setRewardingQuest((Quest) (Object) this);
    }

    @Inject(method = "claimRewards", at = @At("RETURN"))
    private void clearRewardingQuest(
            final String questId,
            final ServerPlayerEntity player,
            final Stream<? extends QuestReward<?>> rewards,
            final CallbackInfo ci) {
        final var tracker = HeraclesTaskInterlocutorTracker.Companion.getHeraclesTaskInterlocutorTracker(player);
        tracker.setRewardingQuest(null);
    }
}
