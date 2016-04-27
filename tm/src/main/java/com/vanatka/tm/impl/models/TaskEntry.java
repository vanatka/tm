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

package com.vanatka.tm.impl.models;

import com.vanatka.tm.interfaces.ITask;
import com.vanatka.tm.interfaces.ITaskEntry;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TaskEntry extends RealmObject implements ITaskEntry {

    @PrimaryKey
    private int     UUID;
    private int     id;
    private boolean hasUniqueId;
    private String  stringRepresentation;
    private String  actorName;

    public TaskEntry() {}

    public TaskEntry(ITask action, int uuid) {
        this.actorName = action.actorName();
        this.UUID = uuid;
        this.id = action.getId();
        this.hasUniqueId = action.hasUniqueId();
        this.stringRepresentation = action.serializeArguments();
    }

    @Override
    public String getStringRepresentation( ) {
        return stringRepresentation;
    }

    @Override
    public int getUUID( ) {
        return UUID;
    }

    @Override
    public boolean getHasUniqueId( ) {
        return hasUniqueId;
    }

    @Override
    public String getActorName( ) {
        return actorName;
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    public void setStringRepresentation(String stringRepresentation) {
        this.stringRepresentation = stringRepresentation;
    }

    public void setActorName(String actorName) {
        this.actorName = actorName;
    }

    public int getId( ) {
        return id;
    }

    public void setId(int uniqueId) {
        this.id = uniqueId;
    }

    public void setHasUniqueId(boolean hasUniqueId) {
        this.hasUniqueId = hasUniqueId;
    }
}
