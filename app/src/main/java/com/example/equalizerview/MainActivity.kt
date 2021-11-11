package com.example.equalizerview

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.equalizerview.equalizer.EqualizerView

class MainActivity : AppCompatActivity(), EqualizerView.OnEqualizerDataChanged {

    private lateinit var equalizer: EqualizerView
    private lateinit var tv : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv = findViewById(R.id.tv1)
        equalizer = findViewById(R.id.equalizer)
        equalizer.setOnEqualizerDataChangedListener(this)
    }

    override fun shareRowsHeight(percentList: ArrayList<Int>) {
        var text = ""
        (percentList.indices).forEach { i ->
            text += if(i != percentList.lastIndex) "${percentList[i]}" + "%, " else "${percentList[i]}"+ "%"
        }
        tv.text = text
    }

}