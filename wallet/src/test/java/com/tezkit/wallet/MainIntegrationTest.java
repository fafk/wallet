package com.tezkit.wallet;

import org.junit.jupiter.api.Test;
import org.testfx.api.FxRobot;

class MainIntegrationTest extends IntegrationTest {

    @Test
    void should_contain_button(FxRobot robot) {
        robot.clickOn(".button");
//        FxAssert.verifyThat(button, LabeledMatchers.hasText("click me!"));
        // expect:
//        assertThat(lookup(".button").queryButton()).hasText("click me!");
//        assertThat(button).hasText("click me!");
//        verifyThat(".button", hasText("click me!"), informedErrorMessage(this));
    }

    @Test
    void should_click_on_button2(FxRobot fxRobot) {
        fxRobot.clickOn(".button");
        // when:
//        clickOn(".button");

        // then:
//        assertThat(lookup(".button").queryButton()).hasText("clicked!");
//        assertThat(button).hasText("clicked!");
//        verifyThat(".button", hasText("clicked!"), informedErrorMessage(this));
    }
}
