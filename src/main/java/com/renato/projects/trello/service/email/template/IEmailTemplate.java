package com.renato.projects.trello.service.email.template;

import com.renato.projects.trello.service.email.EmailData;

public interface IEmailTemplate<T> {
    EmailData build(T context);
}
