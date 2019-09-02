package com.angle.alarmmanager;

import androidx.annotation.MainThread;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;

import java.util.Calendar;

/**
 * @author hejinlong
 * 参考文章:
 * https://blog.csdn.net/wei_chong_chong/article/details/51258336
 * 学习闹钟应用
 * * 三个重载方法:
 * * 1）set(int type，long startTime，PendingIntent pi)；
 * * 该方法用于设置一次性闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟执行时间，第三个参数表示闹钟响应动作。
 * * 2）setRepeating(int type，long startTime，long intervalTime，PendingIntent pi)；
 * * 该方法用于设置重复闹钟，第一个参数表示闹钟类型，第二个参数表示闹钟首次执行时间，第三个参数表示闹钟两次执行的间隔时间，第三个参数表示闹钟响应动作。
 * * 3）setInexactRepeating（int type，long startTime，long intervalTime，PendingIntent pi）；
 * * 该方法也用于设置重复闹钟，与第二个方法相似，不过其两个闹钟执行的间隔时间不是固定的而已。
 * *
 * * type:取值
 * * AlarmManager.ELAPSED_REALTIME表示闹钟在手机睡眠状态下不可用，该状态下闹钟使用相对时间（相对于系统启动开始），状态值为3；
 * * AlarmManager.ELAPSED_REALTIME_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟也使用相对时间，状态值为2；
 * * AlarmManager.RTC表示闹钟在睡眠状态下不可用，该状态下闹钟使用绝对时间，即当前系统时间，状态值为1；
 * * AlarmManager.RTC_WAKEUP表示闹钟在睡眠状态下会唤醒系统并执行提示功能，该状态下闹钟使用绝对时间，状态值为0；
 * * AlarmManager.POWER_OFF_WAKEUP表示闹钟在手机关机状态下也能正常进行提示功能，所以是5个状态中用的最多的状态之一，该状态下闹钟也是用绝对时间，状态值为4；不过本状态好像受SDK版本影响，某些版本并不支持；
 * *
 * * long startTime： 闹钟的第一次执行时间，以毫秒为单位，可以自定义时间，不过一般使用当前时间。需要注意的是，本属性与第一个属性（type）密切相关，如果第一个参数对 应的闹钟使用的是相对时间（ELAPSED_REALTIME和ELAPSED_REALTIME_WAKEUP），那么本属性就得使用相对时间（相对于 系统启动时间来说），比如当前时间就表示为：SystemClock.elapsedRealtime()；如果第一个参数对应的闹钟使用的是绝对时间 （RTC、RTC_WAKEUP、POWER_OFF_WAKEUP），那么本属性就得使用绝对时间，比如当前时间就表示 为：System.currentTimeMillis()。
 * * long intervalTime：对于后两个方法来说，存在本属性，表示两次闹钟执行的间隔时间，也是以毫秒为单位。
 * * PendingIntent pi： 绑定了闹钟的执行动作，比如发送一个广播、给出提示等等。
 * * PendingIntent是Intent的封装类。需要注意的是，如果是通过启动服务来实现闹钟提 示的话，
 * * PendingIntent对象的获取就应该采用Pending.getService(Context c,int i,Intent intent,int j)方法；
 * * 如果是通过广播来实现闹钟提示的话，PendingIntent对象的获取就应该采用
 * * PendingIntent.getBroadcast(Context c,int i,Intent intent,int j)方法；
 * * 如果是采用Activity的方式来实现闹钟提示的话，PendingIntent对象的获取就应该采用
 * * PendingIntent.getActivity(Context c,int i,Intent intent,int j)方法。
 * * 如果这三种方法错用了的话，虽然不会报错，但是看不到闹钟提示效果。
 */
public class AlarmManagerActivity extends AppCompatActivity {

    private AlarmManager mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_manager);

        mManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }

    /**
     * 案例1:6秒后启动一个Activity
     */
    public void openActivity(View view) {
        //获取AlarmManager实例

        if (mManager != null) {
            /* 这里注意
             * SystemClock.elapsedRealtime()
             * System.currentTimeMillis()
             * 的区别
             */
            int anHour = 6 * 1000;
            long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
            Intent intent = new Intent(this, SecondActivity.class);
            PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 0);
            //开启提醒
            mManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        }
    }

    /**
     * 案例2:固定时间后提醒
     */
    public void openRemind(View view) {
        //设定时间为:2019年08月10日12:30:00
        //Calendar calendar = Calendar.getInstance();
        //calendar.set(Calendar.YEAR, 2019);
        //calendar.set(Calendar.MONTH, 7);
        //calendar.set(Calendar.DAY_OF_MONTH, 9);
        //calendar.set(Calendar.HOUR_OF_DAY, 12);
        //calendar.set(Calendar.MINUTE, 40);
        //calendar.set(Calendar.SECOND, 0);

        Calendar current = Calendar.getInstance();
        //获取当前时间
        long currentMillis = current.getTimeInMillis();

        Calendar endCalendar = Calendar.getInstance();
        endCalendar.set(2019, 7, 9, 18, 40, 30);
        long endMillis = endCalendar.getTimeInMillis();

        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("time", endMillis - currentMillis);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        //获取时间
        long triggerAtTime = SystemClock.elapsedRealtime() + endMillis - currentMillis;
        mManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
    }
}
