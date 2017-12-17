package com.biaoyixin.shangcheng.home;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.NotificationCompat;

import com.biaoyixin.shangcheng.R;
import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.user.UserApi;
import com.biaoyixin.shangcheng.base.BaseApp;
import com.biaoyixin.shangcheng.base.PreferencesUtil;
import com.biaoyixin.shangcheng.model.BaseModel;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by zhujj on 17-12-17.
 */

public class MessagesCenter {


    private final static String LOCAL_MAX_MESSAGE_ID = "MessagesCenter_LOCAL_MAX_MESSAGE_ID";


    public static long sNow = 0;
    public static List<UserApi.PushItem> sMessages = new ArrayList<>();

    private static Handler sHandler;

    public static class MessageGetEvent extends BaseModel {

    }

    public static void fetchData(final boolean checkResult, final boolean showNotification) {
        UserApi.IUser iUser = ServerAPI.getInterface(UserApi.IUser.class);
        iUser.getPush(new Callback<UserApi.PushResp>() {
            @Override
            public void success(UserApi.PushResp resp, Response response) {
                if (resp.code != 0) {
                    if (checkResult) {
                        ServerAPI.handleCodeError(resp);
                    }
                } else {
                    sNow = resp.data.now;
                    if (resp.data.pushList == null || resp.data.pushList.size() == 0) {
                        sMessages.clear();
                    } else {
                        sMessages = resp.data.pushList;
                    }


                    EventBus.getDefault().post(new MessageGetEvent());

                    if (showNotification) {
                        checkNotification();


                        if (sHandler == null) {
                            sHandler = new Handler(Looper.getMainLooper());
                        }

                        sHandler.removeCallbacks(null);
                        sHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                MessagesCenter.fetchData(false, true);
                            }
                        }, 1000 * 60 * 30);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (checkResult) {
                    ServerAPI.HandlerException(error);
                }
            }
        });

    }

    private static void checkNotification() {
        String localMaxStr = PreferencesUtil.getString(BaseApp.getApp(), LOCAL_MAX_MESSAGE_ID, "0");
        long localMax = Long.parseLong(localMaxStr);


        String message = "";
        long thisMax = 0;
        for(UserApi.PushItem item : sMessages) {
            if (item.id > thisMax) {
                message = item.message;
                thisMax = item.id;
            }
        }


        if (thisMax > localMax) {
            PreferencesUtil.putString(BaseApp.getApp(), LOCAL_MAX_MESSAGE_ID, Long.toString(thisMax));

            String title = BaseApp.getApp().getString(R.string.app_name2);

            try {
                showNotification(title, message);
            } catch (Exception e) {
            }

        }

    }

    public static void showNotification(String title, String message) {
        Context ctx = BaseApp.getApp();

        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx);
        //===============通知窗口的属性设置===============
        builder.setSmallIcon(R.drawable.ic_launcher);
        builder.setTicker(message);//在状态栏跳出时显示的内容
        builder.setContentTitle(title);//在下拉条里显示的通知的标题
        builder.setContentText(message);//标题下的内容摘要
        builder.setDefaults(Notification.DEFAULT_ALL);//设置通知接收时，系统对应的提醒方式，震动，声音等

        //创建意图,从哪个Activity跳转到哪个Activity的意图
        Intent mIntent = new Intent(ctx, MessagesActivity.class);
        //获得一个用于跳转Activity的延迟意图（何时触发该意图不确定，而Intent是即时的）
//一般跳转Activity的时候，我们需要把最后一个参数改为PendingIntent.FLAG_UPDATE_CURRENT,这样在启动的Activity里就可以用接收Intent传送数据的方法正常接收。
        PendingIntent activity = PendingIntent.getActivity(ctx, 1, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //把这个延迟意图塞到通知里
        builder.setContentIntent(activity);
        //设置当点击通知后，通知是否自动消失
        builder.setAutoCancel(true);

        //把所有设置联合起来，返回一个新的notification
        Notification build = builder.build();
        notificationManager.notify(1,build);

    }

}
