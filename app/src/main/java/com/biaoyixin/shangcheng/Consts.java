package com.biaoyixin.shangcheng;

import android.os.Message;

/**
 * Created by zhujj on 17-10-16.
 */
public class Consts {

    public final static int BoardCast_PriceMsg = 0x13d;
    public final static int BoardCast_TradingListChange = 0x14d;
    public final static int BoardCast_TradeClose = 0x15d;
    public final static int BoardCast_ChongZhi_Refresh = 0x16d;

    public final static int BoardCast_TradingAllListChange = 0x17d;
    public final static int BoardCast_Trade_OpenTimeIntervalChange = 0x18d;

    public final static int BoardCast_PaySuccess = 0x19d;


    public static Message getBoardCastMessage(int boardCastFlag) {
        return Message.obtain(null, boardCastFlag);
    }
}
