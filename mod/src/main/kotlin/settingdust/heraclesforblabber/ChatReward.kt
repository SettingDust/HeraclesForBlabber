package settingdust.heraclesforblabber

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import earth.terrarium.heracles.api.CustomizableQuestElement
import earth.terrarium.heracles.api.quests.QuestIcon
import earth.terrarium.heracles.api.quests.QuestIcons
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon
import earth.terrarium.heracles.api.rewards.QuestReward
import earth.terrarium.heracles.api.rewards.QuestRewardType
import java.util.stream.Stream
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.server.network.ServerPlayerEntity
import net.minecraft.util.Identifier
import org.ladysnake.blabber.impl.common.PlayerDialogueTracker
import settingdust.heraclesforblabber.HeraclesTaskInterlocutorTracker.Companion.heraclesTaskInterlocutorTracker

data class ChatReward(
    val id: String,
    val title: String,
    val icon: QuestIcon<*>,
    val dialogue: Identifier
) : QuestReward<ChatReward>, CustomizableQuestElement {
    override fun id() = id

    override fun title() = title

    override fun icon() = icon

    @Suppress("UnstableApiUsage")
    override fun reward(player: ServerPlayerEntity): Stream<ItemStack> {
        PlayerDialogueTracker.get(player)
            .startDialogue(
                dialogue,
                player.heraclesTaskInterlocutorTracker.rewardingQuest?.let {
                    val entity = player.heraclesTaskInterlocutorTracker.data[it]
                    player.heraclesTaskInterlocutorTracker.data.remove(it)
                    player.heraclesTaskInterlocutorTracker.rewardingQuest = null
                    entity
                }
            )
        return Stream.empty()
    }

    override fun type() = Type

    data object Type : QuestRewardType<ChatReward> {
        override fun id() = HeraclesForBlabber.identifier("chat")

        override fun codec(id: String): Codec<ChatReward> =
            RecordCodecBuilder.create { instance ->
                instance
                    .group(
                        RecordCodecBuilder.point(id),
                        Codec.STRING.fieldOf("title").orElse("").forGetter { it.title },
                        QuestIcons.CODEC.fieldOf("icon")
                            .orElse(ItemQuestIcon(Items.AIR))
                            .forGetter { it.icon },
                        Identifier.CODEC.fieldOf("dialogue").forGetter(ChatReward::dialogue),
                    )
                    .apply(instance, ::ChatReward)
            }
    }
}
