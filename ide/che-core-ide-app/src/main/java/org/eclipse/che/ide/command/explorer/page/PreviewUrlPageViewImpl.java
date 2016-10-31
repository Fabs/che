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

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * //
 *
 * @author Artem Zatsarynnyi
 */
@Singleton
public class PreviewUrlPageViewImpl extends Composite implements PreviewUrlPageView {

    private static final PreviewUrlPageViewImplUiBinder UI_BINDER = GWT.create(PreviewUrlPageViewImplUiBinder.class);

    @UiField
    TextArea editorPanel;

    private ActionDelegate delegate;

    @Inject
    public PreviewUrlPageViewImpl() {
        initWidget(UI_BINDER.createAndBindUi(this));
    }

    @Override
    public void setDelegate(ActionDelegate delegate) {
        this.delegate = delegate;
    }

    @Override
    public void setUrl(String previewUrl) {
        editorPanel.setValue(previewUrl);
    }

    interface PreviewUrlPageViewImplUiBinder extends UiBinder<Widget, PreviewUrlPageViewImpl> {
    }
}
