/*
 * Copyright (c) 2014 IBM Corp.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *    IBM Corp - initial API and implementation and initial documentation
 */
package com.example;


/**
 * A container for the entry point to the "Repository Search" example project.
 */
public final class ExampleSearchMain
{

    /**
     * Constructs a new {@link ExampleSearchMain}.
     */
    private ExampleSearchMain()
    {
    }

    /**
     * Main entry point.
     * 
     */
    public static void main(final String[] args)
    {
        final ExampleSearcher exampleSearcher = new ExampleSearcher();
        exampleSearcher.runSearch();
        return;
    }
}
