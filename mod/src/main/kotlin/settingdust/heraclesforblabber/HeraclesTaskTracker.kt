package settingdust.heraclesforblabber

import com.google.common.collect.BiMap
import dev.onyxstudios.cca.api.v3.entity.PlayerComponent
import earth.terrarium.heracles.api.quests.Quest
import earth.terrarium.heracles.common.handlers.quests.QuestHandler
import net.minecraft.entity.Entity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.nbt.NbtCompound
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.util.Identifier

@Suppress("UnstableApiUsage")
data class HeraclesTaskInterlocutorTracker(val player: PlayerEntity) :
    PlayerComponent<HeraclesTaskInterlocutorTracker> {
    var data = mutableMapOf<Quest, Entity>()
        private set

    var rewardingQuest: Quest? = null

    companion object {
        val PlayerEntity.heraclesTaskInterlocutorTracker
            get() = HeraclesForBlabber.Components.HERACLES_TASK_INTERLOCUTOR_TRACKER[this]
    }

    override fun readFromNbt(tag: NbtCompound) {
        data.clear()
        for (key in tag.keys) {
            data[QuestHandler.get(key)] =
                player.server!!
                    .getWorld(
                        RegistryKey.of(
                            RegistryKeys.WORLD,
                            Identifier.tryParse(tag.getString("world"))
                        )
                    )!!
                    .getEntity(tag.getUuid("entity"))!!
        }
    }

    override fun writeToNbt(tag: NbtCompound) {
        for ((quest, entity) in data) {
            tag.put(
                (QuestHandler.quests() as BiMap<String, Quest>).inverse()[quest]!!,
                NbtCompound().apply {
                    putUuid("entity", entity.uuid)
                    putString("world", entity.world.registryKey.value.toString())
                }
            )
        }
    }
}
