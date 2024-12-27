package syntactics.boilerplate.app.ui.sample.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import syntactics.android.app.databinding.AdapterArticleBinding
import syntactics.boilerplate.app.data.model.TodoModel


import syntactics.boilerplate.app.utils.loadImage


class ArticleAdapter (val context: Context, val clickListener: ArticleCallback) :
    RecyclerView.Adapter<ArticleAdapter.AdapterViewHolder>() {

    private val adapterData = mutableListOf<TodoModel>()

    fun clear(){
        adapterData.clear()
        notifyDataSetChanged()
    }

    fun appendData(newData: List<TodoModel>) {
        val startAt = adapterData.size
        adapterData.addAll(newData)
        notifyItemRangeInserted(startAt, newData.size)
    }


    fun getData(): MutableList<TodoModel> = adapterData

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

        fun displayData(data: TodoModel) = with(itemView) {
            binding.titleTextView.text = data.title
            binding.descTextView.text = data.description
            binding.articleImageView.loadImage("https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcTRQsy_tHu9CJs0UkIa5FGBik1IYntPf1kk2Q&s", context)
            binding.articleLinearLayout.setOnClickListener {
                clickListener.onItemClicked(data)
            }
        }
    }

    interface ArticleCallback{
        fun onItemClicked(data: TodoModel)
    }

    override fun getItemCount(): Int = adapterData.size
}