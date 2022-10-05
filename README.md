Naming Conventions
Please refer to android_guidelines.md for detailed code guidelines on Android and https://android.github.io/kotlin-guides/style.html for detailed code guidelines on Kotlin.

Layouts


A layout defines the structure for a user interface in your app, such as in an activity. For more details about layout, see Layout. Use the format <type>_<name>.xml 




Type
	Name
	Activity
	activity_<activity_name>.xml
	Fragment
	fragment_<fragment_name>.xml
	List Item
	item_<item_name>.xml
	Custom Dialog
	dialog_<dialog_name>.xml
	Custom View
	view_<view_name>.xml
	





Color Resource


For feature specific color, prepend the name of the feature to the name of the layout
Example: chat_label_username




String Resource


String resources must only be used to display a message to the user interface. Use the format <type>_<name


Type
	Name
	

	Label
	label_username
	Prefix for labels. Used for displays only.
	Error Message
	error_connection
	For error messages.
	Progress Message
	progress_sending
	For messages used to portray loading/progress.
	Display Messages
	message_success
	For displaying informative messages.
	Confirmation Messages
	confirm_logout
	For messages that ask the user for confirmations.
	



Java/Kotlin Code


Type
	Name
	Description
	Global Variable
	messages
	All local variables names should be camel case
	Local Variable
	messages
	All local variables names should be camel case
	Constant
	FULL_NAME
	All constants should be all uppercase
	Method
	displayDetails
	All methods should be camel case
	Class
	DashboardActivity
	All classes should be in title case
	



Android Specific Code


Type
	Name
	Example
	Description
	

Type
	Name
	Example
	Description
	Activity
	<name>Activity.java/kt
	LoginActivity
	AndroidActivity class
	Fragment
	<name>Fragment.java/kt
	ProfileFragment
	Fragment class
	ViewModel
	<activity or fragment name>ViewModel.java/kt
	ProfileViewModel
	View Model of an activity/fragment
	Model
	<model_name>.java/kt
	User
	The generic model that may be used outside the context of network
	Retrofit Service
	<feature_name>Service.java/kt
	OrdersService
	Service interface which retrofit will build
	Request Model
	<function_name>Request.java/kt
	CreateOrderRequest
	Request model sent on API endpoints (e.g., HTTP Post body )
	Response Model
	<function_name>Response.java/kt
	OrderListResponse
	Response model for mapping actual API response body of an endpoint
	Repository
	<feature_name>Repository.java/kt
	OrdersRepository
	Singleton Repository class of the feature
	Adapter
	<list_name>Adapter.java/kt
	AlbumAdapter
	List/RecyclerView Adapter
	ViewHolder
	<type_name>ViewHolder
	AlbumViewHolder
	A type of ViewHolder of the Adapter