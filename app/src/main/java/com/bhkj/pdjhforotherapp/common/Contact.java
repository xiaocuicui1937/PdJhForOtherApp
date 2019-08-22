package com.bhkj.pdjhforotherapp.common;

public class Contact {
    //    public static final String BASE_URL = "http://192.168.1.101:80";
    public static final String BASE_URL = "http://192.168.1.104:8080/queue/";

        public static final String GET_ALL_YW = "takenum/getOwnerid";
//    public static final String GET_ALL_YW = "/queue/owner1.json";
//    public static final String GET_ALL_YW2 = "/queue/owner1.json";
    //    public static final String GET_ALL_YW = "/takenum/getAllByOwner";
    //保存选择业务到服务器上
    public static final String SAVE_YW_TO_SERVEr = "takenum/saveUserOptionQueue";
    //版本更新apk下载基础地址
    public static final String BASEPATHDOWNLOAD =BASE_URL+"static/";
    //检测版本更新version获取文件
    public static final String REFRESHURL = BASE_URL+"static/version.json";

    //保存机动车业务
    public static final String JDC_YW_PARAM = "jdc_yw_param";
    //保存驾驶人业务
    public static final String JSR_YW_PARAM = "jsr_yw_param";
    //取号（普通通道）
    public static final String COMMON_WINDOW = "WINDOWS";
    //绿色窗口
    public static final String VIP_WINDOW = "GREEN_TYPE";
    //携带取号类型
    public static final String QHTYPE = "qhtype";
    //携带确认业务key
    public static final String SURE_YW = "sure_yw";
    //保存机动车业务
    public static final String JDC_YW_DATA = "jdc_yw_data";
    //保存驾驶人业务
    public static final String JSR_YW_DATA = "jsr_yw_data";
}
