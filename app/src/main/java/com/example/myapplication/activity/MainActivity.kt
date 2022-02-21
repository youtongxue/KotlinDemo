package com.example.myapplication.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.example.myapplication.*
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.util.Utils

class MainActivity : BaseActivity() {
    //视图绑定
    private lateinit var binding: ActivityMainBinding


    @SuppressLint("LongLogTag")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e("MainActivity is onCreate", this.toString())// check standard mode
        setContentView(R.layout.activity_main)

        //view binding 绑定视图
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ActivityCollector.addActivity(this)

        //ID直接绑定组件 test
        //Toast
        binding.button1.setOnClickListener { Toast.makeText(this,"直接绑定组件",Toast.LENGTH_LONG).show() }
        //Intent putExtra 向下一个Activity传参数
        binding.button2.setOnClickListener {
            val str: String = "这是上一个页面的数据"
            val intent = Intent(this, ScrollingActivity::class.java)
            intent.putExtra("extra_data", str) // "key" - value
            startActivity(intent)
        }
        //Intent startActivityForResult 结束当前Activity后返回数据给上一个Activity
        binding.button3.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivityForResult(intent, 1)
        }

        //this part is test for when this activity in onStop status can save data
        binding.button4.setOnClickListener {
            val intent = Intent(this, ScrollingActivity::class.java)
            startActivity(intent)

            if (savedInstanceState != null){
                binding.editTextTextPersonName2.setText(savedInstanceState.getString("data_key"))
                Log.e("ainactivity", savedInstanceState.getString("data_key"))
          }

        }

        // a standard mode demo, in this part the intent will jump to his own page
        binding.buttonStandardMode.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        //a singleTop mode demo
        binding.buttonSingelTop.setOnClickListener {
            val intent = Intent(this, FirstActivity::class.java)
            startActivity(intent)
        }

        //intent to extend baseActivity thirdActivity
        binding.buttonIntentToThirdActivity.setOnClickListener {
            val intent = Intent(this, ThirdActivity::class.java)
            startActivity(intent)
        }

        //3.6.3启动Activity的最佳写法
        binding.actionStartButton.setOnClickListener {
            ThirdActivity.actionStart(this, "调用 actionStart 启动，传入值")
        }

        //调用静态工具类 显示Toast
        binding.buttonUtilToast.setOnClickListener{
            Utils.utilToast(this, "调用静态工具类 显示Toast")
        }

        //跳转到UIWidgetActivity
        binding.buttonIntentUiwidget.setOnClickListener {
            val intent = Intent(this, UIWidgetActivity::class.java)
            startActivity(intent)
        }

        //显示弹窗对话框
        binding.buttonDialog.setOnClickListener {
            AlertDialog.Builder(this).apply {
                setTitle("This is a dialog")
                setMessage("You can show something in here")
                setCancelable(false)
                setPositiveButton("确定"){
                    dialog, which -> {

                }
                }
                setNegativeButton("取消"){
                    dialog, which ->
                }
                show()
            }
        }

        //跳转到LayoutDemo Activity
        binding.buttonLayout.setOnClickListener {
            val intent = Intent(this, LayoutDemoActivity::class.java)
            startActivity(intent)
        }

        //引入布局
        binding.buttonInclude.setOnClickListener {
            val intent = Intent(this, LayoutDemoActivity::class.java)
            startActivity(intent)
        }

        //ListView Demo
        binding.button11.setOnClickListener {
            val intent = Intent(this, ListViewActivity::class.java)
            startActivity(intent)
        }

        //RecyclerView Demo
        binding.buttonRecyclerview.setOnClickListener {
            val intent = Intent(this, RecyclerViewActivity::class.java)
            startActivity(intent)
        }

        //RecyclerView 水平滑动
        binding.buttonRecyclerviewHoriztonal.setOnClickListener {
            val intent = Intent(this, RecyclerViewHorizontalActivity::class.java)
            startActivity(intent)
        }

        //RecyclerView 瀑布流滑动
        binding.buttonRecyclerSg.setOnClickListener {
            val intent = Intent(this, RecyclerViewSGActivity::class.java)
            startActivity(intent)
        }

        //MessageDemo
        binding.buttonMessage.setOnClickListener {
            val intent = Intent(this, MessageActivity::class.java)
            startActivity(intent)
        }

        //
        binding.buttonRecItem.setOnClickListener {
            val intent = Intent(this, RecSelectActivity::class.java)
            startActivity(intent)
        }

        //Emoji 列子
        binding.buttonEmoji.setOnClickListener {
            val intent = Intent(this, EmojiActivity::class.java)
            startActivity(intent)
        }




    }

    //menu 显示菜单
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    //重写菜单选择方法
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //跳转到第二个 Activity
        val intent = Intent(this, ScrollingActivity::class.java)

        //打开系统浏览器，打开网页
        val intent1 = Intent(Intent.ACTION_VIEW)
        intent1.data = Uri.parse("https://www.baidu.com")

        //拨打电话
        val intent2 = Intent(Intent.ACTION_DIAL)
        intent2.data = Uri.parse("tel:10010")

        when(item.itemId){

            R.id.menu1 -> startActivity(intent)
            R.id.menu2 -> startActivity(intent1)
            R.id.menu3 -> startActivity(intent2)

        }
        return true
    }

    //override onActivityResult for get the extra what second activity send back
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode){
            1 -> if (resultCode == RESULT_OK){
                val returnData = data?.getStringExtra("data_return")
                binding.button3.text = "返回数据给上一个Activity $returnData"
            }
        }
    }

    override fun onSaveInstanceState(outState: Bundle, outPersistentState: PersistableBundle) {
        super.onSaveInstanceState(outState, outPersistentState)
        val data = binding.editTextTextPersonName2.text.toString()
        outState.putString("data_key", data)

    }

    //button功能
    fun buttonFunction(button: Button){

    }

}