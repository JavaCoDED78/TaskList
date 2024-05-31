package com.javaded.service;

import com.javaded.domain.mail.MailType;
import com.javaded.domain.user.User;

import java.util.Properties;

public interface MailService {

    void sendEmail(
            User user,
            MailType type,
            Properties params
    );
}
