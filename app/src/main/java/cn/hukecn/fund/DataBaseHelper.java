package cn.hukecn.fund;

import cn.hukecn.MyApplication;

public class DataBaseHelper {
    private MyDataBase daoSession;
    private static DataBaseHelper instance;

    private DataBaseHelper(){
        initDataBase();
    }

    public static DataBaseHelper getInstance(){
        if(instance == null){
            return new DataBaseHelper();
        }

        return instance;
    }

    /**
     * 初始化GreenDao,直接在Application中进行初始化操作
     */
    private void initDataBase() {
        //创建数据库greendao_use.db"
        //获取Dao对象管理者
        daoSession = new MyDataBase(MyApplication.getInstance().getApplicationContext());
    }

    public MyDataBase getDataBase() {
        return daoSession;
    }
}
