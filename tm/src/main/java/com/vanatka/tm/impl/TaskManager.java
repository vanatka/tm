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

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.vanatka.tm.impl.taskactions.LoadAction;
import com.vanatka.tm.impl.taskactions.RemoveAction;
import com.vanatka.tm.impl.taskactions.SaveAction;
import com.vanatka.tm.impl.taskactions.TickAction;
import com.vanatka.tm.interfaces.IInputInterface;
import com.vanatka.tm.interfaces.ITask;
import com.vanatka.tm.interfaces.ITaskEntry;
import com.vanatka.tm.interfaces.ITaskManager;
import com.vanatka.tm.interfaces.ITasksStorageProvider;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class TaskManager implements ITaskManager {
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors() * 3;
    private static final int KEEP_ALIVE_TIME = 1;
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.SECONDS;
    private static final String TAG = "TaskManager";

    private final ITasksStorageProvider storageProvider;
    private final IInputInterface inputInterface;

    private final BlockingQueue<Runnable> taskWorkQueue;
    private final ThreadPoolExecutor tpe;

    private HashMap<Integer, ITask> currentTasks = new HashMap<Integer, ITask>( );

    public final TickAction tickAction;

    /** current dirty hack, because of realm doesn't support multithread access */
    private final Handler mainThreadHandler = new Handler( Looper.getMainLooper() );

    public TaskManager(IInputInterface inputInterface, ITasksStorageProvider storageProvider) {
        this.inputInterface = inputInterface;
        this.storageProvider = storageProvider;
        taskWorkQueue = new LinkedBlockingQueue<Runnable>();
        tickAction = new TickAction( inputInterface, this, storageProvider );
        tpe = new ThreadPoolExecutor(
            NUMBER_OF_CORES,
            NUMBER_OF_CORES,
            KEEP_ALIVE_TIME,
            KEEP_ALIVE_TIME_UNIT,
            taskWorkQueue);
    }


    @Override
    public void runTask(ITask task) {
        if( null == task ) {
            return;
        }

        putTaskForTracking( task );

        if( task.needUIThread() ) {
            runTaskUIThread( task );
        } else {
            tpe.execute( new TMTask( task, this ) );
        }
    }

    @Override
    public void runTaskUIThread(ITask task) {
        mainThreadHandler.post( new TMTask( task, this ) );
    }

    @Override
    public void saveTask(ITask task) {
        runTask( new SaveAction( inputInterface, task, this ) );
    }

    @Override
    public void runTaskById(int id) {
        runTask( new LoadAction( inputInterface, id, storageProvider, this ) );
    }

    @Override
    public void removeTask(int taskId) {
        runTask( new RemoveAction( inputInterface, taskId, this ) );
    }

    @Override
    public void tick( ) {
        runTask( tickAction );
    }

    @Override
    public ITask getTaskById(int taskId) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        ITask result = null;
        // FIXME: 28/04/2016
        synchronized ( trackingSync ) {
            if (currentTasks.containsKey( taskId )) {
                result = currentTasks.get( taskId );
            } else {
                ITaskEntry taskById = storageProvider.getTaskById( taskId );
                Builder builder = new Builder( taskById.getId(), taskById.getActorName(), taskById.getStringRepresentation() );
                result = builder.build( inputInterface );
            }
        }

        return result;
    }

    @Override
    public List<Integer> getTasksIds( ) {
        List<Integer> result = new ArrayList<Integer>( );
        try {
            ITasksStorageProvider storageProvider = getStorageProvider();
            List<Integer> allTasks = storageProvider.getTasksIds();

            if(null != allTasks) {
                for(Integer taskId : allTasks) {
                    result.add( taskId );
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "getTasks", e);
        }
        return result;
    }

    @Override
    public ITasksStorageProvider getStorageProvider( ) {
        return storageProvider;
    }

    @Override
    public IInputInterface getInputInterface( ) {
        return inputInterface;
    }

    private final Object trackingSync = new Object();

    @Override
    public void putTaskForTracking(ITask task) {
        if(null == task || !task.hasUniqueId()) {
            return;
        }
        synchronized ( trackingSync ) {
            currentTasks.put( task.getId(), task );
        }
    }

    @Override
    public void removeTaskFromTracking(ITask task) {
        if(null == task || !task.hasUniqueId()) {
            return;
        }

        synchronized ( trackingSync ) {
            currentTasks.remove( task.getId() );
        }
    }
}
