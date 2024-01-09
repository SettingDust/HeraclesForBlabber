package settingdust.heraclesforblabber

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler
import earth.terrarium.heracles.common.handlers.quests.QuestHandler
import net.minecraft.entity.Entity
import net.minecraft.server.network.ServerPlayerEntity
import org.ladysnake.blabber.api.DialogueActionV2

data class CompleteTaskAction(val questId: String, val taskId: String) : DialogueActionV2 {
    companion object {
        val CODEC =
            RecordCodecBuilder.create<CompleteTaskAction> { instance ->
                instance
                    .group(
                        Codec.STRING.fieldOf("quest_id").forGetter { it.questId },
                        Codec.STRING.fieldOf("task_id").forGetter { it.taskId },
                    )
                    .apply(instance, ::CompleteTaskAction)
            }!!
    }

    override fun handle(player: ServerPlayerEntity, interlocutor: Entity?) {
        val quest = QuestHandler.get(questId)
        val task = quest.tasks[taskId]
        if (task !is ChatTask) {
            HeraclesForBlabber.LOGGER.error(
                "Task $taskId of quest $questId is not an EntityChatTask."
            )
            return
        }
        val questsProgress = QuestProgressHandler.getProgress(player.server, player.uuid)
        val questProgress = questsProgress.getProgress(questId)
        val taskProgress = questProgress.getTask(task)
        // Mark the chat task completed
        taskProgress.addProgress(task.type(), task, Pair.of(true, null))
    }
}
