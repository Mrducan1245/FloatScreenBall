package com.mrducan.floatscreenball;

public class IpItem {
    private int hIcone;
    private String IP;

    public IpItem(int hIcone, String IP) {
        this.hIcone = hIcone;
        this.IP = IP;
    }


    public int gethIcone() {
        return hIcone;
    }

    public void sethIcone(int hIcone) {
        this.hIcone = hIcone;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }


}
