package settingdust.heraclesforblabber.client

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack
import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings
import earth.terrarium.heracles.api.client.settings.SettingInitializer
import earth.terrarium.heracles.api.client.settings.base.AutocompleteTextSetting
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme
import earth.terrarium.heracles.api.rewards.client.defaults.BaseItemRewardWidget
import earth.terrarium.heracles.client.handlers.ClientQuests
import earth.terrarium.heracles.client.screens.quest.BaseQuestScreen
import earth.terrarium.heracles.common.handlers.progress.QuestProgress
import earth.terrarium.heracles.common.network.NetworkHandler
import earth.terrarium.heracles.common.network.packets.rewards.ClaimRewardsPacket
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.text.Text
import org.ladysnake.blabber.impl.common.DialogueRegistry
import settingdust.heraclesforblabber.ChatReward
import settingdust.heraclesforblabber.HeraclesForBlabber

@Suppress("UnstableApiUsage")
object ChatRewardSettings :
    SettingInitializer<ChatReward>, CustomizableQuestElementSettings<ChatReward> {
    private val DIALOGUES =
        AutocompleteTextSetting(
            { DialogueRegistry.getIds().toList() },
            { text, item ->
                item.toString().lowercase().contains(text.lowercase()) &&
                    !item.toString().equals(text, ignoreCase = true)
            },
            { it?.toString() ?: "" },
        )

    override fun create(`object`: ChatReward?): SettingInitializer.CreationData {
        val settings = super.create(`object`)
        settings.put(
            "dialogue",
            DIALOGUES,
            DialogueRegistry.getIds().firstOrNull() ?: INVALID_ID,
        )
        return settings
    }

    override fun create(id: String, `object`: ChatReward?, data: SettingInitializer.Data) =
        create(`object`, data) { title, icon ->
            ChatReward(
                id,
                title,
                icon,
                data
                    .get("dialogue", DIALOGUES)
                    .orElse(
                        DialogueRegistry.getIds().firstOrNull() ?: INVALID_ID,
                    ),
            )
        }
}

data class ChatRewardWidget(
    val reward: ChatReward,
    val quest: String,
    val progress: QuestProgress?,
    val interactive: Boolean = false
) : BaseItemRewardWidget {
    companion object {
        private const val TITLE_SINGULAR = "reward.${HeraclesForBlabber.ID}.chat.title.singular"
        private const val DESC_SINGULAR = "reward.${HeraclesForBlabber.ID}.chat.desc.singular"

        fun of(reward: ChatReward, interactive: Boolean): ChatRewardWidget {
            val screen = MinecraftClient.getInstance().currentScreen
            if (screen is BaseQuestScreen) {
                return ChatRewardWidget(
                    reward,
                    screen.questId,
                    ClientQuests.getProgress(screen.questId),
                    interactive,
                )
            }
            return ChatRewardWidget(reward, "", null, interactive)
        }
    }

    override fun getIconOverride() = reward.icon

    override fun getIcon() =
        ItemStack(Items.PAPER).apply { setCustomName(Text.translatable(TITLE_SINGULAR)) }

    override fun canClaim() = progress?.canClaim(reward.id) ?: false

    override fun claimReward() {
        progress?.claimReward(reward.id)
        NetworkHandler.CHANNEL.sendToServer(
            ClaimRewardsPacket(
                this.quest,
                reward.id(),
            ),
        )
    }

    override fun isInteractive() = interactive

    override fun render(
        graphics: DrawContext,
        scissor: ScissorBoxStack?,
        x: Int,
        y: Int,
        width: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        partialTicks: Float
    ) {
        val font = MinecraftClient.getInstance().textRenderer
        super.render(graphics, scissor, x, y, width, mouseX, mouseY, hovered, partialTicks)
        val desc: String = reward.dialogue.toString()
        graphics.drawText(
            font,
            reward.titleOr(Text.translatable(TITLE_SINGULAR)),
            x + 48,
            y + 6,
            QuestScreenTheme.getRewardTitle(),
            false,
        )
        graphics.drawText(
            font,
            Text.translatable(DESC_SINGULAR, desc),
            x + 48,
            y + 8 + font.fontHeight,
            QuestScreenTheme.getRewardDescription(),
            false,
        )
    }
}
