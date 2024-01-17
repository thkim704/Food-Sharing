package com.example.project2.simpleDB;

public class comment {

    private String comID;
    private String comauthor;
    private String comdate;
    private String comcontent;

    public comment(){} // 생성자 메서드

    //getter, setter 설정
    public String getcomID() {
        return comID;
    }
    public void setcomID(String comID) {
        this.comID = comID;
    }

    public String getcomauthor() {
        return comauthor;
    }
    public void setcomauthor(String comauthor) {
        this.comauthor = comauthor;
    }

    public String getcomdate() {
        return comdate;
    }
    public void setcomdate(String comdate) {
        this.comdate = comdate;
    }

    public String getcomcontent() {
        return comcontent;
    }
    public void setcomcontent(String comcontent) {
        this.comcontent = comcontent;
    }

    //값을 추가할때 쓰는 함수, MainActivity에서 addanimal함수에서 사용할 것임.
    public comment(String comID, String comauthor, String comdate, String comcontent){
        this.comID = comID;
        this.comauthor = comauthor;
        this.comdate = comdate;
        this.comcontent = comcontent;
    }

}
