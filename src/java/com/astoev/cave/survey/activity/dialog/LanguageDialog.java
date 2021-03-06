/**
 * 
 */
package com.astoev.cave.survey.activity.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.ArrayAdapter;

import com.astoev.cave.survey.R;
import com.astoev.cave.survey.util.ConfigUtil;

import java.util.Locale;

/**
 * Dialog for choosing Language
 * 
 * @author jmitrev
 */
public class LanguageDialog extends DialogFragment {
    
    private static final int LANG_ENGLISH = 0;
    private static final int LANG_BULGARIAN = 1;

    /**
     * @see android.support.v4.app.DialogFragment#onCreateDialog(android.os.Bundle)
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceStateArg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.lang_title);
        ArrayAdapter<String> langAdapter = 
                new ArrayAdapter<String>(this.getActivity(), 
                        android.R.layout.simple_list_item_1, 
                        getResources().getStringArray(R.array.lang_array));
        
        builder.setAdapter(langAdapter, new DialogInterface.OnClickListener() {
            
            @Override
            public void onClick(DialogInterface dialogArg, int whichArg) {
                
                String language = "en";
                switch (whichArg) {
                case LANG_ENGLISH:
                    language = "en";
                    break;
                case LANG_BULGARIAN:
                    language = "bg";
                    break;

                default:
                    break;
                }
                
                // change locale only if it is different
                String savedLanguage = ConfigUtil.getStringProperty(ConfigUtil.PREF_LOCALE);
                if (!language.equals(savedLanguage)){
                
                    // create prefurred locale
                    Locale locale = new Locale(language);
                    Locale.setDefault(locale);
                    Configuration config = new Configuration();
                    config.locale = locale;
    
                    Resources resources = ((Dialog)dialogArg).getOwnerActivity().getBaseContext().getResources();
                    resources.updateConfiguration(config, resources.getDisplayMetrics());
                    
                    // save settings
                    ConfigUtil.setStringProperty(ConfigUtil.PREF_LOCALE, language);
                    
                    // restart parent activity
                    Intent intent = getActivity().getIntent();
                    getActivity().finish();
                    startActivity(intent);
                }
            }
        });
        
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogArg, int whichArg) {
                // cancel
                LanguageDialog.this.getDialog().cancel();
            }
        });
        
        return builder.create();
    }

}
