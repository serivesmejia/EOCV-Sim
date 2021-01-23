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

package org.openftc.easyopencv

import com.github.serivesmejia.eocvsim.pipeline.PipelineManager

class TimestampedPipelineHandler() {

    private var timestampedPipeline: TimestampedOpenCvPipeline? = null

    private var lastNanos = 0L

    //update called from the pipelineManager onUpdate event handler
    fun update() {
        if(lastNanos == 0L) updateLastNanos()

        timestampedPipeline?.setTimestamp(System.nanoTime() - lastNanos)

        updateLastNanos()
    }

    fun pipelineChange(newPipeline: OpenCvPipeline?) {
        timestampedPipeline = if(newPipeline is TimestampedOpenCvPipeline) { newPipeline } else { null }
    }

    //registering event listeners in the pipelineManager
    fun attachToPipelineManager(pipelineManager: PipelineManager) {
        pipelineManager.onPipelineChange.doPersistent { pipelineChange(pipelineManager.currentPipeline) }
        pipelineManager.onUpdate.doPersistent { update() }
    }

    private fun updateLastNanos() {
        lastNanos = System.nanoTime();
    }

}