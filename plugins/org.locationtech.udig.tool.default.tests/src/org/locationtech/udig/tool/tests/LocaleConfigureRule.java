/* uDig - User Friendly Desktop Internet GIS client
 * https://locationtech.org/projects/technology.udig
 * (C) 2018, Eclipse Foundation
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * (http://www.eclipse.org/legal/epl-v10.html), and the Eclipse Distribution
 * License v1.0 (http://www.eclipse.org/org/documents/edl-v10.html).
 */
package org.locationtech.udig.tool.tests;

import java.util.Locale;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Creates a JUnit rule that sets the default system Locale
 * 
 * @author Nikolaos Pringouris <nprigour@gmail.com>
 *
 */
public final class LocaleConfigureRule implements TestRule {

    //will hold the preferred Locale 
    final Locale preference;

    /**
     * constructor that restores the default locale for each test.
     */
    public LocaleConfigureRule() {
        preference = null;
    }

    /**
     * constructor that set the preferred locale for each test.
     * 
     * @param preference
     */
    public LocaleConfigureRule(final Locale preference) {
        this.preference = preference;
    }

    @Override
    public Statement apply(final Statement base, final Description description) {
        return new Statement() {
            final Locale defaultLocale = Locale.getDefault();

            @Override
            public void evaluate() throws Throwable {
                try {
                    if (preference != null) {
                        Locale.setDefault(preference);
                    }

                    base.evaluate();
                } finally {
                    //upon test completion restore the default Locale
                    Locale.setDefault(defaultLocale);
                }
            }
        };
    }
}
