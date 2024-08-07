package com.lhstack.state;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jetbrains.annotations.NotNull;

@State(name = "data",storages = {@Storage("CoolRequestCertificateManager.xml")})
@Service
public final class ProjectState implements PersistentStateComponent<ProjectState.State> {

    private @NotNull ProjectState.State state = new State();

    public static ProjectState getInstance() {
        return ApplicationManager.getApplication().getService(ProjectState.class);
    }

    @Override
    public @NotNull ProjectState.State getState() {
        return state;
    }

    @Override
    public void loadState(@NotNull State state) {
        this.state = state;
    }

    public static class State {

        private String configYaml;

        public String getConfigYaml() {
            return configYaml;
        }

        public void setConfigYaml(String configYaml) {
            this.configYaml = configYaml;
        }
    }
}
