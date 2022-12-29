package com.android.boilerplate.ui.sample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.boilerplate.data.repositories.article.response.ArticleData
import com.android.boilerplate.databinding.AdapterArticleBinding
import com.android.boilerplate.utils.loadImage

class ArticleAdapter (val context: Context, val clickListener: ArticleCallback) :
    RecyclerView.Adapter<ArticleAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<ArticleData>()

    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<ArticleData>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }


    fun getData(): MutableList<ArticleData> = adapterData

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdapterViewHolder {
        val binding = AdapterArticleBinding
            .inflate(LayoutInflater.from(parent.context), parent, false)
        return AdapterViewHolder(binding)

    }

    override fun onBindViewHolder(holder: AdapterViewHolder, position: Int) {
        holder.displayData(adapterData[position])
    }

    inner class AdapterViewHolder(val binding: AdapterArticleBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun displayData(data: ArticleData) = with(itemView) {
            binding.titleTextView.text = data.name
            binding.descTextView.text = data.description
            binding.articleImageView.loadImage(data.image?.thumb_path, context)
            binding.articleLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data)
            }
        }
    }

    interface ArticleCallback{
        fun onItemClicked(data: ArticleData)
    }

    override fun getItemCount(): Int = adapterData.size
}