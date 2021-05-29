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
package com.github.serivesmejia.eocvsim.pipeline.util;

import com.github.serivesmejia.eocvsim.pipeline.PipelineData
import com.github.serivesmejia.eocvsim.util.event.EventHandler
import com.github.serivesmejia.eocvsim.util.StrUtil
import com.github.serivesmejia.eocvsim.util.Log

class PipelineExceptionTracker {

    companion object {
        private const val TAG = "PipelineExceptionTracker"
    }

    var latestException: Pair<PipelineData, Throwable>? = null
        private set

    var timesHappenedCount = 0
        private set

    val onPipelineException = EventHandler("OnPipelineException")

    fun reportException(data: PipelineData, ex: Throwable) {
        if(timesHappenedCount == 0) {
            Log.blank()
            Log.warn(
                TAG, "Uncaught exception thrown while processing pipeline ${data.clazz.simpleName}",
                ex
            )

            Log.warn(TAG, "Note that to avoid spam, continuously equal thrown exceptions are only logged once.")
            Log.warn(TAG, "It will be reported once the pipeline stops throwing the exception, or a new one is thrown.")
            Log.blank()

            timesHappenedCount++;
        }

        latestException?.let {
            if(StrUtil.fromException(ex) == StrUtil.fromException(it.second)) {
                timesHappenedCount++
            }
        }

        latestException = Pair(data, ex)

        onPipelineException.run()
    }

    fun clearException() {
        latestException?.let {
            Log.warn(
                TAG,
                "Pipeline ${it.first.clazz.simpleName} stopped throwing exception after $timesHappenedCount frames"
            )
        }

        latestException = null
        timesHappenedCount = 0
    }


}
