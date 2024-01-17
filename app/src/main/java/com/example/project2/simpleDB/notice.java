package com.example.project2.simpleDB;

public class notice {

    private String notID;
    private String image1;
    private String image2;
    private String image3;
    private String notauthor;
    private String notaddr;
    private String nottype;
    private String notexpiry;
    private String notdate;
    private String nottitle;
    private String notcontent;

    public notice(){} // 생성자 메서드

    //getter, setter 설정
    public String getnotID() { return notID; }
    public void setnotno(String notID) { this.notID = notID; }

    public String getimage1() {
        return image1;
    }
    public void setimage1(String image1) {
        this.image1 = image1;
    }

    public String getimage2() {
        return image2;
    }
    public void setimage2(String image2) {
        this.image2 = image2;
    }

    public String getimage3() {
        return image3;
    }
    public void setimage3(String image3) {
        this.image3 = image3;
    }

    public String getnotauthor() {
        return notauthor;
    }
    public void setnotauthor(String notauthor) {
        this.notauthor = notauthor;
    }

    public String getnotaddr() {
        return notaddr;
    }
    public void setnotaddr(String notaddr) { this.notaddr = notaddr; }

    public String getnottype() {
        return nottype;
    }
    public void setnottype(String nottype) {
        this.nottype = nottype;
    }

    public String getnotexpiry() {
        return notexpiry;
    }
    public void setnotexpiry(String notexpiry) {
        this.notexpiry = notexpiry;
    }

    public String getnotdate() { return notdate; }
    public void setnotdate(String notdate) {
        this.notdate = notdate;
    }

    public String getnottitle() {
        return nottitle;
    }
    public void setnottitle(String nottitle) {
        this.nottitle = nottitle;
    }

    public String getnotcontent() {
        return notcontent;
    }
    public void setnotcontent(String notcontent) {
        this.notcontent = notcontent;
    }

    //값을 추가할때 쓰는 함수, MainActivity에서 addanimal함수에서 사용할 것임.
    public notice(String notID, String image1, String image2, String image3, String notauthor, String notaddr,
                  String nottype, String notexpiry, String notdate, String nottitle, String notcontent){
        this.notID = notID;
        this.image1 = image1;
        this.image2 = image2;
        this.image3 = image3;
        this.notauthor = notauthor;
        this.notaddr = notaddr;
        this.nottype = nottype;
        this.notexpiry = notexpiry;
        this.notdate = notdate;
        this.nottitle = nottitle;
        this.notcontent = notcontent;
    }

}
