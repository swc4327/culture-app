package kr.ac.kumoh.s20150686.mytermproject.ui.music

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.NetworkImageView
import kr.ac.kumoh.s20150686.mytermproject.R

//import 본인의 package 이름.gundammanager.R

class MusicFragment : Fragment() {

    private lateinit var model: MusicViewModel
    private val mAdapter = MusicAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 매번 요청하게 하려면 this (Fragment) 사용
        //model = ViewModelProvider(this).get(GundamViewModel::class.java)

        // 처음에 한 번만 요청하게 하려면 activity 사용
        model = ViewModelProvider(activity as AppCompatActivity).get(MusicViewModel::class.java)

        model.list.observe(viewLifecycleOwner, Observer<ArrayList<MusicViewModel.Music>> {
            mAdapter.notifyDataSetChanged()
        })

        val root = inflater.inflate(R.layout.fragment_music, container, false)
//여기서 fragment_music.xml파일은 리사이클러뷰로 이루어진 xml 파일이다.
        val lsResult = root.findViewById<RecyclerView>(R.id.lsResult)
        lsResult.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
        //리사이클러뷰 속성 설정
        return root
    }

    inner class MusicAdapter: RecyclerView.Adapter<MusicAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val txText1: TextView = itemView.findViewById<TextView>(R.id.text1)
            val txText2: TextView = itemView.findViewById<TextView>(R.id.text2)

            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)
//txText1은 title, txText2는 name, niImage는 이미지.
            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
            }
            //niImage 디폴트 값 설정.
        }

        override fun getItemCount(): Int {
            return model.getSize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                R.layout.item_music,
                parent,
                false)
            return ViewHolder(view)
        }
        //여기서 item_music.xml파일은 전체적으로 cardview로 구성되어 있고 networkimageview와 textview로 이루어져 있다.

        override fun onBindViewHolder(holder: MusicAdapter.ViewHolder, position: Int) {
            holder.txText1.text = model.getMusic(position).title
            holder.txText2.text = model.getMusic(position).name
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)


            holder.itemView.setOnClickListener {
                val uri = Uri.parse("https://www.youtube.com/results?search_query="
                        +holder.txText1.text+" "+holder.txText2.text)
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
            //각 아이템 값을 클릭하면 클릭이벤트로 유튜브 링크로 이동한다.
        }
    }
}