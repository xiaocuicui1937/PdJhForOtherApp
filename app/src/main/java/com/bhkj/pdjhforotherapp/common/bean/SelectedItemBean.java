package com.bhkj.pdjhforotherapp.common.bean;

public class SelectedItemBean {

    private String id;//业务id
    private String name;//业务名称
    private int count;//叠加业务数
    private String img;//图标

    private int pageCount;//页码
    private String ywlx;//业务类型

    public int getPageCount() {
        return pageCount;
    }

    public void setPageCount(int pageCount) {
        this.pageCount = pageCount;
    }

    public String getYwlx() {
        return ywlx;
    }

    public void setYwlx(String ywlx) {
        this.ywlx = ywlx;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

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

    @Override
    public String toString() {
        return "SelectedItemBean{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", count=" + count +
                ", img='" + img + '\'' +
                ", pageCount=" + pageCount +
                ", ywlx='" + ywlx + '\'' +
                '}';
    }
}
