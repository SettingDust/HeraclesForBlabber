package settingdust.heraclesforblabber

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import earth.terrarium.heracles.api.CustomizableQuestElement
import earth.terrarium.heracles.api.quests.QuestIcon
import earth.terrarium.heracles.api.quests.QuestIcons
import earth.terrarium.heracles.api.quests.defaults.ItemQuestIcon
import earth.terrarium.heracles.api.tasks.PairQuestTask
import earth.terrarium.heracles.api.tasks.QuestTaskType
import earth.terrarium.heracles.api.tasks.storage.defaults.BooleanTaskStorage
import net.minecraft.item.Items
import net.minecraft.nbt.NbtByte
import net.minecraft.util.Identifier

data class ChatTask(
    val id: String,
    val title: String,
    val icon: QuestIcon<*>,
    val dialogue: Identifier,
    val needAction: Boolean = true
) : PairQuestTask<Boolean, Identifier?, NbtByte, ChatTask>, CustomizableQuestElement {
    override fun id() = id

    override fun title() = title

    override fun icon() = icon

    override fun storage() = BooleanTaskStorage.INSTANCE!!

    override fun type() = Type

    override fun getProgress(progress: NbtByte) = if (storage().readBoolean(progress)) 1F else 0F

    override fun test(
        type: QuestTaskType<*>,
        progress: NbtByte,
        completed: Boolean,
        dialogue: Identifier?
    ): NbtByte =
        BooleanTaskStorage.INSTANCE.of(
            progress,
            completed || (dialogue == this.dialogue && !needAction)
        )

    object Type : QuestTaskType<ChatTask> {
        override fun id() = HeraclesForBlabber.identifier("chat")

        override fun codec(id: String) =
            RecordCodecBuilder.create<ChatTask> { instance ->
                instance
                    .group(
                        RecordCodecBuilder.point(id),
                        Codec.STRING.fieldOf("title").orElse("").forGetter { it.title },
                        QuestIcons.CODEC.fieldOf("icon")
                            .orElse(ItemQuestIcon(Items.PAPER))
                            .forGetter { it.icon },
                        Identifier.CODEC.fieldOf("dialogue").forGetter { it.dialogue },
                        Codec.BOOL.fieldOf("need_action").orElse(false).forGetter { it.needAction },
                    )
                    .apply(instance, ::ChatTask)
            }!!
    }
}
