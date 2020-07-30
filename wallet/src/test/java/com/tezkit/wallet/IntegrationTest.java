package com.tezkit.wallet;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.testfx.api.FxToolkit;
import org.testfx.framework.junit5.ApplicationExtension;

@ExtendWith(ApplicationExtension.class)
public class IntegrationTest {

    protected AnnotationConfigApplicationContext context;

    @BeforeEach
    void setup() throws Exception {
        FxToolkit.registerPrimaryStage();
        var app = FxToolkit.setupApplication(Main.class);
        context = ((Main) app).getContext();
    }

}
