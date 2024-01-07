/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.testing.espresso.BasicSample;

import android.app.Activity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoSession;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static com.android.dx.mockito.inline.extended.ExtendedMockito.mockitoSession;
import static com.android.dx.mockito.inline.extended.ExtendedMockito.verify;

import static org.mockito.ArgumentMatchers.startsWith;

import com.android.dx.mockito.inline.extended.StaticMockitoSession;
import com.example.android.testing.espresso.BasicSample.util.NetworkUtil;

import java.lang.reflect.Field;


/**
 * Basic tests showcasing simple view matchers and actions like {@link ViewMatchers#withId},
 * {@link ViewActions#click} and {@link ViewActions#typeText}.
 * <p>
 * Note that there is no need to tell Espresso that a view is in a different {@link Activity}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ChangeTextBehaviorTest {

    public static final String STRING_TO_BE_TYPED = "Espresso";

    /**
     * Use {@link ActivityScenarioRule} to create and launch the activity under test, and close it
     * after test completes. This is a replacement for {@link androidx.test.rule.ActivityTestRule}.
     */
    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    /**
     * @param variableName - name of drawable, e.g R.drawable.<b>image</b>
     * @param с            - class of resource, e.g R.drawable.class or R.raw.class
     * @return integer id of resource
     * @author Lonkly
     */
    public static int getResId(String variableName, Class<?> с) {
        Field field = null;
        int resId = 0;
        try {
            field = с.getField(variableName);
            try {
                resId = field.getInt(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resId;
    }

    @Test
    public void changeText_sameActivity() {
        // Type text and then press the button.

        onView(withId(getResId("editTextUserInput", R.id.class))).perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());
        onView(withId(R.id.changeTextBt)).perform(click());

        // Check that the text was changed.
        onView(withId(R.id.textToBeChanged)).check(matches(withText(STRING_TO_BE_TYPED)));
    }

    @Test
    public void changeText_newActivity() {
        MockitoSession session = mockitoSession().spyStatic(NetworkUtil.class).startMocking();
        try {
            // Type text and then press the button.
            onView(withId(R.id.editTextUserInput)).perform(typeText(STRING_TO_BE_TYPED), closeSoftKeyboard());
            onView(withId(R.id.activityChangeTextBtn)).perform(click());

            // This view is in a different Activity, no need to tell Espresso.
            onView(withId(R.id.show_text_view)).check(matches(withText(STRING_TO_BE_TYPED)));
            verify(() -> NetworkUtil.post(startsWith("body")));
        } finally {
            session.finishMocking();
        }
    }
}