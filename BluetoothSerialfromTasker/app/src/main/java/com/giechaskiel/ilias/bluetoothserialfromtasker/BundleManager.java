/*
 * Author: Ilias Giechaskiel
 * Website: https://ilias.giechaskiel.com
 * Description: Class responsible for creating and validating bundles.
 *
 */


package com.giechaskiel.ilias.bluetoothserialfromtasker;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import java.util.Arrays;
import java.util.regex.Pattern;



public class BundleManager {
    // For logging
    private final static String TAG = "BundleManager";

    public final static String PACKAGE_NAME = "com.giechaskiel.ilias.bluetoothserialfromtasker";

    // Keys for bundle
    public final static String BUNDLE_STRING_MAC = PACKAGE_NAME + ".STRING_MAC";
    public final static String BUNDLE_STRING_MSG = PACKAGE_NAME + ".STRING_MSG";
    public final static String BUNDLE_BOOL_CRLF  = PACKAGE_NAME + ".BOOL_CRLF";
    public final static String BUNDLE_BOOL_HEX   = PACKAGE_NAME + ".BOOL_HEX";

    // only accept valid MAC addresses of form 00:11:22:AA:BB:CC, where colons can be dashes
    private static boolean isMacValid(String mac) {
        if (mac == null) {
            return false;
        }

        // We allow variable MACs
        if (mac.startsWith("%")) {
            return true;
        }

        return Pattern.matches("([0-9a-fA-F]{2}[:-]){5}[0-9a-fA-F]{2}", mac);
    }

    // Whether the bundle is valid. Strings must be non-null, and either variables
    // or valid format (correctly-formatted MAC, non-empty, proper hex if binary, etc.)
    public static boolean isBundleValid(final Bundle bundle) {
        if (bundle == null) {
            Log.w(TAG, "Null bundle");
            return false;
        }

        String[] keys = {BUNDLE_BOOL_CRLF, BUNDLE_BOOL_HEX, BUNDLE_STRING_MAC, BUNDLE_STRING_MSG};
        for (String key: keys) {
            if (!bundle.containsKey(key)) {
                Log.w(TAG, "Bundle missing key " + key);
            }
        }

        String mac = getMac(bundle);
        if (!isMacValid(mac)) {
            Log.w(TAG, "Invalid MAC");
            return false;
        }

        String msg = getMsg(bundle);
        if (msg == null) {
            Log.w(TAG, "Null message");
            return false;
        }

        // allow variable replacement, at the expense
        // of sanity checking hex
        if (msg.startsWith("%")) {
            return true;
        }


        boolean hex = getHex(bundle);
        boolean crlf = getCrlf(bundle);

        if (hex) {
            // If we interpret message as hex, we expect it to be non-null
            byte[] normalized = getByteArrayFromHexString(msg);
            boolean valid = normalized != null;
            if (!valid) {
                Log.w(TAG, "Message is not well-formed HEX");
            }
            return valid;
        } else {
            boolean valid = crlf || !msg.isEmpty();
            if (!valid) {
                Log.w(TAG, "Empty message and no CRLF");
            }
            return valid;
        }
    }

    // method to get error message for the given values, or null if no error exists
    public static String getErrorMessage(Context context, final String mac, final String msg, boolean crlf, boolean hex) {
        Resources res = context.getResources();
        if (!isMacValid(mac)) {
            return res.getString(R.string.invalid_mac);
        }

        if (hex) {
            if (getByteArrayFromHexString(msg) == null) {
                return res.getString(R.string.invalid_hex);
            }
        } else {
            if (msg == null || (msg.isEmpty() && !crlf)) {
                return res.getString(R.string.invalid_msg);
            }
        }

        return null;
    }

    // Method to create bundle from the individual values
    public static Bundle generateBundle(final String mac, final String msg, boolean crlf, boolean hex) {
        if (mac == null || msg == null) {
            return null;
        }

        final Bundle bundle = new Bundle();
        bundle.putString(BUNDLE_STRING_MAC, mac);
        bundle.putString(BUNDLE_STRING_MSG, msg);
        bundle.putBoolean(BUNDLE_BOOL_CRLF, crlf);
        bundle.putBoolean(BUNDLE_BOOL_HEX, hex);

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

        final String mac = getMac(bundle);
        final String msg = getMsg(bundle);
        final boolean crlf = getCrlf(bundle);
        final boolean hex = getHex(bundle);

        final String clrf_string = "\\r\\n";

        final int max_len = 60;
        final int crlf_len = crlf ? clrf_string.length() : 0;
        final String ellipses = "...";

        StringBuilder builder = new StringBuilder();
        builder.append(mac);
        builder.append(" <- ");
        if (hex) {
            builder.append("(hex) ");
        }
        builder.append(msg);

        int length = builder.length() + crlf_len;

        if (length > max_len) {
            builder.delete(max_len - crlf_len - ellipses.length(), length);
            builder.append(ellipses);
        }

        if (crlf) {
            builder.append(clrf_string);
        }

        return builder.toString();
    }

    // Method to get MAC address of bundle
    public static String getMac(final Bundle bundle) {
        return bundle.getString(BUNDLE_STRING_MAC, null);
    }

    // Method to get message part of bundle
    public static String getMsg(final Bundle bundle) {
        return bundle.getString(BUNDLE_STRING_MSG, null);
    }

    // Method to get CRLF part of bundle
    public static boolean getCrlf(final Bundle bundle) {
        return bundle.getBoolean(BUNDLE_BOOL_CRLF, true);
    }

    // Method to get whether message should be interpreted as binary hex
    public static boolean getHex(final Bundle bundle) {
        return bundle.getBoolean(BUNDLE_BOOL_HEX, false);
    }

    // method to get the message bytes for the given bundle, or null if the bundle is invalid
    public static byte[] getMsgBytes(final Bundle bundle) {
        if (!isBundleValid(bundle)) {
            return null;
        }
        String msg = getMsg(bundle);

        byte[] msg_bytes;
        if (getHex(bundle)) {
            msg_bytes = getByteArrayFromHexString(msg);
        } else {
            msg_bytes = msg.getBytes();
        }

        if (msg_bytes == null) {
            return null;
        }

        if (getCrlf(bundle)) {
            int old_length = msg_bytes.length;

            // add CRLF bytes
            byte[] crlf_bytes = "\r\n".getBytes();
            msg_bytes = Arrays.copyOf(msg_bytes, old_length + crlf_bytes.length);
            for (int i = 0; i < crlf_bytes.length; ++i) {
                msg_bytes[old_length + i] = crlf_bytes[i];
            }
        }

        return msg_bytes;
    }

    // Hex string to byte array. null if invalid

    private static byte[] getByteArrayFromHexString(String s) {
        if (s == null) {
            return null;
        }
        // remove spaces and convert to uppercase
        s = s.replaceAll("\\s+","").toUpperCase();

        final int length = s.length();

        // need even length
        if (length % 2 != 0) {
            return null;
        }

        // we want at least one character, and make sure it's hex value
        if (!s.matches("^[0-9A-F]+$")) {
            return null;
        }

        byte[] bytes = new byte[length/2];

        for (int i = 0; i < bytes.length; ++i) {
            int cur_index = 2*i;
            bytes[i] = (byte) Short.parseShort(s.substring(cur_index, cur_index + 2), 16);
        }
        return bytes;
    }

}
