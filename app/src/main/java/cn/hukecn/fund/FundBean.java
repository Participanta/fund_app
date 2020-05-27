package cn.hukecn.fund;

/**
 * Created by Kelson on 2015/11/9.
 */
public class FundBean {
    public String id;               //代码
    public String name;             //基金名
    public String pecent_value;     //基金涨幅
    public String fundpz;           //基金净值
    public String gztime;           //估算时间
    public float income;
    public float money;


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

    public String getPecent_value() {
        return pecent_value;
    }

    public void setPecent_value(String pecent_value) {
        this.pecent_value = pecent_value;
    }

    public String getFundpz() {
        return fundpz;
    }

    public void setFundpz(String fundpz) {
        this.fundpz = fundpz;
    }

    public String getGztime() {
        return gztime;
    }

    public void setGztime(String gztime) {
        this.gztime = gztime;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }
}
