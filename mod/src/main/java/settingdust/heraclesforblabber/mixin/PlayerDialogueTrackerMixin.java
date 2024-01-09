package settingdust.heraclesforblabber.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.ladysnake.blabber.impl.common.PlayerDialogueTracker;
import org.ladysnake.blabber.impl.common.model.DialogueTemplate;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import settingdust.heraclesforblabber.EntrypointKt;

@Mixin(PlayerDialogueTracker.class)
public class PlayerDialogueTrackerMixin {
    @Shadow
    @Final
    private PlayerEntity player;

    @Inject(
            method = "startDialogue0",
            at =
                    @At(
                            value = "INVOKE",
                            remap = false,
                            target = "Lorg/ladysnake/blabber/impl/common/PlayerDialogueTracker;openDialogueScreen()V",
                            shift = At.Shift.AFTER))
    private void dialogueStarted(
            final Identifier id,
            final DialogueTemplate template,
            final String start,
            final Entity interlocutor,
            final CallbackInfo ci) {
        EntrypointKt.dialogueStarted(id, (ServerPlayerEntity) player);
    }
}
