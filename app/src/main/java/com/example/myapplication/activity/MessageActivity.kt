package com.example.myapplication.activity

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.os.*
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.helper.widget.MotionEffect.TAG
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myapplication.R
import com.example.myapplication.adapter.EmojiMiniAdapter
import com.example.myapplication.adapter.MsgAdapterBinding
import com.example.myapplication.bean.Msg
import com.example.myapplication.databinding.ActivityMessageBinding
import com.example.myapplication.fragment.EmojiOneFragment
import com.example.myapplication.fragment.EmojiTwoFragment
import com.example.myapplication.myinterface.EmojiInterFace
import com.example.myapplication.util.KeyBoardListenerHelper
import com.example.myapplication.util.KeyBoardListenerHelper.OnKeyBoardChangeListener
import com.example.myapplication.util.Keybords
import com.example.myapplication.util.Utils
import com.google.gson.Gson
import com.gyf.immersionbar.ImmersionBar
import org.eclipse.paho.android.service.MqttAndroidClient
import org.eclipse.paho.client.mqttv3.*


open class MessageActivity : AppCompatActivity(), View.OnClickListener, EmojiInterFace {
    private lateinit var mqttAndroidClient: MqttAndroidClient

    /* 自动Topic, 用于上报消息 */
    private val PUB_TOPIC = "chat"

    /* 自动Topic, 用于接受消息 */
    private val SUB_TOPIC = "chat"

    val host = "tcp://1.14.68.248:1883"
    //private val clientId = "kotlin_test"
    val author = Utils.getDeviceBrand().toString() + " " + Utils.getDeviceModel() //获取设备唯一信息，保证每台客户端，有"唯一"的ID号
    private val userName = "username"
    private val passWord = "1234561"

    private lateinit var binding: ActivityMessageBinding
    private val msgList = ArrayList<Msg>()
    //private var adapter: MsgAdapter? = null
    private var adapter: MsgAdapterBinding? = null
    val type = 1
    private var pull = false
    private var down = true
    private var keyBordH = 0
    var emojiMiniList: MutableList<Drawable> = ArrayList() //EmojiMini 图片
    var temp = 0 //记录上次选中位置
    var emoji = false //记录上次
    lateinit var el: LinearLayout
    lateinit var input: RelativeLayout


    //接收到消息
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            //执行UI更新操作
            when (msg.what) {
                type -> {
                    //获取到消息
                    val bundle = msg.data
                    val content = bundle.getString("recMsg")
                    val msg = Msg(content.toString(), Msg.TYPE_RECEIVED, author)
                    msgList.add(msg)

                    val msgLast = msgList.last()
                    Log.e("Msg","MsgList消息集合 ： > > > ${msgLast.content}")

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        //绑定视图
        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        el = binding.emojiLinear
        input = binding.inputRelative

        //状态栏，底部输入框管理
        clear()
        setMargin()

        /**
         * @Description 初始化，连接， 界面
         * */
        initMsg() //本地消息
        MqttInit() //Mqtt连接
        binding.cardMessageSend.visibility = View.GONE //设置 发送 按钮默认隐藏
        inputEdit() // 文本输入框状态监听
        initKeyBord() //键盘监听
        touchRecycler()//recyclerView监听

        val layoutManager = LinearLayoutManager(this)
        binding.messageRecyclerview.layoutManager = layoutManager
        adapter = MsgAdapterBinding(msgList)
        binding.messageRecyclerview.adapter = adapter
        //发送按钮监听事件
        binding.cardMessageSend.setOnClickListener(this)
        binding.imageViewSmell.setOnClickListener(this)

        /**
         * @Description 表情选择
         * */
        initEmojiMini()//初始化底部 emoji 预览数据
        initFragment()//初始化，表情选择的 fragment 页面
        choseEmoji()


    }



    override fun onClick(v: View?){

        when (v) {
            binding.cardMessageSend -> {
                val content = binding.EditTextTextMessage.text.toString()
                if (content.isNotEmpty()){
                    //App.MqttInit(content)//Mqtt 发送消息

                    val msg = Msg(content, Msg.TYPE_SENT, author) //0
                    val gson = Gson()
                    val jsonContent = gson.toJson(msg,Msg::class.java)
                    publishMessage(jsonContent)

                    msgList.add(msg)
                    adapter?.notifyItemInserted(msgList.size - 1)
                    binding.messageRecyclerview.smoothScrollToPosition(msgList.size - 1)
                    binding.EditTextTextMessage.text.clear()

                    Utils.utilToast(this,"发送 ${msg.content}")
                }
            }
            binding.imageViewSmell -> {
                Utils.utilToast(this,"选择表情")
                //Keybords.openKeybord(binding.EditTextTextMessage, this)
                //设置默认滚动到底部
                binding.messageRecyclerview.smoothScrollToPosition(msgList.size - 1)

            }

        }
    }


    //消息数据
        fun initMsg() {
            val msg1 = Msg("Hello, nice to meet you!", Msg.TYPE_RECEIVED, author)
            msgList.add(msg1)
        }

    /**
     * mqtt test
     */
    fun MqttInit() {
        /* 创建MqttConnectOptions对象并配置username和password */
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isCleanSession = false //告诉服务器，断开链接时，不要清除session
        mqttConnectOptions.connectionTimeout = 10
        mqttConnectOptions.keepAliveInterval = 15
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.userName = userName
        mqttConnectOptions.password = passWord.toCharArray()


        /* 创建MqttAndroidClient对象, 并设置回调接口 */
        mqttAndroidClient = MqttAndroidClient(applicationContext, host, author) //这里的和上次访问的ID需要一致
        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {

            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                val content = String(message.payload)
                Log.e(TAG,"接收到消息 >>>  $content")


                val gson = Gson()
                val msg: Msg = gson.fromJson(content, Msg::class.java)

                if (msg.type == 1 && msg.author != author) {
                    Utils.utilToast(this@MessageActivity, "收到消息 : $content")

                    val ms = Msg(msg.content, Msg.TYPE_RECEIVED, author)
                    msgList.add(ms)
                    adapter?.notifyItemInserted(msgList.size - 1)
                    binding.messageRecyclerview.smoothScrollToPosition(msgList.size - 1)
                }

            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })

        /* Mqtt建连 */
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i(TAG, "connect succeed")
                    subscribeTopic(SUB_TOPIC)
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i(TAG, "connect failed")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    /**
     * 订阅特定的主题
     * @param topic mqtt主题
     */
    fun subscribeTopic(topic: String?) {
        try {
            mqttAndroidClient.subscribe(topic, 1, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i(TAG, "subscribed succeed")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i(TAG, "subscribed failed")
                }
            })
        } catch (e: MqttException) {
            e.printStackTrace()
        }
    }

    /**
     * 向默认的主题发布消息
     * @param payload 消息载荷
     */
    fun publishMessage(payload: String) {
        try {
            if (!mqttAndroidClient.isConnected) {
                mqttAndroidClient.connect()
            }
            val message = MqttMessage()
            message.payload = payload.toByteArray()
            message.qos = 0
            mqttAndroidClient.publish(PUB_TOPIC, message, null, object : IMqttActionListener {
                override fun onSuccess(asyncActionToken: IMqttToken) {
                    Log.i(TAG, "publish succeed!")
                }

                override fun onFailure(asyncActionToken: IMqttToken, exception: Throwable) {
                    Log.i(TAG, "publish failed!")
                }
            })
        } catch (e: MqttException) {
            Log.e(TAG, e.toString())
            e.printStackTrace()
        }
    }

    /**
     * @Description inputEditText 组件的输入状态监听
     * */
    fun inputEdit() {
        binding.EditTextTextMessage.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                //输入监听
                if (s.toString().isNotEmpty()) {
                    binding.cardMessageSend.visibility = View.VISIBLE
                    binding.imageViewMore.visibility = View.GONE


                    //将dp 转换为 px 代码动态设置 输入控件 的 右 margin值
                    val px = Utils.dp2px(this@MessageActivity, 84.0)
                    val cardInput = binding.cardInput
                    val params: RelativeLayout.LayoutParams = cardInput.getLayoutParams() as RelativeLayout.LayoutParams
                    //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
                    params.rightMargin = px.toInt()
                    cardInput.layoutParams = params //使layout更新

                } else {
                    binding.cardMessageSend.visibility = View.GONE
                    binding.imageViewMore.visibility = View.VISIBLE

                    val cardInput = binding.cardInput
                    val params: RelativeLayout.LayoutParams = cardInput.getLayoutParams() as RelativeLayout.LayoutParams
                    params.rightMargin = 0
                    cardInput.layoutParams = params //使layout更新

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    /**
     * recyclerView触摸监听
     * */
    fun touchRecycler() {
        binding.messageRecyclerview.setOnTouchListener(object : View.OnTouchListener{
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Keybords.closeKeybord(binding.EditTextTextMessage, this@MessageActivity)
                return false
            }

        })
    }

    fun initKeyBord(){
        val keyBoardListenerHelper = KeyBoardListenerHelper(this)
        keyBoardListenerHelper.setOnKeyBoardChangeListener(OnKeyBoardChangeListener {
                isShow, keyBoardHeight ->
            //此处可以根据是否显示软键盘，以及软键盘的高度处理逻辑

            Log.e("1234", ">>>>>> >>> isShow: $isShow keyBoardHeight:$keyBoardHeight pull : $pull  keyBord : $keyBordH")
            if (isShow){
                //down = true
                pull = false
                if (keyBordH != keyBoardHeight){
                    Utils.utilToast(this," emoji : $emoji")
                    if (emoji){
                        Utils.utilToast(this," emoji : 调用")
                        val params: RelativeLayout.LayoutParams = el.getLayoutParams() as RelativeLayout.LayoutParams
                        params.bottomMargin = -(Utils.dp2px(this, 340.5).toInt())
                        el.layoutParams = params //使layout更新
                        emoji = false
                    }
                    if (!pull) {

                        keyBordH = keyBoardHeight
                        pull = true
                        //down = false
                        Utils.utilToast(this, "键盘弹出 ： +$keyBoardHeight")

                        //拉起布局,输入框
                        val params: RelativeLayout.LayoutParams = input.getLayoutParams() as RelativeLayout.LayoutParams
                        //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
                        params.bottomMargin = keyBoardHeight
                        input.layoutParams = params //使layout更新
                        pull = true

                        binding.messageRecyclerview.smoothScrollToPosition(msgList.size)
                    }
                }

            }else if (!pull){

                if (keyBordH != 0){
                    //down = true
                    pull = false
                    keyBordH = 0
                    Log.e("tag", " >>> >>> >>> 键盘收起")
                    Utils.utilToast(this, "键盘收起 ： +$keyBoardHeight  emoji : $emoji")

                    //键盘展开，点击表情选择
                    if (!emoji){
                        //收起input输入框
                        val params: RelativeLayout.LayoutParams = input.getLayoutParams() as RelativeLayout.LayoutParams
                        //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
                        params.bottomMargin = 0
                        input.layoutParams = params //使layout更新
                        pull = false
                    }

                    //binding.messageRecyclerview.smoothScrollToPosition(msgList.size)
                }

            }

        })
    }

    /**
     * 获取状态栏高度，设置layout的margin——top值
     *
     */
    fun setMargin() {
        //获取状态栏高度
        var statusBarHeight1 = 0
        //获取status_bar_height资源的ID
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = resources.getDimensionPixelSize(resourceId)
        }
        Log.e("TAG", "方法1状态栏高度:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>$statusBarHeight1")

        val title = binding.relativeLayout2
        val params: ConstraintLayout.LayoutParams = title.getLayoutParams() as ConstraintLayout.LayoutParams
        //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
        params.topMargin = statusBarHeight1
        title.layoutParams = params //使layout更新
    }

    /**
     * 设置状态栏状态，颜色，底部虚拟导航栏颜色
     */
    fun clear() {
        ImmersionBar.with(this)
            .statusBarColor(R.color.white)
            .navigationBarColor(R.color.white)
            .statusBarDarkFont(true) //状态栏字体是深色，不写默认为亮色
            .init()

        //隐藏action bar
        val actionBar = supportActionBar!!
        actionBar!!.hide()
    }


    override fun onStart() {
        Log.e("TAG>>>>>>>>", "-->onStart")
        super.onStart()
    }

    override fun onResume() {
        binding.cardInput.setFocusable(true)
        binding.cardInput.setFocusableInTouchMode(true)
        binding.cardInput.requestFocus()
        Log.e("TAG>>>>>>>>", "-->on1")
        super.onResume()
    }

    override fun onPause() {
        Log.e("TAG>>>>>>>>", "-->on2")
        super.onPause()
    }

    override fun onStop() {
        Log.e("TAG>>>>>>>>", "-->on3")
        super.onStop()
    }

    /**
     * @Description 表情选择
     * */
    //初始化底部 emoji 预览数据
    fun  initEmojiMini() {
        //设置 EmojiMiniRecyclerView的 布局方式（方向）
        val layManager = LinearLayoutManager(this)//实例化 LayoutManager
        layManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.emojiminiRcy.layoutManager = layManager

        //设置adapter
        emojiMiniList.add(getDrawable(R.drawable.ic_app)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_orange)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_grape)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_pear)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_watermelon)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_banana)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_app)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_orange)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_grape)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_pear)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_watermelon)!!)
        emojiMiniList.add(getDrawable(R.drawable.ic_banana)!!)
        val adapter = EmojiMiniAdapter(this, emojiMiniList)
        binding.emojiminiRcy.setAdapter(adapter)

        //RecyclerView Item刷新数据动画
        //(recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false//取消切换动画
        (binding.emojiminiRcy.itemAnimator as DefaultItemAnimator).removeDuration = 2

        //底部 emoji 预览 RecyclerView Item点击监听
        adapter.setOnMyItemClickListener(object : EmojiMiniAdapter.OnMyItemClickListener{
            override fun myClick(pos: Int) {
                Utils.utilToast(this@MessageActivity,"点击 ： $pos")
                binding.emojiViewpager.currentItem = pos//ViewPager滑动到指定页面， 在 ViewPager 的滑动监听回调方法 OnPageChangeCallback -> 做RecyclerView Item修改背景操作
                binding.emojiminiRcy.smoothScrollToPosition(pos+1)//RecyclerView滑动到选择项
                temp = pos//设置当前选中页，在refreshBg方法中 更新单个Item -> notifyItemChange() 需要，若 全部更新 -> notifyDataSetChanged() 则不需要设置

            }

        })

        //ViewPager 滑动监听回调
        binding.emojiViewpager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.e(" >>    >>", "被调用")
                Utils.utilToast(this@MessageActivity, "滑动到 $position 页")
                adapter.refreshBg(position, temp)//向RecyclerView Adapter传入当前ViewPager选中页位置
                temp = position
                binding.emojiminiRcy.smoothScrollToPosition(position+1)
            }

            //删除了另两个方法 override fun onPageScrollStateChanged , override fun onPageScrolled
        })
    }

    //初始化，表情选择的 fragment 页面
    fun initFragment() {
        val list: MutableList<Fragment> = ArrayList()
        list.add(EmojiOneFragment())
        list.add(EmojiTwoFragment())
        //设置预加载的Fragment页面数量，可防止流式布局StaggeredGrid数组越界错误。
        //binding.emojiViewpager.setOffscreenPageLimit(list.size - 1) //设置适配器
        val adapter: FragmentStateAdapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return list[position]
            }

            override fun getItemCount(): Int {
                return list.size
            }
        }
        binding.emojiViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.emojiViewpager.setAdapter(adapter) //把适配器添加给ViewPager2
    }

    //选择某个表情的监听
    override fun onEmojiClick(text: String?) {
        Utils.utilToast(this, text.toString())
        binding.EditTextTextMessage.setText(text)
    }

    //是否弹出选择表情
    fun choseEmoji() {
        val params: RelativeLayout.LayoutParams = el.getLayoutParams() as RelativeLayout.LayoutParams
        params.bottomMargin = -(Utils.dp2px(this, 340.5).toInt())
        el.layoutParams = params //使layout更新
        emoji = false
        binding.imageViewSmell.setOnClickListener {
            Utils.utilToast(this,"点击表情 键盘：$pull emoji ： $emoji")
//            if (!pull){
                Keybords.closeKeybord(binding.EditTextTextMessage,this)
                pull = false
                //down = true
                if (!emoji){
                    Utils.utilToast(this,"拉起 emoji")

                    //拉起布局,输入框
                    val param: RelativeLayout.LayoutParams = input.getLayoutParams() as RelativeLayout.LayoutParams
                    //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
                    param.bottomMargin = Utils.dp2px(this, 340.5).toInt()
                    input.layoutParams = param //使layout更新


                    params.bottomMargin = 0
                    el.layoutParams = params //使layout更新
                    emoji = true

                    binding.imageViewSmell.setImageDrawable(getDrawable(R.drawable.ic_keyborad))
                }else {
                    Keybords.openKeybord(binding.EditTextTextMessage, this)
                    emoji = false
                    params.bottomMargin = -(Utils.dp2px(this, 340.5).toInt())
                    el.layoutParams = params //使layout更新
                    emoji = false

                    binding.imageViewSmell.setImageDrawable(getDrawable(R.drawable.ic_smell))
                }


        }
    }


}

