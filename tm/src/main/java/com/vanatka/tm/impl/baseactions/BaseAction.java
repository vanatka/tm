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

package com.vanatka.tm.impl.baseactions;

import com.vanatka.tm.impl.models.TaskChangeMessage;
import com.vanatka.tm.interfaces.IInputInterface;
import com.vanatka.tm.interfaces.ITask;

import java.util.Observable;

public abstract class BaseAction extends Observable implements ITask {

    private int             UUID = -1;
    private int             id;
    private IInputInterface inputInterface;
    protected String        arguments = "";
    private int             state = State.STATE_IDLE;

    public BaseAction(IInputInterface inputInterface) {
        this.inputInterface = inputInterface;
        if( hasUniqueId() ) {
            this.inputInterface.getStateTracker().putTaskForTracking( this );
        }
    }

    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public void finishedOk( ) {
        state = State.STATE_IDLE;
        setChanged();
        notifyObservers(new TaskChangeMessage( getId(), State.STATE_IDLE, true));
    }

    @Override
    public void finishedError( ) {
        state = State.STATE_IDLE;
        setChanged();
        notifyObservers( new TaskChangeMessage( getId(), State.STATE_IDLE, false ) );
    }

    @Override
    public boolean needUIThread( ) {
        return false;
    }

    @Override
    public void started( ) {
        if( hasUniqueId() ) {
            this.inputInterface.getStateTracker().putTaskForTracking( this );
        }
        state = State.STATE_RUNNING;
        setChanged();
        notifyObservers(new TaskChangeMessage( getId(), State.STATE_RUNNING, true));
    }

    @Override
    public int getUUID( ) {
        return UUID != -1 ? UUID : hashCode();
    }

    @Override
    public String actorName( ) {
        return getClassName();
    }

    @Override
    public boolean retryIfFailed( ) {
        return false;
    }

    @Override
    public String serializeArguments( ) {
        return null != arguments ? arguments : "";
    }

    public void setUUID(int UUID) {
        this.UUID = UUID;
    }

    @Override
    public int getId( ) {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean removeInCaseOfSuccess( ) {
        return true;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    public String getArguments() {
        return arguments;
    }

    @Override
    public boolean hasUniqueId( ) {
        return false;
    }

    public int getState() {
        return state;
    }

    public IInputInterface getInputInterface( ) {
        return inputInterface;
    }
}
