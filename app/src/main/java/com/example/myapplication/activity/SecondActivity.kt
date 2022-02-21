package com.example.myapplication.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.myapplication.R
import com.example.myapplication.databinding.ActivitySecondBinding

class SecondActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySecondBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.e("SecondActivity onCreate", this.toString(),)
        setContentView(R.layout.activity_second)

        binding = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(binding.root)


        startActivityForResult()
        singleTop()
    }

    //finish this activity and send extra to it is parent activity
    private fun startActivityForResult(){
        binding.buttonBack.setOnClickListener {
            val str = binding.editTextTextPersonName.text.toString()
            val intent = Intent()
            intent.putExtra("data_return", str)
            setResult(RESULT_OK, intent)

            finish()
        }
    }

    //this part is a singleTop intent demo //first code of android >>> p120
    private fun singleTop(){
        binding.buttonSingleTopIntentToFirstActivity.setOnClickListener {
            val intent = Intent(this, FirstActivity::class.java)
            startActivity(intent)
        }
    }
}