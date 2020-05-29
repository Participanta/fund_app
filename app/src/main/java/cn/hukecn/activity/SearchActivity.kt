package cn.hukecn.activity

import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import cn.hukecn.adapter.SearchListRecyclerViewAdapter
import cn.hukecn.base.AppBaseActivity
import cn.hukecn.bean.FundItem
import cn.hukecn.fund.R
import cn.hukecn.network.FundRetrofitHolder
import cn.hukecn.network.FundService
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.chad.library.adapter.base.BaseQuickAdapter
import com.jaeger.library.StatusBarUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.*

class SearchActivity : AppBaseActivity() {
    var fundItemList: MutableList<FundItem> = ArrayList()

    @JvmField
    @BindView(R.id.edit_search)
    var editSearch: EditText? = null

    @JvmField
    @BindView(R.id.tv_cancel)
    var tvCancel: TextView? = null

    @JvmField
    @BindView(R.id.recyclerview_search)
    var recyclerViewSearch: RecyclerView? = null
    private var searchListRecyclerViewAdapter: SearchListRecyclerViewAdapter? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tvCancel!!.setOnClickListener { finish() }
        editSearch!!.requestFocus()
        initRecyclerView()
        editSearch!!.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                searchFund(editSearch!!.text.toString())
            }
        })
    }

    private fun initRecyclerView() {
        recyclerViewSearch!!.layoutManager = LinearLayoutManager(this)
        recyclerViewSearch!!.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        searchListRecyclerViewAdapter = SearchListRecyclerViewAdapter(R.layout.fund_search_recycler_item, fundItemList)
        recyclerViewSearch!!.adapter = searchListRecyclerViewAdapter
        searchListRecyclerViewAdapter!!.onItemChildClickListener = BaseQuickAdapter.OnItemChildClickListener { adapter, view, position ->
            val fundItem = fundItemList[position]
            showEditDialog(fundItem)
        }
    }

    private fun showEditDialog(fundItem: FundItem) {
        val type = InputType.TYPE_CLASS_NUMBER
        MaterialDialog(this).show {
            title(R.string.remind)
            input(waitForPositiveButton = true,hint = "请输入购买金额",inputType = type) { dialog, text ->
                var money = text.toString().toFloat()
                addOptionalFund(fundItem,money)
            }
        }
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_search
    }

    private fun searchFund(name: String) {
        FundRetrofitHolder.getRetrofit()
                .create(FundService::class.java)
                .searchFund(name)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ listTemplate ->
                    fundItemList.clear()
                    fundItemList.addAll(listTemplate.data!!)
                    searchListRecyclerViewAdapter!!.notifyDataSetChanged()
                }) { }
    }

    private fun addOptionalFund(fundItem: FundItem,money : Float) {
        val params: MutableMap<String, Any> = HashMap()
        params["open_id"] = "2013551128"
        params["fund_code"] = fundItem.fund_code
        params["fund_name"] = fundItem.fund_name
        params["fund_money"] = money
        FundRetrofitHolder.getRetrofit()
                .create(FundService::class.java)
                .addOptionalFund(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ Toast.makeText(this@SearchActivity, "添加成功", Toast.LENGTH_SHORT).show() }) { }
    }

    companion object {
        private const val TAG = "SearchActivity"
    }
}