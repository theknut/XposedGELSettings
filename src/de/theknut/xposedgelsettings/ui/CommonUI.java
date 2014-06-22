package de.theknut.xposedgelsettings.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import eu.chainfire.libsuperuser.Shell;

@SuppressLint("WorldReadableFiles")
public class CommonUI {

    public static boolean LOADING_ICONPACK;
    public static Activity ACTIVITY;
    public static Bitmap bluredBackground = null;
	public static Context CONTEXT;
	
	public static int UIColor = Color.parseColor("#222222");
    public static int TextColor = -1;
	
	public static boolean AUTO_BLUR_IMAGE;
	public static boolean needFullReboot = false;
	private static Shell.Interactive rootSession;
    public static boolean NO_BACKGROUND_IMAGE;

    public static List<String> getIconPacks(Context context) {
	    String[] sIconPackCategories = new String[] {
	            "com.fede.launcher.THEME_ICONPACK",
	            "com.anddoes.launcher.THEME",
	            "com.teslacoilsw.launcher.THEME"
	    };
	    String[] sIconPackActions = new String[] {
	            "org.adw.launcher.THEMES",
	            "com.gau.go.launcherex.theme"
	    };
	    
	    Intent i = new Intent();
        List<String> packages = new ArrayList<String>();
        PackageManager packageManager = context.getPackageManager();
        for (String action : sIconPackActions) {
            i.setAction(action);
            for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
                
                if (!packages.contains(r.activityInfo.packageName))
                    packages.add(r.activityInfo.packageName);
            }
        }
        
        i = new Intent(Intent.ACTION_MAIN);
        for (String category : sIconPackCategories) {
            i.addCategory(category);
            for (ResolveInfo r : packageManager.queryIntentActivities(i, 0)) {
                if (!packages.contains(r.activityInfo.packageName))
                    packages.add(r.activityInfo.packageName);
            }
            i.removeCategory(category);
        }
	    
        return packages;
    }

    // http://stackoverflow.com/questions/3035692/how-to-convert-a-drawable-to-a-bitmap
    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable == null) return null;

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }
	
	public static View setBackground(View rootView, int layout) {
        if (CommonUI.NO_BACKGROUND_IMAGE && (layout != R.id.welcomebackground)) {
            return rootView;
        }
        
    	if (CommonUI.AUTO_BLUR_IMAGE && CommonUI.setBluredBackground(CONTEXT, rootView, layout)) {
    		return rootView;
    	}

    	ImageView background = (ImageView) rootView.findViewById(layout);
    	background.setImageResource(R.drawable.wall);
    	
    	rootView.setOnTouchListener(new OnTouchListener() {
            
    	    float downX;
    	    
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    downX = event.getRawX();
                    
                    break;
                case MotionEvent.ACTION_MOVE:
                    
                    if ((event.getRawX() - downX) > 100.0f) {
                        MainActivity.openDrawer();
                        return true;
                    } 
                    
                    break;
                default:
                    break;
                }
                
                return false;
            }
        });
    	
    	return rootView;
	}
	
	public static boolean setBluredBackground (Context context, View v, int imageViewId) {
		
		ImageView background = (ImageView) v.findViewById(imageViewId);
		
		if (CommonUI.bluredBackground == null) {
			
			String pathBackground = Environment.getExternalStorageDirectory().getPath() + "/XposedGELSettings/bluredbackground.png";			
			File fileBackground = new File(pathBackground);			
			
			if (fileBackground.exists()) {
				CommonUI.bluredBackground = BitmapFactory.decodeFile(pathBackground);
				background.setImageBitmap(CommonUI.bluredBackground);
			}
			else {
				return false;
			}
		}
		else {
	    	background.setImageBitmap(CommonUI.bluredBackground);
		}
		
		return true;
	}
	
	public static void setCustomStyle(View view, boolean setTitle, boolean setSummary) {
		
		if (setTitle) {
			TextView title = ((TextView) view.findViewById(android.R.id.title));
	        title.setTextColor(Color.WHITE);
	        title.setTextAppearance(CommonUI.CONTEXT, R.style.ShadowText);
		}
        
        if (setSummary) {
	        TextView summary = ((TextView) view.findViewById(android.R.id.summary));
	        summary.setTextColor(Color.WHITE);
	        summary.setTextAppearance(CommonUI.CONTEXT, R.style.ShadowText);
        }
	}
	
	public static List<ResolveInfo> getAllApps() {
	    // load all apps which are listed in the app drawer
	    
	    PackageManager pm = CONTEXT.getPackageManager();        
        final Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        final List<ResolveInfo> apps = pm.queryIntentActivities(mainIntent, 0);
        Collections.sort(apps, new ResolveInfo.DisplayNameComparator(pm));
        
        return apps;
	}
	
	public static void restartLauncherOrDevice() {
    	
    	if (needFullReboot) {
    		
    		new AlertDialog.Builder(CONTEXT)
    	    .setTitle(R.string.alert_reboot_needed_title)
    	    .setMessage(R.string.alert_reboot_needed_summary)
    	    .setPositiveButton("Full reboot", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) {
    	        	if (!InAppPurchase.isPremium) {
    	    			Toast.makeText(CONTEXT, CONTEXT.getString(R.string.toast_donate_only), Toast.LENGTH_SHORT).show();
    	    			return;
    	    		}
    	        	
    	        	openRootShell(new String[]{"su", "-c", "reboot now"});
    	        }
	        }
    	     )
    	     .setNeutralButton("Hot reboot", new DialogInterface.OnClickListener() {

			      public void onClick(DialogInterface dialog, int id) {
			    	if (!InAppPurchase.isPremium) {
			  			Toast.makeText(CONTEXT, CONTEXT.getString(R.string.toast_donate_only), Toast.LENGTH_SHORT).show();
			  			return;
			  		}
			    	  
			    	openRootShell(new String[]{ "su", "-c", "killall system_server"});			
			    }})
    	    .setNegativeButton("Launcher reboot", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) { 
    	            restartLauncher();
    	        }
    	     })
    	     .show();
    	}
    	else {
    		restartLauncher();
    	}
    }
    
    public static boolean restartLauncher(boolean showToast) {   	
    	
		 ActivityManager am = (ActivityManager) CONTEXT.getSystemService(Context.ACTIVITY_SERVICE);
	   	 String msg = "Killed:\n";
	   	 boolean neededRoot = false;
	   	 
	   	 List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
	   	 for (RunningAppProcessInfo process : processes) {
	   	     if (Common.PACKAGE_NAMES.contains(process.processName)) {  

	   	         am.killBackgroundProcesses(process.processName);
	   	         List<RunningAppProcessInfo> processesAfterKill = am.getRunningAppProcesses();
	   	         for (RunningAppProcessInfo processAfterKill : processesAfterKill) {
	   	             if (processAfterKill.pid == process.pid) {
	   	                 // process wasn't killed for some reason
	   	                 // kill it with fire
	   	                 neededRoot = true;
	   	                 CommonUI.openRootShell(new String[]{"su","kill -9 " + processAfterKill.pid});
	   	             }
	   	         }

	   	         if (!neededRoot) {
	   	             msg += process.processName + "\n";
	   	         }
	   	     }                        			 
	   	 }
	   	 
	   	 if (!neededRoot) {
	   		 
		   	 if (msg.equals("Killed:\n")) {
		   		 msg = msg.substring(0, msg.lastIndexOf('\n')) + " " + CONTEXT.getString(R.string.toast_reboot_failed_nothing_msg) + "... :(\n" + CONTEXT.getString(R.string.toast_reboot_failed);
		   	 } else {
		   		 msg = msg.substring(0, msg.lastIndexOf('\n'));
		   	 }
		   	 
		   	if (showToast) {
		   		Toast.makeText(CONTEXT, msg, Toast.LENGTH_LONG).show();
		   	}
	   	 }
	   	 
	   	 return true;
	}
    
    public static boolean restartLauncher() {		 
	   	 
	   	 return restartLauncher(true);
	}
    
    static boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (NameNotFoundException e) {
            return false;
        }
    }
    
    @SuppressWarnings("deprecation")
    static String getAppName(String prefKey) {
    	final PackageManager pm = CONTEXT.getPackageManager();
    	String app = CONTEXT.getSharedPreferences(Common.PREFERENCES_NAME, Context.MODE_WORLD_READABLE).getString(prefKey + "_launch", "");
    	
    	ApplicationInfo ai;
    	try {
    	    ai = pm.getApplicationInfo(app, 0);
    	} catch (final NameNotFoundException e) {
    	    ai = null;
    	}
    	
    	return (String) (ai != null ? pm.getApplicationLabel(ai) : "");
    }
    
	public static void openRootShell(final String[] command) {
	    if (rootSession != null) {
	        sendRootCommand(command);
	    } else {
	        // We're creating a progress dialog here because we want the user to wait.
	        final ProgressDialog dialog = new ProgressDialog(CONTEXT);
	        dialog.setTitle(R.string.progress_requesting_root_title);
	        dialog.setMessage(CONTEXT.getString(R.string.progress_requesting_root_summary));
	        dialog.setIndeterminate(true);
	        dialog.setCancelable(false);
	        dialog.show();
	
	        // start the shell in the background and keep it alive as long as the app is running
	        rootSession = new Shell.Builder().
	                useSU().
	                setWantSTDERR(true).
	                setWatchdogTimeout(5).
	                setMinimalLogging(true).
	                open(new Shell.OnCommandResultListener() {
	
	                    // Callback to report whether the shell was successfully started up 
	                    @Override
	                    public void onCommandResult(int commandCode, int exitCode, List<String> output) {
	                        // note: this will FC if you rotate the phone while the dialog is up
	                        dialog.dismiss();
	
	                        if (exitCode != Shell.OnCommandResultListener.SHELL_RUNNING) {
	                            Toast.makeText(CONTEXT, "Error opening root shell: exitCode " + exitCode, Toast.LENGTH_LONG).show();
	                        } else {
	                            // Shell is up: send our first request 
	                            sendRootCommand(command);
	                        }
	                    }
	                });
	    }
    }
    	
	private static void sendRootCommand(String[] command) {
        rootSession.addCommand(command, 0,
                new Shell.OnCommandResultListener() {
		            public void onCommandResult(int commandCode, int exitCode, List<String> output) {
		                if (exitCode < 0) {
		                	Toast.makeText(CONTEXT, "Error executing commands: exitCode " + exitCode, Toast.LENGTH_LONG).show();
		                } else {
		                	if (output.size() == 0) {
		                		Toast.makeText(CONTEXT, "Success", Toast.LENGTH_LONG).show();
		                	} else {
		                		Toast.makeText(CONTEXT, "Failed: " + output, Toast.LENGTH_LONG).show();
		                	}
		                }
		            }
		        });
    }
}
