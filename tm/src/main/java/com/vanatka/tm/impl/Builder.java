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

import com.vanatka.tm.impl.baseactions.BaseAction;
import com.vanatka.tm.interfaces.IInputInterface;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

public class Builder {
    private final int id;
    private final String actorName;
    private final String stringRepresentation;

    public Builder(int id, String actorName, String stringRepresentation) {
        this.id = id;
        this.actorName = actorName;
        this.stringRepresentation = stringRepresentation;
    }

    public BaseAction build(IInputInterface inputInterface ) throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Class[] type = { IInputInterface.class };
        Class classDefinition = Class.forName(actorName);
        Constructor cons = classDefinition .getConstructor( type );
        Object[] obj = { inputInterface };
        BaseAction baseAction = (BaseAction) cons.newInstance( obj );
        baseAction.setId( id );
        baseAction.setArguments( stringRepresentation );
        return baseAction;
    }
}
