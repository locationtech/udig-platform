package org.locationtech.udig.tool.tests;

import java.util.Locale;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/** JUnit rule for taking control over the Locale. */
public final class DefaultLocaleRule implements TestRule {
  final Locale preference;

  /** Creates the rule and will restore the default locale for each test. */
  public DefaultLocaleRule() {
    preference = null;
  }

  /** Creates the rule and will set the preferred locale for each test. */
  public DefaultLocaleRule(final Locale preference) {
    this.preference = preference;
  }

  @Override public Statement apply(final Statement base, final Description description) {
    return new Statement() {
      @Override public void evaluate() throws Throwable {
        final Locale defaultLocale = Locale.getDefault();

        try {
          if (preference != null) {
            Locale.setDefault(preference);
          }

          base.evaluate();
        } finally {
          Locale.setDefault(defaultLocale);
        }
      }
    };
  }
}
