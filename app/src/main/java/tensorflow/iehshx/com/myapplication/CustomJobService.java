package tensorflow.iehshx.com.myapplication;

import android.app.ActivityManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

/**
 * 创建于  win10 在 2019/3/27.
 */
public class CustomJobService extends JobService {
    private static CustomJobService mKeepAliveService;
    private static final int MESSAGE_ID_TASK = 0x01;

    public static boolean isJobServiceAlive(){
        return mKeepAliveService != null;
    }

    private boolean isAppLive(Context context, String packageName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(100);
        if (list.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo info : list) {
            if (info.baseActivity.getPackageName().equals(packageName)) {
                return true;
            }
        }
        return false;
    }


    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
                if (isAppLive(getApplicationContext(), "tensorflow.iehshx.com.myapplication")){
                    Toast.makeText(getApplicationContext(), "APP活着的", Toast.LENGTH_SHORT)
                            .show();
                }else{
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "APP被杀死，重启...", Toast.LENGTH_SHORT)
                            .show();
                }

                // 通知系统任务执行结束
                jobFinished( (JobParameters) msg.obj, false );
                return true;
        }
    });


    @Override
    public boolean onStartJob(JobParameters params) {
        mKeepAliveService = this;
        // 返回false，系统假设这个方法返回时任务已经执行完毕；
        // 返回true，系统假定这个任务正要被执行
        Message msg = Message.obtain(handler, MESSAGE_ID_TASK, params);
        handler.sendMessage(msg);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        handler.removeMessages(MESSAGE_ID_TASK);
        return false;
    }
}
