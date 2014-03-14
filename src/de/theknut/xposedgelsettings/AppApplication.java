/**
 * 
 */
package de.theknut.xposedgelsettings;

import android.app.Application;

/**
 * @author Dary
 *
 */
public class AppApplication extends Application {

    private static AppApplication mInstance;
    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }
    
    public static AppApplication getInstance(){
        if (mInstance ==null){
            mInstance = new AppApplication();
        }
        return mInstance;
    }

    
}
