package settingdust.heraclesforblabber.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import earth.terrarium.heracles.api.quests.Quest;
import earth.terrarium.heracles.api.tasks.QuestTask;
import earth.terrarium.heracles.api.tasks.QuestTaskType;
import earth.terrarium.heracles.common.handlers.progress.QuestsProgress;
import earth.terrarium.heracles.common.handlers.progress.TaskProgress;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import settingdust.heraclesforblabber.EntrypointKt;

@Mixin(QuestsProgress.class)
public class QuestsProgressMixin {
    @Inject(
            method = "testAndProgressTaskType",
            at = @At(value = "INVOKE", target = "Ljava/util/List;add(Ljava/lang/Object;)Z", shift = At.Shift.AFTER))
    private void onTaskComplete(
            final ServerPlayerEntity player,
            final Object input,
            final QuestTaskType<?> taskType,
            final CallbackInfo ci,
            @Local QuestTask<? super Object, ?, ?> task,
            @Local TaskProgress<?> progress,
            @Local Quest quest) {
        if (progress.isComplete()) EntrypointKt.taskCompleted(quest, task, player, input);
    }
}
