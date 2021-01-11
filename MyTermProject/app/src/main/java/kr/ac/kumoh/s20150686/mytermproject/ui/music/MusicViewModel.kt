package kr.ac.kumoh.s20150686.mytermproject.ui.music
//package 이름은 본인의 것 사용할 것

import androidx.lifecycle.MutableLiveData

import android.app.Application
import android.graphics.Bitmap
import android.widget.Toast
import androidx.collection.LruCache
import androidx.lifecycle.AndroidViewModel
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.net.URLEncoder

class MusicViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"

        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        const val SERVER_URL ="http://172.20.10.6:8080"
    }

    private var mQueue: RequestQueue

    data class Music(var id: Int, var title: String, var name: String,var image: String)
//서버에 구성 되어있는 music table db들은 id,title,name,image로 구성되어 있으므로 Music data class를 위와 같이 구성하였다.
    val list = MutableLiveData<ArrayList<Music>>()
    private val music = ArrayList<Music>()

    val imageLoader: ImageLoader
    init {
        list.value = music
        mQueue = Volley.newRequestQueue(application)

        imageLoader = ImageLoader(mQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(100)
                override fun getBitmap(url: String): Bitmap? {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })

        requestMusic()
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(music[i].image, "utf-8")

    fun requestMusic() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            SERVER_URL,
            null,
            {
                //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                music.clear()
                parseJson(it)
                list.value = music
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )
//Array형식으로 받은 후 밑에 parseJson 함수에서 Array를 Object로 변환 하였다.
        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    fun getMusic(i: Int) = music[i]

    fun getSize() = music.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) {
        for (i in 0 until 8) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val title = item.getString("title")
            val name = item.getString("name")
            val image = item.getString("image")

            music.add(Music(id, title, name, image))
        }
    }
}