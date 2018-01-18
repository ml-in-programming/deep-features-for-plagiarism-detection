package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.ui.Messages

class LaunchButton : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.getData(PlatformDataKeys.PROJECT)

        if (project == null) {
            Messages.showMessageDialog(
                project,
                "Problems with getting of project",
                "Information",
                Messages.getInformationIcon()
            )

            return
        }

        Transformator(project).perform()
    }
}
