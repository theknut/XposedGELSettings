package de.theknut.xposedgelsettings.ui;

import java.io.File;
import java.util.List;

import de.theknut.xposedgelsettings.R;
import de.theknut.xposedgelsettings.hooks.Common;
import eu.chainfire.libsuperuser.Shell;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommonUI {
	
	public static Bitmap bluredBackground = null;
	public static Context CONTEXT;
	
	public static int UIColor = Color.parseColor("#222222");
	
	public static boolean AUTO_BLUR_IMAGE;
	public static boolean needFullReboot = false;
	private static Shell.Interactive rootSession;
	
	public static View setBackground(View rootView, int layout) {
		
    	if (CommonUI.AUTO_BLUR_IMAGE && CommonUI.setBluredBackground(CONTEXT, rootView, layout)) {
    		return rootView;
    	}
    	
    	ImageView background = (ImageView) rootView.findViewById(layout);
    	background.setImageResource(R.drawable.wall);
    	
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
	
	public static void restartLauncherOrDevice(final Context context) {  	
    	
    	if (needFullReboot) {
    		
    		new AlertDialog.Builder(CONTEXT)
    	    .setTitle(R.string.alert_reboot_needed_title)
    	    .setMessage(R.string.alert_reboot_needed_summary)
    	    .setPositiveButton("Full reboot", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) {
    	        	if (!InAppPurchase.isDonate) {
    	    			Toast.makeText(context, context.getString(R.string.toast_donate_only), Toast.LENGTH_SHORT).show();
    	    			return;
    	    		}
    	        	
    	        	openRootShell(new String[]{"su", "-c", "reboot now"});
    	        }
	        }
    	     )
    	     .setNeutralButton("Hot reboot", new DialogInterface.OnClickListener() {

			      public void onClick(DialogInterface dialog, int id) {
			    	if (!InAppPurchase.isDonate) {
			  			Toast.makeText(context, context.getString(R.string.toast_donate_only), Toast.LENGTH_SHORT).show();
			  			return;
			  		}
			    	  
			    	openRootShell(new String[]{ "su", "-c", "busybox killall system_server"});			
			    }})
    	    .setNegativeButton("Launcher reboot", new DialogInterface.OnClickListener() {
    	        public void onClick(DialogInterface dialog, int which) { 
    	            restartLauncher(context);
    	        }
    	     })
    	     .show();
    	}
    	else {
    		restartLauncher(CONTEXT);
    	}
    }
    
    private static boolean restartLauncher(Context context) {   	
    	
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
		   		 msg = msg.substring(0, msg.lastIndexOf('\n')) + " " + context.getString(R.string.toast_reboot_failed_nothing_msg) + "... :(\n" + context.getString(R.string.toast_reboot_failed);
		   	 } else {
		   		 msg = msg.substring(0, msg.lastIndexOf('\n'));
		   	 }
		   	 
		   	 Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
	   	 }
	   	 return true;
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
