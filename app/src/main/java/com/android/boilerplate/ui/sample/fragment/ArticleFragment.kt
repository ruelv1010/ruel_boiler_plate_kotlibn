package com.android.boilerplate.ui.sample.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.boilerplate.R
import com.android.boilerplate.data.model.ArticleModel
import com.android.boilerplate.databinding.FragmentArticleBinding
import com.android.boilerplate.ui.sample.adapter.ArticleAdapter

class ArticleFragment: Fragment(), ArticleAdapter.ArticleCallback {
    private var _binding: FragmentArticleBinding? = null
    private val binding get() = _binding!!
    private var linearLayoutManager: LinearLayoutManager? = null
    private var articleAdapter: ArticleAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArticleBinding.inflate(
            inflater,
            container,
            false
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupArticleList()
    }


    private fun setupArticleList() = binding.run {
        articleAdapter =
            ArticleAdapter(requireActivity(), this@ArticleFragment)
        linearLayoutManager = LinearLayoutManager(context)
        articleRecyclerView.layoutManager = linearLayoutManager
        articleRecyclerView.adapter = articleAdapter

        val articleList = listOf(
            ArticleModel("Title 1", getString(R.string.sample_lorem_ipsum), "https://images.pexels.com/photos/4155255/pexels-photo-4155255.jpeg?cs=srgb&dl=pexels-lumn-4155255.jpg&fm=jpg"),
            ArticleModel("Title 2", getString(R.string.sample_lorem_ipsum), "https://images.pexels.com/photos/15286/pexels-photo.jpg?cs=srgb&dl=pexels-luis-del-r%C3%ADo-15286.jpg&fm=jpg"),
            ArticleModel("Title 3", getString(R.string.sample_lorem_ipsum), "https://images.pexels.com/photos/2775196/pexels-photo-2775196.jpeg?cs=srgb&dl=pexels-roberto-nickson-2775196.jpg&fm=jpg"),
            ArticleModel("Title 4", getString(R.string.sample_lorem_ipsum), "https://images.pexels.com/photos/34107/milky-way-stars-night-sky.jpg?cs=srgb&dl=pexels-pixabay-34107.jpg&fm=jpg"),
            ArticleModel("Title 5", getString(R.string.sample_lorem_ipsum), "https://images.pexels.com/photos/4155255/pexels-photo-4155255.jpeg?cs=srgb&dl=pexels-lumn-4155255.jpg&fm=jpg"),
            ArticleModel("Title 6", getString(R.string.sample_lorem_ipsum), "https://images.pexels.com/photos/15286/pexels-photo.jpg?cs=srgb&dl=pexels-luis-del-r%C3%ADo-15286.jpg&fm=jpg"),
            ArticleModel("Title 7", getString(R.string.sample_lorem_ipsum), "https://images.pexels.com/photos/2775196/pexels-photo-2775196.jpeg?cs=srgb&dl=pexels-roberto-nickson-2775196.jpg&fm=jpg"),
            ArticleModel("Title 8", getString(R.string.sample_lorem_ipsum), "https://images.pexels.com/photos/34107/milky-way-stars-night-sky.jpg?cs=srgb&dl=pexels-pixabay-34107.jpg&fm=jpg")

        )
        articleAdapter?.appendData(articleList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onItemClicked(data: ArticleModel) {
        Toast.makeText(requireActivity(), "Article ${data.title} is clicked.", Toast.LENGTH_LONG).show()
    }
}