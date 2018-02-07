package ru.spbau.bachelors2015.veselov.githubfac

import com.intellij.ide.impl.PatchProjectUtil
import com.intellij.ide.impl.ProjectUtil
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.application.ApplicationStarter
import com.intellij.openapi.application.ex.ApplicationEx
import com.intellij.openapi.project.ex.ProjectManagerEx
import com.intellij.openapi.project.impl.ProjectManagerImpl
import com.intellij.openapi.vfs.VirtualFileManager
import com.intellij.testFramework.runInLoadComponentStateMode
import com.intellij.testFramework.use
import com.intellij.util.Time
import java.io.File

class Starter : ApplicationStarter {
    private var projectFolderPath: String = ""

    override fun getCommandName(): String {
        return "transform"
    }

    override fun premain(args: Array<out String>?) {
        if (args == null || args.size != 2) {
            System.err.println("Invalid number of arguments!")
            System.exit(1)
            return
        }

        projectFolderPath = File(args[1]).absolutePath.replace(File.separatorChar, '/')
    }

    override fun main(args: Array<out String>?) {
        val application = ApplicationManager.getApplication() as ApplicationEx

        try {
            application.doNotSave()
            val project = ProjectUtil.openOrImport(
                projectFolderPath,
                null,
                false
            )

            if (project == null) {
                Log.write("Unable to open project: $projectFolderPath")
                System.exit(1)
                return
            }

            application.runWriteAction {
                VirtualFileManager.getInstance()
                                  .refreshWithoutFileWatcher(false)
            }

            PatchProjectUtil.patchProject(project)

            Log.write("Project $projectFolderPath is opened")
            TransformationManager(project).run()
        } catch (e: Throwable) {
            Log.err("Exception occurred: $e")
            e.printStackTrace()
        }

        Log.write("Closing $projectFolderPath")
        application.exit(true, true)
        // System.exit(0)
    }
}