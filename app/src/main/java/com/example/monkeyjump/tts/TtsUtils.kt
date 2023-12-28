package com.example.monkeyjump.tts

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.baidu.tts.client.SpeechError
import com.baidu.tts.client.SpeechSynthesizeBag
import com.baidu.tts.client.SpeechSynthesizer
import com.baidu.tts.client.SpeechSynthesizerListener
import com.baidu.tts.client.TtsMode

/**
 * @Author fanlitao
 * @Date 2023/12/28
 */
object TtsUtils : SpeechSynthesizerListener {

    private const val TAG = "TtsUtils"
    const val BAG_LENGTH = 8
    private const val appId = "45912966"
    private const val appKey = "HQKUlqGtpXivQA6GPZXcN9s2"
    private const val secretKey = "nIeWrsNMQyZhUuRXPF5ntECM5zh1uv07"
    private val mainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }
    private var tts: SpeechSynthesizer? = null
    private var progressChangedListener: ISpeechProgressChanged? = null

    fun initTTS(context: Context, changeListener: ISpeechProgressChanged) {
        progressChangedListener = changeListener
        tts = SpeechSynthesizer.getInstance();
        tts?.setContext(context)
        tts?.setSpeechSynthesizerListener(this)
        tts?.setAppId(appId)
        tts?.setApiKey(appKey, secretKey)

        // 5. 以下setParam 参数选填。不填写则默认值生效
        // 设置在线发声音人： 0 普通女声（默认） 1 普通男声  3 情感男声<度逍遥> 4 情感儿童声<度丫丫>
        tts?.setParam(SpeechSynthesizer.PARAM_SPEAKER, "3")
        // 设置合成的音量，0-15 ，默认 5
        tts?.setParam(SpeechSynthesizer.PARAM_VOLUME, "9")
        // 设置合成的语速，0-15 ，默认 5
        // 设置合成的语速，0-15 ，默认 5
        tts?.setParam(SpeechSynthesizer.PARAM_SPEED, "5")
        // 设置合成的语调，0-15 ，默认 5
        tts?.setParam(SpeechSynthesizer.PARAM_PITCH, "5")
        // 检测参数，通过一次后可以去除，出问题再打开debug
        // 初始化
        val result = tts?.initTts(TtsMode.ONLINE)
        Log.e(TAG, "initTTS: $result")
    }

    fun speak(text: String) {
        if (text.length > 8) {
            tts?.batchSpeak(splitsBag(text))
        } else {
            tts?.speak(text)
        }
    }

    private fun splitsBag(text: String): MutableList<SpeechSynthesizeBag> {
        val list = mutableListOf<SpeechSynthesizeBag>()
        var cur = 0
        val step = BAG_LENGTH
        while (cur < text.length) {
            val end = if (cur + step > text.length) text.length else cur + step
            val substring = text.substring(cur, end)
            list.add(SpeechSynthesizeBag().apply {
                setText(substring)
            })
            cur += substring.length
        }
        return list
    }

    override fun onSynthesizeStart(p0: String?) {

    }

    override fun onSynthesizeDataArrived(p0: String?, p1: ByteArray?, p2: Int, p3: Int) {
        Log.e(TAG, "onSynthesizeDataArrived: 0:$p0 2:$p2 3:$p3")
    }

    override fun onSynthesizeFinish(p0: String?) {

    }

    override fun onSpeechStart(p0: String?) {

    }

    override fun onSpeechProgressChanged(p0: String, p1: Int) {
//        Log.e(TAG, "onSpeechProgressChanged: $p0 i:$p1")
        progressChangedListener?.let {
            mainHandler.post {
                try {
                    it.onSpeechProgressChanged(p0.toInt(), p1)
                } catch (ex: NumberFormatException) {
                    ex.printStackTrace()
                }
            }
        }
    }

    override fun onSpeechFinish(p0: String?) {

    }

    override fun onError(p0: String?, p1: SpeechError?) {
        Log.e(TAG, "onError: ")
    }

    interface ISpeechProgressChanged {
        fun onSpeechProgressChanged(sequence: Int, index: Int)
    }
}