package com.poc.gctune;

/**
 * Standalone “System Settings” page, opened from the Customization hub.
 * Inherits all page logic from Main; sectionPage() selects the built page.
 */
public class SettingsThemeActivity extends Main {
    @Override
    protected int sectionPage() {
        return PAGE_SETTINGS_THEME;
    }
}
