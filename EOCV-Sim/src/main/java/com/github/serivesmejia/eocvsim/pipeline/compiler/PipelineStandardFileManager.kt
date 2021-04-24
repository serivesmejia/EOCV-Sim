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

package com.github.serivesmejia.eocvsim.pipeline.compiler

import com.github.serivesmejia.eocvsim.EOCVSim
import com.github.serivesmejia.eocvsim.pipeline.compiler.CompiledPipelineManager
import com.github.serivesmejia.eocvsim.util.SysUtil
import com.github.serivesmejia.eocvsim.util.compiler.DelegatingStandardFileManager
import java.io.File
import java.util.*
import java.util.function.Predicate
import javax.tools.StandardJavaFileManager
import javax.tools.StandardLocation

class PipelineStandardFileManager(delegate: StandardJavaFileManager) : DelegatingStandardFileManager(delegate) {

    var sourcePath: Iterable<File>
        set(value) = delegate.setLocation(StandardLocation.SOURCE_PATH, value)
        get() = delegate.getLocation(StandardLocation.SOURCE_PATH)

    init {
        val classpath = arrayListOf<File>()

        for(file in SysUtil.getClasspathFiles()) {
            classpath.addAll(
                SysUtil.filesUnder(file, ".jar")
            )
        }

        for(file in classpath) {
            println(file)
        }

        delegate.setLocation(StandardLocation.CLASS_PATH, classpath)
        delegate.setLocation(StandardLocation.CLASS_OUTPUT, Collections.singletonList(CompiledPipelineManager.CLASSES_OUTPUT_FOLDER))
        delegate.setLocation(StandardLocation.SOURCE_OUTPUT, Collections.singletonList(CompiledPipelineManager.SOURCES_OUTPUT_FOLDER))
    }

}