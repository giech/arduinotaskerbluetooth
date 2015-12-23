/*
 * Author: Ilias Giechaskiel
 * Website: https://ilias.giechaskiel.com
 * Description: Class responsible for creating and validating bundles.
 *
 */


package com.giechaskiel.ilias.bluetoothserialfromtasker;

import android.os.Bundle;
import android.util.Log;

import java.util.regex.Pattern;


public class BundleManager {
    // For logging
    private final static String TAG = "BundleManager";

    // Keys for bundle
    public final static String BUNDLE_STRING_MAC = "com.giechaskiel.ilias.bluetoothserialfromtasker.STRING_MAC";
    public final static String BUNDLE_STRING_MSG = "com.giechaskiel.ilias.bluetoothserialfromtasker.STRING_MSG";
    public final static String BUNDLE_BOOL_CRLF = "com.giechaskiel.ilias.bluetoothserialfromtasker.BOOL_CRLF";


    // only accept valid MAC addresses of form 00:11:22:AA:BB:CC, where colons can be dashes
    private static boolean isMacValid(String mac) {
        if (mac == null) {
            return false;
        }

        return Pattern.matches("([0-9a-fA-F]{2}[:-]){5}[0-9a-fA-F]{2}", mac);
    }

    // Bundle is valid if all entries exist, and are non-null. Mac address must be valid, and
    // message must be non-empty (possibly including the CRLF)
    public static boolean isBundleValid(final Bundle bundle) {
        if (bundle == null) {
            Log.w(TAG, "Null bundle");
            return false;
        }

        String mac = bundle.getString(BUNDLE_STRING_MAC, null);
        if (!isMacValid(mac)) {
            Log.w(TAG, "Invalid MAC");
            return false;
        }

        String msg = bundle.getString(BUNDLE_STRING_MSG, null);

        if (msg == null) {
            Log.w(TAG, "Null message");
            return false;
        }

        if (!bundle.containsKey(BUNDLE_BOOL_CRLF)) {
            Log.w(TAG, "Bundle missing CRLF boolean");
            return false;
        }

        boolean crlf = bundle.getBoolean(BUNDLE_BOOL_CRLF);

        boolean valid = crlf || !msg.isEmpty();
        if (!valid) {
            Log.w(TAG, "Empty message and no CRLF");
        }

        return valid;
    }

    // Method to create bundle from the individual values
    public static Bundle generateBundle(final String mac, final String msg, boolean crlf) {
        final Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_STRING_MAC, mac);
        bundle.putString(BUNDLE_STRING_MSG, msg);
        bundle.putBoolean(BUNDLE_BOOL_CRLF, crlf);

        if (!isBundleValid(bundle)) {
            return null;
        } else {
            return bundle;
        }
    }

    // Method for getting short String description of bundle
    public static String getBundleBlurb(final Bundle bundle) {
        if (!isBundleValid(bundle)) {
            return null;
        }

        final String mac = bundle.getString(BUNDLE_STRING_MAC);
        final String msg = bundle.getString(BUNDLE_STRING_MSG);
        final boolean crlf = bundle.getBoolean(BUNDLE_BOOL_CRLF);


        final int max_len = 60;
        final int crlf_len = crlf ? 4 : 0;
        final String ellipses = "...";

        StringBuilder builder = new StringBuilder();
        builder.append(mac);
        builder.append(" <- ");
        builder.append(msg);

        int length = builder.length() + crlf_len;

        if (length > max_len) {
            builder.delete(max_len - crlf_len - ellipses.length(), length);
            builder.append(ellipses);
        }

        if (crlf) {
            builder.append("\\r\\n");
        }

        return builder.toString();
    }

    // Method to get MAC address of bundle
    public static String getMac(final Bundle bundle) {
        return bundle.getString(BUNDLE_STRING_MAC);
    }

    // Method to get message part of bundle
    public static String getMsg(final Bundle bundle) {
        return bundle.getString(BUNDLE_STRING_MSG);
    }

    // Method to get CRLF part of bundle
    public static boolean getCrlf(final Bundle bundle) {
        return bundle.getBoolean(BUNDLE_BOOL_CRLF);
    }

}
