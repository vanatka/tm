/*
 * The MIT License (MIT)
 * Copyright (c) 2016 ivan.alyakskin@gmail.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.vanatka.tm.impl;

import android.util.Log;

import com.vanatka.tm.impl.taskactions.SaveAction;
import com.vanatka.tm.interfaces.ITask;
import com.vanatka.tm.interfaces.ITaskManager;

public class TMTask implements Runnable {

    private static final String TAG = "TMTask";

    private final ITask task;
    private final ITaskManager taskManager;

    public TMTask(ITask task, ITaskManager taskManager) {
        this.task = task;
        this.taskManager = taskManager;
    }

    @Override
    public void run( ) {
        if( null == task || null == taskManager ) {
            return;
        }

        taskManager.putTaskForTracking( task );

        boolean exception = false;
        try {
            task.started();
            exception = !task.execute();
        } catch (Exception e) {
            exception = true;
            Log.e( TAG, "exception, taskId: " + task.getId(), e );
        } finally {
            try {
                if (exception) {
                    task.finishedError();
                    if (task.retryIfFailed()) {
                        taskManager.runTask( new SaveAction( taskManager.getInputInterface(), task, taskManager ) );
                    }
                } else {
                    task.finishedOk();
                    if (task.removeInCaseOfSuccess() && task.retryIfFailed()) {
                        taskManager.removeTask( task.getId() );
                    }
                }
            } catch (Exception e) {
                Log.e( TAG, "exception, in finally " + task.getId(), e );
            }
        }
    }
}
