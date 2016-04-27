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

package com.vanatka.tm.interfaces;

public interface ITask {

    interface State {
        int STATE_IDLE = 1, STATE_RUNNING = 2;
    }

    boolean     retryIfFailed( );
    boolean     execute( );
    boolean     removeInCaseOfSuccess( );
    boolean     needUIThread( );
    String      serializeArguments( );
    void        setArguments(String arguments);
    String      getArguments( );
    String      actorName( );
    int         getUUID( );
    int         getId( );
    int         getState( );
    void        setId(int id);
    boolean     hasUniqueId( );


    void started( );
    void finishedError( );
    void finishedOk( );

}
