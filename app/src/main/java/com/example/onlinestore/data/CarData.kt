package com.example.onlinestore.data

data class CarData(
    var id: Int?=null,
    var name: Pair<String?, String?> = Pair(null,null),
    var carImage: String?=null,
    var gearType: Pair<String?,String?> = Pair(null,null),
    var doors: Int?=null,
    var seats: Int?=null,
    var rent: Int?=null,
    var fuelType: String?=null,
    var carDetails: Pair<String?,String?> = Pair(null,null),
    var isBlueTooth: Pair<String?,String?> = Pair(null,null),
    var isGps: Pair<String?,String?> = Pair(null,null),
    var bookingTotalPrice: Int?= null,
    var detailCarImages: MutableList<String?> ?=null,
    var viewType: Int?=null,
)


















/*class MyAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val TYPE_ITEM = 0
    private val TYPE_LOADING = 1

    private var data: List<String> = ArrayList()
    private var isLoading = false

    // ... other methods

    override fun getItemViewType(position: Int): Int {
        return if (isLoading && position == itemCount - 1) {
            TYPE_LOADING
        } else {
            TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            // create and return your regular item view holder
        } else {
            // create and return your loading item view holder
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == TYPE_ITEM) {
            // bind your regular item view holder
        } else {
            // bind your loading item view holder
        }
    }

    fun setLoading(isLoading: Boolean) {
        this.isLoading = isLoading
        notifyDataSetChanged()
    }
}
*/

