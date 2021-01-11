package kr.ac.kumoh.s20150686.mytermproject.ui.movie

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

class MovieFragment : Fragment() {

    private lateinit var model: MovieViewModel
    private val mAdapter = MovieAdapter()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // 매번 요청하게 하려면 this (Fragment) 사용
        //model = ViewModelProvider(this).get(GundamViewModel::class.java)

        // 처음에 한 번만 요청하게 하려면 activity 사용
        model = ViewModelProvider(activity as AppCompatActivity).get(MovieViewModel::class.java)

        model.list.observe(viewLifecycleOwner, Observer<ArrayList<MovieViewModel.Movie>> {
            mAdapter.notifyDataSetChanged()
        })

        val root = inflater.inflate(R.layout.fragment_movie, container, false)

        val lsResult = root.findViewById<RecyclerView>(R.id.lsResult)
        lsResult.apply {
            layoutManager = LinearLayoutManager(activity)
            setHasFixedSize(true)
            itemAnimator = DefaultItemAnimator()
            adapter = mAdapter
        }
        return root
    }

    inner class MovieAdapter: RecyclerView.Adapter<MovieAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val txText1: TextView = itemView.findViewById<TextView>(R.id.text1)
            val txText2: TextView = itemView.findViewById<TextView>(R.id.text2)

            val niImage: NetworkImageView = itemView.findViewById<NetworkImageView>(R.id.image)

            init {
                niImage.setDefaultImageResId(android.R.drawable.ic_menu_report_image)
            }
        }

        override fun getItemCount(): Int {
            return model.getSize()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MovieAdapter.ViewHolder {
            val view = layoutInflater.inflate(
                    R.layout.item_movie,
                    parent,
                    false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: MovieAdapter.ViewHolder, position: Int) {
            holder.txText1.text = model.getMovie(position).title
            holder.txText2.text = model.getMovie(position).name
            holder.niImage.setImageUrl(model.getImageUrl(position), model.imageLoader)

            holder.itemView.setOnClickListener {
                val uri = Uri.parse("https://www.youtube.com/results?search_query="+
                        "영화 "+holder.txText1.text+" 예고편")
                val intent = Intent(Intent.ACTION_VIEW, uri)
                startActivity(intent)
            }
        }
    }
}