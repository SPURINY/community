package com.nowcoder.community.entity;

/*
* 封装分页相关信息
* current,limit从页面获取  totalRows,path传给页面
* setter加处理，防止传入奇怪不合理的数
* 加方法 获得当前页起始行 总页数 起始页 结束页
* */
public class Page {
    //当前页码,不传就默认第一页
    private int current=1;
    //一页显示多少条
    private int limit=10;
    //总数居条数（页数=总条数/一页显示条数
    private int totalRows;
    //用于复用分页链接
    private String path;

    /*当前页的起始行*/
    public int getOffset(){
        return (current-1)*limit;
    }
    /*总页数，利用总行数*/
    public int getTotal(){
        if(totalRows%limit==0){
            return totalRows/limit;
        }else return totalRows/limit+1;
    }
    /*地下页数栏显示的：起始页码*/
    public int getFrom(){
        int from=current-2;
        return from<1? 1 : from;
    }
    /*结束页吗*/
    public int getTo(){
        int to=current+2;
        int total=getTotal();
        return to>total?total:to;
    }




    @Override
    public String toString() {
        return "Page{" +
                "current=" + current +
                ", limit=" + limit +
                ", totalRows=" + totalRows +
                ", path='" + path + '\'' +
                '}';
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current>=1){
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if(limit>=1&&limit<=100){
            this.limit = limit;
        }
    }

    public int getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(int totalRows) {
        if(totalRows>=0){
            this.totalRows = totalRows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
