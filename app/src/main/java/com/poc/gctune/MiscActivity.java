package com.poc.gctune;

/**
 * Standalone “Miscellaneous” page, opened from the Customization hub.
 * Inherits all page logic from Main; sectionPage() selects the built page.
 */
public class MiscActivity extends Main {
    @Override
    protected int sectionPage() {
        return PAGE_MISC;
    }
}
