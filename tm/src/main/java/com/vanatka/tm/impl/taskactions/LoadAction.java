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

package com.vanatka.tm.impl.taskactions;

import android.util.Log;

import com.vanatka.tm.impl.Builder;
import com.vanatka.tm.impl.baseactions.BaseAction;
import com.vanatka.tm.impl.baseactions.UIBaseAction;
import com.vanatka.tm.interfaces.IInputInterface;
import com.vanatka.tm.interfaces.ITaskEntry;
import com.vanatka.tm.interfaces.ITaskManager;
import com.vanatka.tm.interfaces.ITasksStorageProvider;

public class LoadAction extends UIBaseAction {

    private final static String TAG = "LoadAction";

    private final int storedId;
    private final ITasksStorageProvider storageProvider;
    private final IInputInterface inputInterface;
    private final ITaskManager taskManager;

    public LoadAction(IInputInterface inputInterface, int storedId, ITasksStorageProvider storageProvider,
                      ITaskManager taskManager) {
        super( inputInterface );
        this.storedId = storedId;
        this.storageProvider = storageProvider;
        this.inputInterface = inputInterface;
        this.taskManager = taskManager;
    }

    @Override
    public boolean execute( ) {
        if (null != taskManager && null != storageProvider && null != inputInterface) {
            BaseAction builtTask = null;
            try {
                ITaskEntry taskById = storageProvider.getTaskById( storedId );
                Builder builder = new Builder( taskById.getUUID(), taskById.getActorName(), taskById.getStringRepresentation() );
                builtTask = builder.build( inputInterface );
                taskManager.runTask( builtTask );
            } catch (Exception e) {
                Log.e( TAG, "exception, taskId: " + storedId, e );
            }
        }
        return true;
    }
}
