package settingdust.heraclesforblabber

import com.mojang.datafixers.util.Pair
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import earth.terrarium.heracles.api.quests.Quest
import earth.terrarium.heracles.api.rewards.QuestRewards
import earth.terrarium.heracles.api.tasks.QuestTask
import earth.terrarium.heracles.api.tasks.QuestTasks
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler
import earth.terrarium.heracles.common.network.NetworkHandler
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.apache.logging.log4j.LogManager
import org.ladysnake.blabber.Blabber
import settingdust.heraclesforblabber.HeraclesTaskInterlocutorTracker.Companion.heraclesTaskInterlocutorTracker

fun init() {
    NetworkHandler.CHANNEL.register(ClaimChatRewardPacket.TYPE)

    Blabber.registerAction(HeraclesForBlabber.identifier("complete_task"), CompleteTaskAction.CODEC)

    QuestTasks.register(ChatTask.Type)
    QuestRewards.register(ChatReward.Type)
}

fun dialogueStarted(dialogue: Identifier, playerEntity: ServerPlayerEntity) {
    QuestProgressHandler.getProgress(playerEntity.server, playerEntity.uuid)
        .testAndProgressTaskType(playerEntity, Pair.of(false, dialogue), ChatTask.Type)
}

fun <T> taskCompleted(
    quest: Quest,
    task: QuestTask<T, *, *>,
    player: ServerPlayerEntity,
    input: T
) {
    if (task !is TaskWithInterlocutor<*>) return
    player.heraclesTaskInterlocutorTracker.data[quest] =
        (task as TaskWithInterlocutor<T>).getInterlocutor(input)
}

object HeraclesForBlabber {
    const val ID = "heracles-for-blabber"
    val LOGGER = LogManager.getLogger()!!

    fun identifier(path: String) = Identifier(ID, path)

    object Components : EntityComponentInitializer {
        val HERACLES_TASK_INTERLOCUTOR_TRACKER =
            ComponentRegistry.getOrCreate(
                identifier("heracles_task_interlocutor_tracker"),
                HeraclesTaskInterlocutorTracker::class.java,
            )

        override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
            registry.registerForPlayers(HERACLES_TASK_INTERLOCUTOR_TRACKER) {
                HeraclesTaskInterlocutorTracker(it)
            }
        }
    }
}
