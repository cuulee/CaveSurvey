package com.astoev.cave.survey.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;
import com.astoev.cave.survey.R;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Created by IntelliJ IDEA.
 * User: astoev
 * Date: 1/24/12
 * Time: 1:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class UIUtilities {

    public static void showNotification(Context aContext, int aResourceId) {
        showNotification(aContext, aResourceId, null);
    }

    public static void showNotification(Context aContext, String aMessage) {
        showNotification(aContext, aMessage, null);
    }

    public static void showNotification(Context aContext, int aResourceId, Object aParams) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(aContext, aContext.getString(aResourceId, aParams), duration);
        toast.show();
    }

    public static void showNotification(Context aContext, String aMessage, Object aParams) {
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(aContext, aMessage, duration);
        toast.show();
    }

    public static void showRawMessage(Context aContext, String rawMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
        builder.setTitle("Debug");
        builder.setMessage(rawMessage);
        builder.show();
    }

    public static void showAlertDialog(Context aContext, int aTitleId, int aMemoId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
        builder.setTitle(aTitleId);
        builder.setMessage(aMemoId);
        builder.show();
    }

    public static void reportException(Context aContext, Exception e) {
        AlertDialog.Builder builder = new AlertDialog.Builder(aContext);
        builder.setTitle(R.string.error);
        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        builder.setMessage(sw.toString());
        builder.show();
    }

    public static void showBusy(Context aContext) {
        ProgressDialog dialog = ProgressDialog.show(aContext, "",
                aContext.getString(R.string.busy), true);
        dialog.show();
    }

}