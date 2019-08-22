package com.bhkj.pdjhforotherapp.common.bean;

import android.content.Intent;

public class JhResultBean {

    private String message;

    public String getMessage() {
        return message;
    }

    /**
     * code : 200
     * datas : {"id":"D25FD70E123D48E88D2A1EFB10A46C19","name":"01","type":"WINDOWS","wip":"192.168.20.117","prefix":"S","userid":"FD987ACF3DF7455194491BD46C4D2114","qunumber":"S0001","status":"1","winstatus":"1","remark":"123","typeName":null,"areaName":null,"username":null,"queuenum":null,"winregionname":null}
     */
    public boolean isSuccess(){
        return code==200;
    }

    private int code;
    private DatasBean datas;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public DatasBean getDatas() {
        return datas;
    }

    public void setDatas(DatasBean datas) {
        this.datas = datas;
    }

    public static class DatasBean {
        /**
         * id : D25FD70E123D48E88D2A1EFB10A46C19
         * name : 01
         * type : WINDOWS
         * wip : 192.168.20.117
         * prefix : S
         * userid : FD987ACF3DF7455194491BD46C4D2114
         * qunumber : S0001
         * status : 1
         * winstatus : 1
         * remark : 123
         * typeName : null
         * areaName : null
         * username : null
         * queuenum : null
         * winregionname : null
         */
        private Integer queuetime;//等候时间

        public Integer getQueuetime() {
            return queuetime;
        }

        private String id;
        private String name;
        private String type;
        private String wip;
        private String prefix;
        private String userid;
        private String qunumber;
        private String status;
        private String winstatus;
        private String remark;
        private Object typeName;
        private Object areaName;
        private Object username;
        private Integer queuenum;
        private Object winregionname;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getWip() {
            return wip;
        }

        public void setWip(String wip) {
            this.wip = wip;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public String getUserid() {
            return userid;
        }

        public void setUserid(String userid) {
            this.userid = userid;
        }

        public String getQunumber() {
            return qunumber;
        }

        public void setQunumber(String qunumber) {
            this.qunumber = qunumber;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getWinstatus() {
            return winstatus;
        }

        public void setWinstatus(String winstatus) {
            this.winstatus = winstatus;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public Object getTypeName() {
            return typeName;
        }

        public void setTypeName(Object typeName) {
            this.typeName = typeName;
        }

        public Object getAreaName() {
            return areaName;
        }

        public void setAreaName(Object areaName) {
            this.areaName = areaName;
        }

        public Object getUsername() {
            return username;
        }

        public void setUsername(Object username) {
            this.username = username;
        }

        public Integer getQueuenum() {
            return queuenum;
        }

        public Object getWinregionname() {
            return winregionname;
        }

        public void setWinregionname(Object winregionname) {
            this.winregionname = winregionname;
        }
    }
}
