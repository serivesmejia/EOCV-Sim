/*
 * Copyright (c) 2021 Sebastian Erives
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 */

package com.github.serivesmejia.eocvsim.workspace

import com.github.serivesmejia.eocvsim.util.Log
import com.github.serivesmejia.eocvsim.util.SysUtil
import com.github.serivesmejia.eocvsim.util.extension.plus
import com.github.serivesmejia.eocvsim.workspace.config.WorkspaceConfig
import com.github.serivesmejia.eocvsim.workspace.config.WorkspaceConfigLoader
import java.io.File

class WorkspaceManager {

    companion object {
        private val TAG = "WorkspaceManager"
    }

    val workspaceConfigLoader by lazy { WorkspaceConfigLoader(workspaceFile) }

    var workspaceFile = File(".")
        set(value) {
            if(value != workspaceFile) {
                workspaceConfigLoader.workspaceFile = value
                field = value

                Log.info(TAG, "Set current workspace to ${value.absolutePath}")
            }

            cachedWorkspConfig = workspaceConfigLoader.loadWorkspaceConfig()

            if(cachedWorkspConfig == null) {
                cachedWorkspConfig = WorkspaceConfig()

                if(value.exists())
                    Log.warn(TAG, "Recreating workspace config file, old one failed to parse")
                else
                    Log.info(TAG, "Creating workspace config file")

                workspaceConfigLoader.saveWorkspaceConfig(workspaceConfig)
            } else {
                Log.info(TAG, "Loaded workspace config successfully")
            }
        }

    private var cachedWorkspConfig: WorkspaceConfig? = null
    var workspaceConfig: WorkspaceConfig
        set(value) {
            Log.info(TAG, "Saving workspace config file of ${workspaceFile.absolutePath}")
            workspaceConfigLoader.saveWorkspaceConfig(value)
            cachedWorkspConfig = value
        }
        get() {
            if(cachedWorkspConfig == null)
                ::workspaceFile.set(workspaceFile)

            return cachedWorkspConfig!!
        }

    // TODO: Excluding ignored paths
    val sourceFiles get() =
        SysUtil.filesUnder(workspaceFile + workspaceConfig.sourcesPath, ".java")

    fun saveCurrentConfig() {
        ::workspaceConfig.set(workspaceConfig)
    }

    fun reloadConfig() = workspaceConfig

}