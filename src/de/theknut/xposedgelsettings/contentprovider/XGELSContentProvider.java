package de.theknut.xposedgelsettings.contentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import de.theknut.xposedgelsettings.hooks.Common;

/**
 * Created by Alexander Schulz on 22.11.2015.
 */
public class XGELSContentProvider extends ContentProvider {

    SharedPreferences sharedPrefs;

    @Override
    public boolean onCreate() {
        sharedPrefs = getContext().getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uri.getPath().equals(Common.URI_SETTINGS_BASE.getPath())) {
            MatrixCursor cursor = new MatrixCursor(new String[]{"value"});
            Object pref = sharedPrefs.getAll().get(uri.getQuery());
            if (pref instanceof HashSet) {
                for (String value : ((HashSet<String>) pref)) {
                    cursor.addRow(new Object[]{value});
                }
            }

            return cursor;
        }
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        update(uri, values, null, null);
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        sharedPrefs.edit().remove(selection).apply();
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (uri.getPath().equals(Common.URI_SETTINGS_BASE.getPath())) {
            sharedPrefs.edit().remove(uri.getQuery()).commit();
            if (values.getAsString("type").equals(ArrayList.class.toString())) {
                Set set = new HashSet(Arrays.asList(values.getAsString("value").replace("[", "").replace("]", "").replace(" ", "").split(",")));
                sharedPrefs.edit().putStringSet(uri.getQuery(), set).commit();
            }
        }
        return 0;
    }
}
