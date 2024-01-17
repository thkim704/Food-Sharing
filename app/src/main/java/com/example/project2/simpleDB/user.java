package com.example.project2.simpleDB;

public class user {

    private String userID;
    private String password;
    private String nickname;
    private String email;
    private String addr;

    public user(){} // 생성자 메서드

    //getter, setter 설정
    public String getuserID() {
        return userID;
    }
    public void setuserID(String userID) {
        this.userID = userID;
    }

    public String getpassword() {
        return password;
    }
    public void setpassword(String password) {
        this.password = password;
    }

    public String getnickname() {
        return nickname;
    }
    public void setnickname(String nickname) {
        this.nickname = nickname;
    }

    public String getemail() {
        return email;
    }
    public void setemail(String email) {
        this.email = email;
    }

    public String getaddr() {
        return addr;
    }
    public void setaddr(String addr) {
        this.addr = addr;
    }

    //값을 추가할때 쓰는 함수, MainActivity에서 addanimal함수에서 사용할 것임.
    public user(String userID, String password, String nickname, String email, String addr){
        this.userID = userID;
        this.password = password;
        this.nickname = nickname;
        this.email = email;
        this.addr = addr;
    }

}
