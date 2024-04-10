package settingdust.heraclesforblabber.client

import com.teamresourceful.resourcefullib.client.scissor.ScissorBoxStack
import earth.terrarium.heracles.api.client.DisplayWidget
import earth.terrarium.heracles.api.client.WidgetUtils
import earth.terrarium.heracles.api.client.settings.CustomizableQuestElementSettings
import earth.terrarium.heracles.api.client.settings.SettingInitializer
import earth.terrarium.heracles.api.client.settings.base.BooleanSetting
import earth.terrarium.heracles.api.client.theme.QuestScreenTheme
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter
import earth.terrarium.heracles.common.handlers.progress.TaskProgress
import net.minecraft.client.MinecraftClient
import net.minecraft.client.gui.DrawContext
import net.minecraft.nbt.AbstractNbtNumber
import net.minecraft.text.Text
import org.ladysnake.blabber.impl.common.DialogueRegistry
import settingdust.heraclesforblabber.ChatTask
import settingdust.heraclesforblabber.HeraclesForBlabber

object ChatSettings : SettingInitializer<ChatTask>, CustomizableQuestElementSettings<ChatTask> {
    override fun create(task: ChatTask?) =
        super.create(task).apply {
            put(
                "dialogue",
                ChatRewardSettings.DIALOGUES,
                task?.dialogue ?: DialogueRegistry.getIds().firstOrNull() ?: INVALID_ID
            )
            put("need_action", BooleanSetting.TRUE, task?.needAction ?: true)
        }!!

    override fun create(id: String, task: ChatTask?, data: SettingInitializer.Data): ChatTask {
        val dialogue =
            data
                .get("dialogue", ChatRewardSettings.DIALOGUES)
                .orElse(task?.dialogue ?: DialogueRegistry.getIds().firstOrNull() ?: INVALID_ID)
        val needAction =
            data.get("need_action", BooleanSetting.TRUE).orElse(task?.needAction ?: true)
        return create(task, data) { title, icon -> ChatTask(id, title, icon, dialogue, needAction) }
    }
}

data class ChatTaskWidget(val task: ChatTask, val progress: TaskProgress<AbstractNbtNumber>) :
    DisplayWidget {
    companion object {
        private const val DESC = "task.${HeraclesForBlabber.ID}.chat.desc.singular"
    }

    override fun render(
        graphics: DrawContext,
        scissor: ScissorBoxStack,
        x: Int,
        y: Int,
        width: Int,
        mouseX: Int,
        mouseY: Int,
        hovered: Boolean,
        partialTicks: Float
    ) {
        val height = getHeight(width)

        val font = MinecraftClient.getInstance().textRenderer
        WidgetUtils.drawBackground(graphics, x, y, width, height)

        val iconSize = 32
        task.icon().render(graphics, scissor, x + 5, y + 5, iconSize, iconSize)
        graphics.drawText(
            font,
            task.titleOr(TaskTitleFormatter.create(task)),
            x + iconSize + 16,
            y + 6,
            QuestScreenTheme.getTaskTitle(),
            false,
        )
        graphics.drawText(
            font,
            Text.translatable(DESC, task.dialogue),
            x + iconSize + 16,
            y + 8 + font.fontHeight,
            QuestScreenTheme.getTaskDescription(),
            false,
        )
        WidgetUtils.drawProgressText(graphics, x, y, width, this.task, this.progress)

        WidgetUtils.drawProgressBar(
            graphics,
            x + iconSize + 16,
            y + height - font.fontHeight - 5,
            x + width - 5,
            y + height - 6,
            this.task,
            this.progress,
        )
    }

    override fun getHeight(width: Int) = 42
}
