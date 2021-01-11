package kr.ac.kumoh.s20150686.mytermproject.ui.book
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

class BookViewModel(application: Application) : AndroidViewModel(application) {
    companion object {
        const val QUEUE_TAG = "VolleyRequest"

        // NOTE: 서버 주소는 본인의 서버 IP 사용할 것
        const val SERVER_URL ="http://172.20.10.6:8080"
    }

    private var mQueue: RequestQueue

    data class Book(var id: Int, var title: String, var name: String,var image: String)

    val list = MutableLiveData<ArrayList<Book>>()
    private val book = ArrayList<Book>()

    val imageLoader: ImageLoader
    init {
        list.value = book
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

        requestBook()
    }

    fun getImageUrl(i: Int): String = "$SERVER_URL/image/" + URLEncoder.encode(book[i].image, "utf-8")

    fun requestBook() {
        val request = JsonArrayRequest(
            Request.Method.GET,
            SERVER_URL,
            null,
            {
                //Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
                book.clear()
                parseJson(it)
                list.value = book
            },
            {
                Toast.makeText(getApplication(), it.toString(), Toast.LENGTH_LONG).show()
            }
        )

        request.tag = QUEUE_TAG
        mQueue.add(request)
    }

    fun getBook(i: Int) = book[i]

    fun getSize() = book.size

    override fun onCleared() {
        super.onCleared()
        mQueue.cancelAll(QUEUE_TAG)
    }

    private fun parseJson(items: JSONArray) {
        for (i in 16 until 24) {
            val item: JSONObject = items[i] as JSONObject
            val id = item.getInt("id")
            val title = item.getString("title")
            val name = item.getString("name")
            val image = item.getString("image")

            book.add(Book(id, title, name, image))
        }
    }
}