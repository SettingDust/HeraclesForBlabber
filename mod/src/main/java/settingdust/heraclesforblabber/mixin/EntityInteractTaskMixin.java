package settingdust.heraclesforblabber.mixin;

import earth.terrarium.heracles.api.tasks.defaults.EntityInteractTask;
import org.spongepowered.asm.mixin.Mixin;
import settingdust.heraclesforblabber.TaskWithInterlocutor;

@Mixin(EntityInteractTask.class)
public class EntityInteractTaskMixin implements TaskWithInterlocutor<EntityInteractTask> {}
