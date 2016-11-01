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
package org.eclipse.che.ide.command.explorer;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.ImplementedBy;

import org.eclipse.che.ide.api.command.CommandImpl;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.mvp.View;
import org.eclipse.che.ide.api.parts.base.BaseActionDelegate;
import org.eclipse.che.ide.api.resources.Project;

import java.util.List;
import java.util.Map;

/**
 * The view of Commands Explorer.
 *
 * @author Artem Zatsarynnyi
 */
@ImplementedBy(CommandsExplorerViewImpl.class)
public interface CommandsExplorerView extends View<CommandsExplorerView.ActionDelegate> {

    /**
     * Adds page for editing command. The pages will be shown in order of adding.
     *
     * @param page
     *         page to add
     * @param title
     *         text that should be used as page's title
     * @param tooltip
     *         text that should be used as page's tooltip
     */
    void addPage(IsWidget page, String title, String tooltip);

    /**
     * Sets the commands to show in the view.
     *
     * @param workspaceCommands
     *         workspace commands grouped by type
     * @param projectsCommands
     *         workspace commands grouped by project and type
     */
    void setCommands(Map<CommandType, List<CommandImpl>> workspaceCommands,
                     Map<Project, Map<CommandType, List<CommandImpl>>> projectsCommands);

    /** Returns the currently selected command. */
    CommandImpl getSelectedCommand();

    /** The action delegate for this view. */
    interface ActionDelegate extends BaseActionDelegate {

        /**
         * Called when some command has been selected.
         *
         * @param command
         *         selected command
         */
        void onCommandSelected(CommandImpl command);

        /**
         * Called when saving command is requested.
         *
         * @param command
         *         command saving of which is requested
         */
        void onCommandSave(CommandImpl command);

        /** Called when adding new command is requested. */
        void onCommandAdd();

        /**
         * Called when removing command is requested.
         *
         * @param command
         *         command removing of which is requested
         */
        void onCommandRemove(CommandImpl command);
    }
}
