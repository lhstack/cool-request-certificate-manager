package com.lhstack.components;

import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.editor.event.EditorMouseEvent;
import com.intellij.openapi.editor.impl.ContextMenuPopupHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DefaultContextMenuPopupHandler extends ContextMenuPopupHandler {


    private final DefaultActionGroup group;

    public DefaultContextMenuPopupHandler(AnAction ...actions) {
        this.group = new DefaultActionGroup(actions);
    }

    @Override
    public @Nullable ActionGroup getActionGroup(@NotNull EditorMouseEvent event) {
        return this.group;
    }
}
