package com.example.myapplication.activity

import android.annotation.SuppressLint
import android.app.Notification.VISIBILITY_PRIVATE
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
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
import androidx.core.app.NotificationCompat
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
import kotlin.math.log


open class MessageActivity : AppCompatActivity(), View.OnClickListener, EmojiInterFace {
    private lateinit var mqttAndroidClient: MqttAndroidClient

    /* ??????Topic, ?????????????????? */
    private val PUB_TOPIC = "chat"

    /* ??????Topic, ?????????????????? */
    private val SUB_TOPIC = "chat"

    val host = "tcp://1.14.68.248:1883"

    //private val clientId = "kotlin_test"
    val author = Utils.getDeviceBrand()
        .toString() + " " + Utils.getDeviceModel() //??????????????????????????????????????????????????????"??????"???ID???
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
    var emojiMiniList: MutableList<Drawable> = ArrayList() //EmojiMini ??????
    var temp = 0 //????????????????????????
    var emoji = false //????????????
    lateinit var el: LinearLayout
    lateinit var input: RelativeLayout
    lateinit var manager: NotificationManager
    var displayNotification = true


    //???????????????
    private val handler = object : Handler(Looper.getMainLooper()) {
        override fun handleMessage(msg: Message) {
            //??????UI????????????
            when (msg.what) {
                type -> {
                    //???????????????
                    val bundle = msg.data
                    val content = bundle.getString("recMsg")
                    val msg = Msg(content.toString(), Msg.TYPE_RECEIVED, author)
                    msgList.add(msg)

                    val msgLast = msgList.last()
                    Log.e("Msg", "MsgList???????????? ??? > > > ${msgLast.content}")

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_message)

        //????????????
        binding = ActivityMessageBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        el = binding.emojiLinear
        input = binding.inputRelative

        //?????????????????????????????????
        clear()
        setMargin()

        /**
         * @Description ????????????????????? ??????
         * */
        initMsg() //????????????
        mqttInit() //Mqtt??????
        binding.cardMessageSend.visibility = View.GONE //?????? ?????? ??????????????????
        inputEdit() // ???????????????????????????
        initKeyBord() //????????????
        touchRecycler()//recyclerView??????

        val layoutManager = LinearLayoutManager(this)
        binding.messageRecyclerview.layoutManager = layoutManager
        adapter = MsgAdapterBinding(msgList)
        binding.messageRecyclerview.adapter = adapter
        //????????????????????????
        binding.cardMessageSend.setOnClickListener(this)
        binding.imageViewSmell.setOnClickListener(this)

        /**
         * @Description ????????????
         * */
        initEmojiMini()//??????????????? emoji ????????????
        initFragment()//??????????????????????????? fragment ??????
        choseEmoji()

        //notification
        initNotification()

        binding.imageView5Back.setOnClickListener {
            finish()
        }

    }


    override fun onClick(v: View?) {

        when (v) {
            binding.cardMessageSend -> {
                val content = binding.EditTextTextMessage.text.toString()
                if (content.isNotEmpty()) {
                    //App.MqttInit(content)//Mqtt ????????????

                    val msg = Msg(content, Msg.TYPE_SENT, author) //0
                    val gson = Gson()
                    val jsonContent = gson.toJson(msg, Msg::class.java)
                    publishMessage(jsonContent)

                    msgList.add(msg)
                    adapter?.notifyItemInserted(msgList.size - 1)
                    binding.messageRecyclerview.smoothScrollToPosition(msgList.size - 1)
                    binding.EditTextTextMessage.text.clear()

                    Utils.utilToast(this, "?????? ${msg.content}")
                }
            }
            binding.imageViewSmell -> {
                Utils.utilToast(this, "????????????")
                //Keybords.openKeybord(binding.EditTextTextMessage, this)
                //???????????????????????????
                binding.messageRecyclerview.smoothScrollToPosition(msgList.size - 1)

            }

        }
    }


    //????????????
    private fun initMsg() {
        val msg1 = Msg("Hello, nice to meet you!", Msg.TYPE_RECEIVED, author)
        msgList.add(msg1)
    }

    /**
     * mqtt init
     */
    private fun mqttInit() {
        /* ??????MqttConnectOptions???????????????username???password */
        val mqttConnectOptions = MqttConnectOptions()
        mqttConnectOptions.isCleanSession = false //????????????????????????????????????????????????session
        mqttConnectOptions.connectionTimeout = 10
        mqttConnectOptions.keepAliveInterval = 15
        mqttConnectOptions.isAutomaticReconnect = true
        mqttConnectOptions.userName = userName
        mqttConnectOptions.password = passWord.toCharArray()


        /* ??????MqttAndroidClient??????, ????????????????????? */
        mqttAndroidClient = MqttAndroidClient(applicationContext, host, author) //???????????????????????????ID????????????
        mqttAndroidClient.setCallback(object : MqttCallback {
            override fun connectionLost(cause: Throwable) {

            }

            override fun messageArrived(topic: String, message: MqttMessage) {
                val content = String(message.payload)
                Log.e(TAG, "??????????????? >>>  $content")


                val gson = Gson()
                val msg: Msg = gson.fromJson(content, Msg::class.java)

                if (msg.type == 1 && msg.author != author) {
                    Utils.utilToast(this@MessageActivity, "???????????? : $content")

                    //notification ????????????Activity?????????????????????????????????????????????????????????onResume?????????????????????????????????????????????????????????onResume?????????????????????boolean?????????Bug
                    //????????????????????????Activity?????????????????????
                    if (displayNotification){
                        createNotification(msg.content, msg.author)
                    }


                    val ms = Msg(msg.content, Msg.TYPE_RECEIVED, author)
                    msgList.add(ms)
                    adapter?.notifyItemInserted(msgList.size - 1)
                    binding.messageRecyclerview.smoothScrollToPosition(msgList.size - 1)

                }

            }

            override fun deliveryComplete(token: IMqttDeliveryToken) {

            }
        })

        /* Mqtt?????? */
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
     * ?????????????????????
     * @param topic mqtt??????
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
     * ??????????????????????????????
     * @param payload ????????????
     */
    private fun publishMessage(payload: String) {
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
     * @Description inputEditText ???????????????????????????
     * */
    private fun inputEdit() {
        binding.EditTextTextMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                //????????????
                if (s.toString().isNotEmpty()) {
                    binding.cardMessageSend.visibility = View.VISIBLE
                    binding.imageViewMore.visibility = View.GONE


                    //???dp ????????? px ?????????????????? ???????????? ??? ??? margin???
                    val px = Utils.dp2px(this@MessageActivity, 84.0)
                    val cardInput = binding.cardInput
                    val params: RelativeLayout.LayoutParams = cardInput.layoutParams as RelativeLayout.LayoutParams
                    //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
                    params.rightMargin = px.toInt()
                    cardInput.layoutParams = params //???layout??????

                } else {
                    binding.cardMessageSend.visibility = View.GONE
                    binding.imageViewMore.visibility = View.VISIBLE

                    val cardInput = binding.cardInput
                    val params: RelativeLayout.LayoutParams = cardInput.layoutParams as RelativeLayout.LayoutParams
                    params.rightMargin = 0
                    cardInput.layoutParams = params //???layout??????

                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })
    }

    /**
     * recyclerView????????????
     * */
    @SuppressLint("ClickableViewAccessibility")
    fun touchRecycler() {
        binding.messageRecyclerview.setOnTouchListener(object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                Keybords.closeKeybord(binding.EditTextTextMessage, this@MessageActivity)
                return false
            }

        })
    }

    private fun initKeyBord() {
        val keyBoardListenerHelper = KeyBoardListenerHelper(this)
        keyBoardListenerHelper.setOnKeyBoardChangeListener(OnKeyBoardChangeListener { isShow, keyBoardHeight ->
            //??????????????????????????????????????????????????????????????????????????????

            Log.e(
                "1234",
                ">>>>>> >>> isShow: $isShow keyBoardHeight:$keyBoardHeight pull : $pull  keyBord : $keyBordH"
            )
            if (isShow) {
                //down = true
                pull = false
                if (keyBordH != keyBoardHeight) {
                    Utils.utilToast(this, " emoji : $emoji")
                    if (emoji) {
                        Utils.utilToast(this, " emoji : ??????")
                        val params: RelativeLayout.LayoutParams =
                            el.layoutParams as RelativeLayout.LayoutParams
                        params.bottomMargin = -(Utils.dp2px(this, 340.5).toInt())
                        el.layoutParams = params //???layout??????
                        emoji = false
                    }
                    if (!pull) {
                        //????????????????????????,????????????
                        keyBordH = keyBoardHeight
                        pull = true
                        Utils.utilToast(this, "???????????? ??? +$keyBoardHeight")

                        //????????????,?????????
                        val params: RelativeLayout.LayoutParams = input.layoutParams as RelativeLayout.LayoutParams
                        //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
                        params.bottomMargin = keyBoardHeight
                        input.layoutParams = params //???layout??????

                        //??????????????????????????????recyclerView ??????????????????
                        binding.messageRecyclerview.smoothScrollToPosition(msgList.size)
                    }
                }

            } else if (!pull) {

                if (keyBordH != 0) {
                    //down = true
                    pull = false
                    keyBordH = 0
                    Log.e("tag", " >>> >>> >>> ????????????")
                    Utils.utilToast(this, "???????????? ??? +$keyBoardHeight  emoji : $emoji")

                    //?????????????????????????????????
                    if (!emoji) {
                        //??????input?????????
                        val params: RelativeLayout.LayoutParams = input.layoutParams as RelativeLayout.LayoutParams
                        //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
                        params.bottomMargin = 0
                        input.layoutParams = params //???layout??????
                        pull = false
                    }

                    //binding.messageRecyclerview.smoothScrollToPosition(msgList.size)
                }

            }

        })
    }

    /**
     * ??????????????????????????????layout???margin??????top???
     *
     */
    private fun setMargin() {
        //?????????????????????
        var statusBarHeight1 = 0
        //??????status_bar_height?????????ID
        val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
            //????????????ID????????????????????????
            statusBarHeight1 = resources.getDimensionPixelSize(resourceId)
        }
        Log.e("TAG", "??????1???????????????:>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>$statusBarHeight1")

        val title = binding.relativeLayout2
        val params: ConstraintLayout.LayoutParams = title.layoutParams as ConstraintLayout.LayoutParams
        //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
        params.topMargin = statusBarHeight1
        title.layoutParams = params //???layout??????
    }

    /**
     * ????????????????????????????????????????????????????????????
     */
    private fun clear() {
        ImmersionBar.with(this)
            .statusBarColor(R.color.white)
            .navigationBarColor(R.color.white)
            .statusBarDarkFont(true) //????????????????????????????????????????????????
            .init()

        //??????action bar
        val actionBar = supportActionBar!!
        actionBar.hide()
    }


    override fun onStart() {
        Log.e("TAG>>>>>>>>", "-->onStart")
        super.onStart()
    }

    override fun onResume() {
        //focusable
        binding.cardInput.isFocusable = true
        binding.cardInput.isFocusableInTouchMode = true
        binding.cardInput.requestFocus()

        //set the displayNotification status
        displayNotification = false

        Log.e("TAG>>>>>>>>", "-->on1")
        super.onResume()
    }

    override fun onPause() {
        Log.e("TAG>>>>>>>>", "-->on2")
        displayNotification = true
        super.onPause()
    }

    override fun onStop() {
        Log.e("TAG>>>>>>>>", "-->on3")
        super.onStop()
    }


    /**
     * @Description ????????????
     * */
    //??????????????? emoji ????????????
    private fun initEmojiMini() {
        //?????? EmojiMiniRecyclerView??? ????????????????????????
        val layManager = LinearLayoutManager(this)//????????? LayoutManager
        layManager.orientation = LinearLayoutManager.HORIZONTAL
        binding.emojiminiRcy.layoutManager = layManager

        //??????adapter
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
        binding.emojiminiRcy.adapter = adapter

        //RecyclerView Item??????????????????
        //(recyclerView.itemAnimator as DefaultItemAnimator).supportsChangeAnimations = false//??????????????????
        (binding.emojiminiRcy.itemAnimator as DefaultItemAnimator).removeDuration = 2

        //?????? emoji ?????? RecyclerView Item????????????
        adapter.setOnMyItemClickListener(object : EmojiMiniAdapter.OnMyItemClickListener {
            override fun myClick(pos: Int) {
                Utils.utilToast(this@MessageActivity, "?????? ??? $pos")
                binding.emojiViewpager.currentItem =
                    pos//ViewPager???????????????????????? ??? ViewPager ??????????????????????????? OnPageChangeCallback -> ???RecyclerView Item??????????????????
                binding.emojiminiRcy.smoothScrollToPosition(pos + 1)//RecyclerView??????????????????
                temp =
                    pos//???????????????????????????refreshBg????????? ????????????Item -> notifyItemChange() ???????????? ???????????? -> notifyDataSetChanged() ??????????????????

            }

        })

        //ViewPager ??????????????????
        binding.emojiViewpager.registerOnPageChangeCallback(object :
            ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                Log.e(" >>    >>", "?????????")
                Utils.utilToast(this@MessageActivity, "????????? $position ???")
                adapter.refreshBg(position, temp)//???RecyclerView Adapter????????????ViewPager???????????????
                temp = position
                binding.emojiminiRcy.smoothScrollToPosition(position + 1)
            }

            //???????????????????????? override fun onPageScrollStateChanged , override fun onPageScrolled
        })
    }

    //??????????????????????????? fragment ??????
    private fun initFragment() {
        val list: MutableList<Fragment> = ArrayList()
        list.add(EmojiOneFragment())
        list.add(EmojiTwoFragment())
        //??????????????????Fragment????????????????????????????????????StaggeredGrid?????????????????????
        //binding.emojiViewpager.setOffscreenPageLimit(list.size - 1) //???????????????
        val adapter: FragmentStateAdapter = object : FragmentStateAdapter(this) {
            override fun createFragment(position: Int): Fragment {
                return list[position]
            }

            override fun getItemCount(): Int {
                return list.size
            }
        }
        binding.emojiViewpager.orientation = ViewPager2.ORIENTATION_HORIZONTAL
        binding.emojiViewpager.adapter = adapter //?????????????????????ViewPager2
    }

    //???????????????????????????
    override fun onEmojiClick(text: String?) {
        Utils.utilToast(this, text.toString())
        binding.EditTextTextMessage.setText(text)
    }

    //????????????????????????
    private fun choseEmoji() {
        val params: RelativeLayout.LayoutParams = el.layoutParams as RelativeLayout.LayoutParams
        params.bottomMargin = -(Utils.dp2px(this, 340.5).toInt())
        el.layoutParams = params //???layout??????
        emoji = false

        binding.imageViewSmell.setOnClickListener {
            Utils.utilToast(this, "???????????? ?????????$pull emoji ??? $emoji")
            Keybords.closeKeybord(binding.EditTextTextMessage, this)
            pull = false

            //????????????????????????????????????emoji??????????????????
            if (!emoji) {
                Utils.utilToast(this, "?????? emoji")
                emoji = true

                //???emoji?????????????????????,?????????????????????????????????
                val param: RelativeLayout.LayoutParams = input.layoutParams as RelativeLayout.LayoutParams
                //params.addRule(RelativeLayout.RIGHT_OF, R.id.imageView_more)
                param.bottomMargin = Utils.dp2px(this, 340.5).toInt()
                input.layoutParams = param //???layout??????

                //?????????????????????????????????????????????0????????????
                params.bottomMargin = 0
                el.layoutParams = params //???layout??????
                //???????????? ?????? ??????
                binding.imageViewSmell.setImageDrawable(getDrawable(R.drawable.ic_keyborad))
            } else {

                emoji = false
                Keybords.openKeybord(binding.EditTextTextMessage, this)
                params.bottomMargin = -(Utils.dp2px(this, 340.5).toInt())
                el.layoutParams = params //???layout??????
                binding.imageViewSmell.setImageDrawable(getDrawable(R.drawable.ic_smell))

            }
        }
    }

    /**
     * init notification
     * @Description app ?????????????????????
     * */
    private fun initNotification() {
        manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager//get Notification manager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("normal", "Normal", NotificationManager.IMPORTANCE_DEFAULT)
            val channel1 = NotificationChannel("important", "Important", NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel1)
        }
    }


    //normal notification
    private fun createNotification(author: String, msg: String) {
        //click the notification intent to activity
        val intent = Intent(this, MessageActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        //here have a problem that the set icon LargeIcon can not display, and the float style notification can not display too

        //???????????????bug ?????????????????????????????????
        val notification = NotificationCompat.Builder(this, "important")
            .setContentTitle(msg)
            .setContentText(author)
            .setSmallIcon(R.drawable.androidstudio)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.ic_orange))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            //.setFullScreenIntent(pendingIntent, true)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .build()

        //if this activity status is onResume that the notification will be display or not
        Log.e(" >>    >>", "the onResume is $displayNotification" )

        //Log.e(" >>    >>", "create the  notification" )
        manager.notify(1, notification)


    }

    //other notification

    








}

