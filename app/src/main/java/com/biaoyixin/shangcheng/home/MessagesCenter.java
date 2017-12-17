package com.biaoyixin.shangcheng.home;

import com.biaoyixin.shangcheng.api.ServerAPI;
import com.biaoyixin.shangcheng.api.user.UserApi;
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
                        // TODO show notification
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

}
