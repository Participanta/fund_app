package cn.hukecn.fund

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

/**
 * Created by Kelson on 2015/12/5.
 */
class AppThreadPool private constructor() {
    //    public void execute(String url, AsyncHttp.HttpListener listener){
    val pool: ExecutorService

    //        if (pool != null)
    //        {
    //            pool.execute(new Thread(new AsyncHttp(url,listener)));
    //        }
    //    }
    //
    //    public void shutDown(){
    //        if(pool != null && !pool.isShutdown())
    //        {
    //            pool.shutdown();
    //            pool = null;
    //        }
    //    }
    companion object {
        private var ourInstance: AppThreadPool? = null
        private val pool: ExecutorService? = null
        val instance: AppThreadPool?
            get() {
                if (ourInstance == null) ourInstance = AppThreadPool()
                return ourInstance
            }
    }

    init {
        pool = Executors.newCachedThreadPool()
    }
}