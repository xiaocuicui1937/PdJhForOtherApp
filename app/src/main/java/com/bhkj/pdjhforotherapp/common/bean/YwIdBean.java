package com.bhkj.pdjhforotherapp.common.bean;

import java.io.Serializable;
import java.util.List;

/**
 * 根据接口返回ywid
 */
public class YwIdBean implements Serializable {

    private String message;

    public String getMessage() {
        return message;
    }

    /**
     * code : 200
     * datas : [{"id":"1","bm":null,"name":"机动车业务","content":"机动车业务","owner":null,"duration":0,"status":"1","remark":null,"img":"static/img/15608402645681.png"},{"id":"2","bm":null,"name":"驾驶人业务","content":"驾驶人业务","owner":null,"duration":0,"status":"1","remark":null,"img":"static/img/15608402744692.png"}]
     */
    public boolean isSuccess() {
        return code == 200;
    }

    private int code;
    private List<DatasBean> datas;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<DatasBean> getDatas() {
        return datas;
    }

    public void setDatas(List<DatasBean> datas) {
        this.datas = datas;
    }

    public static class DatasBean {
        /**
         * id : 1
         * bm : null
         * name : 机动车业务
         * content : 机动车业务
         * owner : null
         * duration : 0
         * status : 1
         * remark : null
         * img : static/img/15608402645681.png
         */

        private String id;
        private Object bm;
        private String name;
        private String content;
        private Object owner;
        private int duration;
        private String status;
        private Object remark;
        private String img;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Object getBm() {
            return bm;
        }

        public void setBm(Object bm) {
            this.bm = bm;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public Object getOwner() {
            return owner;
        }

        public void setOwner(Object owner) {
            this.owner = owner;
        }

        public int getDuration() {
            return duration;
        }

        public void setDuration(int duration) {
            this.duration = duration;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Object getRemark() {
            return remark;
        }

        public void setRemark(Object remark) {
            this.remark = remark;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }
    }
}
