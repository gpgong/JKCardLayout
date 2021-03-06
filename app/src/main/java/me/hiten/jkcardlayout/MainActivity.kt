package me.hiten.jkcardlayout

import android.content.Context
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_card.*
import me.hiten.jkcardlayout.library.CardLayoutHelper
import me.hiten.jkcardlayout.library.OnCardLayoutListener
import me.hiten.jkcardlayout.library.dp
import java.util.*


class MainActivity : AppCompatActivity() {


    private var list = ArrayList<CardEntity>()
    private var cardAdapter : CardAdapter? = null

    private lateinit var mCardLayoutHelper : CardLayoutHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setStatusBar()

        mCardLayoutHelper = CardLayoutHelper()

        mCardLayoutHelper.attachToRecyclerView(recycler_view)

        mCardLayoutHelper.bindDataSource(object : CardLayoutHelper.BindDataSource{
            override fun bind(): List<Any> {
                return list
            }
        })

        mCardLayoutHelper.setOnCardLayoutListener(object :OnCardLayoutListener{
            override fun onSwipe(dx: Float, dy: Float) {
                Log.d("onStateChanged","dx:$dx dy:$dy")
            }

            override fun onStateChanged(state: CardLayoutHelper.State) {
                Log.d("onStateChanged",state.name)

            }

        })

        cardAdapter = CardAdapter(list)

        recycler_view.adapter =cardAdapter


        MockData.getCards(this) {
            val cards = it["cards"]
            if (cards is List<*>) {
                list.clear()
                list.addAll(cards as List<CardEntity>)
                cardAdapter?.notifyDataSetChanged()
            }
            val toolbarItems = it["toolbarItems"]
            if (toolbarItems is List<*>){
                val items = toolbarItems as List<ToolBarEntity>
                if (!items.isEmpty()){
                    layout_top_menu.removeAllViews()
                    for (item in items){
                        val linearLayout = LinearLayout(this)
                        linearLayout.gravity  = Gravity.CENTER
                        linearLayout.orientation = LinearLayout.VERTICAL
                        val iv = ImageView(this)
                        val layoutParams1 = LinearLayout.LayoutParams(66.dp, 66.dp)
                        layoutParams1.bottomMargin = 10.dp
                        linearLayout.addView(iv,layoutParams1)
                        Glide.with(this).load(item.picUrl).circleCrop().into(iv)

                        val tv = TextView(this)
                        tv.textSize = 12f
                        tv.setTextColor(Color.parseColor("#333333"))
                        tv.text = item.title
                        tv.gravity = Gravity.CENTER
                        linearLayout.addView(tv)

                        val layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT)
                        layoutParams.weight =1f
                        layout_top_menu.addView(linearLayout,layoutParams)
                        linearLayout.setOnClickListener {
                            Toast.makeText(this,item.title,Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }



        btn_prev.setOnClickListener {
            if (mCardLayoutHelper.canBack()){
                mCardLayoutHelper.doBack()
            }
        }

        btn_next.setOnClickListener {
            onNextPressed()
        }

        btn_menu.setOnClickListener {
            pull_down_layout.openMenu()
        }
    }

    private fun setStatusBar(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window.statusBarColor = Color.TRANSPARENT
            pull_down_layout.setPadding(pull_down_layout.paddingLeft,getStatusBarHeight(this),pull_down_layout.paddingRight,pull_down_layout.paddingBottom)
        }
    }

    private fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        val resourceId = context.resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
        }
        return statusBarHeight
    }

    private fun onNextPressed(){
        if (mCardLayoutHelper.canNext()) {
            mCardLayoutHelper.doNext()
        }
    }

    override fun onBackPressed() {
        if (mCardLayoutHelper.canBack()){
            mCardLayoutHelper.doBack()
        }else if (mCardLayoutHelper.noBack()){
            super.onBackPressed()
        }
    }

}
