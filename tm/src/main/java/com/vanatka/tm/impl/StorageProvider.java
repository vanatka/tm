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

import com.vanatka.tm.impl.models.TaskEntry;
import com.vanatka.tm.interfaces.ITask;
import com.vanatka.tm.interfaces.ITaskEntry;
import com.vanatka.tm.interfaces.ITasksStorageProvider;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

public class StorageProvider implements ITasksStorageProvider {

    private static final String TAG = "StorageProvider";

    private final Realm realm;

    public StorageProvider(Realm realm) {
        this.realm = realm;
    }

    @Override
    public int store(ITask task) {
        RealmQuery<TaskEntry> where = realm.where( TaskEntry.class );

        int uuid = 0;
        Number uuid1 = where.max( "UUID" );
        if( null != uuid1 ) {
            uuid = uuid1.intValue() + 1;
        }

        try {
            realm.beginTransaction();
            TaskEntry taskEntry = new TaskEntry( task, uuid );
            realm.copyToRealmOrUpdate( taskEntry );
        } catch (Exception e) {
            Log.e( TAG, "exception store ", e );
        } finally {
            realm.commitTransaction();
        }

        return uuid;
    }

    @Override
    public ITaskEntry getTaskById(int id ) {
        ITaskEntry result = null;
        RealmResults<TaskEntry> all = realm.where( TaskEntry.class ).equalTo( "id", id ).findAll();
        if( null != all && all.size() > 0 ) {
            result = (null != all ? all.first() : null);
        }

        return result;
    }

    @Override
    public ITaskEntry getTaskByUUID(String uuid) {
        ITaskEntry result;
        RealmResults<TaskEntry> all = realm.where( TaskEntry.class ).equalTo( "UUID", Integer.valueOf(uuid) ).findAll();
        result = ( null != all ? all.first() : null);

        return result;
    }

    @Override
    public ITaskEntry getFirstTaskEntryByActor(String actorName) {
        return null;
    }

    @Override
    public List<ITaskEntry> getTaskEntriesByActor(String actorName) {
        return null;
    }

    @Override
    public List<ITaskEntry> getAllTasks( ) {
        List<ITaskEntry> result = new ArrayList<ITaskEntry>( );
        RealmResults<TaskEntry> taskEntries = realm.allObjects( TaskEntry.class );
        if( null != taskEntries ) {
            for( TaskEntry entry : taskEntries ) {
                result.add( entry );
            }
        }
        return result;
    }

    @Override
    public List<Integer> getTasksIds( ) {
        RealmResults<TaskEntry> all = realm.where( TaskEntry.class ).findAll();
        List<Integer> result = new ArrayList<Integer>( );
        if( null != all ) {
            for( TaskEntry taskEntry : all ) {
                result.add( taskEntry.getUUID() );
            }
        }

        return result;
    }

    @Override
    public void removeTask(int taskId) {
        RealmResults<TaskEntry> entries = realm.where( TaskEntry.class ).equalTo( "id", taskId ).findAll();
        if( null != entries && entries.size() > 0 ) {
            List<TaskEntry> taskEntries = new ArrayList<TaskEntry>( );
            for (TaskEntry te : entries) {
                taskEntries.add( te );
            }
            realm.beginTransaction();
            try {
                for(TaskEntry te : taskEntries) {
                    te.deleteFromRealm();
                }
            } catch (Exception e) {
                Log.e( TAG, "", e );
            } finally {
                realm.commitTransaction();
            }
        }
    }

    @Override
    public void removeTaskByActor(String actorName) {
        realm.beginTransaction();
        try {
            RealmResults<TaskEntry> entries = realm.where( TaskEntry.class ).equalTo( "actorName", actorName ).findAll();
            if( null != entries ) {
                for(TaskEntry taskEntry : entries) {
                    if( null != taskEntry ) {
                        taskEntry.deleteFromRealm();
                    }
                }
            }
            realm.delete( TaskEntry.class );
        } catch (Exception e) {
            Log.e( TAG, "", e );
        } finally {
            realm.commitTransaction();
        }
    }
}
