package cn.hukecn.bean;

public class OptionalFundItem {

    /**
     * fund_code : 000066
     * fund_en : NAHXHH
     * fund_name : 诺安鸿鑫混合
     * fund_type : 混合型
     * fund_py : NUOANHONGXINHUNHE
     */

    private String fund_code;
    private String fund_name;
    private String open_id;
    private float money;

    public String getFund_code() {
        return fund_code;
    }

    public void setFund_code(String fund_code) {
        this.fund_code = fund_code;
    }

    public String getFund_name() {
        return fund_name;
    }

    public void setFund_name(String fund_name) {
        this.fund_name = fund_name;
    }

    public String getOpen_id() {
        return open_id;
    }

    public void setOpen_id(String open_id) {
        this.open_id = open_id;
    }

    public float getMoney() {
        return money;
    }

    public void setMoney(float money) {
        this.money = money;
    }
}
