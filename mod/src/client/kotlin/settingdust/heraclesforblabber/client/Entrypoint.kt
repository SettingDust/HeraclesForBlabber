package settingdust.heraclesforblabber.client

import com.terraformersmc.modmenu.api.ModMenuApi
import earth.terrarium.heracles.api.client.settings.Settings
import earth.terrarium.heracles.api.rewards.client.QuestRewardWidgets
import earth.terrarium.heracles.api.tasks.client.QuestTaskWidgets
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatter
import earth.terrarium.heracles.api.tasks.client.display.TaskTitleFormatters
import net.minecraft.text.Text
import settingdust.heraclesforblabber.ChatReward
import settingdust.heraclesforblabber.ChatTask
import settingdust.heraclesforblabber.HeraclesForBlabber

val INVALID_ID = HeraclesForBlabber.identifier("invalid")

fun init() {
    Settings.register(ChatTask.Type, ChatSettings)
    QuestTaskWidgets.registerSimple(ChatTask.Type, ::ChatTaskWidget)
    TaskTitleFormatter.register(ChatTask.Type) { task ->
        Text.translatable(TaskTitleFormatters.toTranslationKey(task, true))
    }

    Settings.register(ChatReward.Type, ChatRewardSettings)
    QuestRewardWidgets.register(ChatReward.Type, ChatRewardWidget::of)
}

object ModMenuEntrypoint : ModMenuApi {}
