package settingdust.heraclesforblabber

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import com.teamresourceful.resourcefullib.common.network.Packet
import com.teamresourceful.resourcefullib.common.network.base.PacketType
import com.teamresourceful.resourcefullib.common.network.base.ServerboundPacketType
import earth.terrarium.heracles.api.CustomizableQuestElement
import earth.terrarium.heracles.api.quests.QuestIcon
import earth.terrarium.heracles.api.quests.QuestIcons
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon
import earth.terrarium.heracles.api.rewards.QuestReward
import earth.terrarium.heracles.api.rewards.QuestRewardType
import earth.terrarium.heracles.common.handlers.progress.QuestProgressHandler
import earth.terrarium.heracles.common.handlers.quests.QuestHandler
import java.util.function.Consumer
import java.util.stream.Stream
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.network.PacketByteBuf
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
                    entity
                },
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

@JvmRecord
data class ClaimChatRewardPacket(val quest: String, val reward: String) :
    Packet<ClaimChatRewardPacket> {
    override fun type(): PacketType<ClaimChatRewardPacket> {
        return TYPE
    }

    class Type : ServerboundPacketType<ClaimChatRewardPacket> {
        override fun type(): Class<ClaimChatRewardPacket> {
            return ClaimChatRewardPacket::class.java
        }

        override fun id(): Identifier {
            return HeraclesForBlabber.identifier("claim_chat_reward")
        }

        override fun encode(message: ClaimChatRewardPacket, buffer: PacketByteBuf) {
            buffer.writeString(message.quest)
            buffer.writeString(message.reward)
        }

        override fun decode(buffer: PacketByteBuf): ClaimChatRewardPacket {
            return ClaimChatRewardPacket(buffer.readString(), buffer.readString())
        }

        override fun handle(message: ClaimChatRewardPacket): Consumer<PlayerEntity> {
            return Consumer { player ->
                val quest = QuestHandler.get(message.quest)
                if (quest != null) {
                    val questReward = quest.rewards[message.reward]

                    if (questReward == null || questReward !is ChatReward) {
                        HeraclesForBlabber.LOGGER.error(
                            "Wrong reward type for ${id()}: ${questReward?.javaClass?.simpleName}"
                        )
                        return@Consumer
                    }

                    val progress = QuestProgressHandler.getProgress(player.server!!, player.uuid)

                    if (
                        (progress.isComplete(message.quest) || quest.tasks.isEmpty()) &&
                            progress.isClaimed(message.reward, quest)
                    ) {
                        PlayerDialogueTracker.get(player)
                            .startDialogue(
                                questReward.dialogue,
                                player.heraclesTaskInterlocutorTracker.data[quest]
                            )
                    } else {
                        quest.claimAllowedReward(
                            player as ServerPlayerEntity,
                            message.quest,
                            message.reward,
                        )

                        QuestProgressHandler.sync(
                            player,
                            setOf(message.quest),
                        )
                    }
                }
            }
        }
    }

    companion object {
        val TYPE: ServerboundPacketType<ClaimChatRewardPacket> = Type()
    }
}
