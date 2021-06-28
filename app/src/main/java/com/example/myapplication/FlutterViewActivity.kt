package com.example.myapplication

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.FrameLayout
import io.flutter.embedding.android.FlutterActivity
import io.flutter.embedding.android.FlutterView
import io.flutter.embedding.engine.FlutterEngine
import io.flutter.embedding.engine.FlutterEngineCache
import io.flutter.embedding.engine.dart.DartExecutor
import io.flutter.plugin.common.MethodChannel
import org.json.JSONObject


class FlutterViewActivity : AppCompatActivity() {

    companion object {
        const val CHANNEL = "com.test.mydata/data"

        fun startActivity(context: MainActivity) {
            val intent = Intent(context, FlutterViewActivity::class.java)

            context.startActivityForResult(intent, 100)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flutterEngine = FlutterEngine(this)
        flutterEngine.getDartExecutor().executeDartEntrypoint(
            DartExecutor.DartEntrypoint.createDefault()
        )
        FlutterEngineCache.getInstance().put("id",flutterEngine);
        flutterEngine.getNavigationChannel().setInitialRoute("/");
        MethodChannel(flutterEngine.getDartExecutor().getBinaryMessenger(), CHANNEL).setMethodCallHandler { call, result ->
            // manage method calls here
            print(call.method.toString())
            if (call.method == "FromClientToHost") {
                val resultStr = call.arguments.toString()
                print(resultStr)
                val resultJson = JSONObject(resultStr)
                val res = resultJson.getString("data")
                print("YES")
                Log.d("TAG","YES")
                Log.d("TAG", res)
                val intent = Intent(this@FlutterViewActivity,MainActivity::class.java)
                intent.putExtra("result", res)
                startActivity(intent)

            } else {
                print("NO-")
                finish()
            }
        }
        Handler().postDelayed({
            MethodChannel(flutterEngine.dartExecutor.binaryMessenger, CHANNEL).invokeMethod("fromHostToClient", "hello")
        }, 500)
        startActivity(FlutterActivity.withCachedEngine("id").build(this))



    }
}