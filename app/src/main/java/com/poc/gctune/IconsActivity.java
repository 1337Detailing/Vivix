package com.poc.gctune;

/**
 * Standalone “Icons” page, opened from the Customization hub.
 * Inherits all page logic from Main; sectionPage() selects the built page.
 */
public class IconsActivity extends Main {
    @Override
    protected int sectionPage() {
        return PAGE_ICONS;
    }
}
