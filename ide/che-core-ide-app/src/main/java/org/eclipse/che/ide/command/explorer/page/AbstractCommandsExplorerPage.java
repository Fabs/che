/*******************************************************************************
 * Copyright (c) 2012-2016 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.command.explorer.page;

import org.eclipse.che.ide.api.command.CommandImpl;

/**
 * Abstract {@link CommandsExplorerPage} that provides basic functionality.
 *
 * @author Artem Zatsarynnyi
 */
public abstract class AbstractCommandsExplorerPage implements CommandsExplorerPage {

    private final String title;
    private final String tooltip;

    protected CommandImpl editedCommand;

    private DirtyStateListener listener;

    protected AbstractCommandsExplorerPage(String title, String tooltip) {
        this.title = title;
        this.tooltip = tooltip;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getTooltip() {
        return tooltip;
    }

    @Override
    public void resetFrom(CommandImpl command) {
        editedCommand = command;
    }

    @Override
    public void setDirtyStateListener(DirtyStateListener listener) {
        this.listener = listener;
    }

    /** Should be called by page every time when any command modifications on the page have been performed. */
    protected void notifyDirtyStateChanged() {
        listener.onDirtyStateChanged();
    }
}
