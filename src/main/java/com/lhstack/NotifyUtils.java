package com.lhstack;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.project.Project;

public class NotifyUtils {

    public static void notify(String content, Project project){
        Notifications.Bus.notify(new Notification("证书管理","证书管理",content, NotificationType.INFORMATION),project);
    }
}
