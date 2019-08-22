package com.bhkj.pdjhforotherapp.common.bean;

import java.util.List;

public class MainYwAllBean {


    /**
     * code : 200
     * datas : [{"id":"BC8CE15C01CE4099BF006AA1ECF2267D","bm":null,"name":"转移登记","content":"转移登记","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/1560840979013BC8CE15C01CE4099BF006AA1ECF2267D.png"},{"id":"5E05CA2A3D5C4C5284A65AD6E9F27B3D","bm":null,"name":"核发6年免检机动车检验合格标","content":"核发6年免检机动车检验合格标","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608411538075E05CA2A3D5C4C5284A65AD6E9F27B3D.png"},{"id":"4A35AD85BB1042FA9D7B3265699A40C3","bm":null,"name":"补领、换领检验合格标志","content":"补领、换领检验合格标志","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608412207384A35AD85BB1042FA9D7B3265699A40C3.png"},{"id":"5CCEBAE9CDB64E9D993B53BA27FF501A","bm":null,"name":"注销登记","content":"注销登记","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608420334195CCEBAE9CDB64E9D993B53BA27FF501A.png"},{"id":"4C4B11FE00B443649E1DD7F4329F7D98","bm":null,"name":"核发临时行驶号牌","content":"核发临时行驶号牌","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608425002644C4B11FE00B443649E1DD7F4329F7D98.png"},{"id":"2F24CC42D845436886335824222889A8","bm":null,"name":"校车标牌核发","content":"校车标牌核发","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608425394422F24CC42D845436886335824222889A8.png"},{"id":"3","bm":null,"name":"注册登记","content":"123","owner":"1","duration":3,"status":"1","remark":null,"img":"static/img/15608402891933.png"},{"id":"5","bm":null,"name":"补、换领号牌行驶证","content":"补、换领号牌行驶证","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608404378505.png"},{"id":"FEB1C7BB08034251A5144A81E5F9D071","bm":null,"name":"机动车所有人联系方式变更备案","content":"机动车所有人联系方式变更备案","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/1560842245921FEB1C7BB08034251A5144A81E5F9D071.png"},{"id":"8BDE9BCCFCFB4D759B8BAAF0EA6FFC35","bm":null,"name":"质押备案和解除质押备案","content":"质押备案和解除质押备案","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608423239468BDE9BCCFCFB4D759B8BAAF0EA6FFC35.png"},{"id":"5CD490E362C0428CA530A603D28AF47E","bm":null,"name":"抵押登记","content":"抵押登记","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608423943535CD490E362C0428CA530A603D28AF47E.png"},{"id":"86D69CC6932C450182727F78423F8DD3","bm":null,"name":"除机动车所有人联系方式外的其他变更备案","content":"除机动车所有人联系方式外的其他变更备案","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/156084245629386D69CC6932C450182727F78423F8DD3.png"},{"id":"9695740350F94725B91FF110AAA7D41C","bm":null,"name":"补领、换领、申领登记证书","content":"补领、换领、申领登记证书","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608425833389695740350F94725B91FF110AAA7D41C.png"},{"id":"7244646B04814D07A4B8FF24624B022E","bm":null,"name":"变更登记","content":"123","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/15608403122467244646B04814D07A4B8FF24624B022E.png"},{"id":"D69C328DAA974DD99D12345F8123852F","bm":null,"name":"登记事项更正","content":"登记事项更正","owner":"1","duration":5,"status":"1","remark":null,"img":"static/img/1560841058082D69C328DAA974DD99D12345F8123852F.png"}]
     */

    private int code;
    private List<DatasBean> datas;

    public boolean isSuccess(){
        return code==200;
    }

    private String message;

    public String getMessage() {
        return message;
    }

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
         * id : BC8CE15C01CE4099BF006AA1ECF2267D
         * bm : null
         * name : 转移登记
         * content : 转移登记
         * owner : 1
         * duration : 5
         * status : 1
         * remark : null
         * img : static/img/1560840979013BC8CE15C01CE4099BF006AA1ECF2267D.png
         */

        private String selectedIdJDC;

        private String selectedIdJSR;

        public String getSelectedIdJSR() {
            return selectedIdJSR;
        }

        public void setSelectedIdJSR(String selectedIdJSR) {
            this.selectedIdJSR = selectedIdJSR;
        }

        public String getSelectedIdJDC() {
            return selectedIdJDC;
        }

        public void setSelectedIdJDC(String selectedIdJDC) {
            this.selectedIdJDC = selectedIdJDC;
        }

        private String id;
        private Object bm;
        private String name;
        private String content;
        private String owner;
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

        public String getOwner() {
            return owner;
        }

        public void setOwner(String owner) {
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
