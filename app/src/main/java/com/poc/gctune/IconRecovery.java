package com.poc.gctune;

/**
 * Small shell payloads for the already-authorized Vivo Game Cube uid1000
 * channel. These operations never touch launcher app data or /system.
 *
 * Backups live outside Vivix's app-private storage, under the existing
 * theme directory, and survive app reinstalls. A snapshot captures the CURRENT
 * state, which may already be customized: never call it a factory backup.
 */
final class IconRecovery {
    private IconRecovery() {}

    static final String ROOT = "/data/bbkcore/theme";
    static final String RECOVERY = ROOT + "/.vivix_recovery";

    /**
     * Fail closed. Copy BOTH icon directories before any install clears them.
     * A snapshot is valid only when a complete marker is present.
     */
    static String snapshot() {
        StringBuilder s = new StringBuilder();
        s.append("TD=").append(ROOT).append(";RB=").append(RECOVERY).append(";");
        s.append("mkdir -p \"$RB\" || { echo BACKUP_FAILED=STORE; exit 1; };");
        s.append("chmod 700 \"$RB\" 2>/dev/null;");
        s.append("SNAP_ID=snapshot-$(date +%Y%m%d-%H%M%S)-$$;");
        s.append("SNAP_TMP=$RB/.pending-$$;SNAP=$RB/$SNAP_ID;");
        s.append("mkdir \"$SNAP_TMP\" || { echo BACKUP_FAILED=STAGE; exit 1; };");
        s.append("for n in icons launcher; do ");
        s.append("if [ -d \"$TD/$n\" ]; then ");
        s.append("cp -a \"$TD/$n\" \"$SNAP_TMP/$n\" || { echo BACKUP_FAILED=COPY_$n; rm -rf \"$SNAP_TMP\"; exit 1; };");
        s.append("a=$(find \"$TD/$n\" -type f 2>/dev/null | wc -l);");
        s.append("b=$(find \"$SNAP_TMP/$n\" -type f 2>/dev/null | wc -l);");
        s.append("[ \"$a\" = \"$b\" ] || { echo BACKUP_FAILED=COUNT_$n; rm -rf \"$SNAP_TMP\"; exit 1; };");
        s.append("echo present > \"$SNAP_TMP/$n.state\";");
        s.append("else echo absent > \"$SNAP_TMP/$n.state\"; fi; done;");
        s.append("settings get system theme_icons_style_explore > \"$SNAP_TMP/style-system.txt\" 2>/dev/null;");
        s.append("settings get global theme_icons_style_explore > \"$SNAP_TMP/style-global.txt\" 2>/dev/null;");
        s.append("settings get system icon_corner_value > \"$SNAP_TMP/corners.txt\" 2>/dev/null;");
        s.append("echo current_state_not_factory > \"$SNAP_TMP/notice.txt\";");
        s.append("echo complete > \"$SNAP_TMP/.complete\";");
        s.append("mv \"$SNAP_TMP\" \"$SNAP\" || { echo BACKUP_FAILED=COMMIT; exit 1; };");
        s.append("echo BACKUP_CREATED=$SNAP_ID;");
        return s.toString();
    }

    /** Standalone manual backup, guarded against a running install. */
    static String manualBackup() {
        return guard() + snapshot() + "echo RECOVERY_BACKUP_OK;";
    }

    /** List oldest/newest snapshot IDs and whether overrides currently exist. */
    static String status() {
        return "TD=" + ROOT + ";RB=" + RECOVERY + ";"
                + "echo RECOVERY_STATUS;"
                + "for n in icons launcher; do if [ -d \"$TD/$n\" ]; then echo ACTIVE_$n=yes; else echo ACTIVE_$n=no; fi; done;"
                + "if [ -d \"$RB\" ]; then "
                + "for d in \"$RB\"/snapshot-*; do "
                + "[ -f \"$d/.complete\" ] && echo SNAPSHOT=${d##*/};"
                + "done; else echo NO_BACKUPS; fi;"
                + "echo END_RECOVERY_STATUS;";
    }

    /** Save the current state, then move overrides aside, never delete them. */
    static String disableOverrides() {
        StringBuilder s = new StringBuilder(guard());
        s.append(snapshot());
        s.append("Q=$RB/disabled-$(date +%Y%m%d-%H%M%S)-$$;");
        s.append("mkdir \"$Q\" || { echo RECOVERY_FAILED=QUARANTINE; exit 1; };");
        s.append("OK=1;");
        s.append("for n in icons launcher; do ");
        s.append("if [ -d \"$TD/$n\" ]; then mv \"$TD/$n\" \"$Q/$n\" || { OK=0; break; }; fi; done;");
        s.append("if [ \"$OK\" != 1 ]; then ");
        s.append("for n in icons launcher; do [ -d \"$Q/$n\" ] && mv \"$Q/$n\" \"$TD/$n\"; done;");
        s.append("echo RECOVERY_FAILED=DISABLE; exit 1; fi;");
        s.append("echo OVERRIDES_DISABLED=$Q;");
        s.append(restart());
        return s.toString();
    }

    /** Restore a complete snapshot, with staging and rollback on failure. */
    static String restore(boolean earliest) {
        StringBuilder s = new StringBuilder(guard());
        s.append("RB=").append(RECOVERY).append(";");
        s.append("TARGET=$(for d in \"$RB\"/snapshot-*; do [ -f \"$d/.complete\" ] && echo \"$d\"; done | sort");
        s.append(earliest ? " | head -n 1);" : " | tail -n 1);");
        s.append("[ -n \"$TARGET\" ] && [ -f \"$TARGET/.complete\" ] || { echo RECOVERY_FAILED=NO_BACKUP; exit 1; };");
        s.append("for n in icons launcher; do [ -f \"$TARGET/$n.state\" ] || { echo RECOVERY_FAILED=CORRUPT; exit 1; }; done;");
        // Make a restore-point for the *current* state BEFORE replacement.
        s.append(snapshot());
        s.append("NEXT=$RB/.incoming-$$;Q=$RB/displaced-$(date +%Y%m%d-%H%M%S)-$$;");
        s.append("mkdir \"$NEXT\" \"$Q\" || { echo RECOVERY_FAILED=STAGE; exit 1; };");
        s.append("for n in icons launcher; do ");
        s.append("if [ \"$(cat \"$TARGET/$n.state\")\" = present ]; then ");
        s.append("[ -d \"$TARGET/$n\" ] || { echo RECOVERY_FAILED=MISSING_$n; rm -rf \"$NEXT\"; exit 1; };");
        s.append("cp -a \"$TARGET/$n\" \"$NEXT/$n\" || { echo RECOVERY_FAILED=COPY_$n; rm -rf \"$NEXT\"; exit 1; }; fi; done;");
        s.append("OK=1;");
        s.append("for n in icons launcher; do ");
        s.append("if [ -d \"$TD/$n\" ]; then mv \"$TD/$n\" \"$Q/$n\" || { OK=0; break; }; fi; done;");
        s.append("if [ \"$OK\" = 1 ]; then for n in icons launcher; do ");
        s.append("if [ -d \"$NEXT/$n\" ]; then mv \"$NEXT/$n\" \"$TD/$n\" || { OK=0; break; }; fi; done; fi;");
        s.append("if [ \"$OK\" != 1 ]; then ");
        s.append("for n in icons launcher; do ");
        s.append("if [ -d \"$TD/$n\" ]; then mv \"$TD/$n\" \"$Q/failed-$n\"; fi;");
        s.append("if [ -d \"$Q/$n\" ]; then mv \"$Q/$n\" \"$TD/$n\"; fi; done;");
        s.append("echo RECOVERY_FAILED=ROLLBACK; exit 1; fi;");
        s.append("rmdir \"$NEXT\" 2>/dev/null;");
        s.append("restorecon -RF \"$TD/icons\" \"$TD/launcher\" 2>/dev/null;");
        s.append("echo RESTORED_SNAPSHOT=${TARGET##*/};");
        s.append(restart());
        return s.toString();
    }

    private static String guard() {
        return "TD=" + ROOT + ";"
                + "if [ -d \"$TD/.icons_gctune_lock\" ]; then echo RECOVERY_BUSY; exit 1; fi;";
    }

    private static String restart() {
        // Restart only the HOME process; never clear com.bbk.launcher2 data.
        return "am force-stop com.bbk.launcher2 2>/dev/null;"
                + "am start -a android.intent.action.MAIN -c android.intent.category.HOME >/dev/null 2>&1;"
                + "echo RESTART_ATTEMPTED;";
    }
}
