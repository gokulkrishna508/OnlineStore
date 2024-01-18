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
    var isGps: Pair<String?,String?> = Pair(null,null)
)


/*import okhttp3.OkHttpClient
import okhttp3.Request

fun main() {
    val baseUrl = "https://jsonplaceholder.typicode.com/posts"
    val client = OkHttpClient()

    // Change these values based on your API's pagination parameters
    val pageSize = 5

    // Perform pagination loop
    var currentPage = 1

    while (true) {
        val url = "$baseUrl?_page=$currentPage&_limit=$pageSize"
        val request = Request.Builder().url(url).build()

        val response = client.newCall(request).execute()

        if (!response.isSuccessful) {
            // Handle error
            println("Error: ${response.code()}")
            break
        }

        val responseBody = response.body?.string()
        println("Page $currentPage Response: $responseBody")

        // Process the response data as needed

        // Check if there are more pages based on your API's logic
        val nextPageExists = response.headers["Link"]?.contains("rel=\"next\"") ?: false
        if (!nextPageExists) {
            println("No more pages. Exiting pagination.")
            break
        }

        // Increment the page number for the next request
        currentPage++
    }

    // Close the client when done
    client.dispatcher.executorService.shutdown()
}
*/

/*class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: CarAdapter
    private val carViewModel by viewModels<CarViewModel>()

    private var currentPage = 1
    private var isLoading = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observeApiResponse()
        setupScrollListener()
    }

    private fun initViews() = binding.apply {
        adapter = CarAdapter {}
        rvCarCategory.layoutManager = LinearLayoutManager(requireContext())
        rvCarCategory.adapter = adapter
    }

    private fun observeApiResponse() {
        viewLifecycleOwner.lifecycleScope.launch {
            carViewModel.apiResponseStateFlow.collectLatest { response ->
                // Parse and process the response in the ViewModel
                carViewModel.processApiResponse(response, currentPage)
            }
        }
    }

    private fun setupScrollListener() {
        binding.rvCarCategory.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                val totalItemCount = layoutManager.itemCount

                if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                    isLoading = true
                    currentPage++
                    carViewModel.fetchData(currentPage)
                }
            }
        })
    }
}
*/

/*
class CarViewModel : ViewModel() {
    // ... other ViewModel code

    fun processApiResponse(response: JSONObject?, currentPage: Int) {
        // Parse and process the response
        val meta = response?.optJSONObject("response")?.optJSONObject("result")?.optJSONObject("meta")
        val apiCurrentPage = meta?.optInt("current_page")
        val apiLastPage = meta?.optInt("last_page")

        // Notify the repository about the new data
        if (apiCurrentPage != null && apiCurrentPage <= apiLastPage!!) {
            fetchData(currentPage)
        }
    }
}
 */

