package com.poc.gctune;

/**
 * Standalone “Fonts” page, opened from the Customization hub.
 * Inherits all page logic from Main; sectionPage() selects the built page.
 */
public class FontActivity extends Main {
    @Override
    protected int sectionPage() {
        return PAGE_FONT;
    }
}
