package cn.hukecn.fund;

/**
 * Created by Kelson on 2015/11/9.
 */
public class FundBean {
//    {
//        "fundcode": "320007",
//            "name": "诺安成长混合",
//            "gszzl": "-2.58",
//            "dwjz": "1.5360",
//            "gsz": "1.4964",
//            "gztime": "2020-05-28 11:19",
//            "jzrq": "2020-05-27"
//    }
    public String fundcode;               //代码
    public String name;             //基金名
    public String gszzl;     //基金涨幅
    public String dwjz;           //基金净值
    public String gsz;           //基金净值
    public String gztime;           //估算时间
    public String jzrq;           //净值时间

    public double income;
    public float money;

    public String getFundcode() {
        return fundcode;
    }

    public void setFundcode(String fundcode) {
        this.fundcode = fundcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGszzl() {
        return gszzl;
    }

    public void setGszzl(String gszzl) {
        this.gszzl = gszzl;
    }

    public String getDwjz() {
        return dwjz;
    }

    public void setDwjz(String dwjz) {
        this.dwjz = dwjz;
    }

    public String getGsz() {
        return gsz;
    }

    public void setGsz(String gsz) {
        this.gsz = gsz;
    }

    public String getGztime() {
        return gztime;
    }

    public void setGztime(String gztime) {
        this.gztime = gztime;
    }

    public String getJzrq() {
        return jzrq;
    }

    public void setJzrq(String jzrq) {
        this.jzrq = jzrq;
    }

    public double getIncome() {
        return income;
    }

    public void setIncome(long income) {
        this.income = income;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }
}
