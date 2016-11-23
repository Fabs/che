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
package org.eclipse.che.ide.command.node;

import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;

import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.PromiseProvider;
import org.eclipse.che.ide.api.command.CommandType;
import org.eclipse.che.ide.api.command.CommandTypeRegistry;
import org.eclipse.che.ide.api.data.tree.Node;
import org.eclipse.che.ide.api.icon.Icon;
import org.eclipse.che.ide.api.icon.IconRegistry;
import org.eclipse.che.ide.project.node.SyntheticNode;
import org.eclipse.che.ide.ui.smartTree.presentation.NodePresentation;
import org.vectomatic.dom.svg.ui.SVGImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Tree node that represents {@link CommandType}.
 *
 * @author Artem Zatsarynnyi
 */
public class CommandTypeNode extends SyntheticNode<CommandType> {

    private final List<? extends AbstractCommandNode> commands;
    private final PromiseProvider                     promiseProvider;
    private final CommandTypeRegistry                 commandTypeRegistry;
    private final IconRegistry                        iconRegistry;

    @Inject
    public CommandTypeNode(@Assisted CommandType data,
                           @Assisted List<? extends AbstractCommandNode> commands,
                           PromiseProvider promiseProvider,
                           CommandTypeRegistry commandTypeRegistry,
                           IconRegistry iconRegistry) {
        super(data, null);

        this.commands = commands;
        this.promiseProvider = promiseProvider;
        this.commandTypeRegistry = commandTypeRegistry;
        this.iconRegistry = iconRegistry;
    }

    @Override
    public void updatePresentation(NodePresentation presentation) {
        presentation.setPresentableText(getName());

        // set icon
        final String commandTypeId = getData().getId();
        final CommandType commandType = commandTypeRegistry.getCommandTypeById(commandTypeId);

        if (commandType != null) {
            final Icon icon = iconRegistry.getIconIfExist(commandTypeId + ".commands.category.icon");

            if (icon != null) {
                final SVGImage svgImage = icon.getSVGImage();

                if (svgImage != null) {
                    presentation.setPresentableIcon(icon.getSVGResource());
                }
            }
        }
    }

    @Override
    public String getName() {
        return getData().getDisplayName();
    }

    @Override
    public boolean isLeaf() {
        return false;
    }

    @Override
    protected Promise<List<Node>> getChildrenImpl() {
        List<Node> children = new ArrayList<>();
        children.addAll(commands);

        return promiseProvider.resolve(children);
    }
}
