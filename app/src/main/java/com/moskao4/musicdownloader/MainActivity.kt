package com.moskao4.musicdownloader

import android.app.Activity
import android.os.Bundle
import android.widget.*
import java.util.regex.Pattern

class MainActivity: Activity() {
 override fun onCreate(savedInstanceState: Bundle?) {
  super.onCreate(savedInstanceState)
  setContentView(R.layout.activity_main)

  val input=findViewById<EditText>(R.id.linkInput)
  val btn=findViewById<Button>(R.id.downloadBtn)
  val status=findViewById<TextView>(R.id.status)

  btn.setOnClickListener {
   val text=input.text.toString()
   val id=Pattern.compile("song\\?id=(\\d+)").matcher(text)
   if(id.find()){
    val songId=id.group(1)
    status.text="歌曲ID: $songId\n准备下载"
   }else{
    status.text="未识别网易云链接"
   }
  }
 }
}
