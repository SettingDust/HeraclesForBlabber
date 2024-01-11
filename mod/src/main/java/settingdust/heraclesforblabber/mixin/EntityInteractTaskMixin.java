package settingdust.heraclesforblabber.mixin;

import earth.terrarium.heracles.api.tasks.defaults.EntityInteractTask;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import settingdust.heraclesforblabber.TaskWithInterlocutor;

@Mixin(EntityInteractTask.class)
public class EntityInteractTaskMixin implements TaskWithInterlocutor<Entity> {}
