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

import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.promises.client.Operation;
import org.eclipse.che.api.promises.client.OperationException;
import org.eclipse.che.api.promises.client.PromiseError;
import org.eclipse.che.commons.annotation.Nullable;
import org.eclipse.che.ide.DelayedTask;
import org.eclipse.che.ide.api.command.CommandManager3;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.command.CommandTypeRegistry;
import org.eclipse.che.ide.api.command.ContextualCommand;
import org.eclipse.che.ide.api.command.ContextualCommand.ApplicableContext;
import org.eclipse.che.ide.api.machine.events.WsAgentStateEvent;
import org.eclipse.che.ide.api.machine.events.WsAgentStateHandler;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.parts.WorkspaceAgent;
import org.eclipse.che.ide.api.parts.base.BasePresenter;
import org.vectomatic.dom.svg.ui.SVGResource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.che.ide.api.notification.StatusNotification.DisplayMode.EMERGE_MODE;
import static org.eclipse.che.ide.api.notification.StatusNotification.Status.FAIL;
import static org.eclipse.che.ide.api.parts.PartStackType.NAVIGATION;

/**
 * Presenter for Commands Explorer.
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class CommandsExplorerPresenter extends BasePresenter implements CommandsExplorerView.ActionDelegate,
                                                                        WsAgentStateHandler,
                                                                        CommandManager3.CommandChangedListener {

    private static final String TITLE   = "Commands";
    private static final String TOOLTIP = "Manage commands";

    private final CommandsExplorerView      view;
    private final CommandsExplorerResources resources;
    private final WorkspaceAgent            workspaceAgent;
    private final CommandManager3           commandManager;
    private final CommandTypeRegistry       commandTypeRegistry;
    private final NotificationManager       notificationManager;

    /** {@link DelayedTask} for refreshing the view. */
    private DelayedTask refreshViewTask = new DelayedTask() {
        @Override
        public void onExecute() {
            refreshView();
        }
    };

    @Inject
    public CommandsExplorerPresenter(CommandsExplorerView view,
                                     CommandsExplorerResources commandsExplorerResources,
                                     WorkspaceAgent workspaceAgent,
                                     EventBus eventBus,
                                     CommandManager3 commandManager,
                                     CommandTypeRegistry commandTypeRegistry,
                                     NotificationManager notificationManager) {
        this.view = view;
        resources = commandsExplorerResources;
        this.workspaceAgent = workspaceAgent;
        this.commandManager = commandManager;
        this.commandTypeRegistry = commandTypeRegistry;
        this.notificationManager = notificationManager;

        view.setDelegate(this);

        eventBus.addHandler(WsAgentStateEvent.TYPE, this);

        commandManager.addCommandChangedListener(this);
    }

    @Override
    public void go(AcceptsOneWidget container) {
        Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
            @Override
            public void execute() {
                refreshView();
            }
        });

        container.setWidget(getView());
    }

    @Override
    public String getTitle() {
        return TITLE;
    }

    @Override
    public IsWidget getView() {
        return view;
    }

    @Nullable
    @Override
    public String getTitleToolTip() {
        return TOOLTIP;
    }

    @Nullable
    @Override
    public SVGResource getTitleImage() {
        return resources.explorerPart();
    }

    @Override
    public void onCommandTypeSelected(CommandType commandType) {
    }

    @Override
    public void onCommandSelected(ContextualCommand command) {
    }

    @Override
    public void onCommandAdd() {
        // by default, command should be applicable to the workspace only
        final ApplicableContext defaultApplicableContext = new ApplicableContext();
        defaultApplicableContext.setWorkspaceApplicable(true);

        final CommandType selectedCommandType = view.getSelectedCommandType();
        if (selectedCommandType != null) {
            commandManager.createCommand(selectedCommandType.getId(), defaultApplicableContext).then(new Operation<ContextualCommand>() {
                @Override
                public void apply(ContextualCommand arg) throws OperationException {
                    view.selectCommand(arg);
                }
            }).catchError(new Operation<PromiseError>() {
                @Override
                public void apply(PromiseError arg) throws OperationException {
                    notificationManager.notify("Unable to create command", arg.getMessage(), FAIL, EMERGE_MODE);
                }
            });
        }
    }

    @Override
    public void onCommandDuplicate(final ContextualCommand command) {
        commandManager.createCommand(command).then(new Operation<ContextualCommand>() {
            @Override
            public void apply(ContextualCommand arg) throws OperationException {
                view.selectCommand(arg);
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                notificationManager.notify("Unable to duplicate command", arg.getMessage(), FAIL, EMERGE_MODE);
            }
        });
    }

    @Override
    public void onCommandRemove(final ContextualCommand command) {
        commandManager.removeCommand(command.getName()).then(new Operation<Void>() {
            @Override
            public void apply(Void arg) throws OperationException {
                // TODO: select another command
            }
        }).catchError(new Operation<PromiseError>() {
            @Override
            public void apply(PromiseError arg) throws OperationException {
                notificationManager.notify("Unable to remove command", arg.getMessage(), FAIL, EMERGE_MODE);
            }
        });
    }

    @Override
    public void onWsAgentStarted(WsAgentStateEvent event) {
        // TODO: chose a better time to open Commands Explorer
        workspaceAgent.openPart(this, NAVIGATION);
        workspaceAgent.setActivePart(this);
    }

    @Override
    public void onWsAgentStopped(WsAgentStateEvent event) {
    }

    @Override
    public void onCommandAdded(ContextualCommand command) {
        refreshViewTask.delay(300);
    }

    @Override
    public void onCommandUpdated(ContextualCommand command) {
        refreshViewTask.delay(300);
    }

    @Override
    public void onCommandRemoved(ContextualCommand command) {
        refreshViewTask.delay(300);
    }

    private void refreshView() {
        final Map<CommandType, List<ContextualCommand>> commands = new HashMap<>();

        // all registered command types need to be shown in view
        // so populate map by all registered command types
        for (CommandType commandType : commandTypeRegistry.getCommandTypes()) {
            commands.put(commandType, new ArrayList<ContextualCommand>());
        }

        for (ContextualCommand command : commandManager.getCommands()) {
            final CommandType commandType = commandTypeRegistry.getCommandTypeById(command.getType());

            List<ContextualCommand> commandsByType = commands.get(commandType);
            if (commandsByType == null) {
                commandsByType = new ArrayList<>();
                commands.put(commandType, commandsByType);
            }

            commandsByType.add(command);
        }

        view.setCommands(commands);
    }
}
