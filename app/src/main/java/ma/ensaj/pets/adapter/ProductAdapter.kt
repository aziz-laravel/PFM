package ma.ensaj.pets.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import ma.ensaj.pets.dto.Product
import ma.ensaj.pets.R

class ProductAdapter(private val onProductClick: (Product) -> Unit) :
    ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product_item, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product, onProductClick)
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.product_name)
        private val priceTextView: TextView = itemView.findViewById(R.id.product_price)
        private val productImageView: ImageView = itemView.findViewById(R.id.product_image)

        fun bind(product: Product, onProductClick: (Product) -> Unit) {
            nameTextView.text = product.name
            priceTextView.text = product.price.toString()

            // Charger l'image avec Glide
            Glide.with(itemView.context)
                .load(product.imageUrl) // URL de l'image
                .placeholder(R.drawable.ic_launcher_background) // Image par défaut pendant le chargement
                .error(R.drawable.ic_launcher_foreground) // Image en cas d'erreur de chargement
                .into(productImageView)

            itemView.setOnClickListener {
                onProductClick(product)
            }
        }
    }

    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}
